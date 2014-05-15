package cits3002.util;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.FileWriter;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Scanner;

/**
 * A utility program for creating certificate and key pairs.
 */
public class KeyCreator {
	/**
	 * The number of milliseconds in a day.
	 */
	private static final long MILLISECS_PER_DAY = 24 * 60 * 60 * 1000L;

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * Main method.
	 */
	public static void main(String[] args) throws Exception {
		Scanner input = new Scanner(System.in);
		System.out.print("Enter username: ");
		String username = input.nextLine();
		input.close();

		Date validityBeginDate = new Date(System.currentTimeMillis() - MILLISECS_PER_DAY);
		Date validityEndDate = new Date(System.currentTimeMillis() + 2 * 365 * MILLISECS_PER_DAY);

		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA", "BC");
		keyPairGenerator.initialize(1024, new SecureRandom());

		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();

		X500Name dnName =
				new X500Name(String.format("CN=%s, O=Trustcloud, L=Australia, C=AU", username));
		BigInteger sn = BigInteger.valueOf(System.currentTimeMillis());
		SubjectPublicKeyInfo keyInfo = SubjectPublicKeyInfo.getInstance(publicKey.getEncoded());
		X509v3CertificateBuilder certGen =
				new X509v3CertificateBuilder(dnName, sn, validityBeginDate, validityEndDate, dnName,
						keyInfo);
		ContentSigner sigGen =
				new JcaContentSignerBuilder("SHA1WithRSAEncryption").setProvider("BC").build(privateKey);
		X509Certificate certificate =
				new JcaX509CertificateConverter().setProvider("BC").getCertificate(certGen.build(sigGen));
		certificate.checkValidity();
		certificate.verify(publicKey);

		PEMWriter certWriter = new PEMWriter(new FileWriter(username + ".crt"));
		certWriter.writeObject(certificate);
		certWriter.close();

		PEMWriter keyWriter = new PEMWriter(new FileWriter(username + ".crt.key"));
		keyWriter.writeObject(privateKey);
		keyWriter.close();
	}
}
