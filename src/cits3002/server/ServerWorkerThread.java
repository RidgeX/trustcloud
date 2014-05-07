package cits3002.server;

import cits3002.server.commands.*;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;

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
			socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Connection closed");
	}

	private Command readAndBuildCommand(InputStream in) {
		CommandReader commandReader = new CommandReader();
		try {
			ByteStreams.readBytes(in, commandReader);
			CommandTuple commandTuple = commandReader.getResult();
			if (commandTuple == null) {
				return new UnsupportedCommand();
			}

			System.out.println("Command: " + commandTuple.commandString);
			return new CommandParser().parseCommand(commandTuple);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new UnsupportedCommand();
	}
}
