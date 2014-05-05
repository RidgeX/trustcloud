package cits3002.client;

import cits3002.util.FileUtil;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
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
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			out.write(makeCommandString("FLE test.txt", "THIS IS A NEW FILE!"));
			out.flush();

			System.out.println(FileUtil.readAllBytes(socket.getInputStream()));

			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
