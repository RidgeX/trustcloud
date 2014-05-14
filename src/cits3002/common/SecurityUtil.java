package cits3002.common;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
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
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class SecurityUtil {
	public static X509Certificate loadCertificate(byte[] certData) throws Exception {
		StringReader in = new StringReader(new String(certData, Charsets.ISO_8859_1));
		PEMParser parser = new PEMParser(in);
		X509CertificateHolder certObj = (X509CertificateHolder) parser.readObject();
		parser.close();
		return new JcaX509CertificateConverter().setProvider("BC").getCertificate(certObj);
	}

	public static void checkCertificate(X509Certificate certificate) throws Exception {
		certificate.checkValidity();
		certificate.verify(certificate.getPublicKey());
	}

	public static KeyPair loadKeyPair(byte[] keyData) throws Exception {
		return loadKeyPair(keyData, null);
	}

	public static KeyPair loadKeyPair(byte[] keyData, String password)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		PEMParser parser = new PEMParser(new StringReader(new String(keyData, Charsets.ISO_8859_1)));
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

	public static byte[] makeHash(byte[] fileData) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		return digest.digest(fileData);
	}

	public static byte[] signData(byte[] data, PrivateKey privateKey)
			throws InvalidKeyException, NoSuchProviderException, NoSuchAlgorithmException,
			SignatureException {
		Signature sig = Signature.getInstance("SHA1withRSA", "BC");
		sig.initSign(privateKey);
		sig.update(data);
		return sig.sign();
	}

	public static boolean verifyData(UnpackedSignature unpacked, byte[] data)
			throws NoSuchProviderException, NoSuchAlgorithmException, InvalidKeyException,
			SignatureException {
		Signature sig = Signature.getInstance("SHA1withRSA", "BC");
		sig.initVerify(unpacked.publicKey);
		sig.update(data);
		return sig.verify(unpacked.signatureData);
	}

	public static class UnpackedSignature {
		public PublicKey publicKey;
		public byte[] signatureData;

		public UnpackedSignature(PublicKey publicKey, byte[] signatureData) {
			Preconditions.checkNotNull(publicKey);
			Preconditions.checkNotNull(signatureData);
			this.publicKey = publicKey;
			this.signatureData = signatureData;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			UnpackedSignature that = (UnpackedSignature) o;

			if (!publicKey.equals(that.publicKey)) {
				return false;
			}
			if (!Arrays.equals(signatureData, that.signatureData)) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(publicKey, signatureData);
		}
	}

	public static PublicKey loadBase64PublicKey(String base64PublicKeyData)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				BaseEncoding.base64().decode(base64PublicKeyData));
		return keyFactory.generatePublic(publicKeySpec);
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

	public static String packSignature(UnpackedSignature unpacked) {
		return String.format(
				"%s %s",
				BaseEncoding.base64().encode(unpacked.publicKey.getEncoded()),
				BaseEncoding.base64().encode(unpacked.signatureData));
	}
}
