package no.ntnu.network.message;

import no.ntnu.sigve.communication.Message;

/**
 * A destinationless message containing the client type of the source.
 * <p>Determines whether a client is treated as a node or a control panel.</p>
 */
public class ConnectionMessage extends Message<ClientType> {

	/**
	 * Creates a new connectionMessage with no destination and a clientType as a payload.
	 * @param clientType
	 */
	public ConnectionMessage(ClientType clientType) {
		super(null, clientType);
	}
}
