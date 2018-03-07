package sexy.kostya.serena.test;

import sexy.kostya.serena.Serena;
import sexy.kostya.serena.gui.GuiFramework;
import sexy.kostya.serena.service.LocalSerenaService;
import sexy.kostya.serena.service.ServiceState;

/**
 * Created by RINES on 07.03.2018.
 */
public class FirstTest {

    public static void main(String[] args) {
        Serena serena = Serena.startLocalSerena();
        new GuiFramework().createGui();
        LocalSerenaService alpha = new LocalSerenaService("alpha");
        LocalSerenaService beta = new LocalSerenaService("beta");
        serena.registerService(alpha);
        serena.registerService(beta);
        Thread t = new Thread(() -> {
            while(true) {
                alpha.setState(alpha.getState() == ServiceState.ACTIVE ? ServiceState.INACTIVE : ServiceState.ACTIVE);
                try {
                    alpha.getLogger().info("Info #1");
                    alpha.getLogger().debug("Debug #1");
                    alpha.getLogger().warn("Warning #1");
                    Thread.sleep(3000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();
        t = new Thread(() -> {
            while(true) {
                beta.setState(beta.getState() == ServiceState.ACTIVE ? ServiceState.INACTIVE : ServiceState.ACTIVE);
                try {
                    beta.getLogger().info("Info #1");
                    beta.getLogger().debug("Debug #1");
                    beta.getLogger().warn("Warning #1");
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

}
