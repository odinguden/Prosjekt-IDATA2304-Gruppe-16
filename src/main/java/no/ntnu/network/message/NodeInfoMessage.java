package no.ntnu.network.message;

import java.io.Serializable;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.greenhouse.ActuatorCollection;
import no.ntnu.greenhouse.Sensor;
import no.ntnu.network.message.payload.NodeSensorInfo;
import no.ntnu.sigve.communication.Message;

/**
 * A message containing information about a node and its components. To be sent primarily when
 * a new node or control panel connects.
 */
public class NodeInfoMessage extends Message<NodeInfoMessage.NodeInfoPayload> {

	/**
	 * Creates a new node info message.
	 *
	 * @param destination the message's destination
	 * @param payload the message's payload
	 */
	public NodeInfoMessage(UUID destination, NodeInfoPayload payload) {
		super(destination, payload);
	}

	/**
	 * A payload containing information about a node's components, both its actuators and its
	 * sensor's current state.
	 */
	public static class NodeInfoPayload implements Serializable {
		private final List<NodeSensorInfo> nodeSensors;
		private final List<NodeActuatorInfo> nodeActuators;

		/**
		 * Creates a new node info payload.
		 *
		 * @param sensors the sensors belonging to the node
		 * @param actuators the actuators belonging to the node
		 */
		public NodeInfoPayload(List<Sensor> sensors, ActuatorCollection actuators) {
			this.nodeSensors = sensors
				.stream()
				.map(NodeSensorInfo::new)
				.collect(Collectors.toList());

			// Read the following out loud fast
			Spliterator<Actuator> actuatorSpliterator = Spliterators
				.spliteratorUnknownSize(actuators.iterator(), 0);

			this.nodeActuators = StreamSupport
				.stream(actuatorSpliterator, false)
				.map(NodeActuatorInfo::new)
				.collect(Collectors.toList());
		}

		/**
		 * Gets a list of information about a node's sensors.
		 *
		 * @return a list of information about a node's sensors
		 */
		public List<NodeSensorInfo> getSensors() {
			return this.nodeSensors;
		}

		/**
		 * Gets a list of information about a node's actuators.
		 *
		 * @return a list of information about a node's actuators
		 */
		public List<NodeActuatorInfo> getActuators() {
			return this.nodeActuators;
		}
	}

	public static class NodeActuatorInfo implements Serializable{
		private final String type;
		private final UUID nodeId;
		private NodeActuatorInfo(Actuator actuator) {
			this.type = actuator.getType();
			this.nodeId = actuator.getNodeId();
		}

		public String getType() {
			return this.type;
		}

		public UUID getNodeId() {
			return this.nodeId;
		}
	}
}
