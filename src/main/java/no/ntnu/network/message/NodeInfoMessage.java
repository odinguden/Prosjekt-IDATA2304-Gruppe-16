package no.ntnu.network.message;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import no.ntnu.greenhouse.ActuatorCollection;
import no.ntnu.greenhouse.Sensor;
import no.ntnu.sigve.communication.Message;

public class NodeInfoMessage extends Message<NodeInfoMessage.NodeInfoPayload> {

	public NodeInfoMessage(UUID destination, NodeInfoPayload payload) {
		super(destination, payload);
	}

	//TODO: Odin, implement this
	public record NodeInfoPayload(List<Sensor> sensorList, ActuatorCollection actuators) implements Serializable {}
}
