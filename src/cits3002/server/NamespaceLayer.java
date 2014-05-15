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

public class NamespaceLayer {
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");

	private static final File CERTS_DIR = new File("ns/certs");
	private static final File FILES_DIR = new File("ns/files");

	private static final String ILLEGAL_CHARACTER_REGEX = "[\\/:*?\"'<>|]";

	private static final Multimap<String, String> publicKeyToCertificates =
			MultimapBuilder.hashKeys().hashSetValues().build();

	static {
		if (!CERTS_DIR.exists()) {
			CERTS_DIR.mkdirs();
		}
		if (!FILES_DIR.exists()) {
			FILES_DIR.mkdirs();
		}
	}

	public static void init() throws Exception {
		for (File file : CERTS_DIR.listFiles()) {
			X509Certificate certificate = SecurityUtil.loadCertificate(Files.toByteArray(file));
			publicKeyToCertificates.put(
					SecurityUtil.base64Encode(certificate.getPublicKey().getEncoded()),
					file.getName());
		}
	}

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

	public static byte[] readFile(String filename) throws IOException {
		return Files.toByteArray(getFile(filename));
	}

	public static void deleteFile(String filename) throws Exception {
		File existingFile = getFile(filename);
		if (isCertificate(filename)) {
			X509Certificate certificate = SecurityUtil.loadCertificate(Files.toByteArray(existingFile));
			publicKeyToCertificates.remove(certificate.getPublicKey(), filename);
		}
		existingFile.delete();
	}

	public static boolean fileExists(String filename, boolean isCertificate) {
		return getFile(filename, isCertificate).exists();
	}

	public static boolean fileExists(String filename) {
		return getFile(filename).exists();
	}

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

	public static Collection<String> getCertificateFilenamesForPublicKey(String publicKey) {
		return publicKeyToCertificates.get(publicKey);
	}

	public static boolean isCertificate(String filename) {
		return getFile(filename, true).exists();
	}

	public static boolean isNormalFile(String filename) {
		return getFile(filename, false).exists();
	}

	public static boolean isValidFilename(String filename) {
		return !filename.matches(ILLEGAL_CHARACTER_REGEX);
	}

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

	private static File getFile(String filename, boolean isCertificate) {
		return new File(isCertificate ? CERTS_DIR : FILES_DIR, filename);
	}
}
