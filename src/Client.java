import java.io.*;
import java.net.InetAddress;
import java.util.*;
import javax.net.ssl.*;

public class Client {
	public static void main(String[] args) {
		new Client(443);
	}

	public Client(int port) {
		try {
			SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket socket = (SSLSocket) socketFactory.createSocket(InetAddress.getLoopbackAddress(), port);
			socket.setEnabledCipherSuites(socket.getSupportedCipherSuites());
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			Scanner input = new Scanner(System.in);
			while (input.hasNextLine()) {
				out.write(input.nextLine() + "\n");
				out.flush();
				String line = in.readLine();
				System.out.println(line);
			}

			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
