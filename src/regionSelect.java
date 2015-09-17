import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;
import javax.swing.*;

public class regionSelect {
	public BufferedImage image = null;
	public JFrame frame;
	public static int fileSize;
	private int width = 0, height = 0, x = 0, y = 0;

	public regionSelect(final File screenshotPath, int width, int height, int x, int y) {
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());} catch (Exception ex) {}
		try {image = ImageIO.read(screenshotPath);} catch (Exception e1) {e1.printStackTrace();}
		frame = new JFrame("ScreenSlice - Region");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new BorderLayout());
		frame.add(new CapturePane());
		frame.setAlwaysOnTop(true);
		frame.setSize((int) width, (int) height);
		frame.setLocation(x,y);
		//frame.setIconImage(new ImageIcon(getClass().getResource("icon.png")).getImage());
		frame.setUndecorated(true);
		frame.setVisible(true);
	}

	public class CapturePane extends JPanel {
		private static final long serialVersionUID = 1L;
		private Rectangle selectionBounds;
		private Point clickPoint;
		private int mouseX, mouseY;
		private BufferedImage enlargedPixels,circle;
		
		public CapturePane() {
			try {
				circle = ImageIO.read(getClass().getResource("circle.png"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			Toolkit toolkit = Toolkit.getDefaultToolkit();
			Image cursorImg = toolkit.getImage(getClass().getResource("cursor.png"));
			Point point = new Point(28 / 2 + 1, 26 / 2 + 2);
			Cursor cursor = toolkit.createCustomCursor(cursorImg, point, "Cursor");
			setCursor(cursor);
			setFocusable(true);
			MouseAdapter mouseHandler = new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					if (SwingUtilities.isRightMouseButton(e)) {
						frame.dispose();
					}
				}
				public void mousePressed(MouseEvent e) {
					if (SwingUtilities.isRightMouseButton(e)) {
						frame.dispose();
					} else {
						clickPoint = e.getPoint();
						selectionBounds = null;
					}
				}
				public void mouseMoved(MouseEvent e) {
					mouseX = e.getPoint().x;
					mouseY = e.getPoint().y;
					enlargedPixels = image.getSubimage(mouseX-4, mouseY-4, 11, 11);
					repaint();
				}
				public void mouseDragged(MouseEvent e) {
					if (SwingUtilities.isRightMouseButton(e)) {frame.dispose();}
					else {
						Point dragPoint = e.getPoint();
						int x = Math.min(clickPoint.x, dragPoint.x);
						int y = Math.min(clickPoint.y, dragPoint.y);
						int width = Math.max(clickPoint.x - dragPoint.x, dragPoint.x - clickPoint.x);
						int height = Math.max(clickPoint.y - dragPoint.y, dragPoint.y - clickPoint.y);
						selectionBounds = new Rectangle(x, y, width, height);
						mouseX = e.getPoint().x;
						mouseY = e.getPoint().y;
						enlargedPixels = image.getSubimage(mouseX-4, mouseY-4, 11, 11);
						repaint();
					}
				}
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
				public void keyTyped(KeyEvent e) {}				
				public void keyReleased(KeyEvent e) {}			
				public void keyPressed(KeyEvent e) {if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {frame.dispose();}}
			});
		}

		
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setColor(new Color(255, 255, 255, 128));
			
			g2d.drawImage(image,0,0,width,height,null);
						
			if (System.getProperty("os.name").contains("Windows")) {g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);} 
			else {g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);}

			Area fill = new Area(new Rectangle(new Point(0, 0), getSize()));
			if (selectionBounds != null) {fill.subtract(new Area(selectionBounds));}
			g2d.fill(fill);
			if (selectionBounds != null) {
				g2d.setColor(Color.BLACK);
				g2d.draw(selectionBounds);
			}
			
			g2d.setColor(Color.BLACK);
			g.setClip(new Ellipse2D.Float(mouseX+20, mouseY+20, 161,161));
			BufferedImage roundedPixels = makeRoundedCorner(enlargedPixels, 20);
			if(mouseX < Toolkit.getDefaultToolkit().getScreenSize().getWidth()-172 && mouseY < Toolkit.getDefaultToolkit().getScreenSize().getHeight()-181) {
				g2d.drawImage(roundedPixels, mouseX+20, mouseY+20, 161, 161, null);
				g2d.drawImage(circle, mouseX+20, mouseY+20, 161,161, null);
			} else {
				if(mouseX > Toolkit.getDefaultToolkit().getScreenSize().getWidth()-172 && mouseY > Toolkit.getDefaultToolkit().getScreenSize().getHeight()-181) {
					g2d.drawImage(roundedPixels, mouseX-20-161, mouseY-20-161, 161, 161, null);
					g2d.drawImage(circle, mouseX-20-161, mouseY-20-161, 161,161, null);
				} else if(mouseX > Toolkit.getDefaultToolkit().getScreenSize().getWidth()-172) {
					g2d.drawImage(roundedPixels, mouseX-20-161, mouseY+20, 161, 161, null);
					g2d.drawImage(circle, mouseX-20-161, mouseY+20, 161,161, null);
				} else if(mouseY > Toolkit.getDefaultToolkit().getScreenSize().getHeight()-181) {
					g2d.drawImage(roundedPixels, mouseX+20, mouseY-20-161, 161, 161, null);
					g2d.drawImage(circle, mouseX+20, mouseY-20-161, 161,161, null);
				}
			}
			image.flush();
			circle.flush();
			roundedPixels.flush();
			g2d.dispose();
		}
	}
	
	public static BufferedImage makeRoundedCorner(BufferedImage image, int cornerRadius) {
	    int w = image.getWidth();
	    int h = image.getHeight();
	    BufferedImage output = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2 = output.createGraphics();
	    g2.setComposite(AlphaComposite.Src);
	    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    g2.setColor(Color.WHITE);
	    g2.fill(new RoundRectangle2D.Float(0, 0, w, h, cornerRadius, cornerRadius));
	    g2.setComposite(AlphaComposite.SrcAtop);
	    g2.drawImage(image, 0, 0, null);
	    g2.dispose();
	    return output;
	}

	public void createSubImage(Rectangle bounds) {
		mainWindow.randomNameGenerator();
		final String randomName = mainWindow.randomFileName;
		try {
			BufferedImage subImage = image.getSubimage(bounds.x+1, bounds.y+1, (int) bounds.getWidth()+1, (int) bounds.getHeight()+1);
			File outputfile = new File(mainWindow.checkOSName() + randomName);
			ImageIO.write(subImage, "png", outputfile);
			subImage.flush();
		} catch (IOException e) {e.printStackTrace();}
		try {
			Thread uploadThread = new Thread(new Runnable() {
				public void run() {try {mainWindow.uploadFile(randomName,"");} catch (Exception e) {e.printStackTrace();}}
			});
			uploadThread.start();
		} catch (Exception e) {e.printStackTrace();}
	}
}