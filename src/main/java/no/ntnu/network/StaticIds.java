package no.ntnu.network;

import java.util.UUID;

public class StaticIds {
	public static final UUID CP_BROADCAST = UUID.fromString("00000000-0000-0000-0000-000000000000");
	public static final UUID NODE_BROADCAST = UUID.fromString("ffffffff-ffff-ffff-ffff-ffffffffffff");

	private StaticIds() {
		throw new IllegalStateException("Utility class");
	}
}
