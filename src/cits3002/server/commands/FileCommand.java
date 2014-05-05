package cits3002.server.commands;

import cits3002.server.NamespaceLayer;

import java.io.IOException;

import static cits3002.util.CommandUtil.makeCommandString;

public class FileCommand implements Command {
	private final String filename;
	private final String data;

	public FileCommand(String filename, String data) {
		this.filename = filename;
		this.data = data;
	}

	@Override public String execute() {
		try {
			new NamespaceLayer().writeFile(filename, data, false);
		} catch (IOException e) {
			e.printStackTrace();
			return makeCommandString("FAL", "Could not create file.");
		}
		return makeCommandString("SUC", "File " + filename + " created.");
	}
}
