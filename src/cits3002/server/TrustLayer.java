package cits3002.server;

import cits3002.common.SecurityUtil;
import com.google.common.base.Charsets;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.io.Files;

import java.io.*;
import java.util.Collection;
import java.util.Scanner;

/**
 * A layer which manages signature storage and querying.
 */
public class TrustLayer {
	/**
	 * The directory for storing signature files.
	 */
	private static final File SIGS_DIR = new File("ns/sigs");

	static {
		// Create directories if necessary
		if (!SIGS_DIR.exists()) {
			SIGS_DIR.mkdirs();
		}
	}

	/**
	 * A map of filenames to public key/signature pairs.
	 */
	private static final Multimap<String, SecurityUtil.UnpackedSignature> fileToSigs =
			MultimapBuilder.hashKeys().hashSetValues().build();

	/**
	 * Initialise this layer.
	 */
	public static void init() throws IOException {
		// Populate filename -> signature map
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

	/**
	 * Add a new signature for the specified file.
	 * @param filename The name of the file
	 * @param unpacked The unpacked signature
	 * @return true if the signature was added successfully
	 */
	public static boolean addSignatureForFile(String filename,
			SecurityUtil.UnpackedSignature unpacked) {
		try {
			// Check that the signature is valid
			if (!SecurityUtil.verifyData(unpacked, NamespaceLayer.readFile(filename))) {
				return false;
			}

			// Check that the signature hasn't already been made
			if (fileToSigs.containsEntry(filename, unpacked)) {
				return false;
			}

			// Save the new signature
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

	/**
	 * Return a list of signatures for the specified file.
	 * @param filename The name of the file
	 * @return The list of signatures
	 */
	public static Collection<SecurityUtil.UnpackedSignature> getSignaturesForFile(String filename) {
		return fileToSigs.get(filename);
	}

	/**
	 * Clear all of the signatures for the specified file (called on file replacement).
	 * @param filename The name of the file
	 */
	public static void clearSignaturesForFile(String filename) throws IOException {
		File sigFile = getSignatureFile(filename);
		if (sigFile.exists()) {
			sigFile.delete();
		}
		if (fileToSigs.containsKey(filename)) {
			fileToSigs.removeAll(filename);
		}
	}

	/**
	 * Load all of the signatures from the specified '.sig' file.
	 * @param filename The name of the signature file
	 */
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
		sc.close();
	}

	/**
	 * Locate and return the '.sig' file for the specified file.
	 * @param filename The name of the file
	 */
	private static File getSignatureFile(String filename) {
		return new File(SIGS_DIR, filename + ".sig");
	}
}
