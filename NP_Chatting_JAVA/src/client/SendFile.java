package client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

/**
 * Dialog window for sending file
 * Port : 2011 . use separate port for file transfer
 * After open socket for file transfer, waiting for accepting by receiver
 * in accept() method.
 */
public class SendFile extends JDialog implements ActionListener {

	private JProgressBar pBar;
	private JButton ccBtn;
	private File file;
	private ServerSocket ss;
	private boolean canceled;
	protected Socket socket;
	private JFrame frame;

	public SendFile(JFrame frame, File file) {
		super(frame, "Sending file", false);
		this.file = file;
		this.frame = frame;
		init();
		start();
	}

	private void init() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		getContentPane().setLayout(new BorderLayout());
		JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		pBar = new JProgressBar(1, 100);
		pBar.setBounds(12, 10, 260, 30);
		contentPanel.add(pBar);
		pBar.setStringPainted(true);
		pBar.setString("Waiting...0%");

		ccBtn = new JButton("Cancel");
		ccBtn.setBounds(98, 50, 80, 30);
		contentPanel.add(ccBtn);
		ccBtn.addActionListener(this);
		setSize(300, 130);
		setLocation(frame.getLocation().x + 10, frame.getLocation().y + 10);
	}

	private void start() {
		try {
			ss = new ServerSocket(Message.FILE_PORT);

		} catch (IOException e) {
			e.printStackTrace();
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				BufferedReader in = null;
				PrintWriter out = null;
				try {
					socket = ss.accept(); // waiting for accept signal from destination user.
					in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
					out.println(file.getName() + "," + file.length() / 100);
					out.flush();
					String msg = in.readLine();
					if (msg.equals("Accept")) {
						sendFile();
					} else {
						canceled();
					}
				} catch (IOException e) {
					canceled();
				} finally {
					try {
						if (socket != null)
							socket.close();
						ss.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void canceled() {
		pBar.setString("File transfer canceled");
		ccBtn.setText("Close");
		canceled = true;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			if (socket != null)
				socket.close();
			ss.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			dispose();
		}
	}

	private void sendFile() throws IOException {
		int b = 0;
		int cnt = 0;
		int send = 0;
		DataInputStream inData = new DataInputStream(new FileInputStream(file));
		DataOutputStream outData = new DataOutputStream(socket.getOutputStream());
		while ((b = inData.read()) != -1) { // until end of file
			outData.writeByte(b);
			outData.flush();
			cnt++;
			send = (int) (cnt / (file.length() / 100));
			if (canceled) {
				throw new IOException();
			}
			if (send > pBar.getValue()) {
				pBar.setValue(send);
				pBar.setString("Sending\t" + send + "%");
			}
		}
		System.out.println("__DEBUG__Sendfile : Count :"+cnt+"bytes");

		pBar.setValue(100);
		pBar.setString("File Transfer Completed");
		ccBtn.setText("Close");
		inData.close();
		outData.close();
	}
}
