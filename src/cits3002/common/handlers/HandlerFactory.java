package cits3002.common.handlers;

import cits3002.common.messages.Message;
import cits3002.common.messages.MessageType;
import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedInts;

/**
 * Factory for constructing message handlers.
 */
public class HandlerFactory {
	/**
	 * Return a new handler for the current message.
	 *
	 * @param message The message to be handled
	 * @return The message handler
	 */
	public Handler getHandlerForMessage(Message message) {
		MessageType cmd = message.type;
		switch (cmd) {
			case GET:
				return createGetHandler(message);

			case HASH:
				return createHashHandler(message);

			case LIST:
				return createListHandler(message);

			case PUT:
				return createPutHandler(message);

			case VOUCH:
				return createVouchHandler(message);
		}

		return new InvalidHandler();
	}

	/**
	 * Create a new GET handler.
	 *
	 * @param message The request message
	 * @return The message handler
	 */
	private Handler createGetHandler(Message message) {
		Preconditions.checkArgument(message.args.length == 2);

		String filename = message.args[0];
		int requiredCircumference = UnsignedInts.parseUnsignedInt(message.args[1]);
		return new GetHandler(filename, requiredCircumference);
	}

	/**
	 * Create a new HASH handler.
	 *
	 * @param message The request message
	 * @return The message handler
	 */
	private Handler createHashHandler(Message message) {
		Preconditions.checkArgument(message.args.length == 1);

		String filename = message.args[0];
		return new HashHandler(filename);
	}

	/**
	 * Create a new LIST handler.
	 *
	 * @param message The request message
	 * @return The message handler
	 */
	private Handler createListHandler(Message message) {
		return new ListHandler();
	}

	/**
	 * Create a new PUT handler.
	 *
	 * @param message The request message
	 * @return The message handler
	 */
	private Handler createPutHandler(Message message) {
		Preconditions.checkArgument(message.args.length == 2);

		String filename = message.args[0];
		String typeIndicator = message.args[1];
		boolean isCertificate = false;
		if (typeIndicator.equals("C")) {
			isCertificate = true;
		} else if (!typeIndicator.equals("F")) {
			return new InvalidHandler();
		}
		return new PutHandler(filename, isCertificate, message.data);
	}

	/**
	 * Create a new VOUCH handler.
	 *
	 * @param message The request message
	 * @return The message handler
	 */
	private Handler createVouchHandler(Message message) {
		Preconditions.checkArgument(message.args.length == 1);

		String filename = message.args[0];
		return new VouchHandler(filename, message.data);
	}

}
