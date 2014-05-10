package cits3002.common.handlers;

import cits3002.common.CommandHandler;
import cits3002.common.Message;
import cits3002.server.NamespaceLayer;
import com.google.common.base.Preconditions;

public class GetHandler extends CommandHandler {
	private final String fileName;
	private final int minRingLength;

	public GetHandler(String fileName, int minRingLength) {
		Preconditions.checkNotNull(fileName);
		this.fileName = fileName.replaceAll(ILLEGAL_CHARS, "");
		this.minRingLength = minRingLength;
	}

	@Override
	public Message execute() {
		try {
			// TODO: Check circumference once signing is implemented.
			byte[] data = NamespaceLayer.readFile(fileName);
			return new Message(RESULT_OK, data);
		} catch (Exception e) {
			e.printStackTrace();
			return new Message(RESULT_FAIL, "Couldn't fetch file.");
		}
	}
}
