package no.ntnu.network.message;

import java.util.UUID;

import no.ntnu.network.message.payload.ActuatorUpdateInfo;
import no.ntnu.sigve.communication.Message;

/**
 * A message containing a command to an actuator to update its state.
 * <p>If looking to send information <b>about</b> an updated actuator, see
 * {@link ActuatorUpdateMessage}</p>
 */
public class ActuatorCommandMessage extends Message<ActuatorUpdateInfo> {
	/**
	 * Creates a new actuator command message.
	 *
	 * @param destination the message's destination
	 * @param payload the message's payload
	 */
	public ActuatorCommandMessage(UUID destination, ActuatorUpdateInfo payload) {
		super(destination, payload);
	}
}
