package cits3002.common.messages;

public enum MessageType {
	GET("GET"),
	HASH("HASH"),
	LIST("LIST"),
	PUT("PUT"),
	VOUCH("VOUCH"),
	OK("OK"),
	FAIL("FAIL"),
	INVALID("INVALID");

	public String name;

	private MessageType(String name) {
		this.name = name;
	}
}
