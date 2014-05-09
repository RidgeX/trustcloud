package cits3002.server;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.List;

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

	public boolean fileExists(String filename) {
		return getFile(filename).exists();
	}

	public String describeFile(String filename) {
		File f = getFile(filename);
		String desc = "normal file";
		if (isCertificate(filename)) {
			desc = "certificate";
		}
		return String.format("%s | %s", f.getName(), desc);
	}

	public List<String> getFileList() {
		File certDir = new File(CERT_DIR);
		File fileDir = new File(FILE_DIR);
		List<String> files = Lists.newArrayList();
		for (File f : certDir.listFiles()) {
			files.add(f.getName());
		}
		for (File f : fileDir.listFiles()) {
			files.add(f.getName());
		}
		return files;
	}

	public boolean isCertificate(String filename) {
		return getFile(filename, true).exists();
	}

	public boolean isNormalFile(String filename) {
		return getFile(filename, false).exists();
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
		return new File("./" + (isCertificate ? CERT_DIR : FILE_DIR) + "/" + filename);
	}

}
