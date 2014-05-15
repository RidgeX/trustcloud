package cits3002.server;

import com.google.common.primitives.UnsignedInts;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.security.Security;

public class Server {
	private static final String[] ANONYMOUS_CIPHERS = new String[] {
			"TLS_DH_anon_WITH_AES_256_CBC_SHA256",
			"TLS_DH_anon_WITH_AES_256_CBC_SHA",
			"TLS_DH_anon_WITH_AES_128_CBC_SHA256",
			"TLS_DH_anon_WITH_AES_128_CBC_SHA"
	};
	private static final int DEFAULT_PORT = 4433;

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static void main(String[] args) throws Exception {
		int port;
		if (args.length == 1) {
			port = UnsignedInts.parseUnsignedInt(args[0]);
		} else {
			port = DEFAULT_PORT;
		}
		Server server = new Server();
		server.run(port);
	}

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
