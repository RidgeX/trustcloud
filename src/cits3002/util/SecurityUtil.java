package cits3002.util;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.io.BaseEncoding;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;

import java.io.IOException;
import java.io.StringReader;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SecurityUtil {
	public static KeyPair loadKeyPair(String keyData, String password)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		PEMParser parser = new PEMParser(new StringReader(keyData));
		Object pairObject = parser.readObject();

		if (pairObject instanceof PEMEncryptedKeyPair) {
			Preconditions.checkNotNull(password);
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

		return new KeyPair(publicKey, privateKey);
	}

	public static PublicKey loadBase64PublicKey(String base64PublicKeyData)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				BaseEncoding.base64().decode(base64PublicKeyData));
		return keyFactory.generatePublic(publicKeySpec);
	}

	public static X509Certificate loadAndVerifyCertificate(String certData)
			throws CertificateException, NoSuchProviderException, NoSuchAlgorithmException,
			InvalidKeyException, SignatureException, IOException {
		PEMParser parser = new PEMParser(new StringReader(certData));
		X509CertificateHolder obj = (X509CertificateHolder) parser.readObject();
		X509Certificate certificate =
				new JcaX509CertificateConverter().setProvider("BC").getCertificate(obj);
		certificate.checkValidity();
		certificate.verify(certificate.getPublicKey());
		return certificate;
	}

	public static byte[] signData(byte[] data, KeyPair keyPair)
			throws InvalidKeyException, NoSuchProviderException, NoSuchAlgorithmException,
			SignatureException {
		Signature sig = Signature.getInstance("SHA1withRSA", "BC");
		sig.initSign(keyPair.getPrivate());
		sig.update(data);
		return sig.sign();
	}

	public static boolean verifyData(PublicKey pubKey, byte[] data, byte[] sigData)
			throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException,
			SignatureException {
		Signature sig = Signature.getInstance("SHA1withRSA", "BC");
		sig.initVerify(pubKey);
		sig.update(data);
		return sig.verify(sigData);
	}

	public static class UnpackedSignature {
		public PublicKey publicKey;
		public byte[] signatureData;

		public UnpackedSignature(PublicKey publicKey, byte[] signatureData) {
			this.publicKey = publicKey;
			this.signatureData = signatureData;
		}
	}

	public static UnpackedSignature unpackSignature(String line)
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		String[] args = Iterables.toArray(
				Splitter.on(' ').omitEmptyStrings().trimResults().split(line),
				String.class);
		Preconditions.checkArgument(args.length == 2);
		return new UnpackedSignature(
				SecurityUtil.loadBase64PublicKey(args[0]),
				BaseEncoding.base64().decode(args[1]));
	}

	public static UnpackedSignature unpackSignature(byte[] data)
			throws InvalidKeySpecException, NoSuchAlgorithmException {
		return unpackSignature(new String(data, Charsets.ISO_8859_1));
	}

	public static String packSignature(PublicKey publicKey, byte[] sigData) {
		return String.format(
				"%s %s",
				BaseEncoding.base64().encode(publicKey.getEncoded()),
				BaseEncoding.base64().encode(sigData));
	}
}
