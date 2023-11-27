package no.ntnu.network;

import java.io.IOException;
import no.ntnu.sigve.server.Server;

public class GreenhouseServer {
	public GreenhouseServer() throws IOException {
		MainServerProtocol serverProtocol = new MainServerProtocol();
		Server server = new Server(8080, serverProtocol);
		server.start();
	}
}
