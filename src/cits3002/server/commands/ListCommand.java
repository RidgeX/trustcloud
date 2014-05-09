package cits3002.server.commands;

import cits3002.server.NamespaceLayer;
import cits3002.util.CommandUtil;

import java.util.List;

public class ListCommand implements Command {
	private final NamespaceLayer namespaceLayer;

	public ListCommand() {
		this.namespaceLayer = new NamespaceLayer();
	}

	@Override public byte[] execute() throws Exception {
		try {
			List<String> files = namespaceLayer.getFileList();
			StringBuilder builder = new StringBuilder();
			for (String f : files) {
				builder.append(namespaceLayer.describeFile(f));
				builder.append("\n");
			}
			return CommandUtil.serialiseCommand("SUC", builder.toString());		} catch (Exception e) {
			e.printStackTrace();
		}

		return CommandUtil.serialiseCommand("FAL", "Could not list files");
	}
}
