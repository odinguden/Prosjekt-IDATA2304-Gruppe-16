package no.ntnu.network.message;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import no.ntnu.greenhouse.Sensor;
import no.ntnu.network.message.payload.NodeSensorInfo;
import no.ntnu.sigve.communication.Message;

/**
 * A message containing information about a sensor having changed.
 */
public class SensorUpdateMessage extends Message<SensorUpdateMessage.SensorUpdatePayload> {

	/**
	 * Creates a new sensor update message.
	 *
	 * @param destination the message's destination
	 * @param payload the message's payload
	 */
	public SensorUpdateMessage(UUID destination, SensorUpdatePayload payload) {
		super(destination, payload);
	}

	/**
	 * A payload containing information about a node's sensors
	 */
	public static class SensorUpdatePayload implements Serializable {
		private List<NodeSensorInfo> sensorInfo;

		/**
		 * Creates a new sensor update payload.
		 *
		 * @param sensors a list of a node's sensors
		 */
		public SensorUpdatePayload(List<Sensor> sensors) {
			this.sensorInfo = sensors
				.stream()
				.map(NodeSensorInfo::new)
				.collect(Collectors.toList());
		}

		/**
		 * Gets a list of the node's sensors.
		 *
		 * @return a list of the node's sensors
		 */
		public List<NodeSensorInfo> getSensorInfo() {
			return this.sensorInfo;
		}
	}
}
