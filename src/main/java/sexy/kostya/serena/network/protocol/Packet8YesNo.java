package sexy.kostya.serena.network.protocol;

import org.inmine.network.Buffer;
import org.inmine.network.callback.CallbackPacket;

/**
 * Created by RINES on 07.03.2018.
 */
public class Packet8YesNo extends CallbackPacket {

    private boolean result;

    public Packet8YesNo(boolean result) {
        this.result = result;
    }

    public Packet8YesNo() {}

    public boolean isResult() {
        return result;
    }

    @Override
    public int getId() {
        return 8;
    }

    @Override
    public void write(Buffer buffer) {
        super.write(buffer);
        buffer.writeBoolean(this.result);
    }

    @Override
    public void read(Buffer buffer) {
        super.read(buffer);
        this.result = buffer.readBoolean();
    }
}
