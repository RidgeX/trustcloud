package cits3002.server.commands;

import cits3002.util.CommandUtil;

public class UnsupportedCommand implements Command {
	@Override public byte[] execute() throws Exception {
		return CommandUtil.serialiseCommand("FAL", "No such command or malformed command.");
	}
}
