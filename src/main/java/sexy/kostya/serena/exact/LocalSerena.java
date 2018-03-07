package sexy.kostya.serena.exact;

import sexy.kostya.serena.Serena;
import sexy.kostya.serena.gui.GuiUpdateRequest;
import sexy.kostya.serena.network.SerenaNetworkServer;
import sexy.kostya.serena.network.protocol.Packet5UpdateMaxLogsAmount;
import sexy.kostya.serena.network.protocol.Packet6ServiceRegistration;
import sexy.kostya.serena.secure.Authenticator;
import sexy.kostya.serena.service.LocalSerenaService;

import java.util.logging.Logger;

/**
 * Created by RINES on 07.03.2018.
 */
public class LocalSerena extends Serena<LocalSerenaService> {

    private SerenaNetworkServer server;
    private Authenticator authenticator;

    public void startServer(String host, int port) {
        (this.server = new SerenaNetworkServer(Logger.getGlobal())).start(host, port);
    }

    public void secure(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public Authenticator getAuthenticator() {
        return this.authenticator;
    }

    public void setMaxLogRecordsPerService(int maxLogRecordsPerService) {
        this.maxLogRecordsPerService = maxLogRecordsPerService;
        SerenaNetworkServer.broadcastIfOnline(new Packet5UpdateMaxLogsAmount(maxLogRecordsPerService));
    }

    @Override
    public boolean isHostingServer() {
        return this.server != null;
    }

    public void registerService(LocalSerenaService service) {
        synchronized (super.services) {
            if(super.services.containsKey(service.getName()))
                throw new IllegalArgumentException("Serena Service of name " + service.getName() + " is already registered");
            super.services.put(service.getName(), service);
            service.setRegistered(true);
            SerenaNetworkServer.broadcastIfOnline(new Packet6ServiceRegistration(service.getName(), true, service.getState(), service.getLoggingLevel()));
            updateGui(new GuiUpdateRequest(GuiUpdateRequest.Type.SERVICES_REGISTRY, null));
        }
    }

    public void unregisterService(String serviceName) {
        synchronized (super.services) {
            if(super.services.remove(serviceName) == null) {
                //nothing?
            }else {
                SerenaNetworkServer.broadcastIfOnline(new Packet6ServiceRegistration(serviceName, false));
                updateGui(new GuiUpdateRequest(GuiUpdateRequest.Type.SERVICES_REGISTRY, null));
            }
        }
    }

}
