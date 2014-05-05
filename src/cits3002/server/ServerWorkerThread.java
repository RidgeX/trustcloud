package cits3002.server;

import cits3002.server.commands.Command;
import cits3002.server.commands.CommandParser;
import cits3002.server.commands.UnsupportedCommand;

import javax.net.ssl.SSLSocket;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

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
			String result = clientCommand.execute();
			System.out.println("Result: " + result);
			out.write(result);
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
		try {
			int dataLength = Integer.parseInt(in.readLine());
			String commandStr = in.readLine();

			if (dataLength < 0 || dataLength > MAX_BINARY_DATA) {
				return new UnsupportedCommand();
			}

			StringBuilder builder = new StringBuilder();
			if (dataLength > 0) {
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
			}
			System.out.println("Command: " + commandStr);
			return new CommandParser().parseCommand(commandStr, builder.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new UnsupportedCommand();
	}
}
