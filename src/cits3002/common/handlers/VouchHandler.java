package cits3002.common.handlers;

import cits3002.common.SecurityUtil;
import cits3002.common.messages.Message;
import cits3002.common.messages.MessageType;
import cits3002.common.messages.MessageUtil;
import cits3002.server.NamespaceLayer;
import cits3002.server.TrustLayer;
import com.google.common.base.Preconditions;

/**
 * Handler for signing a file.
 */
public class VouchHandler implements Handler {
	private final String filename;
	private final byte[] data;

	/**
	 * Construct a new VOUCH handler.
	 *
	 * @param filename The file to vouch for
	 * @param data     The encoded public key and signature
	 */
	public VouchHandler(String filename, byte[] data) {
		Preconditions.checkNotNull(filename);
		Preconditions.checkNotNull(data);
		Preconditions.checkArgument(NamespaceLayer.isValidFilename(filename));
		this.filename = filename;
		this.data = data;
	}

	/**
	 * Handle the request.
	 *
	 * @return The response message
	 */
	@Override
	public Message execute() {
		try {
			SecurityUtil.UnpackedSignature unpackedSignature = SecurityUtil.unpackSignature(data);
			if (TrustLayer.addSignatureForFile(filename, unpackedSignature)) {
				return MessageUtil.createMessage(MessageType.OK, "", "File signed.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return MessageUtil.createMessage(MessageType.FAIL, "", "Could not sign file.");
	}
}
