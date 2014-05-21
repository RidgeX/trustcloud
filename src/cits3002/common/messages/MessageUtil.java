package cits3002.common.messages;

import com.google.common.base.Charsets;
import com.google.common.base.Enums;
import com.google.common.base.Joiner;
import com.google.common.io.ByteStreams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * A utility class for constructing and serialising/deserialising messages.
 */
public class MessageUtil {
	/**
	 * Serialise a message to an array of bytes.
	 *
	 * @param message The message object
	 * @return The message bytes
	 */
	public static byte[] serialiseMessage(Message message) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		buffer.write(message.getTypeString().getBytes(Charsets.ISO_8859_1));
		buffer.write("\n".getBytes(Charsets.ISO_8859_1));
		buffer.write(Integer.toString(message.args.length).getBytes(Charsets.ISO_8859_1));
		buffer.write("\n".getBytes(Charsets.ISO_8859_1));
		buffer.write(Integer.toString(message.data.length).getBytes(Charsets.ISO_8859_1));
		buffer.write("\n".getBytes(Charsets.ISO_8859_1));
		for (String arg : message.args) {
			buffer.write(arg.getBytes(Charsets.ISO_8859_1));
			buffer.write("\n".getBytes(Charsets.ISO_8859_1));
		}
		buffer.write(message.data);
		return buffer.toByteArray();
	}

	/**
	 * Create a new message with no data.
	 *
	 * @param messageType The type
	 * @param args       The arguments
	 * @return The constructed message
	 */
	public static Message createMessage(MessageType messageType, List<String> args) {
		return createMessage(messageType, args, new byte[0]);
	}

	/**
	 * Create a new message with no arguments.
	 *
	 * @param messageType The type
	 * @param data       The data
	 * @return The constructed message
	 */
	public static Message createMessage(MessageType messageType, byte[] data) {
		return new Message(messageType, new String[0], data);
	}

	/**
	 * Create a new message with no arguments.
	 *
	 * @param messageType The type
	 * @param data       The data (in string form)
	 * @return The constructed message
	 */
	public static Message createMessage(MessageType messageType, String data) {
		return new Message(messageType, new String[0], data.getBytes(Charsets.ISO_8859_1));
	}

	/**
	 * Create a new message (type string, delimited arguments).
	 *
	 * @param typeString The type
	 * @param args       The arguments
	 * @param data       The data
	 * @return The constructed message
	 */
	public static Message createMessage(String typeString, List<String> args, byte[] data) {
		return createMessage(
				Enums.getIfPresent(MessageType.class, typeString).or(MessageType.INVALID),
				args,
				data);
	}

	/**
	 * Create a new message (type string, argument array, no data).
	 *
	 * @param messageType The type
	 * @param args        The arguments
	 * @return The constructed message
	 */
	public static Message createMessage(MessageType messageType, List<String> args, byte[] data) {
		return new Message(messageType, args.toArray(new String[args.size()]), data);
	}

	/**
	 * Create a new message with no arguments or data.
	 *
	 * @param messageType The type
	 * @param args        The arguments
	 * @return The constructed message
	 */
	public static Message createMessage(MessageType messageType) {
		return new Message(messageType, new String[0], new byte[0]);
	}

	/**
	 * Read and parse a message from the given input stream.
	 *
	 * @param in The input stream
	 * @return The resulting message
	 */
	public static Message parseMessage(InputStream in) throws IOException {
		MessageReader messageReader = new MessageReader();
		ByteStreams.readBytes(in, messageReader);
		return messageReader.getResult();
	}
}
