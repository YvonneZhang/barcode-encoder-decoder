package barcode;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LibraryInfo {

	public static String getLibraryInfo(String EAN13Code) {
		String url = "http://202.120.82.40/search*chx/i?SEARCH=" + EAN13Code
				+ "&sortdropdown=-&availlim=1";
		String content = getHtmlReadLine(url);

		if (getContent(content) != "")
			return getContent(content);
		else
			return "该书不再校图书馆中/该书不可外借";

	}

	private static String getContent(String content) {
		if (content.indexOf("<tr  class=\"bibItemsEntry\">") == -1
				|| content.indexOf("<center><form method=\"post\" action=") == -1)
			return "";
		String str = content.substring(
				content.indexOf("<tr  class=\"bibItemsEntry\">"),
				content.indexOf("<center><form method=\"post\" action="));

		Pattern p = Pattern.compile("<(\\S*?)[^>]*>.*?| <.*? />");
		Matcher m = p.matcher(str);

		String rs = new String(str);
		while (m.find()) {
			rs = rs.replace(m.group(), "");
		}
		rs = rs.replace("&nbsp;", "");
		return rs;
	}

	private static String getHtmlReadLine(String httpurl) {
		String CurrentLine = "";
		String TotalString = "";
		InputStream urlStream;
		String content = "";
		try {
			URL url = new URL(httpurl);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.connect();
			urlStream = connection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					urlStream, "utf-8"));
			while ((CurrentLine = reader.readLine()) != null) {
				TotalString += CurrentLine + "\n";
			}
			content = TotalString;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content;
	}
}
