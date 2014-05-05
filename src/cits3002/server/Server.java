package cits3002.server;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.*;

public class Server {
	public static void main(String[] args) {
		Server server = new Server();
		server.run(4433);
	}

	public void run(int port) {
		try {
			SSLServerSocketFactory ssocketFactory =
					(SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			SSLServerSocket ssocket = (SSLServerSocket) ssocketFactory.createServerSocket(port);
			while (true) {
				SSLSocket socket = (SSLSocket) ssocket.accept();
				socket.setEnabledCipherSuites(socket.getSupportedCipherSuites());
				ServerWorkerThread worker = new ServerWorkerThread(socket);
				worker.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
