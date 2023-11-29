package no.ntnu.network.message;

import java.io.Serializable;
import no.ntnu.network.StaticIds;
import no.ntnu.sigve.communication.Message;

public class NodeInfoRequestMessage extends Message<Serializable> {
	public NodeInfoRequestMessage() {
		super(StaticIds.NODE_BROADCAST, null);
	}
}
