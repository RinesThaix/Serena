package sexy.kostya.serena.network.protocol;

import org.inmine.network.Buffer;
import org.inmine.network.callback.CallbackPacket;

/**
 * Created by RINES on 07.03.2018.
 */
public class Packet7Authorize extends CallbackPacket {

    private String login;
    private String password;

    public Packet7Authorize(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public Packet7Authorize() {}

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public int getId() {
        return 7;
    }

    @Override
    public void write(Buffer buffer) {
        super.write(buffer);
        buffer.writeString(this.login);
        buffer.writeString(this.password);
    }

    @Override
    public void read(Buffer buffer) {
        super.read(buffer);
        this.login = buffer.readString(16);
        this.password = buffer.readString(64);
    }

}
