package cits3002.common.handlers;

import cits3002.common.CommandHandler;
import cits3002.common.Message;
import cits3002.server.NamespaceLayer;
import java.io.File;
import java.util.List;

public class ListHandler extends CommandHandler {
	public ListHandler() {}

	@Override
	public Message execute() {
		try {
			StringBuilder sb = new StringBuilder();
			List<File> files = NamespaceLayer.listFiles();
			for (File f : files) {
				sb.append(NamespaceLayer.describeFile(f) + "\n");
			}
			return new Message(RESULT_OK, sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			return new Message(RESULT_FAIL, "Couldn't list files.");
		}
	}
}
