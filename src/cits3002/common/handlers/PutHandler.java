package cits3002.common.handlers;

import cits3002.common.CommandHandler;
import cits3002.common.Message;
import cits3002.common.SecurityUtil;
import cits3002.server.NamespaceLayer;
import cits3002.server.TrustLayer;
import com.google.common.base.Preconditions;

import java.security.cert.X509Certificate;

public class PutHandler extends CommandHandler {
	private final String fileName;
	private final boolean isCert;
	private final byte[] data;

	public PutHandler(String fileName, boolean isCert, byte[] data) {
		Preconditions.checkNotNull(fileName);
		Preconditions.checkNotNull(data);
		this.fileName = fileName.replaceAll(ILLEGAL_CHARS, "");
		this.isCert = isCert;
		this.data = data;
	}

	@Override
	public Message execute() {
		try {
			if (isCert) {
				X509Certificate cert = SecurityUtil.loadCertificate(data);
				if (!SecurityUtil.checkCertificate(cert)) {
					return new Message(RESULT_FAIL, "Invalid certificate.");
				}
			}
			NamespaceLayer.deleteFile(fileName);
			TrustLayer.clearSignatures(fileName);
			NamespaceLayer.writeFile(fileName, data, isCert);
			return new Message(RESULT_OK, String.format("File '%s' created.", fileName));
		} catch (Exception e) {
			e.printStackTrace();
			return new Message(RESULT_FAIL, "Couldn't create file.");
		}
	}
}
