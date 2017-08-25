package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
//import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * In login windows, Menu -> Signup
 * Sign up new user with this dialog box.
 * User need to input id, password, name and nickname
 * Duplication check needed for id and nickname
 */
public class JoinDialog extends JDialog implements ActionListener {
	private JTextField idField;
	private JPasswordField pwdField1;
	private JPasswordField pwdField2;
	private JTextField nameField;
	private JTextField nickField;
	
	private JButton checkIdBtn;
	private JLabel pwdCheckLabel1;
	private JLabel pwdCheckLabel2;
	private JButton okBtn;
	private JButton ccBtn;
	private JButton checkNickBtn;
	protected boolean idChecked;
	private boolean nickChecked;
	private ClientHandler ch;
	private int mode;

	public JoinDialog(JFrame frame, String title, boolean isModal, int mode) {
		super(frame, title, isModal);
		ch = ClientHandler.getInstance();
		this.mode = mode;
		init();
		start1();
		if (mode == Message.MODE_MODIFY)
			initModify();
		start2();

	}

	private void initModify() {
		idChecked = true;
		nickChecked = true;
		idField.setEnabled(false);
		checkIdBtn.setEnabled(false);
		checkNickBtn.setEnabled(false);

		Member m = ch.getMember();
		idField.setText(m.getId());
		pwdField1.setText(m.getPwd());
		pwdField2.setText(m.getPwd());
		nameField.setText(m.getName());
		nickField.setText(m.getNick());

	}

	private void init() {
		// Size of windows for registration
		setSize(400, 210);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension size = getSize();
		
		// Display window on the center of screen
		setLocation(screenSize.width / 2 - size.width / 2, screenSize.height / 2 - size.height / 2);
		getContentPane().setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		// Column 1
		JLabel idLabel = new JLabel("ID",SwingConstants.RIGHT);
		contentPanel.add(idLabel);
				
		JLabel pwdLabel = new JLabel("Password",SwingConstants.RIGHT);
		contentPanel.add(pwdLabel);
		
		JLabel label = new JLabel("Confirm Password",SwingConstants.RIGHT);
		contentPanel.add(label);
		
		JLabel nameLabel = new JLabel("Name",SwingConstants.RIGHT);
		contentPanel.add(nameLabel);
		
		JLabel nickLabel = new JLabel("Nickname",SwingConstants.RIGHT);
		contentPanel.add(nickLabel);

        // Column 2
		idField = new JTextField();
		contentPanel.add(idField);
		idField.setColumns(10);

		pwdField1 = new JPasswordField();
		pwdField1.setColumns(10);
		contentPanel.add(pwdField1);

		pwdField2 = new JPasswordField();
		pwdField2.setColumns(10);
		contentPanel.add(pwdField2);

		nameField = new JTextField();
		nameField.setColumns(10);
		contentPanel.add(nameField);

		nickField = new JTextField("");
		nickField.setColumns(10);
		contentPanel.add(nickField);
		
		// Column 3
		checkIdBtn = new JButton("Check");
		checkIdBtn.setMargin(new Insets(0, 0, 0, 0));
		contentPanel.add(checkIdBtn);

		checkNickBtn = new JButton("Check");
		checkNickBtn.setMargin(new Insets(0, 0, 0, 0));
		contentPanel.add(checkNickBtn);

		pwdCheckLabel1 = new JLabel(" 4 ~ 16 chars");
		pwdCheckLabel1.setForeground(Color.blue);
		contentPanel.add(pwdCheckLabel1);

		pwdCheckLabel2 = new JLabel();
		pwdCheckLabel2.setForeground(Color.red);
		contentPanel.add(pwdCheckLabel2);

		// Layout 400x210   X : 10, 125, 295, 
		//1st line - id, idtext, check id
		idLabel.setBounds(10, 10, 110, 20);
		idField.setBounds(125, 10, 160, 20);
		checkIdBtn.setBounds(295, 10, 80 , 20);
		
		//2nd line - pwd, pwdtext, pwd check label
		pwdLabel.setBounds(10, 35, 110, 20);
		pwdField1.setBounds(125, 35, 160, 20);
		pwdCheckLabel1.setBounds(295, 35, 80 , 20);
		
		//3rd line - pwd confirm, pwd confirm text, pwd confirm label
		label.setBounds(10, 60, 110, 20);
		pwdField2.setBounds(125, 60, 160, 20);
		pwdCheckLabel2.setBounds(295, 60, 80 , 20);

		//4th line - name, name text
		nameLabel.setBounds(10, 85, 110, 20);
		nameField.setBounds(125, 85, 160, 20);
		
		//5th line - nickname, nickname text, check nickname
		nickLabel.setBounds(10, 110, 110, 20);
		nickField.setBounds(125, 110, 160, 20);
		checkNickBtn.setBounds(295, 110, 80 , 20);      
		
		// Buttons - Register/Cancel
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		okBtn = new JButton("Register");
		buttonPane.add(okBtn);
		getRootPane().setDefaultButton(okBtn);
		ccBtn = new JButton("Cancel");
		buttonPane.add(ccBtn);
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
	}

