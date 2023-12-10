package no.ntnu.run;

import no.ntnu.greenhouse.GreenhouseSimulator;

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
