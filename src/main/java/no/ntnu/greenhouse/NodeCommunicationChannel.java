package no.ntnu.greenhouse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.listeners.greenhouse.SensorListener;
import no.ntnu.network.StaticIds;
import no.ntnu.network.message.ActuatorUpdateMessage;
import no.ntnu.network.message.ClientType;
import no.ntnu.network.message.ConnectionMessage;
import no.ntnu.network.message.NodeDisconnectMessage;
import no.ntnu.network.message.NodeInfoMessage;
import no.ntnu.network.message.NodeInfoRequestMessage;
import no.ntnu.network.message.SensorUpdateMessage;
import no.ntnu.network.message.SensorUpdateMessage.SensorUpdatePayload;
import no.ntnu.network.message.payload.ActuatorUpdateInfo;
import no.ntnu.network.message.NodeInfoMessage.NodeInfoPayload;
import no.ntnu.sigve.client.Client;
import no.ntnu.sigve.communication.Message;
import no.ntnu.sigve.communication.Protocol;
import no.ntnu.tools.Logger;

/**
 * @author Odin Lyngsgård
 */
public class NodeCommunicationChannel implements ActuatorListener, SensorListener, NodeStateListener {
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
		client.sendOutgoingMessage(
			new NodeDisconnectMessage(StaticIds.CP_BROADCAST, client.getSessionId())
		);
		Logger.info("Client " + client.getSessionId() + " disconnected.");
	}

	/**
	 * sends a info message containing sensor and actuator information to control panels.
	 * @param recipient the client the info message will be sent to.
	 */
	public void sendInfoMessage(UUID recipient){
		NodeInfoMessage message;
		NodeInfoPayload payload = new NodeInfoPayload(node.getSensors(), node.getActuators());
		if (recipient == null) {
			message = new NodeInfoMessage(StaticIds.CP_BROADCAST, payload);
		} else {
			message = new NodeInfoMessage(recipient, payload);
		}
		client.sendOutgoingMessage(message);
	}

	/**
	 * When a actuator changes state this method will broadcast a ActuatorUpdateMessage to control panels.
	 */
	@Override
	public void actuatorUpdated(UUID nodeId, Actuator actuator) {
		ActuatorUpdateInfo payload = new ActuatorUpdateInfo(actuator.getId(), actuator.isOn());
		client.sendOutgoingMessage(new ActuatorUpdateMessage(StaticIds.CP_BROADCAST, payload));
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

	/**
	 * Runs when node is ready
	 * @param node
	 */
	@Override
	public void onNodeReady(SensorActuatorNode node) {
		// TODO Auto-generated method stub
		Logger.info("Ready");
	}

	/**
	 * Stops communication with the server after sending a disconnect message.
	 * @param node the node that stopped
	 */
	@Override
	public void onNodeStopped(SensorActuatorNode node) {
		disconnect();
		Logger.info("Stopped communication.");
	}


	private class NodeCommunicationProtocol implements Protocol<Client> {
		@Override
		public void onClientConnect(Client caller, UUID sessionId) {
			Logger.info("Client connected");
			sendInfoMessage(null);
		}

		@Override
		public void onClientDisconnect(Client caller, UUID sessionId) {
			disconnect();
			Logger.error("Client forcefully disconnected");
		}

		@Override
		public void receiveMessage(Client caller, Message<?> message) {
			if (message instanceof NodeInfoRequestMessage) {
				sendInfoMessage(message.getSource());
			}
			if (message instanceof ActuatorUpdateMessage actuatorUpdateMessage) {
				Logger.info("actuator change recived");
				Actuator actuator = node.getActuators().get(actuatorUpdateMessage.getPayload().getId());
				if (actuatorUpdateMessage.getPayload().isNewState()) {
					actuator.turnOn();
				} else {
					actuator.turnOff();
				}
			}
		}
	}
}
