package cits3002.server.commands;

import java.util.Scanner;

public class CommandParser {
	public Command parseCommand(CommandTuple commandTuple) {
		Scanner sc = new Scanner(commandTuple.commandString);

		String type = sc.next();
		if (type.equals("FLE")) {
			return createFileCommand(commandTuple);
		} else if (type.equals("CRT")) {
			return createCertificateCommand(commandTuple);
		} else if (type.equals("LST")) {
			return createListCommand(commandTuple);
		} else if (type.equals("VFY")) {
			return createVerifyCommand(commandTuple);
		} else if (type.equals("FTC")) {
			return createFetchCommand(commandTuple);
		}

		return new UnsupportedCommand();
	}

	public Command createFileCommand(CommandTuple commandTuple) {
		Scanner sc = new Scanner(commandTuple.commandString);
		sc.next();
		String filename = sc.next();
		return new FileCommand(filename, commandTuple.binaryData);
	}

	public Command createCertificateCommand(CommandTuple commandTuple) {
		return new CertificateCommand(null, null);
	}

	public Command createListCommand(CommandTuple commandTuple) {
		return new ListCommand();
	}

	public Command createVerifyCommand(CommandTuple commandTuple) {
		return new VerifyCommand(null, null);
	}

	public Command createFetchCommand(CommandTuple commandTuple) {
		return new FetchCommand(null, 0);
	}
}
