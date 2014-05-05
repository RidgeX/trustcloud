package cits3002.util;

import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

import java.io.File;
import java.io.FileReader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Scanner;

public class KeyLoader {
	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static void main(String[] args) {
		// http://www.hypersocket.com/content/?p=90

		try {
			File keyFile = new File("res/test.key");
			PEMParser parser = new PEMParser(new FileReader(keyFile));
			Object pairObject = parser.readObject();

			if (pairObject instanceof PEMEncryptedKeyPair) {
				System.out.print("Enter password: ");
				Scanner input = new Scanner(System.in);
				String password = input.nextLine();
				System.out.println();
				pairObject = ((PEMEncryptedKeyPair) pairObject)
						.decryptKeyPair(new JcePEMDecryptorProviderBuilder().build(password.toCharArray()));
			}

			PEMKeyPair pair = (PEMKeyPair) pairObject;

			byte[] encodedPublicKey = pair.getPublicKeyInfo().getEncoded();
			byte[] encodedPrivateKey = pair.getPrivateKeyInfo().getEncoded();

			KeyFactory keyFactory = KeyFactory.getInstance("RSA");

			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
			PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

			PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
			PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

			KeyPair keyPair = new KeyPair(publicKey, privateKey);
			System.out.println(keyPair.toString());
			System.out.println(publicKey.getFormat());
			System.out.println(privateKey.getFormat());

			File certFile = new File("res/test.crt");
			parser = new PEMParser(new FileReader(certFile));
			X509CertificateHolder obj = (X509CertificateHolder) parser.readObject();
			Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(obj);
			System.out.println(cert);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
