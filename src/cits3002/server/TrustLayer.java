package cits3002.server;

import cits3002.util.SecurityUtil;
import com.google.common.base.Charsets;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.io.Files;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

public class TrustLayer {
	private static final String SIGN_DIR = "ns/sigs";

	private final NamespaceLayer namespaceLayer;
	private final Multimap<String, PublicKey> fileToSigs;

	public TrustLayer() {
		this.namespaceLayer = new NamespaceLayer();
		this.fileToSigs = MultimapBuilder.hashKeys().hashSetValues().build();
		loadFileToSigMap();
	}

	public boolean addSignatureForFile(String filename, PublicKey pubKey, byte[] sigData) {
		try {
			byte[] fileData = namespaceLayer.readFile(filename);
			if (!SecurityUtil.verifyData(pubKey, fileData, sigData)) {
				return false;
			}

			if (fileToSigs.containsEntry(filename, pubKey)) {
				return false;
			}

			fileToSigs.put(filename, pubKey);
			File f = new File(SIGN_DIR, filename + ".sig");
			Files.touch(f);
			Files.append(SecurityUtil.packSignature(pubKey, sigData) + "\n", f, Charsets.ISO_8859_1);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	private void loadFileToSigMap()  {
		File signDir = new File(SIGN_DIR);
		for (File f : signDir.listFiles()) {
			try {
				String name = f.getName();
				String path = f.getPath();
				if (name.endsWith(".sig")) {
					loadSigMapForFile(name.substring(0, name.length() - 4), path);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// Assumes valid signatures.
	private void loadSigMapForFile(String filename, String sigMapFilename)
			throws FileNotFoundException, InvalidKeySpecException, NoSuchAlgorithmException {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(sigMapFilename), Charsets.ISO_8859_1));
		Scanner sc = new Scanner(in);
		while (sc.hasNext()) {
			String base64PubKey = sc.next();
			fileToSigs.put(filename, SecurityUtil.loadBase64PublicKey(base64PubKey));
			sc.next(); // Ignore signature for now.
		}
	}
}
