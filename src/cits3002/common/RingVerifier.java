package cits3002.common;

import cits3002.server.NamespaceLayer;
import cits3002.server.TrustLayer;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * A class to verify a file's rings of trust.
 */
public class RingVerifier {
	private Multimap<String, String> edges;
	private Set<String> ringBases;
	private Set<String> visited;

	/**
	 * Construct a new ring verifier.
	 *
	 * @param filename The file to be verified
	 */
	public RingVerifier(String filename) {
		this.edges = MultimapBuilder.hashKeys().hashSetValues().build();
		this.ringBases = Sets.newHashSet();
		this.visited = Sets.newHashSet();

		ringBases.addAll(getConnectedCertificates(filename));
		for (String certificateFilename : ringBases) {
			buildEdges(certificateFilename);
		}
	}

	/**
	 * Determine whether a ring of the required length exists.
	 *
	 * @param minimumRingLength The required length
	 * @return true if the minimum requirement is met
	 */
	public boolean hasRingOfSufficientLength(int minimumRingLength) {
		return computeCycleLength(getLargestRing()) >= minimumRingLength;
	}

	/**
	 * Find the largest ring.
	 *
	 * @return The names of the certificates in the ring
	 */
	public List<String> getLargestRing() {
		List<String> maxCycle = Lists.newArrayList();
		for (String certificateFilename : ringBases) {
			visited.clear();
			List<String> cycle = dfs(certificateFilename, certificateFilename);
			if (cycle.size() > maxCycle.size()) {
				maxCycle = cycle;
			}
		}
		return Lists.reverse(maxCycle);
	}

	/**
	 * Return the names of the certificates that have signed the given file.
	 *
	 * @param filename The name of the file
	 * @return The names of connected certificates
	 */
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

	/**
	 * Recursively add edges for the signers of the given certificate.
	 *
	 * @param certificateFilename The certificate name
	 */
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

	/**
	 * Return the largest cycle from the given node.
	 *
	 * @param initial The node the cycle starts and ends at
	 * @param node    The current node
	 * @return The largest cycle found so far
	 */
	private List<String> dfs(String initial, String node) {
		List<String> maxCycle = Lists.newArrayList();

		// Base case: Start of cycle
		if (node.equals(initial)) {
			maxCycle.add(initial);
		}

		// Base case: Already part of the considered path.
		// Return fail unless at the initial node.
		if (visited.contains(node)) {
			return maxCycle;
		}

		visited.add(node);
		for (String adj : edges.get(node)) {
			List<String> cycle = dfs(initial, adj);
			if (!cycle.isEmpty()) {
				cycle.add(node);
				if (cycle.size() > maxCycle.size()) {
					maxCycle = cycle;
				}
			}
		}
		visited.remove(node);
		return maxCycle;
	}

	public static int computeCycleLength(List<String> cycle) {
		if (cycle.size() == 0) {
			return 0;
		}
		if (cycle.size() == 1) {
			return 1;
		}
		return cycle.size() - 1;
	}
}
