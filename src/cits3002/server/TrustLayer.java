package cits3002.server;

import cits3002.common.SecurityUtil;
import com.google.common.base.Charsets;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.io.Files;

import java.io.*;
import java.util.Collection;
import java.util.Scanner;

public class TrustLayer {
	private static final File SIGS_DIR = new File("ns/sigs");

	static {
		if (!SIGS_DIR.exists()) {
			SIGS_DIR.mkdirs();
		}
	}

	private static final Multimap<String, SecurityUtil.UnpackedSignature> fileToSigs =
			MultimapBuilder.hashKeys().hashSetValues().build();

	public static void init() throws IOException {
		for (File f : SIGS_DIR.listFiles()) {
			try {
				String name = f.getName();
				if (name.endsWith(".sig")) {
					loadMapForFile(name.substring(0, name.length() - 4));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static boolean addSignatureForFile(String filename,
			SecurityUtil.UnpackedSignature unpacked) {
		try {
			if (!SecurityUtil.verifyData(unpacked, NamespaceLayer.readFile(filename))) {
				return false;
			}

			if (fileToSigs.containsEntry(filename, unpacked)) {
				return false;
			}

			fileToSigs.put(filename, unpacked);
			File f = getSignatureFile(filename);
			Files.touch(f);
			Files.append(SecurityUtil.packSignature(unpacked) + "\n", f, Charsets.ISO_8859_1);

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public static Collection<SecurityUtil.UnpackedSignature> getSignaturesForFile(String filename) {
		return fileToSigs.get(filename);
	}

	public static void clearSignaturesForFile(String filename) throws IOException {
		File sigFile = getSignatureFile(filename);
		if (sigFile.exists()) {
			sigFile.delete();
		}
		if (fileToSigs.containsKey(filename)) {
			fileToSigs.removeAll(filename);
		}
	}

	private static void loadMapForFile(String filename) throws Exception {
		System.err.println("Loading signatures for file " + filename + "...");
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(getSignatureFile(filename)), Charsets.ISO_8859_1));

		Scanner sc = new Scanner(in);
		while (sc.hasNextLine()) {
			SecurityUtil.UnpackedSignature unpacked = SecurityUtil.unpackSignature(sc.nextLine());
			if (SecurityUtil.verifyData(unpacked, NamespaceLayer.readFile(filename))) {
				fileToSigs.put(filename, unpacked);
			}
		}
	}

	private static File getSignatureFile(String filename) {
		return new File(SIGS_DIR, filename + ".sig");
	}
}
