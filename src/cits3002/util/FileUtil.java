package cits3002.util;

import java.io.IOException;
import java.io.InputStream;

public class FileUtil {
	public static String readAllBytes(InputStream in) throws IOException {
		StringBuilder builder = new StringBuilder();
		byte[] buf = new byte[4096];
		while (in.read(buf, 0, buf.length) != -1) {
			builder.append(new String(buf));
		}
		return builder.toString();
	}
}
