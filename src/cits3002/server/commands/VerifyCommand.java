package cits3002.server.commands;

import cits3002.server.TrustLayer;
import cits3002.util.CommandUtil;
import cits3002.util.SecurityUtil;
import com.google.common.base.Preconditions;

public class VerifyCommand implements Command {
	private final String filename;
	private final byte[] data;
	private final TrustLayer trustLayer;

	public VerifyCommand(String filename, byte[] data) {
		Preconditions.checkNotNull(filename);
		Preconditions.checkNotNull(data);

		this.filename = filename;
		this.data = data;
		this.trustLayer = new TrustLayer();
	}

	@Override public byte[] execute() throws Exception {
		try {
			SecurityUtil.UnpackedSignature unpackedSignature = SecurityUtil.unpackSignature(data);
			if (trustLayer.addSignatureForFile(
					filename,
					unpackedSignature.publicKey,
					unpackedSignature.signatureData)) {
				return CommandUtil.serialiseCommand("SUC", "Verified file");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return CommandUtil.serialiseCommand("FAL", "Could not verify file");
	}
}
