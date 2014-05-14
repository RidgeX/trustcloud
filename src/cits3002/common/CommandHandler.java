package cits3002.common;

import cits3002.common.handlers.*;
import com.google.common.base.Preconditions;

public abstract class CommandHandler {
	public static final String ILLEGAL_CHARS = "\\/:*?\"<>|";
	public static final String RESULT_OK = "OK";
	public static final String RESULT_FAIL = "FAIL";

	public abstract Message execute();

	public static CommandHandler getHandler(Message msg) {
		String fileName, certName;
		int isCert;

		Preconditions.checkArgument(msg.args.length >= 1);
		Command cmd = Command.lookup.get(msg.args[0]);
		switch (cmd) {
			case GET:
				Preconditions.checkArgument(msg.args.length == 3);
				fileName = msg.args[1];
				int minRingLength = Integer.parseInt(msg.args[2]);
				return new GetHandler(fileName, minRingLength);

			case HASH:
				Preconditions.checkArgument(msg.args.length == 2);
				fileName = msg.args[1];
				return new HashHandler(fileName);

			case LIST:
				return new ListHandler();

			case PUT:
				Preconditions.checkArgument(msg.args.length == 3);
				fileName = msg.args[1];
				isCert = Integer.parseInt(msg.args[2]);
				return new PutHandler(fileName, isCert != 0, msg.data);

			case VOUCH:
				Preconditions.checkArgument(msg.args.length == 3);
				fileName = msg.args[1];
				certName = msg.args[2];
				return new VouchHandler(fileName, certName, msg.data);
		}

		return new NullHandler();
	}
}
