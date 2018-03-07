package sexy.kostya.serena;

import sexy.kostya.serena.exact.LocalSerena;
import sexy.kostya.serena.exact.RemoteSerena;
import sexy.kostya.serena.gui.GuiFramework;
import sexy.kostya.serena.gui.GuiUpdateRequest;
import sexy.kostya.serena.service.SerenaService;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by RINES on 07.03.2018.
 */
public abstract class Serena<S extends SerenaService> {

    private static Serena instance;

    protected int maxLogRecordsPerService = 100;

    protected final Map<String, S> services = new HashMap<>();

    public Serena() {
        instance = this;
    }

    public static Serena getInstance() {
        return instance;
    }

    public int getMaxLogRecordsPerService() {
        return maxLogRecordsPerService;
    }

    public abstract boolean isHostingServer();

    public abstract void registerService(S service);

    public abstract void unregisterService(String serviceName);

    public void unregisterService(S service) {
        unregisterService(service.getName());
    }

    public void processServices(Consumer<Map<String, S>> consumer) {
        synchronized (this.services) {
            consumer.accept(this.services);
        }
    }

    public S lookupService(String name) {
        synchronized (this.services) {
            return this.services.get(name);
        }
    }

    public void updateGui(GuiUpdateRequest request) {
        GuiFramework.handleUpdateRequest(request);
    }

    public static LocalSerena startLocalSerena() {
        return new LocalSerena();
    }

    public static RemoteSerena startRemoteSerena(String host, int port) {
        return new RemoteSerena(host, port);
    }

    public static LocalSerena startHostingSerena(String host, int port) {
        LocalSerena serena = startLocalSerena();
        serena.startServer(host, port);
        return serena;
    }

    public static void main(String[] args) {
        Serena serena = null;
        boolean withGui = true;
        for(String arg : args) {
            if(serena == null) {
                switch(arg.toLowerCase()) {
                    case "local":
                        serena = startLocalSerena();
                        break;
                    default:
                        break;
                }
            }
            if(arg.equalsIgnoreCase("no-gui"))
                withGui = false;
        }
        if(serena == null) {
            //open serena connector menu
        }else if(withGui) {
            new GuiFramework().createGui();
        }
    }

}
