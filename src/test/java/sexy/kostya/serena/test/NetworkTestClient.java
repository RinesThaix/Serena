package sexy.kostya.serena.test;

import sexy.kostya.serena.Serena;
import sexy.kostya.serena.gui.GuiFramework;

/**
 * Created by RINES on 07.03.2018.
 */
public class NetworkTestClient {

    public static void main(String[] args) {
        Serena.startRemoteSerena("localhost", 8664);
        new GuiFramework().createGui();
    }

}
