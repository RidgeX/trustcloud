package cits3002.common.messages;

import com.google.common.base.Charsets;
import com.google.common.base.Enums;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * A utility class for constructing and serialising/deserialising messages.
 */
public class MessageUtil {
	/**
	 * Serialise a message to an array of bytes.
	 * @param message The message object
	 * @return The message bytes
	 */
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

	/**
	 * Create a new message (delimited arguments).
	 * @param messageType The type
	 * @param argsString The arguments
	 * @param data The data
	 * @return The constructed message
	 */
	public static Message createMessage(MessageType messageType, String argsString, byte[] data) {
		return new Message(
				messageType,
				Iterables.toArray(
						Splitter.on('|').omitEmptyStrings().trimResults().split(argsString),
						String.class),
				data
		);
	}

	/**
	 * Create a new message (delimited arguments, no data).
	 * @param messageType The type
	 * @param argsString The arguments
	 * @return The constructed message
	 */
	public static Message createMessage(MessageType messageType, String argsString) {
		return createMessage(messageType, argsString, new byte[0]);
	}

	/**
	 * Create a new message (no arguments).
	 * @param messageType The type
	 * @param data The data
	 * @return The constructed message
	 */
	public static Message createMessage(MessageType messageType, byte[] data) {
		return createMessage(messageType, "", data);
	}

	/**
	 * Create a new message (type string, delimited arguments).
	 * @param typeString The type
	 * @param argsString The arguments
	 * @param data The data
	 * @return The constructed message
	 */
	public static Message createMessage(String typeString, String argsString, byte[] data) {
		return createMessage(
				Enums.getIfPresent(MessageType.class, typeString).or(MessageType.INVALID),
				argsString,
				data);
	}

	/**
	 * Create a new message (type string, argument array, no data).
	 * @param messageType The type
	 * @param args The arguments
	 * @return The constructed message
	 */
	public static Message createMessage(MessageType messageType, String[] args) {
		return new Message(messageType, args, new byte[0]);
	}

	/**
	 * Create a new message (type string, argument array).
	 * @param messageType The type
	 * @param args The arguments
	 * @param data The data
	 * @return The constructed message
	 */
	public static Message createMessage(MessageType messageType, String[] args, byte[] data) {
		return new Message(messageType, args, data);
	}

	/**
	 * Create a new message (no data).
	 * @param messageType The type
	 * @return The constructed message
	 */
	public static Message createMessage(MessageType messageType) {
		return new Message(messageType, new String[0], new byte[0]);
	}

	/**
	 * Create a new message (delimited arguments, data string).
	 * @param messageType The type
	 * @param argsString The arguments
	 * @param dataString The data
	 * @return The constructed message
	 */
	public static Message createMessage(MessageType messageType, String argsString,
			String dataString) {
		return createMessage(messageType, argsString, dataString.getBytes(Charsets.ISO_8859_1));
	}

	/**
	 * Read and parse a message from the given input stream. 
	 * @param in The input stream
	 * @return The resulting message
	 */
	public static Message parseMessage(InputStream in) throws IOException {
		MessageReader messageReader = new MessageReader();
		ByteStreams.readBytes(in, messageReader);
		return messageReader.getResult();
	}
}
