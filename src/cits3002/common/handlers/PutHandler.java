package cits3002.common.handlers;

import cits3002.common.SecurityUtil;
import cits3002.common.messages.Message;
import cits3002.common.messages.MessageType;
import cits3002.common.messages.MessageUtil;
import cits3002.server.NamespaceLayer;
import cits3002.server.TrustLayer;
import com.google.common.base.Preconditions;

import java.security.cert.X509Certificate;

/**
 * Handler for uploading a file or certificate.
 */
public class PutHandler implements Handler {
	private final String filename;
	private final boolean isCertificate;
	private final byte[] data;

	/**
	 * Construct a new PUT handler.
	 *
	 * @param filename      The name of the file to upload
	 * @param isCertificate Whether the file is a certificate
	 * @param data          The file data
	 */
	public PutHandler(String filename, boolean isCertificate, byte[] data) {
		Preconditions.checkNotNull(filename);
		Preconditions.checkNotNull(data);
		Preconditions.checkArgument(NamespaceLayer.isValidFilename(filename));
		this.filename = filename;
		this.isCertificate = isCertificate;
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
			NamespaceLayer.deleteFile(filename);
			TrustLayer.clearSignaturesForFile(filename);

			if (isCertificate) {
				X509Certificate certificate = SecurityUtil.loadCertificate(data);
				SecurityUtil.checkCertificate(certificate);
			}
			NamespaceLayer.writeFile(filename, data, isCertificate);

			return MessageUtil.createMessage(MessageType.OK, "File created.");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return MessageUtil.createMessage(MessageType.FAIL, "Couldn't create file.");
	}
}
