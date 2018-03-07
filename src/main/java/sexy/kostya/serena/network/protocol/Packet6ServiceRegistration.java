package sexy.kostya.serena.network.protocol;

import org.inmine.network.Buffer;
import org.inmine.network.Packet;
import sexy.kostya.serena.service.ServiceState;

import java.util.logging.Level;

/**
 * Created by RINES on 07.03.2018.
 */
public class Packet6ServiceRegistration extends Packet {

    private String serviceName;
    private boolean register;
    private ServiceState state;
    private Level loggingLevel;

    public Packet6ServiceRegistration(String serviceName, boolean register) {
        this.serviceName = serviceName;
        this.register = register;
    }

    public Packet6ServiceRegistration(String serviceName, boolean register, ServiceState state, Level loggingLevel) {
        this.serviceName = serviceName;
        this.register = register;
        this.state = state;
        this.loggingLevel = loggingLevel;
    }

    public Packet6ServiceRegistration() { }

    public String getServiceName() {
        return serviceName;
    }

    public boolean isRegister() {
        return register;
    }

    public ServiceState getState() {
        return state;
    }

    public Level getLoggingLevel() {
        return loggingLevel;
    }

    @Override
    public int getId() {
        return 6;
    }

    @Override
    public void write(Buffer buffer) {
        buffer.writeString(this.serviceName);
        buffer.writeBoolean(this.register);
        if(this.register) {
            buffer.writeEnum(this.state);
            buffer.writeString(this.loggingLevel.getName());
        }
    }

    @Override
    public void read(Buffer buffer) {
        this.serviceName = buffer.readString(24);
        this.register = buffer.readBoolean();
        if(this.register) {
            this.state = buffer.readEnum(ServiceState.class);
            this.loggingLevel = Level.parse(buffer.readString(16));
        }
    }

}
