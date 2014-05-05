package cits3002.server.commands;

import java.util.Scanner;

public class CommandParser {
	public static Command parseCommand(String command, String binaryData) {
		Scanner sc = new Scanner(command);
		if (!sc.hasNext()) {
			return new UnsupportedCommand();
		}
		String type = sc.next();
		if (type.equals("FLE")) {
			return createFileCommand(command, binaryData);
		} else if (type.equals("CRT")) {
			return createCertificateCommand(command, binaryData);
		} else if (type.equals("LST")) {
			return createListCommand(command, binaryData);
		} else if (type.equals("VFY")) {
			return createVerifyCommand(command, binaryData);
		} else if (type.equals("FTC")) {
			return createFetchCommand(command, binaryData);
		}

		return new UnsupportedCommand();
	}

	public static Command createFileCommand(String command, String binaryData) {
		return new FileCommand(null, null);
	}

	public static Command createCertificateCommand(String command, String binaryData) {
		return new CertificateCommand(null, null);
	}

	public static Command createListCommand(String command, String binaryData) {
		return new ListCommand();
	}

	public static Command createVerifyCommand(String command, String binaryData) {
		return new VerifyCommand(null, null);
	}

	public static Command createFetchCommand(String command, String binaryData) {
		return new FetchCommand(null, 0);
	}
}
