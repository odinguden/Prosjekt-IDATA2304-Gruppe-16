package no.ntnu.network.message.payload;

import java.io.Serializable;

/**
 * A serializable wrapper containing information about an actuator's state.
 */
public class ActuatorUpdateInfo implements Serializable {
		private int id;
		private boolean newState;

		/**
		 * Creates a new actuator update message.
		 *
		 * @param id the ID of the actuator
		 * @param newState its new state, either to be set or to be reported
		 */
		public ActuatorUpdateInfo(int id, boolean newState) {
			this.id = id;
			this.newState = newState;
		}

		/**
		 * Gets the actuator's id
		 *
		 * @return the actuator's id
		 */
		public int getId() {
			return id;
		}

		/**
		 * Gets the actuator's new state
		 *
		 * @return the actuator's new state
		 */
		public boolean isNewState() {
			return newState;
		}
	}