package cits3002.common.messages;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

public class Message {
	public MessageType type;
	public final String[] args;
	public final byte[] data;

	public Message(MessageType type, String[] args, byte[] data) {
		Preconditions.checkNotNull(type);
		Preconditions.checkNotNull(args);
		Preconditions.checkNotNull(data);
		this.type = type;
		this.args = args;
		this.data = data;
	}

	public String getTypeString() {
		return type.name;
	}

	public String getArgsString() {
		return Joiner.on("|").skipNulls().join(args);
	}

	public String getDataString() {
		return new String(data, Charsets.ISO_8859_1);
	}
}
