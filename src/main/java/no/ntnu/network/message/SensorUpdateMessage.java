package no.ntnu.network.message;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import no.ntnu.greenhouse.Sensor;
import no.ntnu.sigve.communication.Message;

public class SensorUpdateMessage extends Message<SensorUpdateMessage.SensorUpdatePayload> {

	public SensorUpdateMessage(UUID destination, SensorUpdatePayload payload) {
		super(destination, payload);
	}

	//TODO: Implement
	public record SensorUpdatePayload(List<Sensor> sensors) implements Serializable {}
}
