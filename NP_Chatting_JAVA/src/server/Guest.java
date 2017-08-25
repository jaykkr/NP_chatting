package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;

import client.Member;
import client.Message;

/**
 * Server create separate Guest thread to provide a service for each client 
 * Guest thread communicate to client via ServerMsgListner and all of message
 * have 4 digits of prefix to identify functionality of the messages.
 * Guest forward messages from client to ServerHandler to handler the messages.
 * Based on prefix, appropriate method in ServerHandler is called.
 */
public class Guest extends Thread {

	private ServerHandler server;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private Member member;
	private String roomName = "";

	public Guest(ServerHandler server, Socket socket) throws IOException {
		this.server = server;
		this.socket = socket;
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
	}

	@Override
	public void run() {
		String msg;
		String prefix;
		String clientIP;
		
		try {
			while ((msg = in.readLine()) != null) {
				prefix = msg.substring(0, 4);//begin, end
				msg = msg.substring(4);//begin
				
				/* Ip address of client */
				clientIP = this.getSocket().getInetAddress().getHostAddress();
				
				System.out.println("__DEBUG__"+prefix +":"+ msg);
				
				if (prefix.equals(Message.LOGIN_REQUEST_CHECKID)) {
					if (server.checkId(msg)) {
						sendMsg(Message.LOGIN_CHECKID_SUCCESS);
					} else {
						sendMsg(Message.LOGIN_CHECKID_FAIL);
					}
				} else if (prefix.equals(Message.LOGIN_REQUEST_CHECKNICK)) {
					if (server.checkNick(msg)) {
						sendMsg(Message.LOGIN_CHECKNICK_SUCCESS);
					} else {
						sendMsg(Message.LOGIN_CHECKNICK_FAIL);
					}
				} else if (prefix.equals(Message.LOGIN_REQUEST_JOIN)) {
					if (server.requestJoin(msg)) {
						sendMsg(Message.LOGIN_JOIN_SUCCESS);
					} else {
						sendMsg(Message.LOGIN_JOIN_FAIL);
					}
				} else if (prefix.equals(Message.LOGIN_REQUEST)) {
					String tmp[] = msg.split(",");// id, pwd

					Member member = server.getMember(tmp[0]);// get DB data with id
					System.out.println("__DEBUG__Login Get DB info for "+ tmp[0]);

					if (member == null) {
						sendMsg(Message.LOGIN_FAIL_WRONG_ID);
					} else {
						if (!member.getPwd().equals(tmp[1])) {
							sendMsg(Message.LOGIN_FAIL_WRONG_PWD);
						} else {
							if (server.checkIdMap(tmp[0])) {
								sendMsg(Message.LOGIN_FAIL_LOGINED_ID);// logged in already
							} else {
								sendMsg(Message.LOGIN_SUCCESS + member.getId() + "," + member.getPwd() + "," + member.getName() + "," + member.getNick() );
								setMember(member);
								// update waiting room list(guest) and login user list(idMap)
								System.out.println("__DEBUG__Login db info : "+ member.getId() + "," + member.getPwd() + "," + member.getName() + "," + member.getNick());

								server.userLogin(this);
								server.broadcastWaitRoomUpdate();
							}
						}
					}
				} else if (prefix.equals(Message.WAIT_USER_OUT)) {
					userLeave(false);
				} else if (prefix.equals(Message.WAIT_CHAT_MSG)) {
					server.broadcast_waitRoom(Message.WAIT_CHAT_MSG + msg );
					
				} else if (prefix.equals(Message.ROOM_REQUEST_MAKE)) {
					if (server.addRoom(msg)) {
						roomName = msg;
						sendMsg(Message.ROOM_MAKE_SUCCESS + roomName);
						server.addUserToRoom(this, roomName);
						server.removeUserFromWait(this);
						server.broadcastChatRoomUpdate(roomName);
						server.broadcastWaitRoomUpdate();
						sendMsg(Message.ROOM_SET_NAME + roomName);
					} else {
						sendMsg(Message.ROOM_MAKE_FAIL);
					}
				} else if (prefix.equals(Message.ROOM_USER_OUT)) {
					userLeave(false);
				} else if (prefix.equals(Message.ROOM_USER_IN)) {
					/* whenever user join to chatting room, server send broadcast notification 
					 * message to all user in the room
					 */
					roomName = msg;
					server.addUserToRoom(this, msg);
					server.removeUserFromWait(this);
					server.broadcastChatRoomUpdate(msg);
					server.broadcastWaitRoomUpdate();
					server.broadcast_chatRoom(Message.ROOM_CHAT_MSG + "#" + member.getNick() + "# has joined the chatting room", roomName);
					sendMsg(Message.ROOM_SET_NAME + roomName);
				} else if (prefix.equals(Message.ROOM_CHAT_MSG)) {
					// msg = msgcode + ip + msg
					// this.getSocket().getInetAddress().getHostAddress()
					// "["+this.getSocket().getInetAddress().getHostAddress()+"]"+
					server.broadcast_chatRoom(Message.ROOM_CHAT_MSG + "["+this.getSocket().getInetAddress().getHostAddress()+"]"+msg , roomName);
					
				} else if (prefix.equals(Message.ROOM_INVITE_USER)) {
					server.inviteUser(member.getId(), msg, roomName);
				} else if (prefix.equals(Message.ROOM_INVITE_DENY)) {
					String tmp[] = msg.split(",");
					server.inviteDeny(tmp[0], tmp[1], tmp[2]);
				} else if (prefix.equals(Message.ROOM_REQUEST_WAITUSER)) {
					server.returnWaitUsers(this);
				} else if (prefix.equals(Message.LOGOUT)) {
					userLeave(true);
				} else if (prefix.equals(Message.GET_ADDR)) {
					server.getAddr(this, msg, roomName);// msg == nick
				} else if (prefix.equals(Message.ROOM_CHAT_PRIVATE)) {
					//prefix = MSGcode 
					//message = nick + , + message
					// extract nickname out of message
					int index = msg.indexOf(","); //Returns the index within this string of the first occurrence of the specified substring
					String nick = msg.substring(0, index);//begin, end
					msg = msg.substring(index + 1);
					// send message without MSG code - this will be attached in ServerHandler.java
					server.sendPrivate(this, nick, "["+this.getSocket().getInetAddress().getHostAddress()+"]"+msg, roomName);
					
				} else if (prefix.equals(Message.MOD_UPDATE_USERINFO)) {
					String[] tmp = msg.split(","); // msg = id,pwd,name,nick,phone
					String beforeNick = member.getNick();
					String afterNick = tmp[3];
					if (server.modUserInfo(tmp)) {
						sendMsg(Message.MOD_UPDATE_SUCCESS + msg);
						if (!beforeNick.equals(afterNick)) {
							member.setNick(afterNick);
							if (roomName.equals("")) {
								server.broadcastWait_userUpdate();
							} else {
								server.broadcastChatRoomUpdate(roomName);
							}
						}
					} else {
						sendMsg(Message.MOD_UPDATE_FAIL);
					}
				}
			}
		} catch (IOException e) {
			userLeave(false);
			try {
				in.close();
				out.close();
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (SQLException e) {
			String errMsg = "Database error : " + e.getMessage();
			System.out.println(errMsg);
			sendMsg(Message.ERR_DATABASE + errMsg);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public void sendMsg(String msg) {
		out.println(msg);
		out.flush();
	}

	private void userLeave(boolean isLogout) {
		if (member == null) 
			return;
		if (roomName.equals("")) { 
			server.removeUserFromWait(this);
			server.removeUserFromIdMap(member.getId());
			server.broadcastWaitRoomUpdate();
		} else {
			if (isLogout) {
				server.removeUserFromIdMap(member.getId());
			} else {
				server.addUserToWait(this);
			}
			server.removeUserFromRoom(this, roomName);
			server.broadcastWaitRoomUpdate();
			server.broadcastChatRoomUpdate(roomName);
			server.broadcast_chatRoom(Message.ROOM_CHAT_MSG + "#" + member.getNick() + "# left the chatting room, ["+roomName+"]", roomName);
			roomName = "";
		}
	}

	public Member getMember() {
		return member;
	}

	public Socket getSocket() {
		return socket;
	}
}
