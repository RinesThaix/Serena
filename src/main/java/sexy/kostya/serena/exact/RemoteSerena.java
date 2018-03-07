package sexy.kostya.serena.exact;

import sexy.kostya.serena.Serena;
import sexy.kostya.serena.gui.GuiUpdateRequest;
import sexy.kostya.serena.network.SerenaNetworkClient;
import sexy.kostya.serena.network.protocol.Packet5UpdateMaxLogsAmount;
import sexy.kostya.serena.service.RemoteSerenaService;

import java.util.logging.Logger;

/**
 * Created by RINES on 07.03.2018.
 */
public class RemoteSerena extends Serena<RemoteSerenaService> {

    private final String hostname;
    private final int port;

    public RemoteSerena(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
        new SerenaNetworkClient(Logger.getGlobal()).connect(hostname, port);
    }

    public String getHostname() {
        return this.hostname;
    }

    public int getPort() {
        return this.port;
    }

    public void changeMaxLogRecordsPerServiceFromServer(int maxLogRecordsPerService) {
        this.maxLogRecordsPerService = maxLogRecordsPerService;
    }

    public void setMaxLogRecordsPerServiceToServer(int maxLogRecordsPerService) {
        SerenaNetworkClient.sendPacketIfOnline(new Packet5UpdateMaxLogsAmount(maxLogRecordsPerService));
    }

    @Override
    public boolean isHostingServer() {
        return false;
    }

    @Override
    public void registerService(RemoteSerenaService service) {
        synchronized (super.services) {
            super.services.put(service.getName(), service);
            updateGui(new GuiUpdateRequest(GuiUpdateRequest.Type.SERVICES_REGISTRY, null));
        }
    }

    @Override
    public void unregisterService(String serviceName) {
        synchronized (super.services) {
            super.services.remove(serviceName);
            updateGui(new GuiUpdateRequest(GuiUpdateRequest.Type.SERVICES_REGISTRY, null));
        }
    }


}
