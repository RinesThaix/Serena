package sexy.kostya.serena.network.protocol;

import org.inmine.network.Buffer;
import org.inmine.network.Packet;
import sexy.kostya.serena.service.ServiceState;

/**
 * Created by RINES on 07.03.2018.
 */
public class Packet2UpdateServiceState extends Packet {

    private String serviceName;
    private ServiceState state;

    public Packet2UpdateServiceState(String serviceName, ServiceState state) {
        this.serviceName = serviceName;
        this.state = state;
    }

    public Packet2UpdateServiceState() {}

    public String getServiceName() {
        return serviceName;
    }

    public ServiceState getState() {
        return state;
    }

    @Override
    public int getId() {
        return 2;
    }

    @Override
    public void write(Buffer buffer) {
        buffer.writeString(this.serviceName);
        buffer.writeEnum(this.state);
    }

    @Override
    public void read(Buffer buffer) {
        this.serviceName = buffer.readString(24);
        this.state = buffer.readEnum(ServiceState.class);
    }

}
