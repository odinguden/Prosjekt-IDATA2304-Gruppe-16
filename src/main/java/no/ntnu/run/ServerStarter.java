package no.ntnu.run;

import java.io.IOException;
import no.ntnu.network.GreenhouseServer;
import no.ntnu.tools.Logger;

public class ServerStarter {
    public static void main(String[] args) {
        try {
            new GreenhouseServer();
        } catch (IOException ioe) {
            Logger.error("An IOException occurred when trying to connect to the server");
            ioe.printStackTrace();
        }
    }
}
