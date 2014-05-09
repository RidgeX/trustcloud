package cits3002.server.commands;

import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedInts;

import java.security.NoSuchAlgorithmException;

public class CommandParser {
	public Command parseCommand(CommandTuple commandTuple) {
		Preconditions.checkArgument(commandTuple.args.length >= 1);

		String type = commandTuple.args[0];

		try {
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
			} else if (type.equals("HSH")) {
				return createHashCommand(commandTuple);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new UnsupportedCommand();
	}

	private Command createHashCommand(CommandTuple commandTuple) throws NoSuchAlgorithmException {
		Preconditions.checkArgument(commandTuple.args.length == 2);

		String filename = commandTuple.args[1];
		return new HashCommand(filename);
	}

	private Command createFileCommand(CommandTuple commandTuple) {
		Preconditions.checkArgument(commandTuple.args.length == 2);

		String filename = commandTuple.args[1];
		return new FileCommand(filename, commandTuple.data);
	}

	private Command createCertificateCommand(CommandTuple commandTuple) {
		Preconditions.checkArgument(commandTuple.args.length == 2);

		String filename = commandTuple.args[1];
		return new CertificateCommand(filename, commandTuple.data);
	}

	private Command createListCommand(CommandTuple commandTuple) {
		return new ListCommand();
	}

	private Command createVerifyCommand(CommandTuple commandTuple) {
		return new VerifyCommand(null, null);
	}

	private Command createFetchCommand(CommandTuple commandTuple) {
		Preconditions.checkArgument(commandTuple.args.length == 3);

		String filename = commandTuple.args[1];
		int requiredCircumference = UnsignedInts.parseUnsignedInt(commandTuple.args[2]);
		return new FetchCommand(filename, requiredCircumference);
	}
}
