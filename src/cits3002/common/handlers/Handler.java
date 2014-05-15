package cits3002.common.handlers;

import cits3002.common.messages.Message;

/**
 * The interface for all message handlers.
 */
public interface Handler {
	/**
	 * Handle the request.
	 *
	 * @return The response message
	 */
	public Message execute();
}
