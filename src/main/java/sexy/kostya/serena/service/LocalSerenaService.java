package sexy.kostya.serena.service;

import sexy.kostya.serena.Serena;
import sexy.kostya.serena.gui.GuiUpdateRequest;
import sexy.kostya.serena.network.SerenaNetworkServer;
import sexy.kostya.serena.network.protocol.Packet2UpdateServiceState;
import sexy.kostya.serena.network.protocol.Packet3UpdateServiceLoggingLevel;
import sexy.kostya.serena.network.protocol.Packet4LoggingMessage;

import java.util.logging.Level;

/**
 * Created by RINES on 07.03.2018.
 */
public class LocalSerenaService extends SerenaService {

    private final ServiceLogger logger;
    private boolean registered;

    public LocalSerenaService(String name) {
        super(name);
        this.logger = new ServiceLogger();
    }

    public LocalSerenaService(String name, ServiceState state, Level loggingLevel) {
        this(name);
        super.state = state;
        super.loggingLevel = loggingLevel;
    }

    public LocalSerenaService(String name, ServiceState state, Level loggingLevel, boolean logToConsole) {
        this(name, state, loggingLevel);
        setLogToConsole(logToConsole);
    }

    public void setRegistered(boolean registered) {
        this.registered = registered;
    }

    public void setState(ServiceState state) {
        super.state = state;
        if(this.registered)
            SerenaNetworkServer.broadcastIfOnline(new Packet2UpdateServiceState(getName(), state));
        Serena.getInstance().updateGui(new GuiUpdateRequest(GuiUpdateRequest.Type.SERVICE_DATA, this));
    }

    public void setLoggingLevel(Level loggingLevel) {
        super.loggingLevel = loggingLevel;
        if(this.registered)
            SerenaNetworkServer.broadcastIfOnline(new Packet3UpdateServiceLoggingLevel(getName(), loggingLevel));
        Serena.getInstance().updateGui(new GuiUpdateRequest(GuiUpdateRequest.Type.SERVICE_DATA, this));
    }

    public ServiceLogger getLogger() {
        return this.logger;
    }

    public class ServiceLogger {

        public void log(Level priority, String message) {
            if(LocalSerenaService.super.loggingLevel.intValue() > priority.intValue())
                return;
            long time = System.currentTimeMillis();
            addRecord(message, priority, time);
            if(registered)
                SerenaNetworkServer.broadcastIfOnline(new Packet4LoggingMessage(getName(), message, priority, time));
        }

        public void log(Level priority, String message, Object... args) {
            if(LocalSerenaService.super.loggingLevel.intValue() > priority.intValue())
                return;
            long time = System.currentTimeMillis();
            message = String.format(message, args);
            addRecord(message, priority, time);
            if(registered)
                SerenaNetworkServer.broadcastIfOnline(new Packet4LoggingMessage(getName(), message, priority, time));
        }

        public void info(String message) {
            log(Level.INFO, message);
        }

        public void info(String message, Object... args) {
            log(Level.INFO, message, args);
        }

        public void warn(String message) {
            log(Level.WARNING, message);
        }

        public void warn(String message, Object... args) {
            log(Level.WARNING, message, args);
        }

        public void severe(String message) {
            log(Level.SEVERE, message);
        }

        public void severe(String message, Object... args) {
            log(Level.SEVERE, message, args);
        }

        public void fatal(String message) {
            severe(message);
        }

        public void fatal(String message, Object... args) {
            severe(message, args);
        }

        public void fine(String message) {
            log(Level.FINE, message);
        }

        public void fine(String message, Object... args) {
            log(Level.FINE, message, args);
        }

        public void debug(String message) {
            fine(message);
        }

        public void debug(String message, Object... args) {
            fine(message, args);
        }

    }

}
