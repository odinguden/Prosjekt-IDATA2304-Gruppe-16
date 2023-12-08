package no.ntnu.network.message;

import java.util.UUID;

import no.ntnu.sigve.communication.Message;

/**
 * A message that is fired whenever a node disconnects. Contains the disconnected client's
 * session ID as payload.
 */
public class NodeDisconnectMessage extends Message<UUID> {
	/**
	 * Creates a disconnection message.
	 *
	 * @param destination the destination of this message
	 * @param quitter the quitting client
	 */
	public NodeDisconnectMessage(UUID destination, UUID quitter) {
		super(destination, quitter);
	}
}
