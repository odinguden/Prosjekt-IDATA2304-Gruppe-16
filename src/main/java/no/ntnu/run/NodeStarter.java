package no.ntnu.run;

import java.io.IOException;
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
        NodeStarter starter = new NodeStarter();
        starter.start();
    }

    private void start() {
        try {
            initiateSocketCommunication(new SensorActuatorNode(null));
        } catch (IOException ioe) {
            Logger.error("An IOException occurred when trying to connect to the server");
            ioe.printStackTrace();
        }
    }

    public static void initiateSocketCommunication(SensorActuatorNode node) throws IOException {
        String address = "localhost";
        int port = 8080;
        NodeCommunicationChannel nodeChannel = new NodeCommunicationChannel(address, port, node);
        node.addSensorListener(nodeChannel);
        node.addActuatorListener(nodeChannel);
        node.addStateListener(nodeChannel);
        node.start();
    }
}
