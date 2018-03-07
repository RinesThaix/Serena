package sexy.kostya.serena.network;

import org.inmine.network.Packet;
import org.inmine.network.netty.client.NettyClient;
import sexy.kostya.serena.Serena;
import sexy.kostya.serena.exact.RemoteSerena;
import sexy.kostya.serena.gui.GuiUpdateRequest;
import sexy.kostya.serena.network.protocol.*;
import sexy.kostya.serena.service.RemoteSerenaService;

import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by RINES on 07.03.2018.
 */
public class SerenaNetworkClient extends NettyClient {

    private static SerenaNetworkClient instance;

    public static SerenaNetworkClient getInstance() {
        return instance;
    }

    public static void sendPacketIfOnline(Packet packet) {
        if(instance == null)
            return;
        instance.sendPacket(packet);
    }

    public SerenaNetworkClient(Logger logger) {
        super(logger, new SerenaProtocol());
        instance = this;
    }

    public void onConnected() {
        getConnection().getHandler().setup(new Handler());
    }

    public void onDisconnected() {
        Serena serena = Serena.getInstance();
        serena.processServices(ss -> ((Map) ss).clear());
        serena.updateGui(new GuiUpdateRequest(GuiUpdateRequest.Type.RELOAD, null));
    }

    private class Handler extends SerenaPacketProcessor {

        @Override
        protected void register() {
            addHandler(Packet1ServicesMap.class, this::handle);
            addHandler(Packet2UpdateServiceState.class, this::handle);
            addHandler(Packet3UpdateServiceLoggingLevel.class, this::handle);
            addHandler(Packet4LoggingMessage.class, this::handle);
            addHandler(Packet5UpdateMaxLogsAmount.class, this::handle);
            addHandler(Packet6ServiceRegistration.class, this::handle);
            addHandler(Packet9Secured.class, this::handle);
        }

        private void handle(Packet1ServicesMap packet) {
            Serena serena = Serena.getInstance();
            packet.getServices().forEach(serena::registerService);
            serena.updateGui(new GuiUpdateRequest(GuiUpdateRequest.Type.RELOAD, null));
        }

        private void handle(Packet2UpdateServiceState packet) {
            RemoteSerena serena = getRemoteSerena();
            RemoteSerenaService service = serena.lookupService(packet.getServiceName());
            if(service == null)
                return;
            service.changeStateFromServer(packet.getState());
        }

        private void handle(Packet3UpdateServiceLoggingLevel packet) {
            RemoteSerena serena = getRemoteSerena();
            RemoteSerenaService service = serena.lookupService(packet.getServiceName());
            if(service == null)
                return;
            service.changeLoggingLevelFromServer(packet.getLevel());
        }

        private void handle(Packet4LoggingMessage packet) {
            RemoteSerena serena = getRemoteSerena();
            RemoteSerenaService service = serena.lookupService(packet.getServiceName());
            if(service == null)
                return;
            service.addRecord(packet.getMessage(), packet.getLevel(), packet.getTime());
        }

        private void handle(Packet5UpdateMaxLogsAmount packet) {
            getRemoteSerena().changeMaxLogRecordsPerServiceFromServer(packet.getMaxLogs());
        }

        private void handle(Packet6ServiceRegistration packet) {
            if(packet.isRegister()) {
                getRemoteSerena().registerService(new RemoteSerenaService(packet.getServiceName(), packet.getState(), packet.getLoggingLevel()));
            }else {
                getRemoteSerena().unregisterService(packet.getServiceName());
            }
        }

        private void handle(Packet9Secured packet) {
            //open login menu
        }

    }

    private RemoteSerena getRemoteSerena() {
        return (RemoteSerena) Serena.getInstance();
    }

}
