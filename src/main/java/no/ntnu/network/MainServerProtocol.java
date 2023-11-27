package no.ntnu.network;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import no.ntnu.network.message.ClientType;
import no.ntnu.network.message.ConnectionMessage;
import no.ntnu.sigve.communication.Message;
import no.ntnu.sigve.server.Protocol;
import no.ntnu.sigve.server.Server;
import no.ntnu.tools.Logger;

public class MainServerProtocol implements Protocol {
	private Map<UUID, ClientType> clientTypeMapping;

	public MainServerProtocol() {
		super();
		this.clientTypeMapping = new HashMap<>();
	}

	@Override
	public void receiveMessage(Server server, Message<?> message) {
		// If the client is trying to be assigned a type, it will send a ConnectionMessage.
		if (message instanceof ConnectionMessage) {
			this.handleClientAssignment((ConnectionMessage) message);
		}
	}

	@Override
	public void onClientConnect(Server server, UUID clientId) {
		throw new UnsupportedOperationException("Unimplemented method 'onClientConnect'");
	}

	@Override
	public void onClientDisconnect(Server server, UUID clientId) {
		handleDisconnection(clientId);
	}

	/**
	 * Handles a client attempting to assign itself. Will fail if the client is already assigned.
	 *
	 * @param message the connection message containing the connection type.
	 */
	private void handleClientAssignment(ConnectionMessage message) {
		UUID clientId = message.getSource();
		if (clientTypeMapping.containsKey(clientId)) {
			Logger.error(String.format(
				"Could not assign %s's type as it has already been assigned.",
				clientId.toString()));
			return;
		}
		clientTypeMapping.put(clientId, message.getPayload());
	}

	/**
	 * Handles a client attempting to disconnect. Removes the client's assignment from the list.
	 *
	 * @param clientId the session id of the client attempting to disconnect.
	 */
	private void handleDisconnection(UUID clientId) {
		this.clientTypeMapping.remove(clientId);
	}

}
