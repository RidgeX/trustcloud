package cits3002.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Command {
	GET("GET"),
	HASH("HASH"),
	LIST("LIST"),
	PUT("PUT"),
	VOUCH("VOUCH");

	public static Map<String, Command> lookup;
	static {
		lookup = new HashMap<String, Command>();
		for (Command cmd : values()) {
			lookup.put(cmd.name, cmd);
		}
		lookup = Collections.unmodifiableMap(lookup);
	}

	public String name;

	private Command(String name) {
		this.name = name;
	}
}
