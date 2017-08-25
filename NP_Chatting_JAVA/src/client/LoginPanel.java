package client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * @author ebonny
 * Launched at first to login.
 */
public class LoginPanel extends JPanel implements ActionListener {
	private JTextField idField;
	private JPasswordField pwdField;
	private JButton btnLogin;
	private JButton btnClose;
	private ClientHandler ch;

	private void init() {
		setLayout(null);
   
		//setBounds(x, y, width, height);
		JLabel lblId = new JLabel("User ID",SwingConstants.CENTER);
		add(lblId);

		JLabel lblPwd = new JLabel("Password",SwingConstants.CENTER);
		add(lblPwd);

		idField = new JTextField();
		add(idField);
		idField.setColumns(10);

		pwdField = new JPasswordField();
		add(pwdField);
		pwdField.setColumns(10);

		
		btnLogin = new JButton("Login");
		add(btnLogin);

		btnClose = new JButton("Cancel");
		add(btnClose);

		/*
		 *  login layout
		 *  x, y, width, height
		 */
		lblId.setBounds(10, 10, 130, 20);
		lblPwd.setBounds(10, 40, 130, 20);
		
		idField.setBounds(155, 10, 130, 20);
		pwdField.setBounds(155, 40, 130, 20);
		
		btnLogin.setBounds(10, 70, 130, 30);
		btnClose.setBounds(155, 70, 130, 30);

	}

	private void start() {
		pwdField.addActionListener(this);
		btnLogin.addActionListener(this);
		btnClose.addActionListener(this);
	}

	public LoginPanel() {
		init();
		start();
		ch = ClientHandler.getInstance();
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src == pwdField || src == btnLogin || src == "Enter pressed") {
			if (idField.getText().length() == 0) {
				JOptionPane.showMessageDialog(ch.getFrame(), "Input ID");
			} else if (pwdField.getPassword().length == 0) {
				JOptionPane.showMessageDialog(ch.getFrame(), "Input password");
			} else {
				ch.requestLogin(idField.getText() + "," + String.valueOf(pwdField.getPassword()));
				System.out.println("__DEBUG__Login input : ID: "+idField.getText()+"   Password: "+String.valueOf(pwdField.getPassword()));

			}
		} else if (src == btnClose) {
			System.exit(0);
		}
	}


	public void setId(String id) {
		idField.setText(id);
	}

	public void focusIdField() {
		idField.requestFocus();
		idField.selectAll();
	}

	public void focusPwdField() {
		pwdField.requestFocus();
	}

	public void setPwdField(String string) {
		pwdField.setText(string);
	}
	
}
