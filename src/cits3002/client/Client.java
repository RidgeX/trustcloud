package cits3002.client;

import cits3002.server.commands.CommandTuple;
import cits3002.util.CommandUtil;
import com.google.common.io.BaseEncoding;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;

public class Client {
	public static void main(String[] args) throws IOException {
		Client client = new Client();
		client.run(4433);
	}

	public CommandTuple runCommand(CommandTuple commandTuple, int port) {
		try {
			SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket socket =
					(SSLSocket) socketFactory.createSocket(InetAddress.getLoopbackAddress(), port);
			socket.setEnabledCipherSuites(socket.getSupportedCipherSuites());

			InputStreamReader inp = new InputStreamReader(socket.getInputStream(), "ISO-8859-1");
			OutputStream out = socket.getOutputStream();

			out.write(CommandUtil.serialiseCommand(commandTuple));
			out.flush();

			String resultString = CharStreams.toString(inp);

			inp.close();
			out.close();
			socket.close();

			return CommandUtil.parseCommandData(resultString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void run(int port) throws IOException {
		CommandTuple uploadCommand = CommandUtil.makeCommandTuple(
				"FLE test.txt",
				Files.toByteArray(new File("../res/test.crt")));

		CommandTuple hashCommand = CommandUtil.makeCommandTuple("HSH test.txt");
		CommandTuple listCommand = CommandUtil.makeCommandTuple("LST");

		CommandTuple uploadResult = runCommand(uploadCommand, port);
		CommandTuple hashResult = runCommand(hashCommand, port);
		CommandTuple listResult = runCommand(listCommand, port);

		System.out.println("Upload result: " + uploadResult.getArgumentString());
		System.out.println("Hash result: " + hashResult.getArgumentString());
		System.out.println("Hash: " + BaseEncoding.base16().lowerCase().encode(hashResult.data));
		System.out.println("List:");
		System.out.println(listResult.getDataString());
	}
}
