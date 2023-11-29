package no.ntnu.network;

import java.util.UUID;

public class StaticIds {
	public static final UUID CP_BROADCAST = UUID.fromString("0");
	public static final UUID NODE_BROADCAST = UUID.fromString("1");

	private StaticIds() {
		throw new IllegalStateException("Utility class");
	}
}
