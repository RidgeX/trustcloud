package cits3002.server;

import cits3002.common.SecurityUtil;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TrustLayer {
	private static final File SIGS_DIR = new File("ns/sigs");

	static {
		if (!SIGS_DIR.exists()) SIGS_DIR.mkdirs();
	}

	private static Map<String, Properties> fileToSigs;

	public static void init() throws IOException {
		fileToSigs = new HashMap<String, Properties>();
		for (File f : NamespaceLayer.listFiles()) {
			String fileName = f.getName();
			if (getSigFile(fileName).exists()) {
				fileToSigs.put(fileName, loadSignatures(fileName));
			}
		}
	}

	public static boolean addSignature(String fileName, String certName, byte[] sigData) throws IOException {
		Properties sigs = fileToSigs.get(fileName);
		if (sigs == null) {
			sigs = new Properties();
		} else if (sigs.containsKey(certName)) {
			return false;
		}
		sigs.put(certName, SecurityUtil.encodeSignature(sigData));
		saveSignatures(fileName, sigs);
		return true;
	}

	public static void clearSignatures(String fileName) throws IOException {
		File sigFile = getSigFile(fileName);
		if (sigFile.exists()) {
			sigFile.delete();
		}
		if (fileToSigs.containsKey(fileName)) {
			fileToSigs.remove(fileName);
		}
		for (String otherFileName : fileToSigs.keySet()) {
			Properties sigs = fileToSigs.get(otherFileName);
			if (sigs.containsKey(fileName)) {
				sigs.remove(fileName);
				saveSignatures(otherFileName, sigs);
			}
		}
	}

	private static Properties loadSignatures(String fileName) throws IOException {
		System.err.println("Loading " + fileName + "...");

		Properties sigs = new Properties();
		sigs.load(new FileReader(getSigFile(fileName)));

		Properties validSigs = new Properties();
		for (String certName : sigs.stringPropertyNames()) {
			System.err.print("    Validating " + certName + "... ");
			String sig = sigs.getProperty(certName);
			try {
				X509Certificate cert = SecurityUtil.loadCertificate(NamespaceLayer.readFile(certName));
				SecurityUtil.checkCertificate(cert);
				byte[] hash = SecurityUtil.makeHash(NamespaceLayer.readFile(fileName));
				byte[] sigData = SecurityUtil.decodeSignature(sig);
				if (!SecurityUtil.checkSignature(cert.getPublicKey(), hash, sigData)) {
					throw new SecurityException("Validation failed");
				}
				validSigs.put(certName, sig);
				System.err.println("[OK]");
			} catch (Exception e) {
				System.err.println("[FAIL]");
			}
		}

		return validSigs;
	}

	private static void saveSignatures(String fileName, Properties sigs) throws IOException {
		sigs.store(new FileWriter(getSigFile(fileName)), null);
	}

	private static File getSigFile(String fileName) {
		return new File(SIGS_DIR, fileName + ".sig");
	}
}
