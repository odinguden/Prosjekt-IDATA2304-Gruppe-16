package no.ntnu.network;

import java.io.IOException;

import no.ntnu.sigve.server.Server;

public class GreenhouseServer {
	private Server server = null;
	private GreenhouseProtocol protocol = null;

	public GreenhouseServer() {
		this.protocol = new GreenhouseProtocol();
		try {
			this.server = new Server(8080, this.protocol);
			this.protocol.setServer(server);
			server.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new GreenhouseServer();
	}
}