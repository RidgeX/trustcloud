package cits3002.server.commands;

import cits3002.server.NamespaceLayer;
import cits3002.util.CommandUtil;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashCommand implements Command {
	private final String filename;
	private final MessageDigest digestInstance;
	private final NamespaceLayer namespaceLayer;


	public HashCommand(String filename) throws NoSuchAlgorithmException {
		Preconditions.checkNotNull(filename);
		System.out.println(filename);

		this.filename = filename;
		this.digestInstance = MessageDigest.getInstance("SHA-1");
		this.namespaceLayer = new NamespaceLayer();
	}

	@Override public byte[] execute() throws Exception {
		try {
			return CommandUtil.serialiseCommand(
					"SUC",
					digestInstance.digest(namespaceLayer.readFile(filename)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return CommandUtil.serialiseCommand("FAL", "Could not hash file");
	}
}
