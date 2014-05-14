package cits3002.common.handlers;

import cits3002.common.CommandHandler;
import cits3002.common.Message;

public class NullHandler extends CommandHandler {
	public NullHandler() {
	}

	@Override
	public Message execute() {
		return new Message(RESULT_FAIL, "Unknown command.");
	}
}
