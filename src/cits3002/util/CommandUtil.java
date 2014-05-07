package cits3002.util;

import cits3002.server.commands.CommandTuple;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CommandUtil {
	public static byte[] serialiseCommand(String command, byte[] binaryData) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		buffer.write(Integer.toString(binaryData.length).getBytes("ISO-8859-1"));
		buffer.write("\n".getBytes("ISO-8859-1"));
		buffer.write(command.getBytes("ISO-8859-1"));
		buffer.write("\n".getBytes("ISO-8859-1"));
		buffer.write(binaryData);
		return buffer.toByteArray();
	}

	public static byte[] serialiseCommand(String command, String binaryData) throws IOException {
		return serialiseCommand(command, binaryData.getBytes("ISO-8859-1"));
	}

	public static byte[] serialiseCommand(CommandTuple commandTuple) throws IOException {
		return serialiseCommand(commandTuple.commandString, commandTuple.binaryData);
	}
}
