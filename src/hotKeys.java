import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class hotKeys implements NativeKeyListener {
	static int i = 0;
	static boolean ctrl_bool = false;
	static boolean print_bool = false;
	static boolean alt_bool = false;
	static int count = 0;

	public static boolean ctrlprintscreen() {
		if (ctrl_bool && print_bool) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean altprintscreen() {	
		if (alt_bool && print_bool) {
			return true;
		} else {
			return false;
		}
	}

	public static void checkKeys() {
		if (ctrlprintscreen()) {
			mainWindow.randomNameGenerator();
			String randomName = mainWindow.randomFileName;
			try {
				mainWindow.regionScreenshot(randomName);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		if (altprintscreen()) {
			try {
				mainWindow.randomNameGenerator();
				String randomName = mainWindow.randomFileName;
				Thread.sleep(1000);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				BufferedImage image = (BufferedImage) clipboard.getData(DataFlavor.imageFlavor);
				StringSelection stringSelection = new StringSelection("");
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
				File file = new File(mainWindow.checkOSName() + randomName);
				ImageIO.write(image, "png", file);
				count++;
				int fileSize = (int) file.length();
				if(mainWindow.isImgur) {
					new imgurUpload(randomName);
				} else {
					mainWindow.uploadFile(randomName);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void nativeKeyPressed(NativeKeyEvent e) {
		if (e.getKeyCode() == NativeKeyEvent.VK_CONTROL) {
			ctrl_bool = true;
			checkKeys();
		}
		if (e.getKeyCode() == NativeKeyEvent.VK_ALT) {
			alt_bool = true;
			checkKeys();
		}
		if (e.getKeyCode() == NativeKeyEvent.VK_PRINTSCREEN) {
			print_bool = true;
			checkKeys();
		}
	}

	public void nativeKeyReleased(NativeKeyEvent e) {
		if (e.getKeyCode() == NativeKeyEvent.VK_CONTROL) {
			ctrl_bool = false;
			checkKeys();
		}
		if (e.getKeyCode() == NativeKeyEvent.VK_ALT) {
			alt_bool = false;
			checkKeys();
		}
		if (e.getKeyCode() == NativeKeyEvent.VK_PRINTSCREEN) {
			print_bool = false;
			checkKeys();
		}
	}

	public void nativeKeyTyped(NativeKeyEvent e) {
	}

	public static void main(String[] args) {
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}

		GlobalScreen.getInstance().addNativeKeyListener(new hotKeys());

		new mainWindow();
	}
}