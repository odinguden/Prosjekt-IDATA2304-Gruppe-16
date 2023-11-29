package no.ntnu.network.message;

import java.io.Serializable;
import java.util.UUID;
import no.ntnu.sigve.communication.Message;

public class NodeInfoMessage extends Message<NodeInfoMessage.NodeInfoPayload> {

	public NodeInfoMessage(UUID destination, NodeInfoPayload payload) {
		super(destination, payload);
	}

	//TODO: Odin, implement this
	public record NodeInfoPayload(
			// Type someParam,
			// AnotherType someOtherParam
	) implements Serializable {}
}
