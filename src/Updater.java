import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

public class Updater {
	public static double currentVersion = 6;
	private double remoteVersion;
	
	public Updater() {
		System.out.println("Checking for update...");
		if(isUpdate()) {
			updatePrompt();
		} else {
			System.out.println("Up to date.");
		}
	}
	
	private void updatePrompt() {
		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());} catch (Exception e) {e.printStackTrace();}
		
		System.out.println("Update available!");
		JPanel p = new JPanel();
		p.setLayout(null);
		p.setSize(275,250);
		
		final JFrame f = new JFrame();
		f.setTitle("Update available!");
		f.setSize(275,90);
		f.setLocationRelativeTo(null);
		f.setIconImage(new ImageIcon(getClass().getResource("icon.png")).getImage());
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setResizable(false);
		
		JLabel ask = new JLabel("Update available, would you like to download it?");
		ask.setBounds((275/2)-(ask.getPreferredSize().width/2), 10,(int)ask.getPreferredSize().width, (int)ask.getPreferredSize().height);
		p.add(ask);
		
		JButton update = new JButton("Download update");
		update.setBounds(25,30,(int)update.getPreferredSize().width+15, (int) update.getPreferredSize().height);
		update.setFocusable(false);
		update.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					System.out.println("Installing update...");
					URL website = new URL("http://ohdanbo.com/screenslice/screenslice.exe");
					ReadableByteChannel rbc = Channels.newChannel(website.openStream());
					FileOutputStream fos = new FileOutputStream("C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\screenslice.exe");
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
				} catch (Exception ex) {ex.printStackTrace();}
				System.out.println("Update has been installed!");
				JOptionPane.showMessageDialog(null,"Success!", "Update has been installed!", JOptionPane.INFORMATION_MESSAGE);
				Thread process = new Thread(new Runnable() {
					public void run() {
						try{Process p = Runtime.getRuntime().exec("C:\\Users\\" + System.getProperty("user.name") + "\\Desktop\\screenslice.exe");}catch(Exception ex){ex.printStackTrace();}		
					}
				});
			}
		});
		p.add(update);
		
		JButton cancel = new JButton("Cancel");
		cancel.setBounds(30+(int)update.getPreferredSize().width+20,30,(int)cancel.getPreferredSize().width+15, (int) cancel.getPreferredSize().height);
		cancel.setFocusable(false);
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		p.add(cancel);
		f.add(p);
		f.setVisible(true);
	}
	
	private boolean isUpdate() {
		URL url;
		try {
			url = new URL("http://ohdanbo.com/screenslice/version.txt");
			Scanner s = new Scanner(url.openStream());
			remoteVersion = s.nextDouble();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		if(currentVersion < remoteVersion) {return true;} 
		else {return false;}
	}
}
