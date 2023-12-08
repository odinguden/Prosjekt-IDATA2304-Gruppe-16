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

	/**
	 * sends a info message containing sensor and actuator information to control panels.
	 * @enum recipient the client the info message will be sent to.
	 */
	public void sendInfoMessage(UUID recipient){
		NodeInfoPayload payload = new NodeInfoPayload(node.getSensors(), node.getActuators());
		recipient = StaticIds.CP_BROADCAST;
		NodeInfoMessage message = new NodeInfoMessage(recipient, payload);
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
