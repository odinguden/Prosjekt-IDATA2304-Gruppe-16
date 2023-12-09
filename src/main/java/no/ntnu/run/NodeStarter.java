package no.ntnu.run;

import java.io.IOException;

import no.ntnu.greenhouse.GreenhouseSimulator;
import no.ntnu.greenhouse.NodeCommunicationChannel;
import no.ntnu.greenhouse.SensorActuatorNode;
import no.ntnu.tools.Logger;

public class NodeStarter {
    /**
     * Entrypoint for a node client.
     *
     * @param args Command line arguments, not used.
     */
    public static void main(String[] args) {
        GreenhouseSimulator sim = new GreenhouseSimulator(false);
		sim.nodeStarter(3);
		sim.start();
    }
}
