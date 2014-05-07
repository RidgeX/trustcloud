package cits3002.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CommandUtil {
	public static byte[] makeCommand(String command, byte[] binaryData) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		buffer.write(Integer.toString(binaryData.length).getBytes("utf-8"));
		buffer.write("\n".getBytes("utf-8"));
		buffer.write(command.getBytes("utf-8"));
		buffer.write("\n".getBytes("utf-8"));
		buffer.write(binaryData);
		return buffer.toByteArray();
	}

	public static byte[] makeCommand(String command, String binaryData) throws Exception {
		return makeCommand(command, binaryData.getBytes("utf-8"));
	}
}
