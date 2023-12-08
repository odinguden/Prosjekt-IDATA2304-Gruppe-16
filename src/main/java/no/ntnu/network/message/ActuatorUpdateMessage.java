package no.ntnu.network.message;

import java.util.UUID;

import no.ntnu.network.message.payload.ActuatorUpdateInfo;
import no.ntnu.sigve.communication.Message;

/**
 * A message containing a notification about an actuator having been updated.
 * <p>If looking to send an update <b>to</b> an actuator, see {@link ActuatorCommandMessage}</p>
 */
public class ActuatorUpdateMessage extends Message<ActuatorUpdateInfo> {
	/**
	 * Creates a new actuator update message
	 *
	 * @param destination the message's destination
	 * @param payload the message's payload
	 */
	public ActuatorUpdateMessage(UUID destination, ActuatorUpdateInfo payload) {
		super(destination, payload);
	}
}