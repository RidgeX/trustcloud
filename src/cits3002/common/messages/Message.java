package cits3002.common.messages;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

/**
 * A message to be sent across the network.
 */
public class Message {
	public final MessageType type;
	public final String[] args;
	public final byte[] data;

	/**
	 * Construct a new message.
	 *
	 * @param type The type/command
	 * @param args The arguments
	 * @param data The data
	 */
	public Message(MessageType type, String[] args, byte[] data) {
		Preconditions.checkNotNull(type);
		Preconditions.checkNotNull(args);
		Preconditions.checkNotNull(data);
		this.type = type;
		this.args = args;
		this.data = data;
	}

	/**
	 * Return the type as a string.
	 *
	 * @return The message type
	 */
	public String getTypeString() {
		return type.name;
	}

	/**
	 * Return the arguments as a string.
	 *
	 * @return The message arguments
	 */
	public String getArgsString() {
		return Joiner.on("\n").skipNulls().join(args) + "\n";
	}

	/**
	 * Return the data as a string.
	 *
	 * @return The message data
	 */
	public String getDataString() {
		return new String(data, Charsets.ISO_8859_1);
	}
}
