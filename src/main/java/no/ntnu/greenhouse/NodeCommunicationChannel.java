package no.ntnu.greenhouse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.greenhouse.SensorListener;
import no.ntnu.network.StaticIds;
import no.ntnu.network.message.ActuatorUpdateMessage;
import no.ntnu.network.message.ClientType;
import no.ntnu.network.message.ConnectionMessage;
import no.ntnu.network.message.NodeInfoMessage;
import no.ntnu.network.message.NodeInfoRequestMessage;
import no.ntnu.network.message.SensorUpdateMessage;
import no.ntnu.network.message.ActuatorUpdateMessage.ActuatorUpdatePayload;
import no.ntnu.network.message.SensorUpdateMessage.SensorUpdatePayload;
import no.ntnu.network.message.NodeInfoMessage.NodeInfoPayload;
import no.ntnu.sigve.client.Client;
import no.ntnu.sigve.communication.Message;
import no.ntnu.sigve.communication.Protocol;
import no.ntnu.tools.Logger;

/**
 * @author Odin Lyngsgård
 */
public class NodeCommunicationChannel implements ActuatorListener, SensorListener {
	private final Client client;
	private final SensorActuatorNode node;

	public NodeCommunicationChannel(String address, int port, SensorActuatorNode node) throws IOException {
		Protocol<Client> protocol = new NodeCommunicationProtocol();
		this.client = new Client(address, port, protocol);
		this.node = node;
		client.connect();
		client.sendOutgoingMessage(new ConnectionMessage(ClientType.NODE));
	}

	/**
	 * Closes connection to the server
	*/
	public void disconnect() {
		//TODO implement
	}

	/*
	 * TODO: Lær Odin å skrive javadoc istedetfor todo comments
	 */
	public void sendInfoMessage(UUID recipient){
		NodeInfoPayload payload = new NodeInfoPayload(node.getSensors(), node.getActuators());
		UUID uuid = recipient;
		if (recipient == null) {
			uuid = StaticIds.CP_BROADCAST;
		}
		NodeInfoMessage message = new NodeInfoMessage(uuid, payload);
		client.sendOutgoingMessage(message);
	}

	/**
	 * TODO: javadoc
	 */
	@Override
	public void actuatorUpdated(UUID nodeId, Actuator actuator) {
		ActuatorUpdatePayload payload = new ActuatorUpdatePayload(
			actuator.getId(),
			actuator.isOn(),
			actuator.getType(),
			nodeId
			);
		client.sendOutgoingMessage(new ActuatorUpdateMessage(StaticIds.CP_BROADCAST, payload));
	}

	/**
	 * TODO javadoc
	 */
	private void changeActuatorState(int id, boolean newState) {
		if (newState){
			node.getActuators().get(id).turnOff();
		} else {
			node.getActuators().get(id).turnOff();
		}
	}

	/**
	 * Broadcasts a message to the control panels with the updated values of the sensors
	 */
	@Override
	public void sensorsUpdated(List<Sensor> sensors) {
		SensorUpdatePayload payload = new SensorUpdatePayload(sensors);
		SensorUpdateMessage message = new SensorUpdateMessage(StaticIds.CP_BROADCAST, payload);
		client.sendOutgoingMessage(message);
	}

	private class NodeCommunicationProtocol implements Protocol<Client> {
		@Override
		public void onClientConnect(Client caller, UUID sessionId) {
			Logger.info("Client connected");
			sendInfoMessage(null);
		}

		@Override
		public void onClientDisconnect(Client caller, UUID sessionId) {
			Logger.error("Client forcefully disconnected");
		}

		@Override
		public void receiveMessage(Client caller, Message<?> message) {
			if (message instanceof NodeInfoRequestMessage) {
				sendInfoMessage(message.getSource());
			}
		}

	}
}
