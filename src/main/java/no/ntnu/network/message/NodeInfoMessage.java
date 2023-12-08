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

public class NodeInfoMessage extends Message<NodeInfoMessage.NodeInfoPayload> {

	public NodeInfoMessage(UUID destination, NodeInfoPayload payload) {
		super(destination, payload);
	}

	public static class NodeInfoPayload implements Serializable {
		private final List<NodeSensorInfo> nodeSensors;
		private final List<NodeActuatorInfo> nodeActuators;

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

		public List<NodeSensorInfo> getSensors() {
			return this.nodeSensors;
		}

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
