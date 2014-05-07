package cits3002.server.commands;

import cits3002.util.CommandUtil;
import com.google.common.io.ByteProcessor;
import com.google.common.primitives.UnsignedInts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class CommandReader implements ByteProcessor<CommandTuple> {
	private static final int MAX_BINARY_DATA = 100 * 1024 * 1024; // 100 MB

	private ByteArrayOutputStream dataLengthBytes;
	private ByteArrayOutputStream argsStringBytes;
	private ByteArrayOutputStream dataBytes;
	private int dataLength;
	private String argsString;
	private State state;


	private enum State {
		READING_DATA_LENGTH,
		READING_COMMAND,
		READING_DATA,
		DONE
	}

	public CommandReader() {
		this.dataLengthBytes = new ByteArrayOutputStream();
		this.argsStringBytes = new ByteArrayOutputStream();
		this.dataBytes = new ByteArrayOutputStream();
		this.state = State.READING_DATA_LENGTH;
	}

	@Override public boolean processBytes(byte[] bytes, int off, int len) throws IOException {
		int idx = 0;
		while (idx < len) {
			idx += processBytesInternal(bytes, off + idx, len - idx);
		}
		return state != State.DONE;
	}

	@Override public CommandTuple getResult() {
		if (state != State.DONE) {
			return null;
		} else {
			return CommandUtil.makeCommandTuple(argsString, dataBytes.toByteArray());
		}
	}

	private int processBytesInternal(byte[] bytes, int off, int len)
			throws UnsupportedEncodingException {
		int lastIdx = 0;
		switch (state) {
			case READING_DATA_LENGTH:
				while (lastIdx < len && bytes[off + lastIdx] != '\n') {
					++lastIdx;
				}
				dataLengthBytes.write(bytes, off, lastIdx);

				if (lastIdx < len && bytes[off + lastIdx] == '\n') {
					dataLength = UnsignedInts.parseUnsignedInt(dataLengthBytes.toString("ISO-8859-1"));

					if (dataLength > MAX_BINARY_DATA) {
						throw new IllegalArgumentException();
					}
					state = State.READING_COMMAND;
					lastIdx++;
				}
				break;
			case READING_COMMAND:
				while (lastIdx < len && bytes[off + lastIdx] != '\n') {
					lastIdx++;
				}
				argsStringBytes.write(bytes, off, lastIdx);

				if (lastIdx < len && bytes[off + lastIdx] == '\n') {
					argsString = argsStringBytes.toString("ISO-8859-1");
					if (dataLength != 0) {
						state = State.READING_DATA;
					} else {
						state = State.DONE;
					}
					lastIdx++;
				}

				break;
			case READING_DATA:
				int toRead = Math.min(dataLength, len);
				dataBytes.write(bytes, off, toRead);
				dataLength -= toRead;
				if (dataLength == 0) {
					state = State.DONE;
				}
				lastIdx = toRead;
				break;
			case DONE:
				lastIdx = len;
				break;
		}
		return lastIdx;
	}
}
