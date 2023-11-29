package no.ntnu.controlpanel;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;
import no.ntnu.network.message.ClientType;
import no.ntnu.network.message.ConnectionMessage;
import no.ntnu.network.message.NodeInfoMessage;
import no.ntnu.network.message.NodeInfoRequestMessage;
import no.ntnu.sigve.client.Client;
import no.ntnu.sigve.client.MessageObserver;
import no.ntnu.sigve.communication.Message;

public class ControlPanelCommunicationChannel implements CommunicationChannel, MessageObserver {
	private final Client communicationClient;

	public ControlPanelCommunicationChannel(String address, int port) {
		communicationClient = new Client(address, port);
		communicationClient.addObserver(this);
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
			communicationClient.sendOutgoingMessage(new ConnectionMessage(ClientType.CONTROL_PANEL));
			communicationClient.sendOutgoingMessage(new NodeInfoRequestMessage());
		} catch (IOException ioe) {
			connectionSuccessful = false;
		}
		return connectionSuccessful;
	}

	@Override
	public void update(Message<?> message) {
		if (message instanceof NodeInfoMessage nodeInfoMessage) {
			NodeInfoMessage.NodeInfoPayload payload = nodeInfoMessage.getPayload();
			//TODO: Something with this, whenever NodeInfoPayload is implemented
		}
	}

	public static class ActuatorPayload implements Serializable {
		private final int actuatorId;
		private final boolean state;

		private ActuatorPayload(int actuatorId, boolean state) {
			this.actuatorId = actuatorId;
			this.state = state;
		}

		public int getActuatorId() {
			return actuatorId;
		}

		public boolean getState() {
			return state;
		}
	}
}
