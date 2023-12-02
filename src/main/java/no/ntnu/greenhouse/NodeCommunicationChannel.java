package no.ntnu.greenhouse;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.greenhouse.SensorListener;
import no.ntnu.network.StaticIds;
import no.ntnu.network.message.NodeInfoMessage;
import no.ntnu.network.message.NodeInfoMessage.NodeInfoPayload;
import no.ntnu.sigve.client.Client;
import no.ntnu.sigve.client.MessageObserver;
import no.ntnu.sigve.communication.Message;

/**
 * @author Odin Lyngsgård
 */
public class NodeCommunicationChannel implements ActuatorListener, SensorListener, MessageObserver {
	private Client client;
	private SensorActuatorNode node;

	public NodeCommunicationChannel(Client client, SensorActuatorNode node) throws IOException {
		this.client = client;
		this.node = node;
		client.addObserver(this);
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
	public void SendInfoMessage(UUID recipient){
		NodeInfoPayload payload = new NodeInfoPayload(node.getSensors(), node.getActuators());
		UUID uuid = recipient;
		if (recipient == null) {
			uuid = StaticIds.CP_BROADCAST;
		}
		NodeInfoMessage message = new NodeInfoMessage(uuid, payload);
		client.sendOutgoingMessage(message);
	}

	/**
	 * Will send a message to the all the control panels updating them on the state of the actuator. <br>
	 * The message is built up by several parts:<br>
	 * All the parts are divided by ",". <br>
	 * <ul>
	 * <li>The first four characters are "ACUP" these identify the message as an actuator update.</li>
	 * <li>The second part is the UUID of the node..</li>
	 * <li>The third part is the actuator id of the updated actuator.</li>
	 * <li>The fourth part is the state of the actuator, "1" represents on and "0" represents off.</li>
	 * </ul>
	 */
	@Override
	public void actuatorUpdated(UUID nodeId, Actuator actuator) {
		String message = "ACUP" + "," + nodeId + "," + actuator.getId();

		if (actuator.isOn()) {
			message = message + "," + "1";
		} else {
			message = message + "0";
		}

		broadcastToControlPanel(message);
	}

	/**
	 * Sends a message telling how bla bla bla TODO
	 */
	@Override
	public void sensorsUpdated(List<Sensor> sensors) {
		String message = null;
		sensors.size();
		for (Sensor sensor : sensors) {
			sensor.getReading().toString();
		}
		broadcastToControlPanel(message);
		//TODO Send sensor updates
	}

	/**
	 * Will create and send a message that will be broadcast to all control panels by the server
	 * @param message
	 */
	private void broadcastToControlPanel(String message) {
		if (message != null) {
			client.sendOutgoingMessage(
				new Message<>(UUID.fromString("0"), message)
			);
		}
	}

	/**
	 * Will create and send a message that will be broadcast to all nodes by the server
	 * @param message
	 */
	private void broadcastToNode(String message) {
		if (message != null) {
			client.sendOutgoingMessage(
				new Message<>(UUID.fromString("1"), message)
			);
		}
	}

	@Override
	public void update(Message<?> arg0) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'update'");
	}
}
