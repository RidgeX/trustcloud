package cits3002.server;

import cits3002.server.commands.Command;
import cits3002.server.commands.CommandParser;
import cits3002.server.commands.UnsupportedCommand;
import com.google.common.base.Preconditions;
import com.google.common.io.CharStreams;
import com.google.common.primitives.UnsignedInts;

import javax.net.ssl.SSLSocket;
import java.io.*;

class ServerWorkerThread extends Thread {
	private static final int MAX_BINARY_DATA = 100 * 1024 * 1024; // 100 MB
	private SSLSocket socket;

	public ServerWorkerThread(SSLSocket socket) {
		Preconditions.checkNotNull(socket);

		this.socket = socket;
	}

	public void run() {
		System.out.println("Connection opened");
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			OutputStream out = socket.getOutputStream();

			Command clientCommand = readAndBuildCommand(in);
			byte[] result = clientCommand.execute();
			System.out.println("Result: " + new String(result, "utf-8"));
			out.write(result);
			out.flush();

			in.close();
			out.close();
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Connection closed");
	}

	private Command readAndBuildCommand(BufferedReader in) {
		try {
			int dataLength = UnsignedInts.parseUnsignedInt(in.readLine());
			String commandStr = in.readLine();

			if (dataLength > MAX_BINARY_DATA) {
				return new UnsupportedCommand();
			}

			String binaryData = CharStreams.toString(in);

			System.out.println("Command: " + commandStr);
			return new CommandParser().parseCommand(commandStr, binaryData.getBytes("utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new UnsupportedCommand();
	}
}
