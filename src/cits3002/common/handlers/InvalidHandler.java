package cits3002.common.handlers;

import cits3002.common.messages.Message;
import cits3002.common.messages.MessageType;
import cits3002.common.messages.MessageUtil;

/**
 * Handler for invalid requests.
 */
public class InvalidHandler implements Handler {
	/**
	 * Construct a new INVALID handler.
	 */
	public InvalidHandler() {
	}

	/**
	 * Handle the request.
	 *
	 * @return The response message
	 */
	@Override
	public Message execute() {
		return MessageUtil.createMessage(MessageType.INVALID, "Unknown command.");
	}
}
