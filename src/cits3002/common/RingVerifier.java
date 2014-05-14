package cits3002.common;

import cits3002.server.TrustLayer;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;

public class RingVerifier {
	private ListMultimap<String, String> edges;
	private ListMultimap<String, String> revEdges;
	private Set<String> visited;
	private Stack<String> finished;
	private int sccSize;
	private int maxSccSize;

	public RingVerifier(String u) {
		edges = ArrayListMultimap.create();
		revEdges = ArrayListMultimap.create();
		visited = new HashSet<String>();
		finished = new Stack<String>();
		sccSize = 0;
		maxSccSize = 0;

		addEdges(u);

		visited.clear();
		dfs(u);

		visited.clear();
		while (!finished.isEmpty()) {
			String v = finished.pop();
			if (!visited.contains(v)) {
				sccSize = 0;
				dfs2(v);
				if (sccSize > maxSccSize) {
					maxSccSize = sccSize;
				}
			}
		}

		System.err.println("<< Largest ring is of size " + maxSccSize);
	}

	public boolean testLength(int length) {
		return (length <= maxSccSize);
	}

	private void addEdges(String u) {
		visited.add(u);
		Properties sigs = TrustLayer.fileToSigs.get(u);
		for (String v : sigs.stringPropertyNames()) {
			edges.put(u, v);
			revEdges.put(v, u);
			if (!visited.contains(v)) {
				addEdges(v);
			}
		}
	}

	private void dfs(String u) {
		visited.add(u);
		for (String v : edges.get(u)) {
			if (!visited.contains(v)) {
				dfs(v);
			}
		}
		finished.push(u);
	}

	private void dfs2(String v) {
		visited.add(v);
		sccSize++;
		for (String u : revEdges.get(v)) {
			if (!visited.contains(u)) {
				dfs2(u);
			}
		}
	}
}
