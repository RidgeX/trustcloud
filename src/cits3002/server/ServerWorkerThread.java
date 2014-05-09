package cits3002.server;

import cits3002.server.commands.Command;
import cits3002.server.commands.CommandParser;
import cits3002.server.commands.CommandTuple;
import cits3002.server.commands.UnsupportedCommand;
import cits3002.util.CommandUtil;
import com.google.common.base.Preconditions;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class ServerWorkerThread extends Thread {
	private SSLSocket socket;

	public ServerWorkerThread(SSLSocket socket) {
		Preconditions.checkNotNull(socket);

		this.socket = socket;
	}

	public void run() {
		System.out.println("Connection opened");

		try {
			InputStream in = socket.getInputStream();
			OutputStream out = socket.getOutputStream();

			Command clientCommand = readAndBuildCommand(in);
			byte[] result = clientCommand.execute();
			System.out.println("Result: " + new String(result, "ISO-8859-1"));
			out.write(result);
			out.flush();
			in.close();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("Connection closed");
	}

	private Command readAndBuildCommand(InputStream in) {
		try {
			CommandTuple commandTuple = CommandUtil.parseCommandData(in);
			if (commandTuple == null) {
				return new UnsupportedCommand();
			}

			System.out.println("Command: " + commandTuple.getArgumentString());
			return new CommandParser().parseCommand(commandTuple);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new UnsupportedCommand();
	}
}
