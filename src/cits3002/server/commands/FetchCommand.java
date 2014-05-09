package cits3002.server.commands;

import cits3002.server.NamespaceLayer;
import cits3002.util.CommandUtil;
import com.google.common.base.Preconditions;

public class FetchCommand implements Command {
	private final String filename;
	private final int circumference;
	private final NamespaceLayer namespaceLayer;

	public FetchCommand(String filename, int circumference) {
		Preconditions.checkNotNull(filename);
		Preconditions.checkNotNull(circumference);

		this.filename = filename;
		this.circumference = circumference;
		this.namespaceLayer = new NamespaceLayer();
	}

	@Override public byte[] execute() throws Exception {
		// TODO: Check circumference once signing is implemented.
		try {
			return CommandUtil.serialiseCommand("SUC", namespaceLayer.readFile(filename));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return CommandUtil.serialiseCommand("FAL", "Could not fetch file");
	}
}
