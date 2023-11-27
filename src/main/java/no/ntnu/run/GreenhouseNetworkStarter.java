package no.ntnu.run;

import no.ntnu.gui.greenhouse.GreenhouseApplication;
import no.ntnu.tools.Logger;

public class GreenhouseNetworkStarter {
	public static void main(String[] args) {
    	if (args.length == 1 && "fake".equals(args[0])) {
      		Logger.info("Using FAKE events");
    	}
    	GreenhouseApplication.startApp(false);
	}
}
