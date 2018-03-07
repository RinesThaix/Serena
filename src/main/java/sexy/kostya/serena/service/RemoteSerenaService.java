package sexy.kostya.serena.service;

import sexy.kostya.serena.Serena;
import sexy.kostya.serena.gui.GuiUpdateRequest;
import sexy.kostya.serena.network.SerenaNetworkClient;
import sexy.kostya.serena.network.protocol.Packet2UpdateServiceState;
import sexy.kostya.serena.network.protocol.Packet3UpdateServiceLoggingLevel;

import java.util.logging.Level;

/**
 * Created by RINES on 07.03.2018.
 */
public class RemoteSerenaService extends SerenaService {

    public RemoteSerenaService(String name, ServiceState state, Level loggingLevel) {
        super(name);
        changeStateFromServer(state);
        changeLoggingLevelFromServer(loggingLevel);
    }

    public void changeStateFromServer(ServiceState state) {
        super.state = state;
        Serena.getInstance().updateGui(new GuiUpdateRequest(GuiUpdateRequest.Type.SERVICE_DATA, this));
    }

    public void changeLoggingLevelFromServer(Level loggingLevel) {
        super.loggingLevel = loggingLevel;
        Serena.getInstance().updateGui(new GuiUpdateRequest(GuiUpdateRequest.Type.SERVICE_DATA, this));
    }

    public void changeStateToServer(ServiceState state) {
        SerenaNetworkClient.sendPacketIfOnline(new Packet2UpdateServiceState(getName(), state));
    }

    public void changeLoggingLevelToServer(Level loggingLevel) {
        SerenaNetworkClient.sendPacketIfOnline(new Packet3UpdateServiceLoggingLevel(getName(), loggingLevel));
    }

}
