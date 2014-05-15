package cits3002.common.handlers;

import cits3002.common.SecurityUtil;
import cits3002.common.messages.Message;
import cits3002.common.messages.MessageType;
import cits3002.common.messages.MessageUtil;
import cits3002.server.NamespaceLayer;
import com.google.common.base.Preconditions;

public class HashHandler implements Handler {
	private final String filename;

	public HashHandler(String filename) {
		Preconditions.checkNotNull(filename);
		Preconditions.checkArgument(NamespaceLayer.isValidFilename(filename));
		this.filename = filename;
	}

	@Override
	public Message execute() {
		try {
			byte[] hash = SecurityUtil.makeHash(NamespaceLayer.readFile(filename));
			return MessageUtil.createMessage(MessageType.OK, "", hash);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return MessageUtil.createMessage(MessageType.FAIL, "", "Couldn't hash file.");
	}
}
