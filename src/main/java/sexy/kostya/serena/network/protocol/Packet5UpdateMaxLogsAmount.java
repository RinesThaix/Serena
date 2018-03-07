package sexy.kostya.serena.network.protocol;

import org.inmine.network.Buffer;
import org.inmine.network.Packet;

/**
 * Created by RINES on 07.03.2018.
 */
public class Packet5UpdateMaxLogsAmount extends Packet {

    private int maxLogs;

    public Packet5UpdateMaxLogsAmount(int maxLogs) {
        this.maxLogs = maxLogs;
    }

    public Packet5UpdateMaxLogsAmount() {}

    public int getMaxLogs() {
        return maxLogs;
    }

    @Override
    public int getId() {
        return 5;
    }

    @Override
    public void write(Buffer buffer) {
        buffer.writeVarInt(this.maxLogs);
    }

    @Override
    public void read(Buffer buffer) {
        this.maxLogs = buffer.readVarInt();
    }

}
