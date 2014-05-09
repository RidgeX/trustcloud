package cits3002.server.commands;

import cits3002.server.NamespaceLayer;
import cits3002.util.CommandUtil;
import com.google.common.base.Preconditions;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.PEMParser;

import java.io.StringReader;
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
			PEMParser parser = new PEMParser(new StringReader(new String(data, "ISO-8859-1")));
			X509CertificateHolder obj = (X509CertificateHolder) parser.readObject();
			X509Certificate certificate =
					new JcaX509CertificateConverter().setProvider("BC").getCertificate(obj);
			certificate.checkValidity();
			certificate.verify(certificate.getPublicKey());
			namespaceLayer.writeFile(filename, data, true);
			
			return CommandUtil.serialiseCommand("SUC", "Certificate verified");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return CommandUtil.serialiseCommand("FAL", "Could not verify certificate");
	}
}
