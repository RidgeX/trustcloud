package cits3002.server.commands;

import cits3002.util.CommandUtil;
import com.google.common.base.Preconditions;

import java.security.Signature;

public class VerifyCommand implements Command {
	private final String filename;
	private final Signature signature;

	public VerifyCommand(String filename, Signature signature) {
		Preconditions.checkNotNull(filename);
		Preconditions.checkNotNull(signature);

		this.filename = filename;
		this.signature = signature;
	}

	@Override public byte[] execute() throws Exception {
		return CommandUtil.serialiseCommand("FAL", "Verify command not yet implemented.");
	}
}
