package cits3002.common.messages;

import com.google.common.io.ByteProcessor;
import com.google.common.primitives.UnsignedInts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MessageReader implements ByteProcessor<Message> {
	private static final int MAX_BINARY_DATA = 100 * 1024 * 1024; // 100 MB

	private ByteArrayOutputStream dataLengthBytes;
	private ByteArrayOutputStream typeStringBytes;
	private ByteArrayOutputStream argsStringBytes;
	private ByteArrayOutputStream dataBytes;
	private int dataLength;
	private String typeString;
	private String argsString;
	private State state;


	private enum State {
		READING_DATA_LENGTH,
		READING_TYPE,
		READING_ARGUMENTS,
		READING_DATA,
		DONE
	}

	public MessageReader() {
		this.dataLengthBytes = new ByteArrayOutputStream();
		this.typeStringBytes = new ByteArrayOutputStream();
		this.argsStringBytes = new ByteArrayOutputStream();
		this.dataBytes = new ByteArrayOutputStream();
		this.dataLength = 0;
		this.state = State.READING_DATA_LENGTH;
	}

	@Override public boolean processBytes(byte[] bytes, int off, int len) throws IOException {
		int idx = 0;
		while (idx < len) {
			idx += processBytesInternal(bytes, off + idx, len - idx);
		}
		return state != State.DONE;
	}

	@Override public Message getResult() {
		if (state != State.DONE) {
			return null;
		} else {
			return MessageUtil.createMessage(typeString, argsString, dataBytes.toByteArray());
		}
	}

	private boolean readLine(byte[] bytes, int off, int len, ByteArrayOutputStream out) {
		int lastIdx = 0;
		while (lastIdx < len && bytes[off + lastIdx] != '\n') {
			++lastIdx;
		}
		out.write(bytes, off, lastIdx);

		return lastIdx < len && bytes[off + lastIdx] == '\n';
	}

	private int processBytesInternal(byte[] bytes, int off, int len)
			throws UnsupportedEncodingException {
		switch (state) {
			case READING_DATA_LENGTH:
				if (readLine(bytes, off, len, dataLengthBytes)) {
					dataLength = UnsignedInts.parseUnsignedInt(dataLengthBytes.toString("ISO-8859-1"));

					if (dataLength > MAX_BINARY_DATA) {
						throw new IllegalArgumentException();
					}
					state = State.READING_TYPE;

					return dataLengthBytes.size() + 1;
				}
				break;
			case READING_TYPE:
				if (readLine(bytes, off, len, typeStringBytes)) {
					typeString = typeStringBytes.toString("ISO-8859-1");
					state = State.READING_ARGUMENTS;

					return typeStringBytes.size() + 1;
				}
				break;
			case READING_ARGUMENTS:
				if (readLine(bytes, off, len, argsStringBytes)) {
					argsString = argsStringBytes.toString("ISO-8859-1");

					if (dataLength != 0) {
						state = State.READING_DATA;
					} else {
						state = State.DONE;
					}
					return argsStringBytes.size() + 1;
				}
				break;
			case READING_DATA:
				int toRead = Math.min(dataLength, len);
				dataBytes.write(bytes, off, toRead);
				dataLength -= toRead;
				if (dataLength == 0) {
					state = State.DONE;
				}
				return toRead;
		}
		return len;
	}
}
