package no.ntnu.network.message;

import java.io.Serializable;
import no.ntnu.network.StaticIds;
import no.ntnu.sigve.communication.Message;

/**
 * A payloadless message used to request information about sensors from the server.
 */
public class NodeInfoRequestMessage extends Message<Serializable> {
	/**
	 * Creates a new node info request message.
	 */
	public NodeInfoRequestMessage() {
		super(StaticIds.NODE_BROADCAST, null);
	}
}
