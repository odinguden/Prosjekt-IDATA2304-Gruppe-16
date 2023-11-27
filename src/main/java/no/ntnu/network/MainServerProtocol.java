package no.ntnu.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	private List<UUID> controlpanelNodes;

	public MainServerProtocol() {
		super();
		this.clientTypeMapping = new HashMap<>();
		this.controlpanelNodes = new ArrayList<>();
	}

	@Override
	public void receiveMessage(Server server, Message<?> message) {
		UUID destination = message.getDestination();

		if (destination == null) {
			handleMessageIntendedForServer(server, message);
		} else if (destination.toString().equals("0")) {
			server.broadcast(message);
		} else {
			server.route(message);
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

	private void handleMessageIntendedForServer(Server server, Message<?> message) {
		if (message instanceof ConnectionMessage) {
			this.handleClientAssignment((ConnectionMessage) message);
		}
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

	private void broadcastToAllControlPanels(Server server, Message message) {

	}

}
