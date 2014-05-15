package cits3002.common;

import cits3002.server.NamespaceLayer;
import cits3002.server.TrustLayer;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;

public class RingVerifier {
	private Multimap<String, String> edges;
	private Set<String> ringBases;
	private Set<String> visited;

	public RingVerifier(String filename) {
		this.edges = MultimapBuilder.hashKeys().hashSetValues().build();
		this.ringBases = Sets.newHashSet();
		this.visited = Sets.newHashSet();

		ringBases.addAll(getConnectedCertificates(filename));
		for (String certificateFilename : ringBases) {
			buildEdges(certificateFilename);
		}
	}

	public boolean hasRingOfSufficientLength(int minimumRingLength) {
		for (String certificateFilename : ringBases) {
			visited.clear();
			if (dfs(certificateFilename) >= minimumRingLength) {
				return true;
			}
		}

		return minimumRingLength == 0;
	}

	private Set<String> getConnectedCertificates(String filename) {
		Collection<SecurityUtil.UnpackedSignature> signatures =
				TrustLayer.getSignaturesForFile(filename);
		System.err.println("Signatures for " + filename);
		for (SecurityUtil.UnpackedSignature unpacked : signatures) {
			System.err.println(unpacked.publicKey.hashCode());
		}
		Set<String> connectedCertificates = Sets.newHashSet();
		for (SecurityUtil.UnpackedSignature unpacked : signatures) {
			connectedCertificates.addAll(
					NamespaceLayer.getCertificateFilenamesForPublicKey(
							SecurityUtil.base64Encode(unpacked.publicKey))
			);
		}
		System.err.println("Connected certs for " + filename);
		for (String certs : connectedCertificates) {
			System.err.println(certs);
		}
		return connectedCertificates;
	}

	private void buildEdges(String certificateFilename) {
		Set<String> connectedCertificates = getConnectedCertificates(certificateFilename);
		visited.add(certificateFilename);
		for (String certificate : connectedCertificates) {
			edges.put(certificate, certificateFilename);
			if (!visited.contains(certificate)) {
				buildEdges(certificate);
			}
		}
	}

	private int dfs(String node) {
		int max = 1;
		visited.add(node);
		for (String adj : edges.get(node)) {
			if (!visited.contains(adj)) {
				max = Math.max(max, dfs(adj) + 1);
			}
		}
		visited.remove(node);
		return max;
	}
}
