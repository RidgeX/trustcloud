package cits3002.common.handlers;

import cits3002.common.messages.Message;
import cits3002.common.messages.MessageType;
import cits3002.common.messages.MessageUtil;
import cits3002.server.NamespaceLayer;

public class ListHandler implements Handler {
	public ListHandler() {
	}

	@Override
	public Message execute() {
		try {
			StringBuilder builder = new StringBuilder();
			for (String file : NamespaceLayer.listFiles()) {
				builder.append(NamespaceLayer.describeFile(file));
				builder.append("\n");
			}
			return MessageUtil.createMessage(MessageType.OK, builder.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return MessageUtil.createMessage(MessageType.FAIL, "Couldn't list files.");
	}
}
