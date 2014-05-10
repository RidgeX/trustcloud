package cits3002.common.handlers;

import cits3002.common.CommandHandler;
import cits3002.common.Message;
import cits3002.common.SecurityUtil;
import cits3002.server.NamespaceLayer;
import com.google.common.base.Preconditions;

public class HashHandler extends CommandHandler {
	private final String fileName;

	public HashHandler(String fileName) {
		Preconditions.checkNotNull(fileName);
		this.fileName = fileName.replaceAll(ILLEGAL_CHARS, "");
	}

	@Override
	public Message execute() {
		try {
			byte[] hash = SecurityUtil.makeHash(NamespaceLayer.readFile(fileName));
			return new Message(RESULT_OK, hash);
		} catch (Exception e) {
			e.printStackTrace();
			return new Message(RESULT_FAIL, "Couldn't hash file.");
		}
	}
}
