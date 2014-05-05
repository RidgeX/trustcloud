package cits3002.server;

import cits3002.server.commands.Command;
import cits3002.server.commands.CommandParser;
import cits3002.server.commands.UnsupportedCommand;

import javax.net.ssl.SSLSocket;
import java.io.*;

class ServerWorkerThread extends Thread {
	private static final int MAX_BINARY_DATA = 100 * 1024 * 1024; // 100 MB
	private SSLSocket socket;

	public ServerWorkerThread(SSLSocket socket) {
		this.socket = socket;
	}

	public void run() {
		System.out.println("Connection opened");
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

			Command clientCommand = readAndBuildCommand(in);
			out.write(clientCommand.execute());
			out.flush();

			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Connection closed");
	}

	private Command readAndBuildCommand(BufferedReader in) {
		StringBuilder builder = new StringBuilder();
		try {
			int dataLength = Integer.parseInt(in.readLine());
			String commandStr = in.readLine();

			if (dataLength < 0 || dataLength > MAX_BINARY_DATA) {
				return new UnsupportedCommand();
			}

			char[] buf = new char[4096];
			int offset = 0;
			while (offset < dataLength) {
				int result = in.read(buf, offset, buf.length);
				if (result == -1) {
					return new UnsupportedCommand();
				}
				offset += result;
				builder.append(buf);
			}
			CommandParser.parseCommand(commandStr, builder.toString());
		} catch (Exception e) {
			return new UnsupportedCommand();
		}

		return new UnsupportedCommand();
	}
}
