package cits3002.common.handlers;

import cits3002.common.messages.Message;
import cits3002.common.messages.MessageType;
import cits3002.common.messages.MessageUtil;

public class InvalidHandler implements Handler {
	public InvalidHandler() {
	}

	@Override
	public Message execute() {
		return MessageUtil.createMessage(MessageType.FAIL, "Unknown command.");
	}
}