	private void start1() {
		nameField.setDocument(new NameDocument());
	}

	private void start2() {
		okBtn.addActionListener(this);
		ccBtn.addActionListener(this);

		idField.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				idChecked = false;
			}
		});

		checkIdBtn.addActionListener(this);
		checkNickBtn.addActionListener(this);
		PwdListener pwdL = new PwdListener();
		pwdField1.addKeyListener(pwdL);
		pwdField2.addKeyListener(pwdL);

		final NameNickListener nnl = new NameNickListener();
		nameField.addKeyListener(nnl);
		nickField.addKeyListener(nnl);
		nameField.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
			}

			@Override
			public void focusLost(FocusEvent e) {
				nameField.removeKeyListener(nnl);
			}
		});

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		String pwd1 = String.valueOf(pwdField1.getPassword());
		String pwd2 = String.valueOf(pwdField2.getPassword());
		if (src == okBtn) {
			if (!idChecked) {
				JOptionPane.showMessageDialog(this, "Check ID duplication", "Warning", JOptionPane.WARNING_MESSAGE);
				idField.requestFocus();
			} else if (pwd1.length() < 4 || pwd1.length() > 16) {
				JOptionPane.showMessageDialog(this, "Password should be 4 to 16 characters", "Warning", JOptionPane.WARNING_MESSAGE);
				pwdField1.requestFocus();
			} else if (!pwd2.equals(pwd1)) {
				JOptionPane.showMessageDialog(this, "Password missmatched", "Warning", JOptionPane.WARNING_MESSAGE);
				pwdField2.requestFocus();
			} else if (nameField.getText().length() == 0) {
				JOptionPane.showMessageDialog(this, "Input name", "Warning", JOptionPane.WARNING_MESSAGE);
				nameField.requestFocus();
			} else if (!nickChecked) {
				JOptionPane.showMessageDialog(this, "Check nickname duplication", "Warning", JOptionPane.WARNING_MESSAGE);
				nickField.requestFocus();
				
			} else {
				String msg = idField.getText() + "," + String.valueOf(pwdField1.getPassword()) + "," + nameField.getText() + "," + nickField.getText();
				switch (mode) {
				case Message.MODE_JOIN:
					System.out.println(msg);// ex. id, pwd, name, nick = user4, user4, user4, user4nick
					ch.requestJoin(msg);
					break;
				case Message.MODE_MODIFY:
					ch.requestModify(msg);
					break;
				}

			}
		} else if (src == ccBtn) {
			dispose();
		} else if (src == checkIdBtn) {
			if (idField.getText().length() < 4) {
				JOptionPane.showMessageDialog(this, "ID should be more than 4 characters", "Warning", JOptionPane.WARNING_MESSAGE);
				idField.requestFocus();
			} else {
				ch.requestCheckId(idField.getText());
			}
		} else if (src == checkNickBtn) {
			if (nickField.getText().length() == 0) {
				JOptionPane.showMessageDialog(this, "Input nickname", "Warning", JOptionPane.WARNING_MESSAGE);
				nickField.requestFocus();
			} else {
				ch.requestCheckNick(nickField.getText(), Message.LOGIN_CHECK);
			}
		}
	}

	public void setIdChecked(boolean idChecked) {
		this.idChecked = idChecked;
	}

	public void setNickChecked(boolean nickChecked) {
		this.nickChecked = nickChecked;
	}

	class NameNickListener implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
			Object src = e.getSource();
			if (src == nameField && mode == Message.MODE_JOIN) {// sign up
				String name = nameField.getText();
				nickField.setText(name);
			} else if (src == nickField) {
				String nick = nickField.getText();
				if (nick.length() > 10) { // nickname max : 10 chars
					e.consume();
				} else {
					if (mode == Message.MODE_MODIFY) {// Modify	
						checkNickBtn.setEnabled(true);
					}
				}
			}
		}
	}
	
	public String getId() {
		return idField.getText();
	}

	class PwdListener implements KeyListener {
		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}

		@Override
		public void keyReleased(KeyEvent e) {
			String pwd1 = String.valueOf(pwdField1.getPassword());
			String pwd2 = String.valueOf(pwdField2.getPassword());
			if (!pwd1.equals(pwd2)) {
				/* Wrong password */
				pwdCheckLabel2.setText("Incorrect password");
			} else {
				pwdCheckLabel2.setText("");
			}
			if (e.getSource() == pwdField1) {
				if (pwd1.length() < 4 || pwd1.length() > 16) {
					/* Password : 4~ 16 chars */
					pwdCheckLabel1.setText("Password : 4~16chars");
				} else {
					pwdCheckLabel1.setText("");
				}
			}
		}
	}

	class NameDocument extends PlainDocument {
		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			if (getLength() + str.length() <= 10)	// nameField max : 10 chars
				super.insertString(offs, str, a);
		}
	}

	class IdDocument extends PlainDocument {
		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			char ch = str.charAt(0);
			if(Character.getType(ch)==5)		    
				return;
			if(getLength() + str.length() <= 16)	// idField : 16 chars
				super.insertString(offs, str, a);
		}
	}
}
