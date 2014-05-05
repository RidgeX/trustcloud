package cits3002.server.commands;

import java.security.Signature;

import static cits3002.util.CommandUtil.makeCommandString;

public class VerifyCommand implements Command {
	private final String filename;
	private final Signature signature;

	public VerifyCommand(String filename, Signature signature) {
		this.filename = filename;
		this.signature = signature;
	}

	@Override public String execute() {
		return makeCommandString("FAL", "Verify command not yet implemented.");
	}
}
