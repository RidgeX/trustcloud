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
	private final String base64PublicKey;
	private final String base64SignatureData;

	/**
	 * Construct a new VOUCH handler.
	 *
	 * @param filename            The file to vouch for
	 * @param base64PublicKey     The base64 public key
	 * @param base64SignatureData The base64 signature data
	 */
	public VouchHandler(String filename, String base64PublicKey, String base64SignatureData) {
		Preconditions.checkNotNull(filename);
		Preconditions.checkNotNull(base64PublicKey);
		Preconditions.checkNotNull(base64SignatureData);
		Preconditions.checkArgument(NamespaceLayer.isValidFilename(filename));
		this.filename = filename;
		this.base64PublicKey = base64PublicKey;
		this.base64SignatureData = base64SignatureData;
	}

	/**
	 * Handle the request.
	 *
	 * @return The response message
	 */
	@Override
	public Message execute() {
		try {
			SecurityUtil.SignaturePair signaturePair =
					new SecurityUtil.SignaturePair(base64PublicKey, base64SignatureData);
			if (TrustLayer.hasSignatureForFile(filename, signaturePair)) {
				return MessageUtil.createMessage(MessageType.FAIL, "File already signed.");
			}
			if (TrustLayer.addSignatureForFile(filename, signaturePair)) {
				return MessageUtil.createMessage(MessageType.OK, "File signed.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return MessageUtil.createMessage(MessageType.FAIL, "Could not sign file.");
	}
}
