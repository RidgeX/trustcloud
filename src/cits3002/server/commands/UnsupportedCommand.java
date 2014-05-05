package cits3002.server.commands;

import static cits3002.util.CommandUtil.makeCommandString;

public class UnsupportedCommand implements Command {
	@Override public String execute() {
		return makeCommandString("FAL", "No such command or malformed command.");
	}
}
