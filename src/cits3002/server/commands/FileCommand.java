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
			new NamespaceLayer().writeFile(filename, data, false);
		} catch (IOException e) {
			e.printStackTrace();
			return CommandUtil.makeCommand("FAL", "Could not create file.");
		}
		return CommandUtil.makeCommand("SUC", "File " + filename + " created.");
	}
}
