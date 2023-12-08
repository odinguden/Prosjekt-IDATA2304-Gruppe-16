package no.ntnu.controlpanel;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.SensorReading;
import no.ntnu.network.message.ActuatorUpdateMessage;
import no.ntnu.network.message.ClientType;
import no.ntnu.network.message.ConnectionMessage;
import no.ntnu.network.message.NodeInfoMessage;
import no.ntnu.network.message.NodeInfoMessage.NodeActuatorInfo;
import no.ntnu.network.message.payload.ActuatorUpdateInfo;
import no.ntnu.network.message.payload.NodeSensorInfo;
import no.ntnu.network.message.NodeInfoRequestMessage;
import no.ntnu.network.message.SensorUpdateMessage;
import no.ntnu.sigve.client.Client;
import no.ntnu.sigve.communication.Message;
import no.ntnu.sigve.communication.Protocol;
import no.ntnu.tools.Logger;

public class ControlPanelCommunicationChannel implements CommunicationChannel {
	private final Client communicationClient;

	public ControlPanelCommunicationChannel(ControlPanelLogic logic, String address, int port) {
		this.communicationClient = new Client(address, port, new ControlPanelCommunicationProtocol(logic));
	}

	@Override
	public void sendActuatorChange(UUID nodeId, int actuatorId, boolean isOn) {
		communicationClient.sendOutgoingMessage(new ActuatorUpdateMessage(nodeId, new ActuatorUpdateInfo(actuatorId, isOn)));
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
		private ControlPanelLogic logic;

		public ControlPanelCommunicationProtocol (ControlPanelLogic logic) {
			this.logic = logic;
		}

		@Override
		public void receiveMessage(Client client, Message<?> message) {
			Logger.info("Got message " + message.getClass().toString());
			if (message instanceof NodeInfoMessage nodeInfoMessage) {
				SensorActuatorNodeInfo sensorActuatorNodeInfo = new SensorActuatorNodeInfo(nodeInfoMessage.getSource());
				List<NodeActuatorInfo> actuatorList = nodeInfoMessage.getPayload().getActuators();
				for (NodeActuatorInfo nodeActuatorInfo : actuatorList) {
					Actuator actuator = new Actuator(
							nodeActuatorInfo.getActuatorId(),
							nodeActuatorInfo.getType(),
							nodeInfoMessage.getSource()
							);
					actuator.setListener(logic);
					sensorActuatorNodeInfo.addActuator(actuator);
				}
				logic.onNodeAdded(sensorActuatorNodeInfo);

				updateSensorReadings(nodeInfoMessage.getSource(), nodeInfoMessage.getPayload().getSensors());

				Logger.info(sensorActuatorNodeInfo.getActuators().toString());
			}

			if (message instanceof ActuatorUpdateMessage actuatorUpdateMessage) {
				Logger.info("Actuator state change");
				logic.onActuatorStateChanged(
					actuatorUpdateMessage.getSource(),
					actuatorUpdateMessage.getPayload().getId(),
					actuatorUpdateMessage.getPayload().isNewState()
				);
				Logger.info("actuator update");
			}

			if (message instanceof SensorUpdateMessage sensorUpdateMessage) {
				updateSensorReadings(sensorUpdateMessage.getSource(), sensorUpdateMessage.getPayload().getSensorInfo());
				Logger.info("sensor update");

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

		private void updateSensorReadings(UUID nodeID, List<NodeSensorInfo> sensors){
			List<SensorReading> sensorReadings = new LinkedList<>();
			for (NodeSensorInfo sensor : sensors) {
				sensorReadings.add(new SensorReading(sensor.getType(), sensor.getValue(), sensor.getUnit()));
			}
			logic.onSensorData(nodeID, sensorReadings);
		}
	}

	public static class ActuatorPayload implements Serializable {
		private final int actuatorId;
		private final boolean state;

		public ActuatorPayload(int actuatorId, boolean state) {
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
