package GUI;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

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
		buttonPanel.requestFocus();
		buttonPanel.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent arg0) {
				
			}
			public void keyReleased(KeyEvent arg0) {
				
			}
			public void keyTyped(KeyEvent arg0) {
			}});
		
		final JPanel uploadPanel = new JPanel();
		uploadPanel.setBounds(5+110+5,0,530,360);
//		uploadPanel.setBackground(Color.BLUE);
		uploadPanel.setLayout(null);
		
		final JPanel imgurPanel = new JPanel();
		imgurPanel.setBounds(5+110+5,0,530,360);
//		imgurPanel.setBackground(Color.BLUE);
		imgurPanel.setLayout(null);
		
		final JPanel localPanel = new JPanel();
		localPanel.setBounds(5+110+5,0,530,360);
//		localPanel.setBackground(Color.BLUE);
		localPanel.setLayout(null);
		localPanel.setVisible(false);
		
		customButton uploadSettings = new customButton(" FTP Settings", "../ftp.png");
		uploadSettings.setBounds(5, 5,100, 20);
		uploadSettings.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				localPanel.setVisible(false);
				uploadPanel.setVisible(true);
			}
			public void mouseEntered(MouseEvent arg0) {
			}
			public void mouseExited(MouseEvent arg0) {
			}
			public void mousePressed(MouseEvent arg0) {
			}
			public void mouseReleased(MouseEvent arg0) {
			}
			
		});
		buttonPanel.add(uploadSettings);
		
		final customButton imgurSettings = new customButton(" Imgur Settings", "../imgur.png");
		imgurSettings.setBounds(5, 27, 100, 20);
		imgurSettings.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				uploadPanel.setVisible(false);
				localPanel.setVisible(true);
			}
			public void mouseEntered(MouseEvent arg0) {
			}
			public void mouseExited(MouseEvent arg0) {
			}
			public void mousePressed(MouseEvent arg0) {
				imgurSettings.setBackground(new Color(153, 204, 255));
			}
			public void mouseReleased(MouseEvent arg0) {
				imgurSettings.setBackground(null);
			}
			
		});
		buttonPanel.add(imgurSettings);
		
		final JRadioButton imgurBtn = new JRadioButton("Use Imgur");
		imgurBtn.setActionCommand("");
		try {imgurBtn.setFont(new customButton("","").defaultFont());} catch (Exception e2) {}
		imgurBtn.setBounds(10,65,(int)imgurBtn.getPreferredSize().getWidth(),(int)imgurBtn.getPreferredSize().getHeight());
		imgurBtn.setFocusPainted(false);
		if(mainWindow.isImgur) {
			imgurBtn.setSelected(true);
		} else {
			imgurBtn.setSelected(false);
		}
		
		JRadioButton ftpBtn = new JRadioButton("Use FTP");
		ftpBtn.setActionCommand("");
		try {ftpBtn.setFont(new customButton("","").defaultFont());} catch (Exception e2) {}
		ftpBtn.setBounds(10,65+(int)imgurBtn.getPreferredSize().getHeight(),(int)ftpBtn.getPreferredSize().getWidth(),(int)ftpBtn.getPreferredSize().getHeight());
		ftpBtn.setFocusPainted(false);
		if(mainWindow.isImgur) {
			ftpBtn.setSelected(false);
		} else {
			ftpBtn.setSelected(true);
		}
		
		ButtonGroup group = new ButtonGroup();
		group.add(ftpBtn);
		group.add(imgurBtn);
		buttonPanel.add(ftpBtn);
		buttonPanel.add(imgurBtn);
		
		//-------------------------------Upload panel-------------------------------//
		
		
		JLabel imgurLbl = new JLabel("<html><u>Imgur Settings</u></html>");
		imgurLbl.setBounds(5,0,110,25);
		try {imgurLbl.setFont(new customButton("","").defaultFont());} catch (Exception e2) {}
		imgurPanel.add(imgurLbl);
		
		JLabel uploadLbl = new JLabel("<html><u>FTP Settings</u></html>");
		uploadLbl.setBounds(5,0,110,25);
		try {uploadLbl.setFont(new customButton("","").defaultFont());} catch (Exception e2) {}
		uploadPanel.add(uploadLbl);
		
		JLabel hostLbl = new JLabel("Hostname: ");
		hostLbl.setBounds(5, 30, 275, 25);
		try {hostLbl.setFont(new customButton("","").defaultFont());} catch (Exception e2) {}
		uploadPanel.add(hostLbl);

		JLabel userLbl = new JLabel("Username: ");
		userLbl.setBounds(5, 60, 275, 25);
		try {userLbl.setFont(new customButton("","").defaultFont());} catch (Exception e2) {}
		uploadPanel.add(userLbl);

		JLabel passLbl = new JLabel("Password: ");
		passLbl.setBounds(5, 90, 275, 25);
		try {passLbl.setFont(new customButton("","").defaultFont());} catch (Exception e2) {}
		uploadPanel.add(passLbl);

		JLabel uploadDirLbl = new JLabel("Upload dir: ");
		uploadDirLbl.setBounds(5, 120, 275, 25);
		try {uploadDirLbl.setFont(new customButton("","").defaultFont());} catch (Exception e2) {}
		uploadPanel.add(uploadDirLbl);

		final JTextField hostname = new JTextField(mainWindow.hostStr);
		hostname.setBounds(70, 30, 180, 25);
		try {hostname.setFont(new customButton("","").defaultFont());} catch (Exception e2) {}
		uploadPanel.add(hostname);

		final JTextField user = new JTextField(mainWindow.userStr);
		user.setBounds(70, 60, 180, 25);
		try {user.setFont(new customButton("","").defaultFont());} catch (Exception e2) {}
		uploadPanel.add(user);

		final JTextField pass = new JPasswordField(mainWindow.passStr);
		pass.setBounds(70, 90, 180, 25);
		try {pass.setFont(new customButton("","").defaultFont());} catch (Exception e2) {}
		uploadPanel.add(pass);

		final JTextField uploadDir = new JTextField(mainWindow.uploadDir);
		uploadDir.setBounds(70, 120, 180, 25);
		try {uploadDir.setFont(new customButton("","").defaultFont());} catch (Exception e2) {}
		uploadPanel.add(uploadDir);

		JButton save = new JButton("Save");
		save.setBounds(110, 150, 70, 25);
		save.setFocusable(false);
		try {save.setFont(new customButton("","").defaultFont());} catch (Exception e2) {}
		save.setFocusPainted(false);
		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(imgurBtn.isSelected()) {
					mainWindow.isImgur = true;
				} else {
					mainWindow.isImgur = false;
				}
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
		try {cancel.setFont(new customButton("","").defaultFont());} catch (Exception e2) {}
		cancel.setFocusPainted(false);
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		uploadPanel.add(cancel);
				
		//-------------------------------Imgur panel-------------------------------//
		
		JLabel imgurLoginLbl = new JLabel("<html><u>Imgur Settings</u></html>");
		imgurLoginLbl.setBounds(5,0,110,25);
		try{imgurLoginLbl.setFont(new customButton("","").defaultFont());}catch(Exception e2){}
		localPanel.add(imgurLoginLbl);
		
		JLabel imgurUsrLbl = new JLabel("Username: ");
		imgurUsrLbl.setBounds(5, 30, 275, 25);
		try {imgurUsrLbl.setFont(new customButton("","").defaultFont());} catch (Exception e2) {}
		localPanel.add(imgurUsrLbl);
		
		
		final JTextField imgurUsr = new JTextField();
		imgurUsr.setBounds(70, 30, 180, 25);
		localPanel.add(imgurUsr);
		
		JLabel imgurPassLbl = new JLabel("Password: ");
		imgurPassLbl.setBounds(5, 60, 275, 25);
		try {imgurPassLbl.setFont(new customButton("","").defaultFont());} catch (Exception e2) {}
		localPanel.add(imgurPassLbl);
		
		
		final JPasswordField imgurPass = new JPasswordField();
		imgurPass.setBounds(70, 60, 180, 25);
		localPanel.add(imgurPass);
		
		JButton saveLocal = new JButton("Save");
		saveLocal.setBounds(110, 90, 70, 25);
		saveLocal.setFocusable(false);
		try {saveLocal.setFont(new customButton("","").defaultFont());} catch (Exception e2) {}
		saveLocal.setFocusPainted(false);
		saveLocal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		localPanel.add(saveLocal);

		JButton cancelLocal = new JButton("Cancel");
		cancelLocal.setBounds(181, 90, 70, 25);
		cancelLocal.setFocusable(false);
		try {cancelLocal.setFont(new customButton("","").defaultFont());} catch (Exception e2) {}
		cancelLocal.setFocusPainted(false);
		cancelLocal.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		localPanel.add(cancelLocal);
		
		//-------------------------------------------------------------------------////
		
		setTitle("ScreenSlice - Settings");
		setSize(420-35,420/16*9-25);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setIconImage(new ImageIcon(getClass().getResource("../icon.png")).getImage());
		setResizable(false);
		setVisible(true);
		add(uploadPanel);
		add(localPanel);
		add(buttonPanel);
	}
	
	public void saveInfo() throws Exception {
		File createDirectories = new File(mainWindow.checkOSName() + "//ScreenSlice Files//");
		createDirectories.mkdirs();
		if(mainWindow.isImgur) {
			
		}
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
		BufferedWriter imgurorftpFile = new BufferedWriter(new FileWriter(path + "//ScreenSlice Files//" + "imgurOrFTP.txt"));

		hostFile.write(mainWindow.hostStr);
		userFile.write(mainWindow.userStr);
		passFile.write(mainWindow.passStr);
		uploadDirFile.write(mainWindow.uploadDir);
		
		if(mainWindow.isImgur) {
			imgurorftpFile.write("imgur");
		} else {
			imgurorftpFile.write("ftp");
		}

		if (hostFile != null || userFile != null || passFile != null || uploadDirFile != null || imgurorftpFile != null) {
			hostFile.close();
			userFile.close();
			passFile.close();
			uploadDirFile.close();
			imgurorftpFile.close();
		}
	}
}
