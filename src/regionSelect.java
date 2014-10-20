import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.*;

public class regionSelect {
	public BufferedImage image = null;
	public JFrame frame;
	public static int fileSize;

	public regionSelect(final File screenshotPath) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
				}
				try {
					image = ImageIO.read(screenshotPath);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

//				int width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
//				int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();

				int width = 0;
				int height = 0;
				
				if(mainWindow.threescreens) {
					GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
					GraphicsDevice[] gs = ge.getScreenDevices();
					for (GraphicsDevice curGs : gs) {
						DisplayMode mode = curGs.getDisplayMode();
						width += mode.getWidth();
						height = mode.getHeight();
					}
				} else {
					width = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
					height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
				}
				
				frame = new JFrame("ScreenSlice - Region");
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.setLayout(new BorderLayout());
				frame.add(new CapturePane());
				frame.setAlwaysOnTop(true);
				frame.setSize((int) width, (int) height);
				frame.setUndecorated(true);
				if(mainWindow.threescreens) {
					frame.setLocation(-1280, 0);
				} else {
					frame.setLocation(0,0);
				}
				frame.setVisible(true);

				JLabel label = new JLabel();
				label.setBounds(0, 0, (int) width, (int) height);
				label.setIcon(new ImageIcon(image));
				frame.add(label);
				fileSize = (int) screenshotPath.length();
			}
		});
	}

	public class CapturePane extends JPanel {
		private static final long serialVersionUID = 1L;
		private Rectangle selectionBounds;
		private Point clickPoint;

		public CapturePane() {
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Image cursorImg = toolkit.getImage(getClass().getResource("cursor.png"));
			Point point = new Point(28 / 2 + 1, 26 / 2 + 2);
			Cursor cursor = toolkit.createCustomCursor(cursorImg, point, "Cursor");
			setCursor(cursor);
			setFocusable(true);
			MouseAdapter mouseHandler = new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (SwingUtilities.isRightMouseButton(e)) {
						frame.dispose();
					}
				}

				@Override
				public void mousePressed(MouseEvent e) {
					if (SwingUtilities.isRightMouseButton(e)) {
						frame.dispose();
					} else {
						clickPoint = e.getPoint();
						selectionBounds = null;
					}
				}

				@Override
				public void mouseDragged(MouseEvent e) {
					if (SwingUtilities.isRightMouseButton(e)) {
						frame.dispose();
					}
					else {
						Point dragPoint = e.getPoint();
						int x = Math.min(clickPoint.x, dragPoint.x);
						int y = Math.min(clickPoint.y, dragPoint.y);
						int width = Math.max(clickPoint.x - dragPoint.x, dragPoint.x - clickPoint.x);
						int height = Math.max(clickPoint.y - dragPoint.y, dragPoint.y - clickPoint.y);
						selectionBounds = new Rectangle(x, y, width, height);
						repaint();
					}
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					clickPoint = null;
					createSubImage(selectionBounds);
					selectionBounds = null;
					frame.dispose();
				}
			};
			addMouseListener(mouseHandler);
			addMouseMotionListener(mouseHandler);
			addKeyListener(new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {
				}

				@Override
				public void keyReleased(KeyEvent e) {
				}

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
						frame.dispose();
					}
				}
			});
		}

		@Override
		protected void paintComponent(Graphics g) {
			int width = 0;
			int height = 0;
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice[] gs = ge.getScreenDevices();
			for (GraphicsDevice curGs : gs) {
				DisplayMode mode = curGs.getDisplayMode();
				width += mode.getWidth();
				height = mode.getHeight();
			}
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setColor(new Color(255, 255, 255, 128));
			g2d.drawImage(image, 0, 0, (int) width, (int) height, null);
			if (System.getProperty("os.name").contains("Windows")) {
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			} else {
				g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			}

			Area fill = new Area(new Rectangle(new Point(0, 0), getSize()));
			if (selectionBounds != null) {
				fill.subtract(new Area(selectionBounds));
			}
			g2d.fill(fill);
			if (selectionBounds != null) {
				g2d.setColor(Color.BLACK);
				g2d.draw(selectionBounds);
			}
			g2d.dispose();
		}
	}

	public void createSubImage(Rectangle bounds) {
		mainWindow.randomNameGenerator();
		final String randomName = mainWindow.randomFileName;
		try {
			BufferedImage subImage = image.getSubimage(bounds.x, bounds.y, (int) bounds.getWidth(), (int) bounds.getHeight());
			File outputfile = new File(mainWindow.checkOSName() + randomName);
			ImageIO.write(subImage, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			Thread uploadThread = new Thread(new Runnable() {
				public void run() {
					try {
						mainWindow.uploadFile(randomName);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			uploadThread.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}