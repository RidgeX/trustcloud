package cits3002.server.commands;

import cits3002.server.NamespaceLayer;
import cits3002.util.CommandUtil;

import java.util.List;

public class ListCommand implements Command {
	private NamespaceLayer namespaceLayer;

	public ListCommand() {
		this.namespaceLayer = new NamespaceLayer();
	}

	@Override public byte[] execute() throws Exception {
		List<String> files = namespaceLayer.getFileList();
		StringBuilder builder = new StringBuilder();
		for (String f : files) {
			builder.append(namespaceLayer.describeFile(f));
			builder.append("\n");
		}
		return CommandUtil.serialiseCommand("SUC", builder.toString());
	}
}
