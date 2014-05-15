package cits3002.common.handlers;

import cits3002.common.messages.Message;
import cits3002.common.messages.MessageType;
import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedInts;

public class HandlerFactory {
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

	private Handler createGetHandler(Message message) {
		Preconditions.checkArgument(message.args.length == 2);

		String filename = message.args[0];
		int requiredCircumference = UnsignedInts.parseUnsignedInt(message.args[1]);
		return new GetHandler(filename, requiredCircumference);
	}

	private Handler createHashHandler(Message message) {
		Preconditions.checkArgument(message.args.length == 1);

		String filename = message.args[0];
		return new HashHandler(filename);
	}

	private Handler createListHandler(Message message) {
		return new ListHandler();
	}

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

	private Handler createVouchHandler(Message message) {
		Preconditions.checkArgument(message.args.length == 1);

		String filename = message.args[0];
		return new VouchHandler(filename, message.data);
	}

}
