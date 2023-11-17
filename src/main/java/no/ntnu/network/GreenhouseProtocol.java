package no.ntnu.network;

import java.net.InetAddress;

import no.ntnu.sigve.server.Protocol;
import no.ntnu.sigve.server.Server;

public class GreenhouseProtocol implements Protocol {
	private Server server = null;

	@Override
	public void receiveMessage(String message, InetAddress address) {
		System.out.println("?");
		server.route(address, "Pong!");
		System.out.println("!");
	}

	public void setServer(Server server) {
		this.server = server;
	}
}
