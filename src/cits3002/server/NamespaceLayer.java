package cits3002.server;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NamespaceLayer {
	private static final String CL_NONE = "\033[0m";
	private static final String CL_GREEN = "\033[32;1m";
	private static final String CL_CYAN = "\033[36;1m";
	private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");

	private static final File CERTS_DIR = new File("ns/certs");
	private static final File FILES_DIR = new File("ns/files");

	static {
		if (!CERTS_DIR.exists()) CERTS_DIR.mkdirs();
		if (!FILES_DIR.exists()) FILES_DIR.mkdirs();
	}

	public static byte[] readFile(String fileName) throws IOException {
		File normalFile = getFile(fileName, false);
		File certFile = getFile(fileName, true);
		if (normalFile.exists()) {
			return Files.toByteArray(normalFile);
		} else if (certFile.exists()) {
			return Files.toByteArray(certFile);
		}
		throw new FileNotFoundException();
	}

	public static void writeFile(String fileName, byte[] data, boolean isCert) throws IOException {
		Files.write(data, getFile(fileName, isCert));
	}

	public static void deleteFile(String fileName) {
		File normalFile = getFile(fileName, false);
		File certFile = getFile(fileName, true);
		if (normalFile.exists()) {
			normalFile.delete();
		} else if (certFile.exists()) {
			certFile.delete();
		}
	}

	public static String describeFile(File file) {
		long size = file.length();
		Date date = new Date(file.lastModified());
		boolean isCert = file.getParentFile().equals(NamespaceLayer.CERTS_DIR);
		String name = (isCert ? CL_CYAN : CL_GREEN) + file.getName() + CL_NONE;

		return String.format("%s\t%12d\t%s", DATE_FORMAT.format(date), size, name);
	}

	public static List<File> listFiles() {
		List<File> files = new ArrayList<File>();
		for (File f : FILES_DIR.listFiles()) {
			files.add(f);
		}
		for (File f : CERTS_DIR.listFiles()) {
			files.add(f);
		}
		return files;
	}

	private static File getFile(String fileName, boolean isCert) {
		return new File(isCert ? CERTS_DIR : FILES_DIR, fileName);
	}
}
