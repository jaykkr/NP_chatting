package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import client.Member;
import server.database.DBManager;
import server.database.OracleManager;
import client.Message;

/**
 * Main class of Server
 * Create Guest thread upon request from client.
 * Handle message from Guest class based on prefix.
 * 
 * guest : user list of waiting room
 * roomMap : user list of chatting room
 * idMap : login user list( id and ip ) - IP is used for file transfer

 * HashMap usage 
 * http://forum.falinux.com/zbxe/?mid=lecture_tip&page=1&document_srl=570168
 */
public class ServerHandler extends Thread {
	List<Guest> guests;
	private HashMap<String, String> idMap;
	private HashMap<String, ArrayList<Guest>> roomMap;
	private MemberDAO dao;

	public static void main(String[] args) {
		try {
			new ServerHandler(new OracleManager()).start();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	public ServerHandler(DBManager dbmgr) {
		guests = new ArrayList<Guest>();// user list in waiting room. exclude users in chatting room
		idMap = new HashMap<String, String>();// login user list
		roomMap = new HashMap<String, ArrayList<Guest>>();// user list in chatting room
		try {
			dao = MemberDAO.getInstance(dbmgr);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		ServerSocket ss = null;
		Socket socket = null;
		try {
			ss = new ServerSocket(Message.CHAT_PORT);
			System.out.println("Server starting ....");
			while (true) {
				/*
				 * Arrive new request from client--> create thread to serve new client 
				 */
				socket = ss.accept();
				Guest guest = new Guest(this, socket);
				guest.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public boolean checkId(String id) throws SQLException {
		return dao.checkId(id);
	}

	public boolean checkNick(String nick) throws SQLException {
		return dao.checkNick(nick);
	}

	public boolean requestJoin(String msg) throws SQLException {
		return dao.requestJoin(msg.split(","));
	}

	public Member getMember(String id) throws SQLException {
		Member member = dao.getMember(id);
		return member;
	}

	public void addUserToWait(Guest guest) {
		guests.add(guest);
	}

	public void userLogin(Guest guest) {
		// add to waitroom user list - exclude users in chatting room
		guests.add(guest);
		// add to login list - all of user list in server 
		idMap.put(guest.getMember().getId(), guest.getSocket().getInetAddress().getHostAddress());
		System.out.println("__DEBUG__Login :"+ guest.getMember().getId()+" "+guest.getSocket().getInetAddress().getHostAddress());

	}

	public void broadcastWaitRoomUpdate() {
		broadcastWait_userUpdate();
		broadcastWait_roomUpdate();
	}

	public void broadcastWait_userUpdate() {
		String msg = "";
		Iterator<Guest> it = guests.iterator();
		while (it.hasNext()) {
			Guest g = it.next();
			msg += g.getMember().getNick() + ",";
		}
		broadcast_waitRoom(Message.WAIT_USER_UPDATE + msg);
	}

	public void broadcastWait_roomUpdate() {
		String msg2 = "";
		Set<String> set = roomMap.keySet();
		Iterator<String> it2 = set.iterator();
		while (it2.hasNext()) {
			String roomName = it2.next();
			ArrayList<Guest> list = roomMap.get(roomName);
			msg2 += roomName + " : " + list.size() + "users,";
		}
		System.out.println("__DEBUG__"+ msg2);
		broadcast_waitRoom(Message.WAIT_ROOM_UPDATE + msg2);
	}

	public void broadcast_waitRoom(String msg) {
		Iterator<Guest> it = guests.iterator();

		while (it.hasNext()) {
			Guest guest = it.next();
			guest.sendMsg(msg);
		}
	}

	public void broadcast_chatRoom( String msg, String roomName) {
		ArrayList<Guest> list = roomMap.get(roomName);
		if (list == null)
			return;
		Iterator<Guest> it = list.iterator();
	
		while (it.hasNext()) {
			Guest g = it.next();
			g.sendMsg(msg);
		}
	}

	public void removeUserFromWait(Guest guest) {
		guests.remove(guest);
	}

	public void removeUserFromIdMap(String id) {
		idMap.remove(id);
	}

	public void removeUserFromRoom(Guest guest, String roomName) {
		ArrayList<Guest> list = roomMap.get(roomName);
		if (list == null)
			return;
		list.remove(guest);
		if (list.size() == 0)
			roomMap.remove(roomName);
	}

	public void broadcastChatRoomUpdate(String roomName) {
		String msg = "";
		ArrayList<Guest> list = roomMap.get(roomName);
		if (list == null)
			return;
		Iterator<Guest> it = list.iterator();
		while (it.hasNext()) {
			Guest g = it.next();
			msg += g.getMember().getNick() + ",";
		}
		broadcast_chatRoom(Message.CHAT_USER_UPDATE + msg, roomName);
	}

	public boolean checkRoomName(String roomName) {
		return !roomMap.containsKey(roomName);
	}

	public boolean addRoom(String roomName) {
		if (roomMap.containsKey(roomName)) {
			return false;
		} else {
			roomMap.put(roomName, new ArrayList<Guest>());
			return true;
		}
	}

	public void addUserToRoom(Guest g, String roomName) {
		ArrayList<Guest> list = roomMap.get(roomName);
		list.add(g);
	}

	public boolean checkIdMap(String id) {
		return idMap.containsKey(id);
	}

	public void inviteUser(String inviterId, String receiverNick, String roomName) {
		for (Guest g : guests) {
			if (g.getMember().getNick().equals(receiverNick)) {// find a guest whose nickname is receiverNick
				g.sendMsg(Message.ROOM_INVITE_REQUEST + inviterId + "," + roomName);
				break;
			}
		}
	}

	public void inviteDeny(String inviterId, String roomName, String denierNick) {
		ArrayList<Guest> list = roomMap.get(roomName);
		for (Guest g : list) {
			if (g.getMember().getId().equals(inviterId)) {
				g.sendMsg(Message.ROOM_INVITE_DENIED + denierNick);
				break;
			}
		}
	}

	public void returnWaitUsers(Guest guest) {
		String msg = "";
		for (Guest g : guests) {
			msg += g.getMember().getNick() + ",";
		}
		guest.sendMsg(Message.ROOM_RETURN_WAITUSER + msg);
	}

	public void getAddr(Guest guest, String nick, String roomName) {
		ArrayList<Guest> list = roomMap.get(roomName);
		for (Guest g : list) {
			if (g.getMember().getNick().equals(nick)) {
				g.sendMsg(Message.GET_ADDR + idMap.get(guest.getMember().getId()));
				break;
			}
		}
	}

	// Private message
	public void sendPrivate(Guest sender, String receiverNick, String msg, String roomName) {
		String senderNick = sender.getMember().getNick();
		ArrayList<Guest> users = roomMap.get(roomName);
		Guest receiver = null;

		System.out.println("__DEBUG__:"+sender+";"+receiverNick+";"+msg+";"+roomName);
		
		Iterator<Guest> it = users.iterator();
		while (it.hasNext()) {
			Guest g = it.next();
			// Find specified user(receiver) - Private message
			if (g.getMember().getNick().equals(receiverNick)) {
				receiver = g;
				break;
			}
		}
		if (receiver == null) {
			sender.sendMsg(Message.ROOM_PRIVATE_FAIL + receiverNick);
		} else {
			/* forward message to both sender and receiver
			 * Send message as normal ROOM Chat message 
			 */
			sender.sendMsg(Message.ROOM_CHAT_MSG + msg);
			receiver.sendMsg(Message.ROOM_CHAT_MSG + msg);

		}
	}

	public boolean modUserInfo(String[] tmp) throws SQLException {
		return dao.updateMember(tmp);
	}

}
