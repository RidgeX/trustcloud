package cits3002.server.commands;

import cits3002.server.NamespaceLayer;
import cits3002.util.CommandUtil;
import com.google.common.base.Preconditions;

import java.io.IOException;

public class FileCommand implements Command {
	private final String filename;
	private final byte[] data;

	public FileCommand(String filename, byte[] data) {
		Preconditions.checkNotNull(filename);
		Preconditions.checkNotNull(data);

		this.filename = filename;
		this.data = data;
	}

	@Override public byte[] execute() throws Exception {
		try {
			// TODO: Revoke signatures on file overwrite/delete.
			new NamespaceLayer().writeFile(filename, data, false);
			return CommandUtil.serialiseCommand("SUC", "File " + filename + " created.");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return CommandUtil.serialiseCommand("FAL", "Could not create file");
	}
}
