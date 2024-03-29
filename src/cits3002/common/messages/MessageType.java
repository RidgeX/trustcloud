package cits3002.common.messages;

/**
 * The possible message types.
 */
public enum MessageType {
	GET("GET"),
	LIST("LIST"),
	PUT("PUT"),
	VOUCH("VOUCH"),
	OK("OK"),
	FAIL("FAIL"),
	INVALID("INVALID");

	public final String name;

	/**
	 * Construct a new message type.
	 *
	 * @param name The type name
	 */
	private MessageType(String name) {
		this.name = name;
	}
}
