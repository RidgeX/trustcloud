package cits3002.util;

public class CommandUtil {
	public static String makeCommandString(String command, String binaryData) {
		StringBuilder builder = new StringBuilder();
		builder.append(Integer.toString(binaryData.length()));
		builder.append("\n");
		builder.append(command);
		builder.append("\n");
		builder.append(binaryData);
		return builder.toString();
	}
}
