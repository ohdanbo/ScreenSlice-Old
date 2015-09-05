
import java.awt.Toolkit;
import java.awt.TrayIcon.MessageType;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.swing.JOptionPane;

public class pastebinUpload {
	String content;

	public pastebinUpload(String content) {
		this.content = content;
		try{
			if(!uploadTxt().contains("http://")) {
				JOptionPane.showMessageDialog(null,
						"Error! use FTP server for text uploads",
						"Error!", JOptionPane.ERROR_MESSAGE);
			} else {
				System.out.println(uploadTxt());
				String link = uploadTxt();
				String linkSplit[] = link.split("com/");
				String name = linkSplit[1];
				mainWindow.tray.displayMessage("Successfully uploaded!", "Image Uploaded, URL has been copied to clipboard.", MessageType.INFO);
				mainWindow.model.insertRow(0, new Object[] { name, "0" + "KB","<html><a href='" + link + "'>" + link + "</a></html>" });
				StringSelection stringSelection = new StringSelection(link);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null);
			}
		}catch(Exception e){e.printStackTrace();}
	}

	public String uploadTxt() throws Exception {
		String data = "api_option=paste&api_user_key=" + ""
				+ "&api_paste_private=1&api_paste_name="
				+ System.getProperty("user.name") + "'s paste"
				+ "&api_paste_expire_date=N&api_paste_format=" + "Java"
				+ "&api_dev_key=6a17a97a57725c90028a042a9bdf477d"
				+ "&api_paste_code=" + content;

		URL url = new URL("http://www.pastebin.com/api/api_post.php");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");

		connection.setRequestProperty("Content-Length",
				"" + Integer.toString(data.getBytes().length));
		connection.setRequestProperty("Content-Language", "en-US");

		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);

		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.writeBytes(data);
		wr.flush();
		wr.close();

		InputStream is = connection.getInputStream();
		BufferedReader rd = new BufferedReader(new InputStreamReader(is));
		String line;
		StringBuffer response = new StringBuffer();
		while ((line = rd.readLine()) != null) {
			response.append(line);
		}
		rd.close();
		return response.toString();
	}
}
