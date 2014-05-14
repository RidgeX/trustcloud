package cits3002.common.messages;

import com.google.common.base.Charsets;
import com.google.common.base.Enums;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MessageUtil {
	public static byte[] serialiseMessage(Message message) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		buffer.write(Integer.toString(message.data.length).getBytes(Charsets.ISO_8859_1));
		buffer.write("\n".getBytes(Charsets.ISO_8859_1));
		buffer.write(message.getTypeString().getBytes(Charsets.ISO_8859_1));
		buffer.write("\n".getBytes(Charsets.ISO_8859_1));
		buffer.write(message.getArgsString().getBytes(Charsets.ISO_8859_1));
		buffer.write("\n".getBytes(Charsets.ISO_8859_1));
		buffer.write(message.data);
		return buffer.toByteArray();
	}

	public static Message createMessage(MessageType messageType, String argsString, byte[] data) {
		return new Message(
				messageType,
				Iterables.toArray(
						Splitter.on('|').omitEmptyStrings().trimResults().split(argsString),
						String.class),
				data
		);
	}

	public static Message createMessage(MessageType messageType, String argsString) {
		return createMessage(messageType, argsString, new byte[0]);
	}

	public static Message createMessage(MessageType messageType, byte[] data) {
		return createMessage(messageType, "", data);
	}

	public static Message createMessage(String typeString, String argsString, byte[] data) {
		return createMessage(
				Enums.getIfPresent(MessageType.class, typeString).or(MessageType.INVALID),
				argsString,
				data);
	}

	public static Message createMessage(MessageType messageType, String[] args) {
		return new Message(messageType, args, new byte[0]);
	}

	public static Message createMessage(MessageType messageType, String[] args, byte[] data) {
		return new Message(messageType, args, data);
	}

	public static Message createMessage(MessageType messageType) {
		return new Message(messageType, new String[0], new byte[0]);
	}

	public static Message createMessage(MessageType messageType, String argsString, String dataString) {
		return createMessage(messageType, argsString, dataString.getBytes(Charsets.ISO_8859_1));
	}

	public static Message parseMessage(InputStream in) throws IOException {
		MessageReader messageReader = new MessageReader();
		ByteStreams.readBytes(in, messageReader);
		return messageReader.getResult();
	}
}
