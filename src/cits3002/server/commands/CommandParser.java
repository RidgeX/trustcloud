package cits3002.server.commands;

import java.util.Scanner;

public class CommandParser {
	public Command parseCommand(String command, String binaryData) {
		Scanner sc = new Scanner(command);

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

	public Command createFileCommand(String command, String binaryData) {
		Scanner sc = new Scanner(command);
		sc.next();
		String filename = sc.next();
		return new FileCommand(filename, binaryData);
	}

	public Command createCertificateCommand(String command, String binaryData) {
		return new CertificateCommand(null, null);
	}

	public Command createListCommand(String command, String binaryData) {
		return new ListCommand();
	}

	public Command createVerifyCommand(String command, String binaryData) {
		return new VerifyCommand(null, null);
	}

	public Command createFetchCommand(String command, String binaryData) {
		return new FetchCommand(null, 0);
	}
}
