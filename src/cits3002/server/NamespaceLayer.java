package cits3002.server;

import cits3002.common.SecurityUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * A layer which manages file/certificate storage and querying.
 */
public class NamespaceLayer {
	/**
	 * A formatter for file modification times.
	 */
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");

	/**
	 * The directory for storing certificate files.
	 */
	private static final File CERTS_DIR = new File("ns/certs");

	/**
	 * The directory for storing normal files.
	 */
	private static final File FILES_DIR = new File("ns/files");

	/**
	 * A regex pattern for illegal filename character.
	 */
	private static final String ILLEGAL_CHARACTER_REGEX = "[\\/:*?\"'<>|]";

	/**
	 * A map of Base64-encoded public keys to certificate names.
	 */
	private static final Multimap<String, String> publicKeyToCertificates =
			MultimapBuilder.hashKeys().hashSetValues().build();

	static {
		// Create directories if necessary
		if (!CERTS_DIR.exists()) {
			CERTS_DIR.mkdirs();
		}
		if (!FILES_DIR.exists()) {
			FILES_DIR.mkdirs();
		}
	}

	/**
	 * Initialise this layer.
	 */
	public static void init() throws Exception {
		// Populate public key -> certificate name map
		for (File file : CERTS_DIR.listFiles()) {
			X509Certificate certificate = SecurityUtil.loadCertificate(Files.toByteArray(file));
			publicKeyToCertificates.put(
					SecurityUtil.base64Encode(certificate.getPublicKey().getEncoded()),
					file.getName());
		}
	}

	/**
	 * Write a file to disk.
	 * @param filename The name of the file
	 * @param data The file data
	 * @param isCertificate Whether the file is a certificate
	 */
	public static void writeFile(String filename, byte[] data, boolean isCertificate)
			throws Exception {
		if (isCertificate) {
			X509Certificate certificate = SecurityUtil.loadCertificate(data);
			publicKeyToCertificates.put(
					SecurityUtil.base64Encode(certificate.getPublicKey().getEncoded()),
					filename);
		}
		Files.write(data, getFile(filename, isCertificate));
	}

	/**
	 * Read a file from disk.
	 * @param filename The name of the file
	 * @return The file data
	 * @throws IOException if the file doesn't exist
	 */
	public static byte[] readFile(String filename) throws IOException {
		return Files.toByteArray(getFile(filename));
	}

	/**
	 * Delete an existing file.
	 * @param filename The name of the file
	 */
	public static void deleteFile(String filename) throws Exception {
		File existingFile = getFile(filename);
		if (isCertificate(filename)) {
			X509Certificate certificate = SecurityUtil.loadCertificate(Files.toByteArray(existingFile));
			publicKeyToCertificates.remove(certificate.getPublicKey(), filename);
		}
		existingFile.delete();
	}

	/**
	 * Return whether the specified file exists.
	 * @param filename The name of the file
	 * @param isCertificate Whether the file is a certificate
	 * @return true if the file exists
	 */
	public static boolean fileExists(String filename, boolean isCertificate) {
		return getFile(filename, isCertificate).exists();
	}

	/**
	 * Return whether the specified file exists.
	 * @param filename The name of the file
	 * @return true if the file exists
	 */
	public static boolean fileExists(String filename) {
		return getFile(filename).exists();
	}

	/**
	 * Return a description of the specified file.
	 * @param filename The name of the file
	 * @return The file's description
	 */
	public static String describeFile(String filename) {
		File file = getFile(filename);
		long size = file.length();
		Date date = new Date(file.lastModified());

		String desc = "normal file";
		if (isCertificate(filename)) {
			desc = "certificate";
		}
		return String.format("%s\t%12d\t%s\t%s", DATE_FORMAT.format(date), size, filename, desc);
	}

	/**
	 * Return a list of existing files.
	 * @return The list of filenames
	 */
	public static List<String> listFiles() {
		List<String> files = Lists.newArrayList();
		for (File file : FILES_DIR.listFiles()) {
			files.add(file.getName());
		}
		for (File file : CERTS_DIR.listFiles()) {
			files.add(file.getName());
		}
		return files;
	}

	/**
	 * Return a list of certificates with the given public key.
	 * @param publicKey The public key being searched for
	 * @return The list of certificate names
	 */
	public static Collection<String> getCertificateFilenamesForPublicKey(String publicKey) {
		return publicKeyToCertificates.get(publicKey);
	}

	/**
	 * Return whether the specified file is a certificate file.
	 * @param filename The name of the file
	 * @return true if the file is a certificate file
	 */
	public static boolean isCertificate(String filename) {
		return getFile(filename, true).exists();
	}

	/**
	 * Return whether the specified file is a normal file.
	 * @param filename The name of the file
	 * @return true if the file is a certificate file
	 */
	public static boolean isNormalFile(String filename) {
		return getFile(filename, false).exists();
	}

	/**
	 * Return whether the specified file has a valid name.
	 * @param filename The name of the file
	 * @return true if the filename is valid
	 */
	public static boolean isValidFilename(String filename) {
		return !filename.matches(ILLEGAL_CHARACTER_REGEX);
	}

	/**
	 * Locate and return the specified file.
	 * @param filename The name of the file
	 * @return The file object
	 */
	private static File getFile(String filename) {
		File normalFile = getFile(filename, false);
		File certFile = getFile(filename, true);
		if (normalFile.exists()) {
			return normalFile;
		} else if (certFile.exists()) {
			return certFile;
		}
		return normalFile;
	}

	/**
	 * Locate and return the specified file.
	 * @param filename The name of the file
	 * @param isCertificate Whether the file is a certificate
	 * @return The file object
	 */
	private static File getFile(String filename, boolean isCertificate) {
		return new File(isCertificate ? CERTS_DIR : FILES_DIR, filename);
	}
}
