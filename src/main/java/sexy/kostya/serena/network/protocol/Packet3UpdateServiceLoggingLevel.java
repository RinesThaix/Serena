package sexy.kostya.serena.network.protocol;

import org.inmine.network.Buffer;
import org.inmine.network.Packet;

import java.util.logging.Level;

/**
 * Created by RINES on 07.03.2018.
 */
public class Packet3UpdateServiceLoggingLevel extends Packet {

    private String serviceName;
    private Level level;

    public Packet3UpdateServiceLoggingLevel(String serviceName, Level level) {
        this.serviceName = serviceName;
        this.level = level;
    }

    public Packet3UpdateServiceLoggingLevel() {}

    public String getServiceName() {
        return serviceName;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public int getId() {
        return 3;
    }

    @Override
    public void write(Buffer buffer) {
        buffer.writeString(this.serviceName);
        buffer.writeString(this.level.getName());
    }

    @Override
    public void read(Buffer buffer) {
        this.serviceName = buffer.readString(24);
        this.level = Level.parse(buffer.readString(16));
    }

}
