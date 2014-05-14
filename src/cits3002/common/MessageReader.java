package cits3002.common;

import com.google.common.base.Charsets;
import com.google.common.io.ByteProcessor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MessageReader implements ByteProcessor<Message> {
	private static final int MAX_DATA_LENGTH = 100 * 1024 * 1024;  // 100 MB

	private ByteArrayOutputStream dataLengthBytes;
	private ByteArrayOutputStream argsStringBytes;
	private ByteArrayOutputStream dataBytes;
	private int dataLength;
	private String argsString;

	private int toRead;
	private State state;


	private enum State {
		READING_DATA_LENGTH,
		READING_COMMAND,
		READING_DATA,
		DONE
	}

	public MessageReader() {
		dataLengthBytes = new ByteArrayOutputStream();
		argsStringBytes = new ByteArrayOutputStream();
		dataBytes = new ByteArrayOutputStream();
		toRead = 0;
		state = State.READING_DATA_LENGTH;
	}

	@Override
	public boolean processBytes(byte[] bytes, int off, int len) throws IOException {
		ByteBuffer buffer = ByteBuffer.wrap(bytes, off, len);
		while (buffer.hasRemaining()) {
			byte b;
			switch (state) {
				case READING_DATA_LENGTH:
					b = buffer.get();
					if (b == '\n') {
						dataLength =
								Integer.parseInt(new String(dataLengthBytes.toByteArray(), Charsets.ISO_8859_1));
						if (dataLength > MAX_DATA_LENGTH) {
							throw new IllegalArgumentException("Maximum data length exceeded");
						}
						state = State.READING_COMMAND;
					} else {
						dataLengthBytes.write(b);
					}
					break;

				case READING_COMMAND:
					b = buffer.get();
					if (b == '\n') {
						argsString = new String(argsStringBytes.toByteArray(), Charsets.ISO_8859_1);
						if (dataLength > 0) {
							toRead = dataLength;
							state = State.READING_DATA;
						} else {
							state = State.DONE;
						}
					} else {
						argsStringBytes.write(b);
					}
					break;

				case READING_DATA:
					int chunkSize = Math.min(toRead, buffer.remaining());
					byte[] chunk = new byte[chunkSize];
					buffer.get(chunk);
					dataBytes.write(chunk);
					toRead -= chunkSize;
					if (toRead == 0) {
						state = State.DONE;
					}
					break;

				case DONE:
					buffer.position(buffer.limit());
					break;
			}
		}
		return (state != State.DONE);
	}

	@Override
	public Message getResult() {
		if (state == State.DONE) {
			return new Message(argsString, dataBytes.toByteArray());
		}
		return null;
	}
}
