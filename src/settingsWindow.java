import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.io.FileWriter;

import javax.swing.*;

public class settingsWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private static String windowsPath;
	private static String linuxPath;

	public settingsWindow() {
		windowsPath = "C:\\Users\\" + mainWindow.username + "\\Pictures\\Screenshots\\";
		 linuxPath = "//home//" + mainWindow.username + "//Pictures//Screenshots//";
		try {
			mainWindow.loadInfo();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		final JPanel buttonPanel = new JPanel();
		buttonPanel.setBounds(0,0,110,360);
		buttonPanel.setLayout(null);
		
		final JPanel uploadPanel = new JPanel();
		uploadPanel.setBounds(5+110+5,0,530,360);
//		uploadPanel.setBackground(Color.BLUE);
		uploadPanel.setLayout(null);
		
		final JPanel localPanel = new JPanel();
		localPanel.setBounds(5+110+5,0,530,360);
//		localPanel.setBackground(Color.BLUE);
		localPanel.setLayout(null);
		localPanel.setVisible(false);
		
		JButton uploadSettings = new JButton("FTP Settings");
		uploadSettings.setBounds(5,5,110,25);
		uploadSettings.setFocusable(false);
		uploadSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				localPanel.setVisible(false);
				uploadPanel.setVisible(true);
			}
		});
		buttonPanel.add(uploadSettings);
		
		JButton localSettings = new JButton("Local Settings");
		localSettings.setBounds(5,35,110,25);
		localSettings.setFocusable(false);
		localSettings.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				uploadPanel.setVisible(false);
				localPanel.setVisible(true);
			}
		});
		buttonPanel.add(localSettings);
		
		JLabel uploadLbl = new JLabel("<html><u>FTP Settings</u></html>");
		uploadLbl.setBounds(5,0,110,25);
		uploadPanel.add(uploadLbl);
		
		JLabel localLbl = new JLabel("<html><u>Local Settings</u></html>");
		localLbl.setBounds(5,0,110,25);
		localPanel.add(localLbl);
		
		//-------------------------------Upload panel-------------------------------//
		
		JLabel hostLbl = new JLabel("Hostname: ");
		hostLbl.setBounds(5, 30, 275, 25);
		uploadPanel.add(hostLbl);

		JLabel userLbl = new JLabel("Username: ");
		userLbl.setBounds(5, 60, 275, 25);
		uploadPanel.add(userLbl);

		JLabel passLbl = new JLabel("Password: ");
		passLbl.setBounds(5, 90, 275, 25);
		uploadPanel.add(passLbl);

		JLabel uploadDirLbl = new JLabel("Upload dir: ");
		uploadDirLbl.setBounds(5, 120, 275, 25);
		uploadPanel.add(uploadDirLbl);

		final JTextField hostname = new JTextField(mainWindow.hostStr);
		hostname.setBounds(70, 30, 180, 25);
		uploadPanel.add(hostname);

		final JTextField user = new JTextField(mainWindow.userStr);
		user.setBounds(70, 60, 180, 25);
		uploadPanel.add(user);

		final JTextField pass = new JPasswordField(mainWindow.passStr);
		pass.setBounds(70, 90, 180, 25);
		uploadPanel.add(pass);

		final JTextField uploadDir = new JTextField(mainWindow.uploadDir);
		uploadDir.setBounds(70, 120, 180, 25);
		uploadPanel.add(uploadDir);

		JButton save = new JButton("Save");
		save.setBounds(110, 150, 70, 25);
		save.setFocusable(false);
		save.setFocusPainted(false);
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainWindow.hostStr = hostname.getText();
				mainWindow.userStr = user.getText();
				mainWindow.passStr = pass.getText();
				mainWindow.uploadDir = uploadDir.getText();
				try {
					saveInfo();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				dispose();
			}
		});
		uploadPanel.add(save);

		JButton cancel = new JButton("Cancel");
		cancel.setBounds(181, 150, 70, 25);
		cancel.setFocusable(false);
		cancel.setFocusPainted(false);
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		uploadPanel.add(cancel);
		
		//-------------------------------------------------------------------------//
		
		
		//-------------------------------Local panel-------------------------------//
		
		JLabel localPathLbl = new JLabel("Local path: ");
		localPathLbl.setBounds(5, 30, 275, 25);
		localPanel.add(localPathLbl);
		
		final JTextField localPath = new JTextField();
		localPath.setBounds(70, 30, 180, 25);
		localPanel.add(localPath);
		
		JButton saveLocal = new JButton("Save");
		saveLocal.setBounds(110, 60, 70, 25);
		saveLocal.setFocusable(false);
		saveLocal.setFocusPainted(false);
		saveLocal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainWindow.hostStr = hostname.getText();
				mainWindow.userStr = user.getText();
				mainWindow.passStr = pass.getText();
				mainWindow.uploadDir = uploadDir.getText();
				try {
					saveInfo();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				dispose();
			}
		});
		localPanel.add(saveLocal);

		JButton cancelLocal = new JButton("Cancel");
		cancelLocal.setBounds(181, 60, 70, 25);
		cancelLocal.setFocusable(false);
		cancelLocal.setFocusPainted(false);
		cancelLocal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		localPanel.add(cancelLocal);
		
		//-------------------------------------------------------------------------//
		
		setTitle("ScreenSlice - Settings");
		setSize(420-35,420/16*9-25);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconImage(new ImageIcon(getClass().getResource("screenslice.png")).getImage());
		setResizable(false);
		setVisible(true);
		add(uploadPanel);
		add(localPanel);
		add(buttonPanel);
	}
	
	public void saveInfo() throws Exception {
		String path = "";
		if (System.getProperty("os.name").contains("Windows")) {
			path = windowsPath;
		} else {
			path = linuxPath;
		}
		BufferedWriter hostFile = new BufferedWriter(new FileWriter(path + "//ScreenSlice Files//" + "host.txt"));
		BufferedWriter userFile = new BufferedWriter(new FileWriter(path + "//ScreenSlice Files//" + "user.txt"));
		BufferedWriter passFile = new BufferedWriter(new FileWriter(path + "//ScreenSlice Files//" + "pass.txt"));
		BufferedWriter uploadDirFile = new BufferedWriter(new FileWriter(path + "//ScreenSlice Files//" + "uploadDir.txt"));

		hostFile.write(mainWindow.hostStr);
		userFile.write(mainWindow.userStr);
		passFile.write(mainWindow.passStr);
		uploadDirFile.write(mainWindow.uploadDir);

		if (hostFile != null || userFile != null || passFile != null || uploadDirFile != null) {
			hostFile.close();
			userFile.close();
			passFile.close();
			uploadDirFile.close();
		}
	}
}
