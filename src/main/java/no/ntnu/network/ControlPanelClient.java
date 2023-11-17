package no.ntnu.network;

import java.io.IOException;

import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.sigve.client.Client;

/**
 *
 */
public class ControlPanelClient extends Client{

	public ControlPanelClient(String address, int port) throws IOException {
		super(address, port);
	}
}
