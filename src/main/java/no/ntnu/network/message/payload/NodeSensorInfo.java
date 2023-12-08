package no.ntnu.network.message.payload;

import java.io.Serializable;

import no.ntnu.greenhouse.Sensor;

public final class NodeSensorInfo implements Serializable {
	private final String type;
	private final double value;
	private final String unit;

	public NodeSensorInfo(Sensor sensor) {
		this.type = sensor.getType();
		this.value = sensor.getReading().getValue();
		this.unit = sensor.getReading().getUnit();
	}

	public String getType() {
		return this.type;
	}

	public double getValue() {
		return this.value;
	}

	public String getUnit() {
		return this.unit;
	}
}