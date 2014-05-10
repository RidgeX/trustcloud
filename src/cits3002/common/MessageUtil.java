package cits3002.common;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.ByteStreams;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MessageUtil {
	public static Message receive(DataInputStream in) throws IOException {
		MessageReader reader = new MessageReader();
		ByteStreams.readBytes(in, reader);
		Message result = reader.getResult();
		return result;
	}

	public static void send(DataOutputStream out, Message msg) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(msg.data.length + "\n");
		sb.append(Joiner.on('|').join(msg.args) + "\n");
		out.write(sb.toString().getBytes(Charsets.ISO_8859_1));
		out.write(msg.data);
		out.flush();
	}
}
