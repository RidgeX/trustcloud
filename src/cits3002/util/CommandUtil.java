package cits3002.util;

import cits3002.server.commands.CommandReader;
import cits3002.server.commands.CommandTuple;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CommandUtil {
	public static byte[] serialiseCommand(String argsString, byte[] data) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		buffer.write(Integer.toString(data.length).getBytes("ISO-8859-1"));
		buffer.write("\n".getBytes("ISO-8859-1"));
		buffer.write(argsString.getBytes("ISO-8859-1"));
		buffer.write("\n".getBytes("ISO-8859-1"));
		buffer.write(data);
		return buffer.toByteArray();
	}

	public static byte[] serialiseCommand(String argsString, String data) throws IOException {
		return serialiseCommand(argsString, data.getBytes("ISO-8859-1"));
	}

	public static byte[] serialiseCommand(String[] args, byte[] data) throws IOException {
		String joinedArgs = Joiner.on(' ').join(args);
		return serialiseCommand(joinedArgs, data);
	}

	public static byte[] serialiseCommand(CommandTuple commandTuple) throws IOException {
		return serialiseCommand(commandTuple.args, commandTuple.data);
	}

	public static CommandTuple makeCommandTuple(String args) {
		return makeCommandTuple(args, new byte[0]);
	}

	public static CommandTuple makeCommandTuple(String args, byte[] data) {
		Iterable<String> splitArgs = Splitter.on(' ').trimResults().omitEmptyStrings().split(args);
		return makeCommandTuple(Iterables.toArray(splitArgs, String.class), data);
	}

	public static CommandTuple makeCommandTuple(String[] args, byte[] data) {
		return new CommandTuple(args, data);
	}

	public static CommandTuple parseCommandData(InputStream in) throws IOException {
		CommandReader commandReader = new CommandReader();
		ByteStreams.readBytes(in, commandReader);
		return commandReader.getResult();
	}

	public static CommandTuple parseCommandData(byte[] allData) throws IOException {
		CommandReader commandReader = new CommandReader();
		commandReader.processBytes(allData, 0, allData.length);
		return commandReader.getResult();
	}

	public static CommandTuple parseCommandData(String allData) throws IOException {
		return parseCommandData(allData.getBytes("ISO-8859-1"));
	}
}
