import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
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
	public ImageIcon icon = new ImageIcon(getClass().getResource("icon.png"));
	static boolean uploaded = false;
	public static TrayIcon tray;
	static String link;
	public static String imgurLink;
	public static boolean isImgur = true; //true by default because most people don't have an ftp server.
	public static boolean threescreens = false; //only on creators computer for now, can't figure out how to get screen position on windows.

	public mainWindow() {
		windowsPath = "C:\\Users\\" + username + "\\Pictures\\Screenshots\\";
		linuxPath = "//home//" + System.getProperty("user.name") + "//Pictures//Screenshots//";
		File f = new File(windowsPath + "");
		if(f.exists() && !f.isDirectory()) { 
			threescreens = true; 
		}
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
						if(isImgur) {
							return;
						} else {
							link = "http://" + hostStr + uploadDir + randomName + ".txt";							
							model.insertRow(0, new Object[] { randomName + ".txt", fileSize / 1024 + "KB", "<html><a href='" + link + "'>" + link + "</a></html>" });
						}
						
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
		table.setBackground(Color.WHITE);
		table.setEnabled(false);
		try {table.setFont(new customButton("","").defaultFont());} catch (Exception e2) {}
		table.setFocusable(true);
		table.requestFocus();
		table.setOpaque(true);
		table.setShowGrid(false);
		table.setBackground(Color.WHITE);
		table.getTableHeader().setReorderingAllowed(false);
		model.addColumn("Filename");
		model.addColumn("Size");
		model.addColumn("Path");
		table.getColumnModel().getColumn(0).setMaxWidth(120);
		table.getColumnModel().getColumn(0).setMinWidth(120);
		table.getColumnModel().getColumn(1).setMaxWidth(50);
		table.getColumnModel().getColumn(1).setMinWidth(50);
		try {table.getTableHeader().setFont(new customButton("","").defaultFont());} catch (Exception e2) {}
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int row = table.rowAtPoint(new Point(e.getX(), e.getY()));
				int col = table.columnAtPoint(new Point(e.getX(), e.getY()));
				String url = (String) table.getModel().getValueAt(row, col);
				String[] splitLink = url.split("'>http://");
				String[] newLink = splitLink[1].split("</a>");
//				System.out.println(newLink[0]);
				if (url.contains("http://")) {
					try {
						Desktop.getDesktop().browse(new URL("http://" + newLink[0]).toURI());
					} catch (Exception ex) {
					}
				}
			}
		});

		JScrollPane jsp = new JScrollPane(table);
		jsp.setBounds(140, -2, 500, 334);
		jsp.getViewport().setBackground(Color.WHITE);
		try {jsp.setFont(new customButton("","").defaultFont());} catch (Exception e2) {}
		panel.add(jsp);
		
		customButton screenshotBtn = new customButton(" Screenshot", "screenshot.png");
		screenshotBtn.setBounds(5, 5,130, 20);
		screenshotBtn.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				try {
					randomNameGenerator();
					String randomName = randomFileName;
					takeScreenshot(randomName);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
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
		panel.add(screenshotBtn);
		
		customButton regionBtn = new customButton(" Region", "region.png");
		regionBtn.setBounds(5, screenshotBtn.getY() + screenshotBtn.getHeight() + 2,130, 20);
		regionBtn.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				randomNameGenerator();
				String randomName = randomFileName;
				try {
					regionScreenshot(randomName);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
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
		panel.add(regionBtn);
		
		JSeparator screenshotSeperator = new JSeparator(SwingConstants.HORIZONTAL);
		screenshotSeperator.setBounds(5, regionBtn.getY() + regionBtn.getHeight() + 2,130, (int)screenshotSeperator.getPreferredSize().getHeight());
		panel.add(screenshotSeperator);
		
		customButton settingsBtn = new customButton(" Settings", "settings.png");
		settingsBtn.setBounds(5,regionBtn.getY() + regionBtn.getHeight() + 5,130, 20);
		settingsBtn.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				JOptionPane.showMessageDialog(null, "Settings coming soon!", "Coming soon", JOptionPane.INFORMATION_MESSAGE);
				/*				new settingsWindow();
				try {
					loadInfo();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
*/			}
			public void mouseEntered(MouseEvent arg0) {
			}
			public void mouseExited(MouseEvent arg0) {
			}
			public void mousePressed(MouseEvent arg0) {
			}
			public void mouseReleased(MouseEvent arg0) {
			}
			
		});
		panel.add(settingsBtn);
		
		customButton destsettingsBtn = new customButton(" Destination Settings", "destinations.png");
		destsettingsBtn.setBounds(5,settingsBtn.getY() + settingsBtn.getHeight() +2, 130, 20);
		destsettingsBtn.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				new settingsWindow();
				try {
					loadInfo();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
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
		panel.add(destsettingsBtn);
		
		JSeparator settingsSeperator = new JSeparator(SwingConstants.HORIZONTAL);
		settingsSeperator.setBounds(5, destsettingsBtn.getY() + destsettingsBtn.getHeight() + 2,130, (int)settingsSeperator.getPreferredSize().getHeight());
		panel.add(settingsSeperator);
		
		customButton exitBtn = new customButton(" Exit", "exit.png");
		exitBtn.setBounds(5,destsettingsBtn.getY() + destsettingsBtn.getHeight() + 5,130, 20);
		exitBtn.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent arg0) {
				GlobalScreen.unregisterNativeHook();
				System.exit(0);
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
		panel.add(exitBtn);
		
		setTitle("ScreenSlice");
		setSize(640, 360);
		setLocationRelativeTo(null);
		setIconImage(icon.getImage());
		setDefaultCloseOperation(HIDE_ON_CLOSE);//CHANGE ME TO HIDE_ON_CLOSE!!!!
		setResizable(false);
		setVisible(true);
		setBackground(Color.WHITE);
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
			BufferedImage trayIconImage = ImageIO.read(getClass().getResource("icon.png"));
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
		String randomLetters = chars[new Random().nextInt(chars.length)] + chars[new Random().nextInt(chars.length)] + chars[new Random().nextInt(chars.length)] + chars[new Random().nextInt(chars.length)] + chars[new Random().nextInt(chars.length)] + chars[new Random().nextInt(chars.length)] + chars[new Random().nextInt(chars.length)];
		randomFileName = randomLetters + ".png";
		randomTxtName = randomLetters;
	}

	public static void regionScreenshot(final String randomName) throws Exception {
		if(threescreens) {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice[] screens = ge.getScreenDevices();
			Rectangle allScreenBounds = new Rectangle();
			for (GraphicsDevice screen : screens) {
				Rectangle screenBounds = screen.getDefaultConfiguration().getBounds();
				allScreenBounds.width += screenBounds.width;
				allScreenBounds.height = Math.max(allScreenBounds.height, screenBounds.height);
				BufferedImage screenShot = new Robot().createScreenCapture(new Rectangle((int) allScreenBounds.getX() - 1280, (int) allScreenBounds.getY(), allScreenBounds.width, allScreenBounds.height));
				ImageIO.write(screenShot, "png", new File(checkOSName() + "tempShot.png"));
			}
		} else {
			BufferedImage screenShot = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
			ImageIO.write(screenShot, "png", new File(checkOSName() + "tempShot.png"));
		}
		File f = new File(checkOSName() + "tempShot.png");
		new regionSelect(f);
	}

	public void takeScreenshot(final String randomName) throws Exception {
		setVisible(false);
		Thread.sleep(400);
		BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
		ImageIO.write(image, "png", new File(checkOSName() + randomName));
		File f = new File(checkOSName() + randomFileName);
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

	public static void uploadFile(final String randomName) throws Exception {
		if(isImgur) {
			Thread imgurThread = new Thread(new Runnable() {
				public void run() {
					try {
						new imgurUpload(randomName);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			imgurThread.start();
			
		} else {
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
			File f = new File(checkOSName() + randomFileName);
			link = "http://" + hostStr + uploadDir + randomFileName;
			model.insertRow(0, new Object[] { randomFileName, f.length() / 1024 + "KB", "<html><a href='" + link + "'>" + link + "</a></html>" });
		}
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