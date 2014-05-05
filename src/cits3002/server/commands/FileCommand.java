package cits3002.server.commands;

import static cits3002.util.CommandUtil.makeCommandString;

public class FileCommand implements Command {
	private final String filename;
	private final String data;

	public FileCommand(String filename, String data) {
		this.filename = filename;
		this.data = data;
	}

	@Override public String execute() {
		return makeCommandString("FAL", "File command not yet implemented.");
	}
}
