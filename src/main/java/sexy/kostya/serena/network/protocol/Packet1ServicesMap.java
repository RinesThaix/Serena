package sexy.kostya.serena.network.protocol;

import org.inmine.network.Buffer;
import org.inmine.network.Packet;
import sexy.kostya.serena.Serena;
import sexy.kostya.serena.service.RemoteSerenaService;
import sexy.kostya.serena.service.SerenaService;
import sexy.kostya.serena.service.ServiceState;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Created by RINES on 07.03.2018.
 */
public class Packet1ServicesMap extends Packet {

    private Collection<SerenaService> services;
    private Collection<RemoteSerenaService> remote;

    public Packet1ServicesMap() { }

    public Packet1ServicesMap(Collection<SerenaService> services) {
        this.services = services;
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public void write(Buffer buffer) {
        buffer.writeCollection(this.services, service -> {
            buffer.writeString(service.getName());
            buffer.writeEnum(service.getState());
            buffer.writeString(service.getLoggingLevel().getName());
            service.processRecords(records -> {
                buffer.writeCollection(records, record -> {
                    buffer.writeString(record.getLevel().getName());
                    buffer.writeVarLong(record.getMillis());
                    buffer.writeString(record.getMessage());
                });
            });
        });
    }

    @Override
    public void read(Buffer buffer) {
        int maxRecords = Serena.getInstance().getMaxLogRecordsPerService();
        this.remote = buffer.readCollection(256, ArrayList::new, () -> {
            RemoteSerenaService service = new RemoteSerenaService(buffer.readString(24), buffer.readEnum(ServiceState.class), Level.parse(buffer.readString(16)));
            List<LogRecord> records = buffer.readCollection(maxRecords, ArrayList::new, () -> {
                Level level = Level.parse(buffer.readString(16));
                long time = buffer.readVarLong();
                LogRecord record = new LogRecord(level, buffer.readString(1024));
                record.setMillis(time);
                return record;
            });
            records.forEach(record -> service.addRecord(record.getMessage(), record.getLevel(), record.getMillis()));
            return service;
        });
    }

    public Collection<RemoteSerenaService> getServices() {
        return this.remote;
    }

}
