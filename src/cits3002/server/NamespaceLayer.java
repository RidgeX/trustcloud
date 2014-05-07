package cits3002.server;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

public class NamespaceLayer {
	private static final String CERT_DIR = "ns/certs";
	private static final String FILE_DIR = "ns/files";

	public void writeFile(String filename, byte[] data, boolean isCertificate) throws IOException {
		Files.write(data, getFile(filename, isCertificate));
	}

	public byte[] readFile(String filename) throws IOException {
		return Files.toByteArray(getFile(filename));
	}

	public boolean fileExists(String filename, boolean isCertificate) {
		return getFile(filename, isCertificate).exists();
	}

	private File getFile(String filename) {
		File normalFile = getFile(filename, false);
		File certFile = getFile(filename, true);
		if (normalFile.exists()) {
			return normalFile;
		} else if (certFile.exists()) {
			return certFile;
		}
		return normalFile;
	}

	private File getFile(String filename, boolean isCertificate) {
		return new File(isCertificate ? CERT_DIR : FILE_DIR + "/" + filename);
	}
}
