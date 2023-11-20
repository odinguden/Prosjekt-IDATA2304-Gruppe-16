package no.ntnu.network;

import java.io.IOException;

import no.ntnu.listeners.common.ActuatorListener;
import no.ntnu.listeners.common.CommunicationChannelListener;
import no.ntnu.sigve.client.Client;

/**
 *
 */
public class ControlPanelClient implements CommunicationChannelListener{

	public ControlPanelClient(String address, int port) throws IOException {
	}

	@Override
	public void onCommunicationChannelClosed() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'onCommunicationChannelClosed'");
	}
}
