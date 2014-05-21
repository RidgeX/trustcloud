package cits3002.common.handlers;

import cits3002.common.RingVerifier;
import cits3002.common.messages.Message;
import cits3002.common.messages.MessageType;
import cits3002.common.messages.MessageUtil;
import cits3002.server.NamespaceLayer;
import com.google.common.base.Preconditions;

/**
 * Handler for fetching a file.
 */
public class GetHandler implements Handler {
	private final String filename;
	private final int minimumRingLength;

	/**
	 * Constuct a new GET handler.
	 *
	 * @param filename          The name of the file to fetch
	 * @param minimumRingLength The required ring length
	 */
	public GetHandler(String filename, int minimumRingLength) {
		Preconditions.checkNotNull(filename);
		Preconditions.checkArgument(NamespaceLayer.isValidFilename(filename));
		this.filename = filename;
		this.minimumRingLength = minimumRingLength;
	}

	/**
	 * Handle the request.
	 *
	 * @return The response message
	 */
	@Override
	public Message execute() {
		try {
			RingVerifier verifier = new RingVerifier(filename);
			if (!verifier.hasRingOfSufficientLength(minimumRingLength)) {
				return MessageUtil
						.createMessage(MessageType.FAIL, "Minimum trust requirement not met.");
			}
			byte[] data = NamespaceLayer.readFile(filename);
			return MessageUtil.createMessage(MessageType.OK, data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return MessageUtil.createMessage(MessageType.FAIL, "Couldn't fetch file.");
	}
}
