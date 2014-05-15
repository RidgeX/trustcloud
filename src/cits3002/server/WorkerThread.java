package cits3002.server;

import cits3002.common.handlers.Handler;
import cits3002.common.handlers.HandlerFactory;
import cits3002.common.messages.Message;
import cits3002.common.messages.MessageUtil;
import com.google.common.base.Preconditions;

import javax.net.ssl.SSLSocket;
import java.io.IOException;

public class WorkerThread extends Thread {
	private final SSLSocket socket;
	private HandlerFactory handlerFactory;

	public WorkerThread(SSLSocket socket) {
		Preconditions.checkNotNull(socket);
		this.socket = socket;
		this.handlerFactory = new HandlerFactory();
	}

	@Override
	public void run() {
		System.err.println("Connection opened");
		try {
			Message request = MessageUtil.parseMessage(socket.getInputStream());
			System.err.println(">> " + request.data.length);
			System.err.println(">> " + request.getTypeString());
			System.err.println(">> " + request.getArgsString());
			if (request.data.length <= 1024) {
				System.err.println(">> " + request.getDataString());
			}

			Handler handler = handlerFactory.getHandlerForMessage(request);
			Message response = handler.execute();
			System.err.println(">>> " + response.data.length);
			System.err.println(">>> " + response.getTypeString());
			System.err.println(">>> " + response.getArgsString());
			if (response.data.length <= 1024) {
				System.err.println(">>> " + response.getDataString());
			}
			socket.getOutputStream().write(MessageUtil.serialiseMessage(response));
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
