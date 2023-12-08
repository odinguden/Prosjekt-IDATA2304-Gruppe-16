package no.ntnu.network;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import no.ntnu.network.message.ClientType;
import no.ntnu.network.message.ConnectionMessage;
import no.ntnu.network.message.NodeDisconnectMessage;
import no.ntnu.network.message.NodeInfoMessage;
import no.ntnu.sigve.communication.Message;
import no.ntnu.sigve.communication.Protocol;
import no.ntnu.sigve.server.Server;
import no.ntnu.tools.Logger;

public class MainServerProtocol implements Protocol<Server> {
	private final Map<UUID, ClientType> clientTypeMapping;

	public MainServerProtocol() {
		super();
		this.clientTypeMapping = new HashMap<>();
	}

	@Override
	public void receiveMessage(Server server, Message<?> message) {
		UUID destination = message.getDestination();

		Logger.info("Got message");

		if (destination == null) {
			handleMessageIntendedForServer(server, message);
		} else if (destination.equals(StaticIds.CP_BROADCAST)) {
			broadcastToAllControlPanels(server, message);
		} else if (destination.equals(StaticIds.NODE_BROADCAST)) {
			broadcastToAllNodes(server, message);
		} else {
			server.route(message);
		}
	}

	@Override
	public void onClientConnect(Server server, UUID clientId) {
		Logger.info("Client connected " + clientId);
	}

	@Override
	public void onClientDisconnect(Server server, UUID clientId) {
		handleDisconnection(server, clientId);
	}

	private void handleMessageIntendedForServer(Server server, Message<?> message) {
		if (message instanceof ConnectionMessage connectionMessage) {
			this.handleClientAssignment(connectionMessage);
		} else {
			Logger.error(String.format(
				"Received unknown message from %s, discarding.",
				message.getSource().toString()));
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
	private void handleDisconnection(Server server, UUID clientId) {
		ClientType type = this.clientTypeMapping.get(clientId);
		this.clientTypeMapping.remove(clientId);
		if (type == ClientType.NODE) {
			NodeDisconnectMessage message = new NodeDisconnectMessage(StaticIds.CP_BROADCAST, clientId);
			broadcastToAllControlPanels(server, message);
		}
	}

	/**
	 * Broadcasts a message only to control panels
	 *
	 * @param server the server with which to interact
	 * @param message the message to be broadcasted
	 */
	private void broadcastToAllControlPanels(Server server, Message<?> message) {
		List<UUID> controlPanels = clientTypeMapping.entrySet().stream()
			.filter(set -> set.getValue() == ClientType.CONTROL_PANEL)
			.map(Map.Entry::getKey)
			.toList();

		server.broadcastFiltered(message, controlPanels::contains);
	}

	/**
	 * Broadcasts a message only to nodes
	 *
	 * @param server the server with which to interact
	 * @param message the message to be broadcasted
	 */
	private void broadcastToAllNodes(Server server, Message<?> message) {
		List<UUID> controlPanels = clientTypeMapping.entrySet().stream()
			.filter(set -> set.getValue() == ClientType.NODE)
			.map(Map.Entry::getKey)
			.toList();

		server.broadcastFiltered(message, controlPanels::contains);
	}
}
