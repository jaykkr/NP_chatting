package client;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * Display GUI for Chatting room
 */
public class ChatRoom extends JPanel implements ActionListener {
	private JList<String> userList;
	private JTextArea textArea;
	private JButton fileSendBtn;
	private JTextField textField;
	private JButton inviteBtn;
	private JButton exitBtn;
	private ClientHandler ch;
	private String userBorder = "User list : ";
	private JPopupMenu popup;
	private JMenuItem privateMenu;
	//=== private JMenuItem kickOffMenu;

	public ChatRoom() {
		ch = ClientHandler.getInstance();

		init();
		start();

	}

	private void init() {
		setLayout(null);
		
		// Display user nickname on the frame
		ch.getFrame().setTitle(ch.getMember().getNick());

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 10, 138, 380);
		add(scrollPane);

		userList = new JList<String>();
		scrollPane.setViewportView(userList);
		userList.setBorder(new TitledBorder(userBorder));
		
		/* Yellow - background color for selected user */
		userList.setSelectionBackground(Color.yellow);
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(162, 10, 469, 380);
		add(scrollPane_1);

		textArea = new JTextArea();
		scrollPane_1.setViewportView(textArea);
		textArea.setEditable(false);
        
		/* File button */
		fileSendBtn = new JButton("File");
		fileSendBtn.setBounds(12, 400, 60, 31);
		fileSendBtn.setMargin(new Insets(0, 0, 0, 0));
		add(fileSendBtn);

		textField = new JTextField();
		textField.setBounds(162, 400, 400, 31);
		add(textField);
		textField.setColumns(10);

		/* Invite button */
		inviteBtn = new JButton("Invite");
		inviteBtn.setMargin(new Insets(0, 0, 0, 0));
		inviteBtn.setBounds(90, 400, 60, 31);
		add(inviteBtn);

		/* Invite button */
		exitBtn = new JButton("Exit");
		exitBtn.setMargin(new Insets(0, 0, 0, 0));
		exitBtn.setBounds(570, 400, 60, 31);
		add(exitBtn);

		/* Popup menu for private message */
		popup = new JPopupMenu();
		privateMenu = new JMenuItem("Private Msg");
		popup.add(privateMenu);
		userList.add(popup);
	}

	private void start() {
		fileSendBtn.addActionListener(this);
		textField.addActionListener(this);
		inviteBtn.addActionListener(this);
		exitBtn.addActionListener(this);
		privateMenu.addActionListener(this);
		userList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// if Right button is clicked and destination user is selected
				if (e.getModifiers() == MouseEvent.BUTTON3_MASK && userList.getSelectedIndex() != -1) {
					System.out.println("Right button pressed.");
					popup.show(userList, e.getX(), e.getY());
				}
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		
		/* Handle Exit button */
		if (src == exitBtn) {
			ch.exitChatRoom();
		} 
		/* Input message - normal and private message */
		else if (src == textField) {
			String text = textField.getText();
			if (text.isEmpty())
				return;
			/* Private message */
			if (text.startsWith("/w ")) {
				text = text.substring(3);
				int index = text.indexOf(":");
				if (index != -1) {
					String nick = text.substring(0, index);
					String msg = text.substring(index + 1);
					if (msg.isEmpty())
						return;
					/*configure text area again for private message*/

					textField.setText("/w " + nick + ":");
					textField.setCaretPosition(textField.getText().length());
					
					
					/* send message to specified user(nick) */
					ch.sendPrivate(nick, ch.getMember().getNick()+ "\t▶"+ msg);
					//System.out.println(nick+":"+msg); 
				}
			}
			/* Normal message */
			else {
				// send input message to other user
				//System.out.println(ch.getMember().getNick() + "\t▷ " + text);
				ch.sendRoomChat(ch.getMember().getNick() + "\t▷ " + text);
				textField.setForeground(null);
				textField.setText("");
				

				
			}
		} 
		/* Invite button */
		else if (src == inviteBtn) {
			ch.openInvite();
		}
		/* File send button */
		else if (src == fileSendBtn) {
			/* Destination user for sending file */
			String nick = userList.getSelectedValue();
			
			if (nick == null) { // Destination user is NOT selected 
				JOptionPane.showMessageDialog(ch.getFrame(), "Select user","Message",JOptionPane.INFORMATION_MESSAGE);
			} else if (nick.equals(ch.getMember().getNick())) { // Unable to send file oneself
				JOptionPane.showMessageDialog(ch.getFrame(), "Select other user!","Error", JOptionPane.ERROR_MESSAGE);
			} else {
				/* Open file */
				FileDialog dlg = new FileDialog(ch.getFrame(), "Open file", FileDialog.LOAD);
				dlg.setDirectory("C:");
				dlg.setVisible(true);
				if (dlg.getDirectory() == null)
					return;
				/* Send file send command to destination user */
				ch.sendfile(nick);
				/* Be ready to send file */
				File file = new File(dlg.getDirectory() + dlg.getFile());
				System.out.println("File selected :"+dlg.getDirectory() + dlg.getFile());

				SendFile sf = new SendFile(ch.getFrame(), file);
				sf.setVisible(true);
			}
		} 
		/* Send private message */
		else if (src == privateMenu) {
			/* Get destination user for sending private message*/
			String nick = userList.getSelectedValue();
			if (nick.equals(ch.getMember().getNick())) {
				JOptionPane.showMessageDialog(ch.getFrame(), "Select other user!","Error", JOptionPane.ERROR_MESSAGE);
			} else {
				//configure text area /w + nickname
				textField.setForeground(Color.RED);
				//textField.setBackground(Color.yellow);

				textField.setText("/w " + userList.getSelectedValue() + ":");
				textField.setCaretPosition(textField.getText().length());
				textField.requestFocus();
			}
		} 
	} // end of actionPerformed()

	public void setUserList(String[] nicks) {
		/* Update user list of chat room*/
		userList.setListData(nicks);
		userList.setBorder(new TitledBorder(userBorder + nicks.length + " users"));
	}

	public void appendMsg(String msg) {
		/* Display message in text window */
		textArea.append(msg + "\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

	public void setChatBorder(String msg) {
		textArea.setBorder(new TitledBorder(msg));
	}

	public void focusText() {
		textField.requestFocus();
	}
}
