package cits3002.client;

import cits3002.common.*;
import com.google.common.io.Files;
import gnu.getopt.Getopt;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.security.PrivateKey;
import java.security.Security;

public class Client {
	private static final String[] ANON_CIPHERS = new String[] {
			"TLS_DH_anon_WITH_AES_256_CBC_SHA256",
			"TLS_DH_anon_WITH_AES_256_CBC_SHA",
			"TLS_DH_anon_WITH_AES_128_CBC_SHA256",
			"TLS_DH_anon_WITH_AES_128_CBC_SHA"
	};
	private static final int DEFAULT_PORT = 4433;

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static void main(String[] args) throws Exception {
		InetAddress host = InetAddress.getLoopbackAddress();
		int port = DEFAULT_PORT;

		Command cmd = null;
		String fileName = null;
		String certName = null;
		int minRingLength = 3;

		Getopt g = new Getopt("Client", args, "a:c:f:h:lu:v:");
		g.setOpterr(false);
		int c;
		String arg;
		while ((c = g.getopt()) != -1) {
			arg = g.getOptarg();
			switch (c) {
				case 'a':  // Add new file
					if (cmd != null) {
						usage();
					}
					cmd = Command.PUT;
					fileName = arg;
					break;

				case 'c':  // Ring circumference
					minRingLength = Integer.parseInt(arg);
					break;

				case 'f':  // Fetch file
					if (cmd != null) {
						usage();
					}
					cmd = Command.GET;
					fileName = arg;
					break;

				case 'h':  // Host and port
					String[] addr = arg.split(":");
					host = InetAddress.getByName(addr[0]);
					port = Integer.parseInt(addr[1]);
					break;

				case 'l':  // List files
					cmd = Command.LIST;
					break;

				case 'u':  // Upload new certificate
					if (cmd != null) {
						usage();
					}
					cmd = Command.PUT;
					certName = arg;
					break;

				case 'v':  // Vouch for file
					if (cmd != null) {
						usage();
					}
					cmd = Command.VOUCH;
					fileName = arg;
					int optind = g.getOptind();
					if (optind == args.length) {
						usage();
					}
					certName = args[optind];
					g.setOptind(optind + 1);
					break;

				default:
					usage();
			}
		}
		if (cmd == null) {
			usage();
		}

		Client client = new Client();
		client.run(host, port, cmd, fileName, certName, minRingLength);
	}

	private static void usage() {
		System.err.println("Usage: java Client [options]");
		System.err.println("\t-a filename");
		System.err.println("\t\tadd or replace a file to the trustcloud");
		System.err.println("\t-c length");
		System.err.println("\t\tprovide the required length of a ring of trust");
		System.err.println("\t-f filename");
		System.err.println("\t\tfetch an existing file from the trustcloud server");
		System.err.println("\t-h hostname:port");
		System.err.println("\t\tprovide the remote address hosting the trustcloud server");
		System.err.println("\t-l");
		System.err.println("\t\tlist all stored files and how they are protected");
		System.err.println("\t-u filename");
		System.err.println("\t\tupload a certificate to the trustcloud server");
		System.err.println("\t-v filename certname");
		System.err.println("\t\tvouch for the authenticity of an existing file in the");
		System.err.println("\t\ttrustcloud server using the indicated certificate");
		System.exit(1);
	}

	public void run(InetAddress host, int port, Command cmd, String fileName, String certName,
			int minRingLength) throws Exception {
		String args;
		byte[] data;
		Message response = null;

		switch (cmd) {
			case PUT:
				File file;
				int isCert;
				if (certName != null) {
					file = new File(certName);
					isCert = 1;
				} else {
					file = new File(fileName);
					isCert = 0;
				}
				args = String.format("%s|%s|%d", Command.PUT.name, file.getName(), isCert);
				data = Files.toByteArray(file);
				response = sendReceive(host, port, new Message(args, data));
				if (response.args[0].equals(CommandHandler.RESULT_OK)) {
					System.out.println(response.getDataString());
				} else {
					System.err.println("Error: " + response.getDataString());
				}
				break;

			case GET:
				args = String.format("%s|%s|%d", Command.GET.name, fileName, minRingLength);
				response = sendReceive(host, port, new Message(args));
				if (response.args[0].equals(CommandHandler.RESULT_OK)) {
					DataOutputStream out = new DataOutputStream(System.out);
					out.write(response.data);
				} else {
					System.err.println("Error: " + response.getDataString());
				}
				break;

			case LIST:
				args = Command.LIST.name;
				response = sendReceive(host, port, new Message(args));
				if (response.args[0].equals(CommandHandler.RESULT_OK)) {
					System.out.print(response.getDataString());
				} else {
					System.err.println("Error: " + response.getDataString());
				}
				break;

			case VOUCH:
				args = String.format("%s|%s", Command.HASH.name, fileName);
				response = sendReceive(host, port, new Message(args));
				if (!response.args[0].equals(CommandHandler.RESULT_OK)) {
					System.err.println("Error: " + response.getDataString());
					return;
				}

				File certFile = new File(certName);
				File keyFile = new File(certName + ".key");

				byte[] keyData = Files.toByteArray(keyFile);
				PrivateKey privateKey = SecurityUtil.loadKeyPair(keyData).getPrivate();
				byte[] hash = response.data;
				byte[] sigData = SecurityUtil.makeSignature(privateKey, hash);

				args = String.format("%s|%s|%s", Command.VOUCH.name, fileName, certFile.getName());
				response = sendReceive(host, port, new Message(args, sigData));
				if (response.args[0].equals(CommandHandler.RESULT_OK)) {
					System.out.println(response.getDataString());
				} else {
					System.err.println("Error: " + response.getDataString());
				}
				break;

			default:
				break;
		}
	}

	public Message sendReceive(InetAddress host, int port, Message request) throws IOException {
		SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket socket = (SSLSocket) socketFactory.createSocket(host, port);
		socket.setEnabledCipherSuites(ANON_CIPHERS);
		DataInputStream in = new DataInputStream(socket.getInputStream());
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());

		MessageUtil.send(out, request);
		Message response = MessageUtil.receive(in);

		socket.close();
		return response;
	}
}
