package cits3002.server;

import cits3002.util.FileUtil;

import java.io.*;

public class NamespaceLayer {
	private static final String CERT_DIR = "ns/certs";
	private static final String FILE_DIR = "ns/files";


	public void writeFile(String filename, String data, boolean isCertificate) throws IOException {
		OutputStream out = new FileOutputStream(getFile(filename, isCertificate));
		out.write(data.getBytes());
		out.close();
	}

	public String readFile(String filename) throws IOException {
		return FileUtil.readAllBytes(new FileInputStream(getFile(filename)));
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
