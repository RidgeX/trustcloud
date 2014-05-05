package cits3002.server.commands;

import static cits3002.util.CommandUtil.makeCommandString;

public class FetchCommand implements Command {
	private final String filename;
	private final int circumference;

	public FetchCommand(String filename, int circumference) {
		this.filename = filename;
		this.circumference = circumference;
	}

	@Override public String execute() {
		return makeCommandString("FAL", "Fetch command not yet implemented.");
	}
}
