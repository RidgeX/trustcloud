package cits3002.common.messages;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.ByteProcessor;
import com.google.common.primitives.UnsignedInts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * A utility class for reading messages.
 */
public class MessageReader implements ByteProcessor<Message> {
	/**
	 * The maximum allowed size for the message payload.
	 */
	private static final int MAX_BINARY_DATA = 100 * 1024 * 1024; // 100 MB

	private final ByteArrayOutputStream byteStream;
	private String currentLine;
	private State state;

	private String typeString;
	private int argsLength;
	private int dataLength;
	private final List<String> args;
	private byte[] data;


	/**
	 * The possible reading states.
	 */
	private enum State {
		READING_TYPE,
		READING_ARGUMENT_LENGTH,
		READING_DATA_LENGTH,
		READING_ARGUMENTS,
		READING_DATA,
		DONE
	}

	/**
	 * Construct a new message reader.
	 */
	public MessageReader() {
		this.byteStream = new ByteArrayOutputStream();
		this.currentLine = null;
		this.state = State.READING_TYPE;

		this.args = Lists.newArrayList();
		this.data = new byte[0];
	}

	/**
	 * Process the given bytes.
	 *
	 * @param bytes The bytes to be processed
	 * @param off   The offset
	 * @param len   The length
	 * @return true if there is more data to process
	 */
	@Override public boolean processBytes(byte[] bytes, int off, int len) throws IOException {
		int idx = 0;
		while (idx < len) {
			idx += processBytesInternal(bytes, off + idx, len - idx);
		}
		return state != State.DONE;
	}

	/**
	 * Return the resulting message.
	 *
	 * @return The message
	 */
	@Override public Message getResult() {
		if (state != State.DONE) {
			return null;
		} else {
			return MessageUtil.createMessage(typeString, args, data);
		}
	}

	/**
	 * Try reading a line from the given bytes.
	 *
	 * @param bytes The bytes being processed
	 * @param off   The offset
	 * @param len   The length
	 * @param out   The in-memory line buffer
	 * @return number of bytes consumed
	 */
	private int readLine(byte[] bytes, int off, int len, ByteArrayOutputStream out) {
		currentLine = null;
		int lastIdx = 0;
		while (lastIdx < len && bytes[off + lastIdx] != '\n') {
			++lastIdx;
		}
		out.write(bytes, off, lastIdx);

		if (lastIdx < len && bytes[off + lastIdx] == '\n') {
			currentLine = new String(out.toByteArray(), Charsets.ISO_8859_1);
			out.reset();
		}
		return Math.min(len, lastIdx + 1);
	}

	/**
	 * Process the given bytes and return the number of bytes read.
	 *
	 * @param bytes The bytes to be processed.
	 * @param off   The offset
	 * @param len   The length
	 * @return The number of bytes read
	 */
	private int processBytesInternal(byte[] bytes, int off, int len) {
		if (state == State.DONE) {
			return len;
		}

		if (state == State.READING_DATA) {
			int toRead = Math.min(dataLength, len);
			byteStream.write(bytes, off, toRead);
			dataLength -= toRead;
			if (dataLength == 0) {
				data = byteStream.toByteArray();
				state = State.DONE;
			}
			return toRead;
		}

		int bytesRead = readLine(bytes, off, len, byteStream);
		if (currentLine != null) {
			switch (state) {
				case READING_TYPE:
					typeString = currentLine;
					state = State.READING_ARGUMENT_LENGTH;
					break;
				case READING_ARGUMENT_LENGTH:
					argsLength = UnsignedInts.parseUnsignedInt(currentLine);
					state = State.READING_DATA_LENGTH;
					break;
				case READING_DATA_LENGTH:
					dataLength = UnsignedInts.parseUnsignedInt(currentLine);
					if (dataLength > MAX_BINARY_DATA) {
						throw new IllegalArgumentException();
					}
					state = argsLength == 0 ?
							(dataLength == 0 ? State.DONE : State.READING_DATA) :
							State.READING_ARGUMENTS;
					break;
				case READING_ARGUMENTS:
					args.add(currentLine);

					if (args.size() == argsLength) {
						state = dataLength == 0 ? State.DONE : State.READING_DATA;
					}
					break;
			}
		}
		return bytesRead;
	}
}
