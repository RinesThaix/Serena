package sexy.kostya.serena.network.protocol;

import org.inmine.network.Buffer;
import org.inmine.network.Packet;

import java.util.logging.Level;

/**
 * Created by RINES on 07.03.2018.
 */
public class Packet4LoggingMessage extends Packet {

    private String serviceName;
    private String message;
    private Level level;
    private long time;

    public Packet4LoggingMessage(String serviceName, String message, Level level, long time) {
        this.serviceName = serviceName;
        this.message = message;
        this.level = level;
        this.time = time;
    }

    public Packet4LoggingMessage() {}

    public String getServiceName() {
        return serviceName;
    }

    public String getMessage() {
        return message;
    }

    public Level getLevel() {
        return level;
    }

    public long getTime() {
        return time;
    }

    @Override
    public int getId() {
        return 4;
    }

    @Override
    public void write(Buffer buffer) {
        buffer.writeString(this.serviceName);
        buffer.writeString(this.message);
        buffer.writeString(this.level.getName());
        buffer.writeVarLong(this.time);
    }

    @Override
    public void read(Buffer buffer) {
        this.serviceName = buffer.readString(24);
        this.message = buffer.readString(1024);
        this.level = Level.parse(buffer.readString(16));
        this.time = buffer.readVarLong();
    }

}
