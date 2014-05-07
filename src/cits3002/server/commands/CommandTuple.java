package cits3002.server.commands;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;

public class CommandTuple {
	public final String[] args;
	public final byte[] data;

	public CommandTuple(String[] args, byte[] data) {
		Preconditions.checkNotNull(args);
		Preconditions.checkNotNull(data);

		this.args = args;
		this.data = data;
	}

	public String getArgumentString() {
		return Joiner.on(' ').join(args);
	}
}
