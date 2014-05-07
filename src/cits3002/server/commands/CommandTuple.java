package cits3002.server.commands;

import com.google.common.base.Preconditions;

public class CommandTuple {
	public final String commandString;
	public final byte[] binaryData;

	public CommandTuple(String commandString, byte[] binaryData) {
		Preconditions.checkNotNull(commandString);
		Preconditions.checkNotNull(binaryData);

		this.commandString = commandString;
		this.binaryData = binaryData;
	}
}
