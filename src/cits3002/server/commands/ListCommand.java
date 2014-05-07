package cits3002.server.commands;

import cits3002.util.CommandUtil;

public class ListCommand implements Command {
	@Override public byte[] execute() throws Exception {
		return CommandUtil.makeCommand("FAL", "List command not yet implemented.");
	}
}
