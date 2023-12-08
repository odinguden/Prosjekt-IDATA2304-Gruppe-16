package no.ntnu.network.message.payload;

import java.io.Serializable;

import no.ntnu.greenhouse.Sensor;

/**
 * A wrapper containing basic information about a sensor.
 */
public final class NodeSensorInfo implements Serializable {
	private final String type;
	private final double value;
	private final String unit;

	/**
	 * Creates a new node sensor info wrapper.
	 * @param sensor
	 */
	public NodeSensorInfo(Sensor sensor) {
		this.type = sensor.getType();
		this.value = sensor.getReading().getValue();
		this.unit = sensor.getReading().getUnit();
	}

	/**
	 * Gets the sensor's type.
	 *
	 * @return the sensor's type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * Gets the sensor's current value.
	 *
	 * @return the sensor's current value
	 */
	public double getValue() {
		return this.value;
	}

	/**
	 * Gets the sensor's unit.
	 *
	 * @return the sensor's unit
	 */
	public String getUnit() {
		return this.unit;
	}
}