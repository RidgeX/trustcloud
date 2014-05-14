package cits3002.server;

import cits3002.common.CommandHandler;
import cits3002.common.Message;
import cits3002.common.MessageUtil;
import com.google.common.base.Preconditions;

import javax.net.ssl.SSLSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class WorkerThread extends Thread {
	private SSLSocket socket;

	public WorkerThread(SSLSocket socket) {
		Preconditions.checkNotNull(socket);
		this.socket = socket;
	}

	@Override
	public void run() {
		System.err.println("Connection opened");
		try {
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());

			Message request = MessageUtil.receive(in);
			System.err.println(">> " + request.data.length);
			System.err.println(">> " + request.getArgsString());

			CommandHandler handler = CommandHandler.getHandler(request);
			Message response = handler.execute();
			MessageUtil.send(out, response);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
		System.err.println("Connection closed");
	}
}
