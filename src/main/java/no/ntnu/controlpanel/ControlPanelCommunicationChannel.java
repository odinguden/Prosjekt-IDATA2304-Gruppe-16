package no.ntnu.controlpanel;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;
import no.ntnu.network.message.ClientType;
import no.ntnu.network.message.ConnectionMessage;
import no.ntnu.sigve.client.Client;
import no.ntnu.sigve.communication.Message;

public class ControlPanelCommunicationChannel implements CommunicationChannel {
	private final Client communicationClient;

	public ControlPanelCommunicationChannel(String address, int port) {
		communicationClient = new Client(address, port);
		communicationClient.sendOutgoingMessage(new ConnectionMessage(ClientType.CONTROL_PANEL));
	}

	@Override
	public void sendActuatorChange(UUID nodeId, int actuatorId, boolean isOn) {
		communicationClient.sendOutgoingMessage(new Message<>(nodeId, new ActuatorPayload(actuatorId, isOn)));
	}

	@Override
	public boolean open() {
		boolean connectionSuccessful = true;
		try {
			communicationClient.connect();
		} catch (IOException ioe) {
			connectionSuccessful = false;
		}
		return connectionSuccessful;
	}

	private record ActuatorPayload(int actuatorId, boolean state) implements Serializable {}
}
