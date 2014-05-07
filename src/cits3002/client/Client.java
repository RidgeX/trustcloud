package cits3002.client;

import cits3002.util.CommandUtil;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;

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

			InputStreamReader inp = new InputStreamReader(socket.getInputStream(), "ISO-8859-1");
			OutputStream out = socket.getOutputStream();
			out.write(CommandUtil.serialiseCommand(
					"FLE test.txt",
					Files.toByteArray(new File("../lib/bcpkix-jdk15on-150.jar"))));
			out.flush();

			System.out.println(CharStreams.toString(inp));

			inp.close();
			out.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
