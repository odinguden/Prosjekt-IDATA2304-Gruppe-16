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
import no.ntnu.network.message.SensorUpdateMessage;
import no.ntnu.network.message.ActuatorUpdateMessage.ActuatorUpdatePayload;
import no.ntnu.network.message.SensorUpdateMessage.SensorUpdatePayload;
import no.ntnu.network.message.NodeInfoMessage.NodeInfoPayload;
import no.ntnu.sigve.client.Client;
import no.ntnu.sigve.client.MessageObserver;
import no.ntnu.sigve.communication.Message;

/**
 * @author Odin Lyngsgård
 */
public class NodeCommunicationChannel implements ActuatorListener, SensorListener, MessageObserver {
	private final Client client;
	private final SensorActuatorNode node;

	public NodeCommunicationChannel(String address, int port, SensorActuatorNode node) throws IOException {
		this.client = new Client(address, port);
		this.node = node;
		client.addObserver(this);
		client.connect();
		client.sendOutgoingMessage(new ConnectionMessage(ClientType.NODE));
	}

	/**
	 * Sends a message containing a list of the actuators and sensors to the server.
	 * The server will forward these to the control panels.
	 */
	public void onFirstConnect(){
		sendInfoMessage(null);
	}

	/**
	 * Closes connection to the server
	*/
	public void disconnect() {
		//TODO
	}

	/*
	 * TODO:
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
	 * TODO:
	 */
	@Override
	public void actuatorUpdated(UUID nodeId, Actuator actuator) {
		ActuatorUpdatePayload payload = new ActuatorUpdatePayload(actuator, nodeId);
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

	@Override
	public void update(Message<?> arg0) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'update'");
	}
}
