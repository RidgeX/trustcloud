package cits3002.server.commands;

import cits3002.server.NamespaceLayer;
import cits3002.util.CommandUtil;
import cits3002.util.SecurityUtil;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;

import java.security.cert.X509Certificate;

public class CertificateCommand implements Command {
	private final String filename;
	private final byte[] data;
	private final NamespaceLayer namespaceLayer;

	public CertificateCommand(String filename, byte[] data) {
		Preconditions.checkNotNull(filename);
		Preconditions.checkNotNull(data);

		this.filename = filename;
		this.data = data;
		this.namespaceLayer = new NamespaceLayer();
	}

	@Override public byte[] execute() throws Exception {
		try {
			X509Certificate certificate = SecurityUtil.loadAndVerifyCertificate(
					new String(data, Charsets.ISO_8859_1));
			namespaceLayer.writeFile(filename, data, true);

			return CommandUtil.serialiseCommand("SUC", "Certificate verified");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return CommandUtil.serialiseCommand("FAL", "Could not verify certificate");
	}
}
