import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import org.apache.commons.codec.binary.Base64OutputStream;

public class imgurUpload {
	public imgurUpload(String randName) {
		try {
			String response = imageToString(mainWindow.checkOSName() + randName);
			String fileSize[] = response.split("\"size\":\"");
			String fileSize1[] = fileSize[1].split("\",\"views");
			int imageSize = Integer.parseInt(fileSize1[0]);
			int imgSize = imageSize / 1024;
			String link = response.substring(
					response.lastIndexOf("\"http") + 1,
					response.lastIndexOf(".png"))
					+ ".png";
			String newLink = link.replaceAll("\\\\", "");
			mainWindow.imgurLink = newLink;
			String[] parts = newLink.split("m/");
			String name = parts[1];
			JOptionPane.showMessageDialog(null,
					"File uploaded! Address has been copied to clipboard.",
					"Success!", JOptionPane.INFORMATION_MESSAGE);
			mainWindow.model.insertRow(0, new Object[] {
					name,
					imgSize + "KB",
					"<html><a href='" + newLink + "'>" + newLink
							+ "</a></html>" });
			StringSelection stringSelection = new StringSelection(newLink);
			Clipboard clipboard = Toolkit.getDefaultToolkit()
					.getSystemClipboard();
			clipboard.setContents(stringSelection, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String imageToString(String path) throws Exception {
		BufferedImage img = ImageIO.read(new File(path));
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		OutputStream b64 = new Base64OutputStream(os);
		ImageIO.write(img, "png", b64);
		String result = os.toString("UTF-8");

		URL url = new URL("https://api.imgur.com/3/image");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Authorization", "Client-ID "
				+ "4d9fc949d3d87e9");
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");

		conn.connect();
		StringBuilder stb = new StringBuilder();
		OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		wr.write(result);
		wr.flush();

		BufferedReader rd = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			stb.append(line).append("\n");
		}
		wr.close();
		rd.close();

		return stb.toString();
	}
}