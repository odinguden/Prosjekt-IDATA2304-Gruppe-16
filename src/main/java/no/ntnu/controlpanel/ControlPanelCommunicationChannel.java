package no.ntnu.controlpanel;

import java.io.IOException;
import java.io.Serializable;
import java.util.UUID;
import no.ntnu.network.message.ClientType;
import no.ntnu.network.message.ConnectionMessage;
import no.ntnu.network.message.NodeInfoMessage;
import no.ntnu.network.message.NodeInfoRequestMessage;
import no.ntnu.sigve.client.Client;
import no.ntnu.sigve.communication.Message;
import no.ntnu.sigve.communication.Protocol;
import no.ntnu.tools.Logger;

public class ControlPanelCommunicationChannel implements CommunicationChannel {
	private final ControlPanelLogic logic;
	private final Client communicationClient;

	public ControlPanelCommunicationChannel(ControlPanelLogic logic, String address, int port) {
		this.logic = logic;
		this.communicationClient = new Client(address, port, new ControlPanelCommunicationProtocol());
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
			Logger.error("An IOException occurred when trying to connect to the server");
			ioe.printStackTrace();
		}
		return connectionSuccessful;
	}

	private class ControlPanelCommunicationProtocol implements Protocol<Client> {
		@Override
		public void receiveMessage(Client client, Message<?> message) {
			Logger.info("Got message " + message.getClass().toString());
			if (message instanceof NodeInfoMessage nodeInfoMessage) {
				SensorActuatorNodeInfo info = new SensorActuatorNodeInfo(message.getSource());
				Logger.info(info.getActuators().toString());
			}
		}

		@Override
		public void onClientConnect(Client client, UUID uuid) {
			client.sendOutgoingMessage(new ConnectionMessage(ClientType.CONTROL_PANEL));
			client.sendOutgoingMessage(new NodeInfoRequestMessage());
		}

		@Override
		public void onClientDisconnect(Client client, UUID uuid) {
			Logger.error("Forcefully disconnected");
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
