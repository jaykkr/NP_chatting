package client;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.Inet4Address;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.TitledBorder;

/**
 * Waiting room. Users wait for chatting in this room after log in.
 */
public class WaitRoom extends JPanel implements ActionListener {

	private JTextField textField;
	private JList<String> roomList;
	private JList<String> userList;
	private JButton makeBtn;
	private JButton enterBtn;
	private JTextArea textArea;
	private ClientHandler ch;
	private JButton exitBtn;

	public WaitRoom() {
		ch = ClientHandler.getInstance();

		init();
		start();
	}

	private void init() {
		setLayout(null);
		// Display user nickname on the frame
		ch.getFrame().setTitle(ch.getMember().getNick());
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 570, 148);
		add(scrollPane);

		roomList = new JList<String>();
		scrollPane.setViewportView(roomList);
		roomList.setBorder(new TitledBorder("Chatting Room List : "));
		roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(12, 168, 162, 210);
		add(scrollPane_1);

		userList = new JList<String>();
		scrollPane_1.setViewportView(userList);
		userList.setBorder(new TitledBorder("Connected users: "));
		userList.setEnabled(false);

		/* Make room button */
		makeBtn = new JButton("Room");
		makeBtn.setBounds(12, 388, 75, 23);
		makeBtn.setMargin(new Insets(0, 0, 0, 0));
		add(makeBtn);

		/* join button */
		enterBtn = new JButton("Join");
		enterBtn.setMargin(new Insets(0, 0, 0, 0));
		enterBtn.setBounds(99, 388, 75, 23);
		add(enterBtn);

		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(186, 168, 396, 210);
		add(scrollPane_2);

		textArea = new JTextArea();
		scrollPane_2.setViewportView(textArea);
		textArea.setBorder(new TitledBorder("Chatting"));
		textArea.setEditable(false);

		/* Text display area */
		textField = new JTextField();
		textField.setBounds(186, 389, 317, 21);
		add(textField);
		textField.setColumns(10);

		/* exit button */
		exitBtn = new JButton("Exit");
		exitBtn.setMargin(new Insets(0, 0, 0, 0));
		exitBtn.setBounds(515, 388, 64, 23);
		add(exitBtn);
	}

	private void start() {
		makeBtn.addActionListener(this);
		enterBtn.addActionListener(this);
		textField.addActionListener(this);
		roomList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					String roomName = roomList.getSelectedValue();
					if (roomName == null || roomName.isEmpty())
						return;
					roomName = roomName.substring(0, roomName.indexOf(":") - 1);
					ch.enterRoom(roomName);
				}
			}
		});
		exitBtn.addActionListener(this);
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		
		if (src == textField) {
			String txt = textField.getText();
			if (txt.length() == 0)
				return;
			// Broadcast message to all other users in waiting room
			ch.sendWaitChat(ch.getMember().getNick() + "\t▷ " + txt);
			textField.setText("");
		} 
		/* Make room button */
		else if (src == makeBtn) {
			String roomName = JOptionPane.showInputDialog("Input Room name");
			
			if (roomName != null){ // roomname will be null if user select cancel button
				ch.makeRoom(roomName);
				System.out.println("__DEBUG__waitingroom : New Room Created : "+roomName);
			}
			else{
				System.out.println("__DEBUG__waitingroom : Canceled !! "+roomName);
			}
			
		} 
		/* Join room button. Need to select room first */
		else if (src == enterBtn) {
			String roomName = roomList.getSelectedValue();
			if (roomName == null) {
				JOptionPane.showMessageDialog(ch.getFrame(), "Select Room name");
			} else {
				roomName = roomName.substring(0, roomName.indexOf(":") - 1);
				ch.enterRoom(roomName);
			}
		} else if (src == exitBtn) {
			ch.exitWaitRoom();
		}
	}

	/* User list in waiting room */
	public void setUserList(String msg) {
		String users[] = msg.split(",");
		userList.setListData(users);
		userList.setBorder(new TitledBorder("Userlist: " + users.length + " users"));
	}

	/* Chatting Room list */
	public void setRoomList(String msg) {
		String rooms[] = msg.split(",");
		roomList.setListData(rooms);
		roomList.setBorder(new TitledBorder("RoomList : " + (rooms[0].equals("") ? 0 : rooms.length) + " rooms"));
	}

	public void focusText() {
		textField.requestFocus();
	}

	/* Display chatting message  */
	public void appendMsg(String msg) {
		textArea.append(msg + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void setChatBorder(String msg) {
		textArea.setBorder(new TitledBorder(msg));
	}
}
