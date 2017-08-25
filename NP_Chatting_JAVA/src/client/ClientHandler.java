package client;

import java.net.Socket;

import javax.swing.JOptionPane;

/**
 * Work between Gui and ServerMsgListener
 * 
 * Send message from Chatting room, waiting room, login and join to 
 * Server MsgListener. ServerMsgListener utilize getChatRoom() and 
 * getWaitRoom() method to get instance to update appropriate GUI.
 * 
 */
public class ClientHandler {
	private static final ClientHandler instance = new ClientHandler();
	private ServerMsgListener sml;
	private JoinDialog joinDialog;
	private MainFrame frame;
	private LoginPanel loginPanel;
	private WaitRoom waitRoom;
	private Member member;
	private ChatRoom chatRoom;
	private InviteDialog inviteDialog;
	private PwdDialog pwdDialog;

	public static void main(String[] args) {
		System.out.println("Client Starting...");

		ClientHandler.getInstance().startClient();
	}

	private ClientHandler() {
		frame = new MainFrame();
		frame.setHandler(this);
	}

	/* Return instance of ClientHandler */
	public static ClientHandler getInstance() {
		return instance;
	}

	/* Start client with login */
	public void startClient() {
		openLogin();
	}

	private void openLogin() {
		Socket socket = null;
		try {
			/*Server url : localhost,  Chat port : 2002 */
			socket = new Socket(Message.SERVER_URL, Message.CHAT_PORT);
			sml = new ServerMsgListener(this, socket);
			sml.start();
			loginPanel = new LoginPanel();
			frame.setPanel(loginPanel);
			frame.setVisible(true);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Unable to connect server");
		}
	}

	public void openJoin() {
		joinDialog = new JoinDialog(frame, "Sign Up", true, Message.MODE_JOIN);
		joinDialog.setVisible(true);
	}

	public void requestJoin(String msg) {
		System.out.println(Message.LOGIN_REQUEST_JOIN + msg);
		sml.sendMsg(Message.LOGIN_REQUEST_JOIN + msg);
	}

	public MainFrame getFrame() {
		return frame;
	}

	public LoginPanel getLoginPanel() {
		return loginPanel;
	}

	public void requestCheckId(String id) {
		System.out.println(this.hashCode());
		sml.sendMsg(Message.LOGIN_REQUEST_CHECKID + id);
	}

	public void requestCheckNick(String nick, int checkMode) {
		switch (checkMode) {
		case Message.LOGIN_CHECK:
			sml.sendMsg(Message.LOGIN_REQUEST_CHECKNICK + nick);
			break;
		case Message.MOD_CHECK:
			sml.sendMsg(Message.MOD_REQUEST_CHECKNICK + nick);
			break;
		}
	}

	public JoinDialog getJoinDialog() {
		return joinDialog;
	}

	public void requestLogin(String msg) {
		sml.sendMsg(Message.LOGIN_REQUEST + msg);
	}

	public void openWaitRoom() {
		System.out.println("__DEBUG__ClientHandler : openWaitRoom() ");

		waitRoom = new WaitRoom();
		frame.setPanel(waitRoom);
		frame.setMenuItemEnabled(true);
		if (member != null) {
			waitRoom.setChatBorder("Chatting(" + member.getNick() + ")");
		}
		waitRoom.focusText();
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public WaitRoom getWaitRoom() {
		return waitRoom;
	}

	public void logout() {
		sml.sendMsg(Message.LOGOUT);
		loginPanel.setPwdField("");
		frame.setPanel(loginPanel);
		frame.setMenuItemEnabled(false);
	}

	public void sendWaitChat(String txt) {
		sml.sendMsg(Message.WAIT_CHAT_MSG + txt);
	}

	public Member getMember() {
		return member;
	}

	public void makeRoom(String roomName) {
		sml.sendMsg(Message.ROOM_REQUEST_MAKE + roomName);
	}

	public void openChatRoom(String msg) {
		System.out.println("__DEBUG__ClientHandler : openChatRoom() ");
		
		chatRoom = new ChatRoom();
		frame.setPanel(chatRoom);// control moves to chatroom from waitroom
		chatRoom.focusText();
	}

	public void exitChatRoom() {
		System.out.println("__DEBUG__ClientHandler : exitChatRoom() and call openWaitRoom()");
		openWaitRoom();
		sml.sendMsg(Message.ROOM_USER_OUT);
	}

	public void enterRoom(String roomName) {
		openChatRoom(roomName);
		sml.sendMsg(Message.ROOM_USER_IN + roomName);
	}

	public ChatRoom getChatRoom() {
		return chatRoom;
	}

	public void sendRoomChat(String text) {
		sml.sendMsg(Message.ROOM_CHAT_MSG + text);
	}

	public void invite(String nick) {
		sml.sendMsg(Message.ROOM_INVITE_USER + nick);
	}

	public void inviteDeny(String msg) {
		sml.sendMsg(Message.ROOM_INVITE_DENY + msg);
	}

	public void openInvite() {
		inviteDialog = new InviteDialog(frame, "Invitation", true);
		sml.sendMsg(Message.ROOM_REQUEST_WAITUSER); 
		inviteDialog.setVisible(true);
	}

	public InviteDialog getInviteDialog() {
		return inviteDialog;
	}

	public void exitWaitRoom() {
		System.out.println("__DEBUG__ClientHandler : ExitWaitRoom()");

		sml.sendMsg(Message.WAIT_USER_OUT);
		frame.dispose();
		System.exit(0);
	}

	public void sendfile(String nick) {
		sml.sendMsg(Message.GET_ADDR + nick);
	}

	public void sendPrivate(String nick, String msg) {
		/* need destination user - nick and add indicator "," to extract message only
		 * msg = ch.getMember().getNick() + "\t▷ " + msg 
		 */
		sml.sendMsg(Message.ROOM_CHAT_PRIVATE + nick + "," + msg);
	}

	public void requestModify(String msg) {
		sml.sendMsg(Message.MOD_UPDATE_USERINFO + msg);
	}

	public void openModify() {
		joinDialog = new JoinDialog(frame, "Update Information", true, Message.MODE_MODIFY);
		joinDialog.setVisible(true);
	}

	public void openPwdCheck() {
		pwdDialog = new PwdDialog(frame);
		pwdDialog.setVisible(true);
	}

}
