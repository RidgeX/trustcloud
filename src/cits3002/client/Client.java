package cits3002.client;

import cits3002.server.commands.CommandTuple;
import cits3002.util.CommandUtil;
import cits3002.util.SecurityUtil;
import com.google.common.io.BaseEncoding;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.InetAddress;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

import static com.google.common.base.Charsets.ISO_8859_1;

public class Client {
	private final MessageDigest digestInstance;

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public Client() throws NoSuchAlgorithmException {
		digestInstance = MessageDigest.getInstance("SHA-1");
	}

	public static void main(String[] args)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException,
			NoSuchProviderException, SignatureException {
		Client client = new Client();
		client.run(4433);
	}

	public CommandTuple runCommand(CommandTuple commandTuple, int port) {
		try {
			SSLSocketFactory socketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
			SSLSocket socket =
					(SSLSocket) socketFactory.createSocket(InetAddress.getLoopbackAddress(), port);
			socket.setEnabledCipherSuites(socket.getSupportedCipherSuites());

			InputStreamReader inp = new InputStreamReader(socket.getInputStream(), ISO_8859_1);
			OutputStream out = socket.getOutputStream();

			out.write(CommandUtil.serialiseCommand(commandTuple));
			out.flush();

			String resultString = CharStreams.toString(inp);

			inp.close();
			out.close();
			socket.close();

			return CommandUtil.parseCommandData(resultString);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void run(int port)
			throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, InvalidKeyException,
			NoSuchProviderException, SignatureException {
		CommandTuple uploadCommand = CommandUtil.makeCommandTuple(
				"FLE test.txt",
				Files.toByteArray(new File("../res/test.crt")));


		CommandTuple hashCommand = CommandUtil.makeCommandTuple("HSH test.txt");
		CommandTuple listCommand = CommandUtil.makeCommandTuple("LST");
		CommandTuple fetchCommand = CommandUtil.makeCommandTuple("FTC test.txt 0");

		CommandTuple uploadCertCommand = CommandUtil.makeCommandTuple(
				"CRT test.crt",
				Files.toByteArray(new File("../res/test.crt")));

		CommandTuple uploadInvalidCertCommand = CommandUtil.makeCommandTuple(
				"CRT test2.crt",
				Files.toByteArray(new File("../res/hello.rsa")));

		String keyData = CharStreams.toString(
				new BufferedReader(new InputStreamReader(new FileInputStream("../res/test.key"), ISO_8859_1)));
		KeyPair keyPair = SecurityUtil.loadKeyPair(keyData, null);
		byte[] fileData = Files.toByteArray(new File("../res/test.crt"));
		byte[] sigData = SecurityUtil.signData(fileData, keyPair);
		fileData[0]++;
		byte[] badSigData = SecurityUtil.signData(fileData, keyPair);
		String badSigString = SecurityUtil.packSignature(keyPair.getPublic(), badSigData);
		String sigString = SecurityUtil.packSignature(keyPair.getPublic(), sigData);
		CommandTuple badVerifyCommand = CommandUtil.makeCommandTuple("VFY test.txt", badSigString);
		CommandTuple verifyCommand = CommandUtil.makeCommandTuple("VFY test.txt", sigString);

		CommandTuple uploadResult = runCommand(uploadCommand, port);
		CommandTuple hashResult = runCommand(hashCommand, port);
		CommandTuple listResult = runCommand(listCommand, port);
		CommandTuple fetchResult = runCommand(fetchCommand, port);
		CommandTuple uploadCertResult = runCommand(uploadCertCommand, port);
		CommandTuple uploadInvalidCertResult = runCommand(uploadInvalidCertCommand, port);
		CommandTuple verifyResult = runCommand(verifyCommand, port);
		CommandTuple badVerifyResult = runCommand(badVerifyCommand, port);

		System.out.println("Upload result: " + uploadResult.getArgumentString());
		System.out.println("Hash result: " + hashResult.getArgumentString());
		System.out.println("Hash: " + BaseEncoding.base16().lowerCase().encode(hashResult.data));
		System.out.println("List:");
		System.out.println(listResult.getDataString());
		System.out.println("Fetch result: " + fetchResult.getArgumentString());
		System.out.println("Hash of fetch: " + BaseEncoding.base16().lowerCase()
				.encode(digestInstance.digest(fetchResult.data)));

		System.out.println("Upload valid cert result: " + uploadCertResult.getArgumentString());
		System.out.println("Upload invalid cert result: " + uploadInvalidCertResult.getArgumentString());

		System.out.println("Bad verify result: " + badVerifyResult.getArgumentString());
		System.out.println("Verify result: " + verifyResult.getArgumentString());
	}
}
