package cits3002.client;

import cits3002.common.SecurityUtil;
import cits3002.common.messages.Message;
import cits3002.common.messages.MessageType;
import cits3002.common.messages.MessageUtil;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import com.google.common.primitives.UnsignedInts;
import gnu.getopt.Getopt;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.security.KeyPair;
import java.security.Security;

/**
 * The Trustcloud client application.
 *
 * @author Eliot Courtney (21141563), Ridge Shrubsall (21112211)
 */
public class Client {
	/**
	 * A list of allowed cipher suites for establishing a connection.
	 */
	private static final String[] ANONYMOUS_CIPHERS = new String[] {
			"TLS_DH_anon_WITH_AES_128_CBC_SHA",
			"SSL_DH_anon_WITH_RC4_128_MD5"
	};

	/**
	 * The default port for connecting to the server.
	 */
	private static final int DEFAULT_PORT = 4433;

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * Main method for the client.
	 *
	 * @param args The command-line options to be used
	 */
	public static void main(String[] args) throws Exception {
		InetAddress host = InetAddress.getLocalHost();
		int port = DEFAULT_PORT;

		MessageType messageType = null;
		String filename = null;
		String certificateName = null;
		int minimumRingLength = 0;

		// Parse options
		Getopt g = new Getopt("Client", args, "a:c:f:h:lu:v:");
		g.setOpterr(false);
		int c;
		String arg;
		while ((c = g.getopt()) != -1) {
			arg = g.getOptarg();
			switch (c) {
				case 'a':  // Add new file
					if (messageType != null) {
						usage();
					}
					messageType = MessageType.PUT;
					filename = arg;
					break;

				case 'c':  // Ring circumference
					minimumRingLength = Integer.parseInt(arg);
					break;

				case 'f':  // Fetch file
					if (messageType != null) {
						usage();
					}
					messageType = MessageType.GET;
					filename = arg;
					break;

				case 'h':  // Host and port
					String[] addr = Iterables.toArray(
							Splitter.on(':').omitEmptyStrings().trimResults().split(arg),
							String.class);
					host = InetAddress.getByName(addr[0]);
					port = UnsignedInts.parseUnsignedInt(addr[1]);
					break;

				case 'l':  // List files
					messageType = MessageType.LIST;
					break;

				case 'u':  // Upload new certificate
					if (messageType != null) {
						usage();
					}
					messageType = MessageType.PUT;
					certificateName = arg;
					break;

				case 'v':  // Vouch for file
					if (messageType != null) {
						usage();
					}
					messageType = MessageType.VOUCH;
					filename = arg;
					int optind = g.getOptind();
					if (optind == args.length) {
						usage();
					}
					certificateName = args[optind];
					g.setOptind(optind + 1);
					break;

				default:
					usage();
			}
		}
		if (messageType == null) {
			usage();
		}

		// Start client
		Client client = new Client();
		client.run(host, port, messageType, filename, certificateName, minimumRingLength);
	}

	/**
	 * Print usage and exit.
	 */
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

	/**
	 * Starts the client process.
	 *
	 * @param host              The server's address
	 * @param port              The server's port
	 * @param messageType       The message type
	 * @param filename          The name of the file to upload/fetch/vouch
	 * @param certificateName   The name of the certificate to upload/sign with
	 * @param minimumRingLength The minimum trust requirement
	 */
	public void run(InetAddress host, int port, MessageType messageType, String filename,
			String certificateName, int minimumRingLength) throws Exception {
		Message request = null;
		// Construct request message
		switch (messageType) {
			case PUT:
				File file;
				String isCertificate;
				if (certificateName != null) {
					file = new File(certificateName);
					isCertificate = "C";
				} else {
					file = new File(filename);
					isCertificate = "F";
				}
				request = MessageUtil
						.createMessage(messageType, ImmutableList.of(file.getName(), isCertificate),
								Files.toByteArray(file));
				break;

			case GET:
				request = MessageUtil.createMessage(messageType,
						ImmutableList.of(filename, Integer.toString(minimumRingLength)));
				break;

			case LIST:
				request = MessageUtil.createMessage(messageType);
				break;

			case VOUCH:
				File keyFile = new File(certificateName + ".key");

				KeyPair keyPair = SecurityUtil.loadKeyPair(Files.toByteArray(keyFile));
				byte[] sigData =
						SecurityUtil.signData(Files.toByteArray(new File(filename)), keyPair.getPrivate());

				request = MessageUtil.createMessage(
						messageType,
						ImmutableList.of(new File(filename).getName(),
								SecurityUtil.base64Encode(keyPair.getPublic().getEncoded()),
								SecurityUtil.base64Encode(sigData))
				);
				break;
		}

		// Send request and print response data
		if (request != null) {
			Message response = doRequest(host, port, request);
			if (response != null) {
				if (Objects.equal(response.type, MessageType.OK)) {
					if (Objects.equal(messageType, MessageType.GET)) {
						DataOutputStream out = new DataOutputStream(System.out);
						out.write(response.data);
					} else {
						System.out.println(response.getDataString());
					}
				} else {
					System.err.println("Error: " + response.getDataString());
				}
			} else {
				System.err.println("Unknown error.");
			}
		} else {
			System.err.println("Error constructing message to server.");
		}
	}

	/**
	 * Sends a request to the server and returns the response.
	 *
	 * @param host    The server's address
	 * @param port    The server's port
	 * @param request The request message
	 * @return The response message
	 */
	public Message doRequest(InetAddress host, int port, Message request) throws IOException {
		SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
		SSLSocket socket = (SSLSocket) socketFactory.createSocket(host, port);
		socket.setEnabledCipherSuites(ANONYMOUS_CIPHERS);

		socket.getOutputStream().write(MessageUtil.serialiseMessage(request));
		Message response = MessageUtil.parseMessage(socket.getInputStream());

		socket.close();
		return response;
	}
}
