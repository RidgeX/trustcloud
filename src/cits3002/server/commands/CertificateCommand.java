package cits3002.server.commands;

import cits3002.util.CommandUtil;
import com.google.common.base.Preconditions;

import java.security.cert.X509Certificate;

public class CertificateCommand implements Command {
	private final String filename;
	private final X509Certificate certificate;

	public CertificateCommand(String filename, X509Certificate certificate) {
		Preconditions.checkNotNull(filename);
		Preconditions.checkNotNull(certificate);

		this.filename = filename;
		this.certificate = certificate;
	}

	@Override public byte[] execute() throws Exception {
		return CommandUtil
				.makeCommand("FAL", "Certificate command not yet implemented.");
	}
}
