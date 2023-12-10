package no.ntnu.run;

import java.util.UUID;
import no.ntnu.controlpanel.CommunicationChannel;
import no.ntnu.controlpanel.ControlPanelCommunicationChannel;
import no.ntnu.controlpanel.ControlPanelLogic;
import no.ntnu.controlpanel.FakeCommunicationChannel;
import no.ntnu.gui.controlpanel.ControlPanelApplication;
import no.ntnu.tools.Logger;

/**
 * Starter class for the control panel.
 * Note: we could launch the Application class directly, but then we would have issues with the
 * debugger (JavaFX modules not found)
 */
public class ControlPanelStarter {
  private final boolean fake;

  public ControlPanelStarter(boolean fake) {
    this.fake = fake;
  }

  /**
   * Entrypoint for the application.
   *
   * @param args Command line arguments, only the first one of them used: when it is "fake",
   *             emulate fake events, when it is either something else or not present,
   *             use real socket communication.
   */
  public static void main(String[] args) {
    boolean fake = false;
    ControlPanelStarter starter = new ControlPanelStarter(fake);
    starter.start();
  }

  private void start() {
    ControlPanelLogic logic = new ControlPanelLogic();
    CommunicationChannel channel = initiateCommunication(logic, fake);
    ControlPanelApplication.startApp(logic, channel);
    // This code is reached only after the GUI-window is closed
    Logger.info("Exiting the control panel application");
    stopCommunication(logic);
  }

  private CommunicationChannel initiateCommunication(ControlPanelLogic logic, boolean fake) {
    CommunicationChannel channel;
    if (fake) {
      channel = initiateFakeSpawner(logic);
    } else {
      channel = initiateSocketCommunication(logic);
    }
    return channel;
  }

  private CommunicationChannel initiateSocketCommunication(ControlPanelLogic logic) {
    // You communication class(es) may want to get reference to the logic and call necessary
    // logic methods when events happen (for example, when sensor data is received)
    String address = "localhost";
    int port = 8080;
    ControlPanelCommunicationChannel cpChannel = new ControlPanelCommunicationChannel(logic, address, port);
    logic.setCommunicationChannel(cpChannel);

    return cpChannel;
  }

  private CommunicationChannel initiateFakeSpawner(ControlPanelLogic logic) {
    // Here we pretend that some events will be received with a given delay
    FakeCommunicationChannel spawner = new FakeCommunicationChannel(logic);
    UUID id4 = UUID.fromString("4");
    UUID id1 = UUID.fromString("1");
    UUID id8 = UUID.fromString("8");

    logic.setCommunicationChannel(spawner);
    spawner.spawnNode(String.format("%s;3_window", id4), 2);
    spawner.spawnNode(id1.toString(), 3);
    spawner.spawnNode(id1.toString(), 4);
    spawner.advertiseSensorData(String.format("%s;temperature=27.4 °C,temperature=26.8 °C,humidity=80 %%", id4), 4);
    spawner.spawnNode(String.format("%s;2_heater", id8), 5);
    spawner.advertiseActuatorState(id4, 1, true, 5);
    spawner.advertiseActuatorState(id4, 1, false, 6);
    spawner.advertiseActuatorState(id4, 1, true, 7);
    spawner.advertiseActuatorState(id4, 2, true, 7);
    spawner.advertiseActuatorState(id4, 1, false, 8);
    spawner.advertiseActuatorState(id4, 2, false, 8);
    spawner.advertiseActuatorState(id4, 1, true, 9);
    spawner.advertiseActuatorState(id4, 2, true, 9);
    spawner.advertiseSensorData(String.format("%s;temperature=22.4 °C,temperature=26.0 °C,humidity=81 %%", id4), 9);
    spawner.advertiseSensorData(String.format("%s;humidity=80 %%,humidity=82 %%", id1), 10);
    spawner.advertiseRemovedNode(id8,11);
    spawner.advertiseRemovedNode(id8,12);
    spawner.advertiseSensorData(String.format("%s;temperature=25.4 °C,temperature=27.0 °C,humidity=67 %%", id1), 13);
    spawner.advertiseSensorData(String.format("%s;temperature=25.4 °C,temperature=27.0 °C,humidity=82 %%", id4), 14);
    spawner.advertiseSensorData(String.format("%s;temperature=25.4 °C,temperature=27.0 °C,humidity=82 %%", id4), 16);
    return spawner;
  }

  private void stopCommunication(ControlPanelLogic logic) {
    logic.closeCommunication();
  }
}
