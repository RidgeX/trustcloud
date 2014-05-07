package cits3002.server.commands;

import cits3002.util.CommandUtil;
import com.google.common.base.Preconditions;

public class FetchCommand implements Command {
	private final String filename;
	private final int circumference;

	public FetchCommand(String filename, int circumference) {
		Preconditions.checkNotNull(filename);
		Preconditions.checkNotNull(circumference);

		this.filename = filename;
		this.circumference = circumference;
	}

	@Override public byte[] execute() throws Exception {
		return CommandUtil.makeCommand("FAL", "Fetch command not yet implemented.");
	}
}
