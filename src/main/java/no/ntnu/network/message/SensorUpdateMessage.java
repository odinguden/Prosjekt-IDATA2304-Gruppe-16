package no.ntnu.network.message;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import no.ntnu.greenhouse.Sensor;
import no.ntnu.network.message.payload.NodeSensorInfo;
import no.ntnu.sigve.communication.Message;

public class SensorUpdateMessage extends Message<SensorUpdateMessage.SensorUpdatePayload> {

	public SensorUpdateMessage(UUID destination, SensorUpdatePayload payload) {
		super(destination, payload);
	}

	public static class SensorUpdatePayload implements Serializable {
		private List<NodeSensorInfo> sensorInfo;

		public SensorUpdatePayload(List<Sensor> sensors) {
			this.sensorInfo = sensors
				.stream()
				.map(NodeSensorInfo::new)
				.collect(Collectors.toList());
		}

		public List<NodeSensorInfo> getSensorInfo() {
			return this.sensorInfo;
		}
	}
}
