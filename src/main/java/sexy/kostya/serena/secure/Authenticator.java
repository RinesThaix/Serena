package sexy.kostya.serena.secure;

/**
 * Created by RINES on 07.03.2018.
 */
public abstract class Authenticator {

    public abstract boolean authenticate(String login, String password);

}
