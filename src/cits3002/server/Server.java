package cits3002.server;

import com.google.common.primitives.UnsignedInts;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.security.Security;

/**
 * The Trustcloud server application.
 * @author Eliot Courtney (21141563), Ridge Shrubsall (21112211)
 */
public class Server {
	/**
	 * A list of allowed cipher suites for establishing a connection.
	 */
	private static final String[] ANONYMOUS_CIPHERS = new String[] {
			"TLS_DH_anon_WITH_AES_256_CBC_SHA256",
			"TLS_DH_anon_WITH_AES_256_CBC_SHA",
			"TLS_DH_anon_WITH_AES_128_CBC_SHA256",
			"TLS_DH_anon_WITH_AES_128_CBC_SHA"
	};

	/**
	 * The default port to run the server on.
	 */
	private static final int DEFAULT_PORT = 4433;

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * Main method for the server.
	 * @param args The port to use (optional)
	 */
	public static void main(String[] args) throws Exception {
		int port;
		if (args.length == 1) {
			port = UnsignedInts.parseUnsignedInt(args[0]);
		} else {
			port = DEFAULT_PORT;
		}

		// Start server
		Server server = new Server();
		server.run(port);
	}

	/**
	 * Starts the server process.
	 * @param port The port to listen on
	 */
	public void run(int port) throws Exception {
		NamespaceLayer.init();
		TrustLayer.init();

		System.err.println("Starting server");
		SSLServerSocketFactory ssocketFactory =
				(SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
		SSLServerSocket ssocket = (SSLServerSocket) ssocketFactory.createServerSocket(port);
		while (true) {
			SSLSocket socket = (SSLSocket) ssocket.accept();
			socket.setEnabledCipherSuites(ANONYMOUS_CIPHERS);
			WorkerThread worker = new WorkerThread(socket);
			worker.start();
		}
	}
}
