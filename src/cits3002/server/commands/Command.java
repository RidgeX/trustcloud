package cits3002.server.commands;

public interface Command {
	byte[] execute() throws Exception;
}
