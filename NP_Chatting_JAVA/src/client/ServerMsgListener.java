package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

/**
 * Send and receive messages from/to Guest class in Server. Each message start with
 * 4 digits prefix defined in message interface.
 * 
 * in.readLine() keep receiving messages from server until connection is closed. After receiving
 * messages, based on the prefix, appropriate methods in Clienthandler or GUI class are called 
 * to update client's GUI.  
 */
public class ServerMsgListener extends Thread {
	private ClientHandler ch;
	private BufferedReader in;
	private PrintWriter out;
	private Socket socket;

	ServerMsgListener(ClientHandler ch, Socket socket) throws IOException {
		this.ch = ch;
		this.socket = socket;
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
	}

	@Override
	public void run() {
		String msg;
		try {
			/* Receive message from server */
			while ((msg = in.readLine()) != null) {
				/* Extract message prefix. 4 digits */
				String prefix = msg.substring(0, 4);
				msg = msg.substring(4);
				if (prefix.equals(Message.LOGIN_CHECKID_SUCCESS)) {
					//Login sucess
					JOptionPane.showMessageDialog(ch.getFrame(), Message.SUCESS_MSG, "Message",JOptionPane.INFORMATION_MESSAGE);
					ch.getJoinDialog().setIdChecked(true);
				} else if (prefix.equals(Message.LOGIN_CHECKID_FAIL)) {
					// Duplicated user name
					JOptionPane.showMessageDialog(ch.getFrame(), Message.DUPLICATED_MSG,  "Error", JOptionPane.ERROR_MESSAGE);
					ch.getJoinDialog().setIdChecked(false);
				} else if (prefix.equals(Message.LOGIN_CHECKNICK_SUCCESS)) {
					// user name available
					JOptionPane.showMessageDialog(ch.getFrame(), Message.SUCESS_MSG, "Message",JOptionPane.INFORMATION_MESSAGE);
					ch.getJoinDialog().setNickChecked(true);
				} else if (prefix.equals(Message.LOGIN_CHECKNICK_FAIL)) {
					// Duplicated Nick name
					JOptionPane.showMessageDialog(ch.getFrame(), Message.DUPLICATED_MSG,  "Error", JOptionPane.ERROR_MESSAGE);
					ch.getJoinDialog().setNickChecked(false);
				} else if (prefix.equals(Message.LOGIN_JOIN_SUCCESS)) {
					// sign up approved
					JOptionPane.showMessageDialog(ch.getFrame(), Message.APPROVED_MSG, "Message",JOptionPane.INFORMATION_MESSAGE); 
					ch.getLoginPanel().setId(ch.getJoinDialog().getId());
					ch.getJoinDialog().dispose();
				} else if (prefix.equals(Message.LOGIN_JOIN_FAIL)) {
					// sign up fail
					JOptionPane.showMessageDialog(ch.getFrame(), Message.FAILURE_MSG, "Error", JOptionPane.ERROR_MESSAGE);
				} else if (prefix.equals(Message.LOGIN_FAIL_WRONG_ID)) {
					// No login id exist
					JOptionPane.showMessageDialog(ch.getFrame(), Message.FAILURE_MSG, "Error", JOptionPane.ERROR_MESSAGE);
					ch.getLoginPanel().focusIdField();
				} else if (prefix.equals(Message.LOGIN_FAIL_WRONG_PWD)) {
					// wrong password
					JOptionPane.showMessageDialog(ch.getFrame(), Message.FAILURE_MSG, "Error", JOptionPane.ERROR_MESSAGE);
					ch.getLoginPanel().focusPwdField();
				} else if (prefix.equals(Message.LOGIN_FAIL_LOGINED_ID)) {
					// logged in already
					JOptionPane.showMessageDialog(ch.getFrame(), Message.LOGGED_IN_STATUS_MSG, "Message",JOptionPane.INFORMATION_MESSAGE);
					ch.getLoginPanel().focusIdField();
				} else if (prefix.equals(Message.LOGIN_SUCCESS)) {
					// login success
					// Get into waitroom and be ready to chat 
					ch.setMember(new Member(msg.split(",")));
					ch.openWaitRoom();
					ch.getWaitRoom().focusText();
					ch.getWaitRoom().setChatBorder("Chatting(" + ch.getMember().getNick() + ")");
				} else if (prefix.equals(Message.WAIT_USER_UPDATE)) {
					// update user list 
					ch.getWaitRoom().setUserList(msg);
				} else if (prefix.equals(Message.WAIT_ROOM_UPDATE)) {
					// update room list
					ch.getWaitRoom().setRoomList(msg);
				} else if (prefix.equals(Message.CHAT_USER_UPDATE)) {
					// update chat user
					String tmp[] = msg.split(",");
					ch.getChatRoom().setUserList(tmp);
				} else if (prefix.equals(Message.WAIT_CHAT_MSG)) {
					// chat message in wait room
					ch.getWaitRoom().appendMsg(msg);
				} else if (prefix.equals(Message.ROOM_MAKE_SUCCESS)) {
					// Open chat room. Close wait and open chat room
					ch.openChatRoom(msg);
				} else if (prefix.equals(Message.ROOM_MAKE_FAIL)) {
					// Duplicated room name
					JOptionPane.showMessageDialog(ch.getFrame(), Message.DUPLICATED_MSG, "Error", JOptionPane.ERROR_MESSAGE);
				} else if (prefix.equals(Message.ROOM_CHAT_MSG)) {
					// chatting room message
					ch.getChatRoom().appendMsg(msg);
				} else if (prefix.equals(Message.ROOM_SET_NAME)) {
					// display room name on boarder
					ch.getChatRoom().setChatBorder("[" + msg + "]");
				} else if (prefix.equals(Message.ROOM_INVITE_REQUEST)) {
					// invite reqeust
					String tmp[] = msg.split(",");
					String roomName = tmp[1];
					String inviterID = tmp[0];
					String inviteMsg = "Invited from [" + roomName + "] by "+ inviterID + "\n Accept?";

					if (JOptionPane.showConfirmDialog(ch.getFrame(), inviteMsg, "Notice", JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
						ch.enterRoom(roomName);
					} else {
						ch.inviteDeny(msg + "," + ch.getMember().getNick()); // msg : id,roomName
					}
				} else if (prefix.equals(Message.ROOM_INVITE_DENIED)) {
					// decline invitation
					JOptionPane.showMessageDialog(ch.getFrame(), msg + " Declined the invitation", "Message",JOptionPane.INFORMATION_MESSAGE);
				} else if (prefix.equals(Message.ROOM_RETURN_WAITUSER)) {
					ch.getInviteDialog().setListData(msg);// display available user list( all of waiting room users )
				} else if (prefix.equals(Message.GET_ADDR)) {
					// file receiving
					System.out.println("__DEBUG__SvcMsgListen : "+prefix +": IP addr :"+ msg);

					GetFile gf = new GetFile(ch.getFrame(), msg);// msg == sender's ip
					gf.setVisible(true);
				} else if (prefix.equals(Message.ROOM_PRIVATE_FAIL)) {
					// no user exist.
					// In case user exit the chatting room before sending private message.
					JOptionPane.showMessageDialog(ch.getFrame(), "[" + msg + "] : Does not exist", "Error", JOptionPane.ERROR_MESSAGE);
				} else if (prefix.equals(Message.MOD_UPDATE_SUCCESS)) {
					JOptionPane.showMessageDialog(ch.getJoinDialog(), Message.SUCESS_MSG, "Message",JOptionPane.INFORMATION_MESSAGE);
					ch.getJoinDialog().dispose();
					String tmp[] = msg.split(",");
					ch.getMember().setAll(tmp);
					ch.getWaitRoom().setChatBorder("Chatting(" + tmp[3] + ")");// tmp[3] : nickname
				} else if (prefix.equals(Message.MOD_UPDATE_FAIL)) {
					JOptionPane.showMessageDialog(ch.getJoinDialog(), Message.FAILURE_MSG, "Error", JOptionPane.ERROR_MESSAGE);
				} else if (prefix.equals(Message.ERR_DATABASE)) {
					JOptionPane.showMessageDialog(ch.getFrame(), msg, "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		} catch (Exception e) {
			try {
				in.close();
				out.close();
				socket.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void sendMsg(String msg) {
		out.println(msg);
		out.flush();
	}

}
