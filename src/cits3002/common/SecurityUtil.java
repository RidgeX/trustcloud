package cits3002.common;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.BaseEncoding;
import java.io.StringReader;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

public class SecurityUtil {
	public static X509Certificate loadCertificate(byte[] certData) throws Exception {
		StringReader in = new StringReader(new String(certData, Charsets.ISO_8859_1));
		PEMParser parser = new PEMParser(in);
		X509CertificateHolder certObj = (X509CertificateHolder) parser.readObject();
		parser.close();
		X509Certificate cert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(certObj);
		return cert;
	}

	public static boolean checkCertificate(X509Certificate cert) {
		try {
			cert.checkValidity();
			cert.verify(cert.getPublicKey());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static KeyPair loadKeyPair(byte[] keyData) throws Exception {
		return loadKeyPair(keyData, null);
	}

	public static KeyPair loadKeyPair(byte[] keyData, String password) throws Exception {
		StringReader in = new StringReader(new String(keyData, Charsets.ISO_8859_1));
		PEMParser parser = new PEMParser(in);
		Object pairObj = parser.readObject();
		parser.close();

		if (pairObj instanceof PEMEncryptedKeyPair) {
			Preconditions.checkNotNull(password);
			pairObj = ((PEMEncryptedKeyPair) pairObj).decryptKeyPair(new JcePEMDecryptorProviderBuilder().build(password.toCharArray()));
		}

		PEMKeyPair pair = (PEMKeyPair) pairObj;
		byte[] encodedPublicKey = pair.getPublicKeyInfo().getEncoded();
		byte[] encodedPrivateKey = pair.getPrivateKeyInfo().getEncoded();

		KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

		return new KeyPair(publicKey, privateKey);
	}

	public static byte[] decodeSignature(String sig) {
		return BaseEncoding.base64().decode(sig);
	}

	public static String encodeSignature(byte[] sigData) {
		return BaseEncoding.base64().encode(sigData);
	}

	public static byte[] makeSignature(PrivateKey privateKey, byte[] hash) throws Exception {
		Signature sig = Signature.getInstance("SHA1withRSA", "BC");
		sig.initSign(privateKey);
		sig.update(hash);
		return sig.sign();
	}

	public static boolean checkSignature(PublicKey publicKey, byte[] hash, byte[] sigData) {
		try {
			Signature sig = Signature.getInstance("SHA1withRSA", "BC");
			sig.initVerify(publicKey);
			sig.update(hash);
			return sig.verify(sigData);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static byte[] makeHash(byte[] fileData) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		return digest.digest(fileData);
	}
}
