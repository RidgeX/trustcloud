package cits3002.common.handlers;

import cits3002.common.CommandHandler;
import cits3002.common.Message;
import cits3002.common.SecurityUtil;
import cits3002.server.NamespaceLayer;
import cits3002.server.TrustLayer;
import com.google.common.base.Preconditions;

import java.security.cert.X509Certificate;

public class VouchHandler extends CommandHandler {
	private final String fileName;
	private final String certName;
	private final byte[] data;

	public VouchHandler(String fileName, String certName, byte[] data) {
		Preconditions.checkNotNull(fileName);
		Preconditions.checkNotNull(certName);
		Preconditions.checkNotNull(data);
		this.fileName = fileName.replaceAll(ILLEGAL_CHARS, "");
		this.certName = certName.replaceAll(ILLEGAL_CHARS, "");
		this.data = data;
	}

	@Override
	public Message execute() {
		try {
			byte[] hash = SecurityUtil.makeHash(NamespaceLayer.readFile(fileName));
			byte[] certData = NamespaceLayer.readFile(certName);
			X509Certificate cert = SecurityUtil.loadCertificate(certData);
			if (!SecurityUtil.checkSignature(cert.getPublicKey(), hash, data)) {
				return new Message(RESULT_FAIL, "Invalid signature.");
			}
			if (!TrustLayer.addSignature(fileName, certName, data)) {
				return new Message(RESULT_FAIL, "File already signed.");
			}
			return new Message(RESULT_OK,
					String.format("File '%s' was signed using '%s'.", fileName, certName));
		} catch (Exception e) {
			e.printStackTrace();
			return new Message(RESULT_FAIL, "Couldn't sign file.");
		}
	}
}
