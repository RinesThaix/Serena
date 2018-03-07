package sexy.kostya.serena.network;

import org.inmine.network.Packet;
import org.inmine.network.netty.NettyConnection;
import org.inmine.network.netty.server.NettyServer;
import sexy.kostya.serena.Serena;
import sexy.kostya.serena.exact.LocalSerena;
import sexy.kostya.serena.network.protocol.*;
import sexy.kostya.serena.service.LocalSerenaService;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by RINES on 07.03.2018.
 */
public class SerenaNetworkServer extends NettyServer {

    private static SerenaNetworkServer instance;

    public static SerenaNetworkServer getInstance() {
        return instance;
    }

    public static void broadcastIfOnline(Packet packet) {
        if(instance == null)
            return;
        instance.broadcastPacket(packet);
    }

    private final Set<NettyConnection> authenticated = new HashSet<>();

    public SerenaNetworkServer(Logger logger) {
        super(logger, new SerenaProtocol());
        instance = this;
    }

    @Override
    public void onNewConnection(NettyConnection nettyConnection) {
        if(getLocalSerena().getAuthenticator() != null) {
            nettyConnection.getHandler().setup(new AuthHandler());
            nettyConnection.sendPacket(new Packet9Secured());
            return;
        }
        nettyConnection.getHandler().setup(new Handler());
        sendServices(nettyConnection);
        this.authenticated.add(nettyConnection);
    }

    @Override
    public void onDisconnecting(NettyConnection nettyConnection) {
        this.authenticated.remove(nettyConnection);
    }

    public void broadcastPacket(Packet packet) {
        this.authenticated.forEach(connection -> connection.sendPacket(packet));
    }

    private void sendServices(NettyConnection connection) {
        Serena.getInstance().processServices(services -> connection.sendPacket(new Packet1ServicesMap(new HashSet<>(((Map) services).values()))));
    }

    private class AuthHandler extends SerenaPacketProcessor {

        @Override
        protected void register() {
            addHandler(Packet7Authorize.class, this::handle);
        }

        private void handle(Packet7Authorize packet) {
            boolean result = getLocalSerena().getAuthenticator().authenticate(packet.getLogin(), packet.getPassword());
            packet.respond(super.connection, new Packet8YesNo(result));
            if(result) {
                authenticated.add((NettyConnection) super.connection);
                super.connection.getHandler().setup(new Handler());
            }
        }

    }

    private class Handler extends SerenaPacketProcessor {

        @Override
        protected void register() {
            addHandler(Packet2UpdateServiceState.class, this::handle);
            addHandler(Packet3UpdateServiceLoggingLevel.class, this::handle);
            addHandler(Packet5UpdateMaxLogsAmount.class, this::handle);
        }

        private void handle(Packet2UpdateServiceState packet) {
            LocalSerenaService service = getLocalSerena().lookupService(packet.getServiceName());
            if(service != null)
                service.setState(packet.getState());
        }

        private void handle(Packet3UpdateServiceLoggingLevel packet) {
            LocalSerenaService service = getLocalSerena().lookupService(packet.getServiceName());
            if(service != null)
                service.setLoggingLevel(packet.getLevel());
        }

        private void handle(Packet5UpdateMaxLogsAmount packet) {
            getLocalSerena().setMaxLogRecordsPerService(packet.getMaxLogs());
        }

    }

    private LocalSerena getLocalSerena() {
        return (LocalSerena) Serena.getInstance();
    }

}
