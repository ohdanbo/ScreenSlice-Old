import java.awt.*;
import java.awt.image.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.apache.commons.net.ftp.*;
import org.jnativehook.GlobalScreen;

public class mainWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	public static String hostStr = null;
	public static String userStr = null;
	public static String passStr = null;
	public static String uploadDir = null;
	public static String versionStr = null;
	public static String versionID = "1.1";
	public static String username = System.getProperty("user.name");
	public static String windowsPath;
	public static String linuxPath;
	static DefaultTableModel model = new DefaultTableModel();
	static String randomFileName = "";
	static String randomTxtName = "";
	public ImageIcon icon = new ImageIcon(getClass().getResource("screenslice.png"));
	static boolean uploaded = false;
	public static TrayIcon tray;
	static String link;

	public mainWindow() {
		 windowsPath = "C:\\Users\\" + username + "\\Pictures\\Screenshots\\";
		 linuxPath = "//home//" + System.getProperty("user.name") + "//Pictures//Screenshots//";
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		trayIcon();
		try {
			loadInfo();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		JPanel panel = new JPanel();
		panel.setLayout(null);
		panel.setSize(640, 360);
		panel.setFocusable(true);
		panel.requestFocus();
		panel.addKeyListener(new KeyListener() {
			boolean ctrl_bool = false;
			boolean v_bool = false;

			public void checkKeys() {
				if (ctrlv()) {
					try {
						String path = "";
						if (System.getProperty("os.name").contains("Windows")) {
							path = windowsPath;
						} else {
							path = linuxPath;
						}
						randomNameGenerator();
						String randomName = randomTxtName;
						String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
						BufferedWriter txt = new BufferedWriter(new FileWriter(path + randomName + ".txt"));
						txt.write(data);
						if (txt != null)
							txt.close();
						uploadFile(randomName + ".txt");
						File f = new File(path + randomName + ".txt");
						int fileSize = (int) f.length();
						link = "http://" + hostStr + uploadDir + randomName + ".txt";
						model.insertRow(0, new Object[] { randomName + ".txt", fileSize / 1024 + "KB", "<html><a href='" + link + "'>" + link + "</a></html>" });
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			public boolean ctrlv() {
				if (ctrl_bool && v_bool) {
					return true;
				} else {
					return false;
				}
			}

			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
					ctrl_bool = true;
					checkKeys();
				}
				if (e.getKeyCode() == KeyEvent.VK_V) {
					v_bool = true;
					checkKeys();
				}
			}

			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
					ctrl_bool = false;
					checkKeys();
				}
				if (e.getKeyCode() == KeyEvent.VK_V) {
					v_bool = false;
					checkKeys();
				}
			}

			public void keyTyped(KeyEvent arg0) {
			}
		});

		final JTable table = new JTable(model);
		table.setEnabled(false);
		table.setFocusable(true);
		table.requestFocus();
		table.setShowGrid(false);
		table.getTableHeader().setReorderingAllowed(false);
		model.addColumn("Filename");
		model.addColumn("Size");
		model.addColumn("Path");
		table.getColumnModel().getColumn(0).setMaxWidth(120);
		table.getColumnModel().getColumn(0).setMinWidth(120);
		table.getColumnModel().getColumn(1).setMaxWidth(50);
		table.getColumnModel().getColumn(1).setMinWidth(50);
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int row = table.rowAtPoint(new Point(e.getX(), e.getY()));
				int col = table.columnAtPoint(new Point(e.getX(), e.getY()));
				String url = (String) table.getModel().getValueAt(row, col);
				if (url.contains("http://")) {
					try {
						Desktop.getDesktop().browse(new URL(link).toURI());
					} catch (Exception ex) {
					}
				}
			}
		});

		JScrollPane jsp = new JScrollPane(table);
		jsp.setBounds(110, -2, 527, 360);
		panel.add(jsp);

		JButton screenshotBtn = new JButton("Screenshot");
		screenshotBtn.setBounds(5, 5, 100, 26);
		screenshotBtn.setFocusable(false);
		screenshotBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					randomNameGenerator();
					String randomName = randomFileName;
					takeScreenshot(randomName);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		panel.add(screenshotBtn);

		JButton regionBtn = new JButton("Region");
		regionBtn.setBounds(5, 35, 100, 26);
		regionBtn.setFocusPainted(false);
		regionBtn.setFocusable(false);
		regionBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				randomNameGenerator();
				String randomName = randomFileName;
				try {
					regionScreenshot(randomName);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		panel.add(regionBtn);

		JButton settingsBtn = new JButton("Settings");
		settingsBtn.setBounds(5, 65, 100, 26);
		settingsBtn.setFocusPainted(false);
		settingsBtn.setFocusable(false);
		settingsBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new settingsWindow();
				try {
					loadInfo();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		panel.add(settingsBtn);

		JButton exitBtn = new JButton("Exit");
		exitBtn.setBounds(5, 95, 100, 26);
		exitBtn.setFocusPainted(false);
		exitBtn.setFocusable(false);
		exitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				GlobalScreen.unregisterNativeHook();
				System.exit(0);
			}
		});
		panel.add(exitBtn);

		setTitle("ScreenSlice");
		setSize(640, 360);
		setLocationRelativeTo(null);
		setIconImage(icon.getImage());
		setDefaultCloseOperation(EXIT_ON_CLOSE);//CHANGE ME TO HIDE_ON_CLOSE!!!!
		setResizable(false);
		setVisible(true);
		add(panel);
	}
	
	public void trayIcon() {
		SystemTray sysTray = SystemTray.getSystemTray();
		PopupMenu menu = new PopupMenu();
		MenuItem item2 = new MenuItem("Show");
		item2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				setVisible(true);
			}
		});
		menu.add(item2);
		MenuItem item1 = new MenuItem("Settings");
		menu.add(item1);
		item1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new settingsWindow();
			}
		});
		MenuItem item3 = new MenuItem("Exit");
		menu.add(item3);
		item3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				GlobalScreen.unregisterNativeHook();
				System.exit(0);
			}
		});
		try {
			BufferedImage trayIconImage = ImageIO.read(getClass().getResource("screenslice.png"));
			int trayIconWidth = new TrayIcon(trayIconImage).getSize().width;
			TrayIcon trayIcon = new TrayIcon(trayIconImage.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH));
			tray = new TrayIcon(trayIcon.getImage(), "ScreenSlice", menu);
			sysTray.add(tray);
			tray.addMouseListener(new MouseListener() {
				public void mousePressed(MouseEvent e) {
					if (e.getClickCount() >= 2) {
						setVisible(true);
					}
				}

				public void mouseClicked(MouseEvent e) {
				}

				public void mouseEntered(MouseEvent e) {
				}

				public void mouseExited(MouseEvent e) {
				}

				public void mouseReleased(MouseEvent e) {
				}
			});
		} catch (Exception e3) {
			e3.printStackTrace();
		}
	}

	public static String checkOSName() {
		String path = "";
		if (System.getProperty("os.name").contains("Windows")) {
			path = windowsPath;
		} else {
			path = linuxPath;
		}
		return path;
	}

	public static void randomNameGenerator() {
		String[] chars = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9" };
		String randomLetters = chars[new Random().nextInt(chars.length)] + chars[new Random().nextInt(chars.length)] + chars[new Random().nextInt(chars.length)] + chars[new Random().nextInt(chars.length)] + chars[new Random().nextInt(chars.length)] + chars[new Random().nextInt(chars.length)];
		randomFileName = randomLetters + ".png";
		randomTxtName = randomLetters;
	}

	public static void regionScreenshot(final String randomName) throws Exception {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] screens = ge.getScreenDevices();
		Rectangle allScreenBounds = new Rectangle();
		for (GraphicsDevice screen : screens) {
			Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
			allScreenBounds.width += screenBounds.width;
			allScreenBounds.height = Math.max(allScreenBounds.height, screenBounds.height);
		}
		Robot robot = new Robot();
		BufferedImage screenShot = robot.createScreenCapture(new Rectangle((int) allScreenBounds.getX() - 1280, (int) allScreenBounds.getY(), allScreenBounds.width, allScreenBounds.height));
		ImageIO.write(screenShot, "png", new File(checkOSName() + "tempShot.png"));
		File f = new File(checkOSName() + "tempShot.png");
		new regionSelect(f);

	}

	public void takeScreenshot(final String randomName) throws Exception {
		setVisible(false);
		Thread.sleep(400);
		BufferedImage image = null;
		image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		ImageIO.write(image, "png", new File(checkOSName() + randomName));
		File f = new File(checkOSName() + randomFileName);
		link = "http://" + hostStr + uploadDir + randomFileName;
		model.insertRow(0, new Object[] { randomFileName, f.length() / 1024 + "KB", "<html><a href='" + link + "'>" + link + "</a></html>" });
		setVisible(true);
		if (hostStr == null || userStr == null || passStr == null) {
			JOptionPane.showMessageDialog(null, "Go to settings to enter FTP information!", "FTP Error!", JOptionPane.ERROR_MESSAGE);
		} else {
			Thread uploadThread = new Thread(new Runnable() {
				public void run() {
					try {
						uploadFile(randomName);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			uploadThread.start();
		}
	}

	public static void uploadFile(String randomName) throws Exception {
		FTPClient ftp = new FTPClient();
		int reply;
		ftp.connect(hostStr);
		reply = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(reply)) {
			ftp.disconnect();
			throw new Exception("Exception in connecting to FTP Server");
		}
		ftp.login(userStr, passStr);
		ftp.setFileType(FTP.BINARY_FILE_TYPE);
		ftp.enterLocalPassiveMode();

		try (InputStream input = new FileInputStream(new File(checkOSName() + randomName))) {
			ftp.makeDirectory(uploadDir);
			ftp.storeFile(uploadDir + randomName, input);
		} catch (Exception e){
			e.printStackTrace();
		}
		ftp.logout();
		ftp.disconnect();
		String address = "http://" + hostStr + uploadDir + randomName;
		StringSelection stringSelection = new StringSelection(address);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
		JOptionPane.showMessageDialog(null, "File uploaded! Address has been copied to clipboard.", "Success!", JOptionPane.INFORMATION_MESSAGE);

	}

	public static void loadInfo() throws Exception {
		File createDirectories = new File(checkOSName() + "//ScreenSlice Files//");
		createDirectories.mkdirs();
		BufferedReader hostReader = new BufferedReader(new FileReader(checkOSName() + "//ScreenSlice Files//" + "host.txt"));
		BufferedReader userReader = new BufferedReader(new FileReader(checkOSName() + "//ScreenSlice Files//" + "user.txt"));
		BufferedReader passReader = new BufferedReader(new FileReader(checkOSName() + "//ScreenSlice Files//" + "pass.txt"));
		BufferedReader uploadDirFile = new BufferedReader(new FileReader(checkOSName() + "//ScreenSlice Files//" + "uploadDir.txt"));

		hostStr = hostReader.readLine();
		userStr = userReader.readLine();
		passStr = passReader.readLine();
		uploadDir = uploadDirFile.readLine();

		if (hostReader != null || userReader != null || passReader != null || uploadDirFile != null) {
			hostReader.close();
			userReader.close();
			passReader.close();
			uploadDirFile.close();
		}
	}
}