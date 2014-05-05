package cits3002.server.commands;

import java.security.cert.X509Certificate;

import static cits3002.util.CommandUtil.makeCommandString;

public class CertificateCommand implements Command {
	private final String filename;
	private final X509Certificate certificate;

	public CertificateCommand(String filename, X509Certificate certificate) {
		this.filename = filename;
		this.certificate = certificate;
	}

	@Override public String execute() {
		return makeCommandString("FAL", "Certificate command not yet implemented.");
	}
}
