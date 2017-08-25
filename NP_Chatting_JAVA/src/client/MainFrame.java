package client;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

/**
 * Main frame for chatting room, waiting room and login/sign up windows
 * Switch GUI using setPanel() method across ChatRoom, WaitRoom and login window
 */
public class MainFrame extends JFrame implements ActionListener {
	private JMenuItem joinItem;
	private JMenuItem modItem;
	private JMenuItem logoutItem;
	private JMenuItem infoItem;
	private ClientHandler ch;

	public MainFrame() {
		JMenuBar menubar = new JMenuBar();
		JMenu menu1 = new JMenu("Menu");
		JMenu menu2 = new JMenu("Help");
		menubar.add(menu1);
		menubar.add(menu2);

		joinItem = new JMenuItem("Sign Up");
		modItem = new JMenuItem("Update");
		logoutItem = new JMenuItem("Logout");
		infoItem = new JMenuItem("Info");	
		
		menu1.add(joinItem);
		menu1.add(modItem);
		menu1.add(new JSeparator());
		menu1.add(logoutItem);
		menu2.add(infoItem);
		modItem.setEnabled(false);
		logoutItem.setEnabled(false);

		setJMenuBar(menubar);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setResizable(false);

		joinItem.addActionListener(this);
		modItem.addActionListener(this);
		logoutItem.addActionListener(this);
		infoItem.addActionListener(this);
	}

	public void setHandler(ClientHandler ch) {
		this.ch = ch;
	}

	/*
	 * Select one of panel among login, waitroom and Chatroom
	 */
	public void setPanel(JPanel panel) {

		if (panel instanceof LoginPanel) {
            // login window 
			setTitle("Login");
		    setSize(300, 160);

			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			Dimension size = getSize();
			setLocation(screenSize.width / 2 - size.width / 2, screenSize.height / 2 - size.height);
			
		} else if (panel instanceof WaitRoom) {
			/* Waiting Room */
			setSize(600, 480);
		} else if (panel instanceof ChatRoom) {
			/* Chatting Room*/
			setSize(650, 500);
		}
		setContentPane(panel);
		revalidate();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == joinItem) {
			ch.openJoin();
		} else if (src == infoItem) {
			JOptionPane.showMessageDialog(this, "Network Programming\nAssignment2\nJinTae Kim\nLecturer: Nilufar","Message",JOptionPane.INFORMATION_MESSAGE);
		} else if (src == logoutItem) {
			ch.logout();
		} else if (src == modItem) {
			ch.openPwdCheck();
		}
	}

	public void setMenuItemEnabled(boolean flag) {
		joinItem.setEnabled(!flag);
		modItem.setEnabled(flag);
		logoutItem.setEnabled(flag);
	}
}
