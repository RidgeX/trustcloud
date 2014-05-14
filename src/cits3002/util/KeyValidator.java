package cits3002.util;

import cits3002.common.SecurityUtil;
import com.google.common.io.Files;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPrivateCrtKey;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.BCRSAPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.File;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Scanner;

public class KeyValidator {
	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static void main(String[] args) throws Exception {
		Scanner input = new Scanner(System.in);
		System.out.print("Enter username: ");
		String username = input.nextLine();
		input.close();

		File certFile = new File(username + ".crt");
		X509Certificate cert = SecurityUtil.loadCertificate(Files.toByteArray(certFile));
		File keyFile = new File(username + ".crt.key");
		KeyPair keyPair = SecurityUtil.loadKeyPair(Files.toByteArray(keyFile));

		try {
			if (!SecurityUtil.checkCertificate(cert)) {
				throw new SecurityException("Invalid certificate");
			}
			System.out.println(cert);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			BCRSAPublicKey publicKey = (BCRSAPublicKey) keyPair.getPublic();
			BigInteger modulus = publicKey.getModulus();
			BigInteger publicExponent = publicKey.getPublicExponent();
			BCRSAPrivateCrtKey privateKey = (BCRSAPrivateCrtKey) keyPair.getPrivate();
			if (!privateKey.getModulus().equals(modulus) || !privateKey.getPublicExponent()
					.equals(publicExponent)) {
				throw new SecurityException("Invalid key pair");
			}
			System.out.println(publicKey);
			System.out.println(privateKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
