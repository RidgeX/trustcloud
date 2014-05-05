package cits3002.client;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;

import static cits3002.util.CommandUtil.makeCommandString;

public class Client {
	public static void main(String[] args) {
		Client client = new Client();
		client.run(4433);
	}

	public void run(int port) {
		try {
			SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket socket =
					(SSLSocket) socketFactory.createSocket(InetAddress.getLoopbackAddress(), port);
			socket.setEnabledCipherSuites(socket.getSupportedCipherSuites());
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			out.write(makeCommandString("LST", ""));
			out.flush();

			StringBuilder builder = new StringBuilder();
			char[] buf = new char[4096];
			while (in.read(buf, 0, buf.length) != -1) {
				builder.append(buf);
			}

			System.out.println(builder.toString());

			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
