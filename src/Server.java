import java.io.*;
import javax.net.ssl.*;

public class Server {
	public static void main(String[] args) {
		new Server(443);
	}

	public Server(int port) {
		try {
			SSLServerSocketFactory ssocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
			SSLServerSocket ssocket = (SSLServerSocket) ssocketFactory.createServerSocket(port);
			while (true) {
				SSLSocket socket = (SSLSocket) ssocket.accept();
				socket.setEnabledCipherSuites(socket.getSupportedCipherSuites());
				WorkerThread worker = new WorkerThread(socket);
				worker.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static class WorkerThread extends Thread {
		private SSLSocket socket;

		public WorkerThread(SSLSocket socket) {
			this.socket = socket;
		}

		public void run() {
			System.out.println("Connection opened");
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				String line;
				while ((line = in.readLine()) != null) {
					System.out.println(">> " + line);
					out.write("<< " + line + "\n");
					out.flush();
				}
				in.close();
				out.close();
				socket.close();
				System.out.println("Connection closed");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
