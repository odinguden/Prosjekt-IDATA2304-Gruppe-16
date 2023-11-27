package no.ntnu.network;

import java.net.InetAddress;

import no.ntnu.sigve.communication.Message;
import no.ntnu.sigve.server.Protocol;
import no.ntnu.sigve.server.Server;

public class GreenhouseProtocol implements Protocol {

	@Override
	public void receiveMessage(Server arg0, Message<?> arg1) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'receiveMessage'");
	}

	@Override
	public void onClientConnect() {
		
	}

	@Override
	public void onClientDisconnect() {

	}

}
