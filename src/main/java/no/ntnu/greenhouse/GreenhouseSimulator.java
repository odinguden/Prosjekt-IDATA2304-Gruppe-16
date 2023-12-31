package no.ntnu.greenhouse;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import no.ntnu.listeners.greenhouse.NodeStateListener;
import no.ntnu.tools.Logger;

/**
 * Application entrypoint - a simulator for a greenhouse.
 */
public class GreenhouseSimulator {
  private final Map<UUID, SensorActuatorNode> nodes = new HashMap<>();

  private final List<PeriodicSwitch> periodicSwitches = new LinkedList<>();
  private final boolean fake;

  /**
   * Create a greenhouse simulator.
   *
   * @param fake When true, simulate a fake periodic events instead of creating
   *             socket communication
   */
  public GreenhouseSimulator(boolean fake) {
    this.fake = fake;
  }

  /**
   * Initialize the greenhouse but don't start the simulation just yet.
   */
  public void initialize() {
    createNode(1, 2, 1, 0, 0);
    createNode(1, 0, 0, 2, 1);
    createNode(2, 0, 0, 0, 0);
    Logger.info("Greenhouse initialized");
  }

  private void createNode(int temperature, int humidity, int windows, int fans, int heaters) {
    SensorActuatorNode node = DeviceFactory.createNode(
        temperature, humidity, windows, fans, heaters);
    nodes.put(node.getId(), node);
  }

  /**
   * Start a simulation of a greenhouse - all the sensor and actuator nodes inside it.
   */
  public void start() {
    initiateCommunication();
    for (SensorActuatorNode node : nodes.values()) {
      node.start();
    }
    for (PeriodicSwitch periodicSwitch : periodicSwitches) {
      periodicSwitch.start();
    }

    Logger.info("Simulator started");
  }

  private void initiateCommunication() {
    if (fake) {
      initiateFakePeriodicSwitches();
    } else {
      initiateRealCommunication();
    }
  }

  private void initiateRealCommunication() {
    for (SensorActuatorNode node : nodes.values()) {
      try {
      initiateSocketCommunication(node);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Initiates socket communication for a node
   * @param node
   * @throws IOException
   */
  public static void initiateSocketCommunication(SensorActuatorNode node) throws IOException {
    String address = "localhost";
    int port = 8080;
    NodeCommunicationChannel nodeChannel = new NodeCommunicationChannel(address, port, node);
    node.addSensorListener(nodeChannel);
    node.addActuatorListener(nodeChannel);
    node.addStateListener(nodeChannel);
}


  private void initiateFakePeriodicSwitches() {
    periodicSwitches.add(new PeriodicSwitch("Window DJ", nodes.get(UUID.fromString("1")), 2, 20000));
    periodicSwitches.add(new PeriodicSwitch("Heater DJ", nodes.get(UUID.fromString("2")), 7, 8000));
  }

  /**
   * Stop the simulation of the greenhouse - all the nodes in it.
   */
  public void stop() {
    stopCommunication();
    for (SensorActuatorNode node : nodes.values()) {
      node.stop();
    }
  }

  private void stopCommunication() {
    if (fake) {
      for (PeriodicSwitch periodicSwitch : periodicSwitches) {
        periodicSwitch.stop();
      }
    } else {
      for (SensorActuatorNode node : nodes.values()) {
        node.stop();
      }
    }
  }

  /**
   * Add a listener for notification of node staring and stopping.
   *
   * @param listener The listener which will receive notifications
   */
  public void subscribeToLifecycleUpdates(NodeStateListener listener) {
    for (SensorActuatorNode node : nodes.values()) {
      node.addStateListener(listener);
    }
  }

  /**
   * Will start a number of nodes with a randomized number of sensors and actuators.
   *
   * @param count number of nodes the application will start.
   */
  public void nodeStarter(int count){
    int i = 0;
    while (i < count) {
      Random random = new Random();
      createNode(
        random.nextInt(4),
        random.nextInt(4),
        random.nextInt(4),
        random.nextInt(4),
        random.nextInt(4)
        );
        i++;
    }
  }
}
