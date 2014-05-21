package cits3002.common;

import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
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

/**
 * A utility class for handling certificates, keys and signatures.
 */
public class SecurityUtil {
	/**
	 * Load a PEM-encoded X509 certificate from a byte array.
	 *
	 * @param certData The certificate bytes
	 * @return The certificate object
	 */
	public static X509Certificate loadCertificate(byte[] certData) throws Exception {
		StringReader in = new StringReader(new String(certData, Charsets.ISO_8859_1));
		PEMParser parser = new PEMParser(in);
		X509CertificateHolder certObj = (X509CertificateHolder) parser.readObject();
		parser.close();
		return new JcaX509CertificateConverter().setProvider("BC").getCertificate(certObj);
	}

	/**
	 * Check the validity of a certificate.
	 *
	 * @param certificate The certificate object
	 * @throws Exception if the certificate is expired or its signature is invalid
	 */
	public static void checkCertificate(X509Certificate certificate) throws Exception {
		certificate.checkValidity();
		certificate.verify(certificate.getPublicKey());
	}

	/**
	 * Load a PEM-encoded PKCS#8 keypair from a byte array.
	 *
	 * @param keyData The keypair bytes
	 * @return The keypair object
	 */
	public static KeyPair loadKeyPair(byte[] keyData) throws Exception {
		return loadKeyPair(keyData, null);
	}

	/**
	 * Load a PEM-encoded PKCS#8 keypair from a byte array.
	 *
	 * @param keyData  The keypair bytes
	 * @param password The password to decrypt the keypair (if applicable)
	 * @return The keypair object
	 */
	public static KeyPair loadKeyPair(byte[] keyData, String password)
			throws IOException, NoSuchProviderException, NoSuchAlgorithmException,
			InvalidKeySpecException {
		PEMParser parser = new PEMParser(new StringReader(new String(keyData, Charsets.ISO_8859_1)));
		Object pairObject = parser.readObject();
		parser.close();

		if (pairObject instanceof PEMEncryptedKeyPair) {
			Preconditions.checkNotNull(password);
			pairObject = ((PEMEncryptedKeyPair) pairObject)
					.decryptKeyPair(new JcePEMDecryptorProviderBuilder().build(password.toCharArray()));
		}

		PEMKeyPair pair = (PEMKeyPair) pairObject;

		byte[] encodedPublicKey = pair.getPublicKeyInfo().getEncoded();
		byte[] encodedPrivateKey = pair.getPrivateKeyInfo().getEncoded();

		KeyFactory keyFactory = KeyFactory.getInstance("RSA", "BC");

		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

		return new KeyPair(publicKey, privateKey);
	}

	/**
	 * Generate a SHA-1 hash of the given data.
	 *
	 * @param data The data to be hashed
	 * @return The hash bytes
	 */
	public static byte[] makeHash(byte[] data) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("SHA-1");
		return digest.digest(data);
	}

	/**
	 * Generate a RSA signature of the given data.
	 *
	 * @param data       The data to be hashed and signed
	 * @param privateKey The private key to sign with
	 * @return The signature bytes
	 */
	public static byte[] signData(byte[] data, PrivateKey privateKey)
			throws InvalidKeyException, NoSuchProviderException, NoSuchAlgorithmException,
			SignatureException {
		Signature sig = Signature.getInstance("SHA1withRSA", "BC");
		sig.initSign(privateKey);
		sig.update(data);
		return sig.sign();
	}

	/**
	 * Verify a signature against the given data.
	 *
	 * @param signaturePair The signature and public key
	 * @param data          The data being vouched
	 * @return true if the signature is valid
	 */
	public static boolean verifyData(SignaturePair signaturePair, byte[] data)
			throws Exception {
		Signature sig = Signature.getInstance("SHA1withRSA", "BC");
		sig.initVerify(signaturePair.getPublicKey());
		sig.update(data);
		return sig.verify(signaturePair.signatureData);
	}

	/**
	 * An signature pair object, which bundles a public key with a signature.
	 */
	public static class SignaturePair {
		public byte[] publicKey;
		public byte[] signatureData;

		/**
		 * Create a new signature pair.
		 *
		 * @param base64PublicKey     The base64 public key data
		 * @param base64SignatureData The base64 signature data
		 */
		public SignaturePair(String base64PublicKey, String base64SignatureData) {
			Preconditions.checkNotNull(base64PublicKey);
			Preconditions.checkNotNull(base64SignatureData);
			this.publicKey = base64Decode(base64PublicKey);
			this.signatureData = base64Decode(base64SignatureData);
		}

		/**
		 * Parse and return the public key.
		 *
		 * @return The public key object
		 */
		public PublicKey getPublicKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
			return loadPublicKey(publicKey);
		}

		/**
		 * Compute and return the base64 public key.
		 *
		 * @return The base64 public key data
		 */
		public String getBase64PublicKey() {
			return base64Encode(publicKey);
		}

		/**
		 * Compute and return the base64 signature data.
		 *
		 * @return The base64 signature data
		 */
		public String getBase64SignatureData() {
			return base64Encode(signatureData);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}

			SignaturePair that = (SignaturePair) o;

			if (!Arrays.equals(publicKey, that.publicKey)) {
				return false;
			}
			if (!Arrays.equals(signatureData, that.signatureData)) {
				return false;
			}

			return true;
		}

		@Override
		public int hashCode() {
			return Objects.hashCode(Arrays.hashCode(publicKey), Arrays.hashCode(signatureData));
		}
	}

	/**
	 * Base64-encode the given data.
	 *
	 * @param data The data to be encoded
	 * @return The encoded data
	 */
	public static String base64Encode(byte[] data) {
		return BaseEncoding.base64().encode(data);
	}

	/**
	 * Base64-decode the given data.
	 *
	 * @param data The data to be decoded
	 * @return The decoded data
	 */
	public static byte[] base64Decode(String data) {
		return BaseEncoding.base64().decode(data);
	}

	/**
	 * Load an X509 public key from a byte array.
	 *
	 * @param data The public key bytes
	 * @return The public key object
	 */
	public static PublicKey loadPublicKey(byte[] data)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(data);
		return keyFactory.generatePublic(publicKeySpec);
	}
}
