package cits3002.common;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

public class Message {
	public final String[] args;
	public final byte[] data;

	public Message(String argsString) {
		this(argsString, new byte[0]);
	}

	public Message(String argsString, String dataString) {
		this(argsString, dataString.getBytes(Charsets.ISO_8859_1));
	}

	public Message(String argsString, byte[] data) {
		Preconditions.checkNotNull(argsString);
		Preconditions.checkNotNull(data);
		this.args = argsString.split("\\|");
		this.data = data;
	}

	public String getArgsString() {
		return Joiner.on(' ').join(args);
	}

	public String getDataString() {
		return new String(data, Charsets.ISO_8859_1);
	}
}
