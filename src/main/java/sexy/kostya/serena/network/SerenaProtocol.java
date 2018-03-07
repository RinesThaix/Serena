package sexy.kostya.serena.network;

import org.inmine.network.PacketRegistry;
import sexy.kostya.serena.network.protocol.*;

/**
 * Created by RINES on 07.03.2018.
 */
public class SerenaProtocol extends PacketRegistry {

    public SerenaProtocol() {
        super(1,
                Packet1ServicesMap::new,
                Packet2UpdateServiceState::new,
                Packet3UpdateServiceLoggingLevel::new,
                Packet4LoggingMessage::new,
                Packet5UpdateMaxLogsAmount::new,
                Packet6ServiceRegistration::new,
                Packet7Authorize::new,
                Packet8YesNo::new,
                Packet9Secured::new
        );
    }

}
