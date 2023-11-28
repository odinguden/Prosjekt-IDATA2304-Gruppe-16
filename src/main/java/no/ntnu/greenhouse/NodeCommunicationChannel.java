package no.ntnu.greenhouse;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.greenhouse.SensorListener;

import no.ntnu.sigve.client.Client;
import no.ntnu.sigve.communication.Message;

/**
 * @author Odin Lyngsgård
 */
public class NodeCommunicationChannel implements ActuatorListener, SensorListener {
	private Client client;

	public NodeCommunicationChannel(String address, int port) throws IOException {
		client = new Client(address, port);
	}

	/**
	 * Sends a message containing a list of the actuators and sensors to the server.
	 * The server will forward these to the control panels.
	 */
	public void onFirstConnect(){
		sendInfoMessage();
	}

	/**
	 * Closes connection to the server
	*/
	public void disconnect() {
		client.close();
	}

	/**
	 * Sends a message containing a list of the actuators and sensors to the control panel that requested it
	 */
	public void sendInfoMessage() {
		//TODO implement
	}

	/**
	 * Will send a message to the all the control panels updating them on the state of the actuator
	 * The message is built up by several parts:
	 * TODO finish javadoc explaining the message
	 */
	@Override
	public void actuatorUpdated(int nodeId, Actuator actuator) {
		String message = "au" + nodeId + actuator.getId();

		if (actuator.isOn()) {
			message = message + "1";
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
			sensor.getReading().getValue();
			sensor.getReading().getType();
			sensor.getReading().getUnit();
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
				new Message<>(UUID.fromString("0"), message)
			);
		}
	}
}
