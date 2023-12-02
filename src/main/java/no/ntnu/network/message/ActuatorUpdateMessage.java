package no.ntnu.network.message;

import java.io.Serializable;
import java.util.UUID;

import no.ntnu.greenhouse.Actuator;
import no.ntnu.sigve.communication.Message;

public class ActuatorUpdateMessage extends Message<ActuatorUpdateMessage.ActuatorUpdatePayload> {

	public ActuatorUpdateMessage(UUID destination, ActuatorUpdatePayload payload) {
		super(destination, payload);
	}

	//TODO: Implement
	public record ActuatorUpdatePayload(Actuator actuator, UUID nodeID) implements Serializable {}
}
