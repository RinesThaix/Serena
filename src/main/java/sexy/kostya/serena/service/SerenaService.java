package sexy.kostya.serena.service;

import sexy.kostya.serena.Serena;
import sexy.kostya.serena.gui.GuiUpdateRequest;

import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Created by RINES on 07.03.2018.
 */
public abstract class SerenaService {

    private final String name;
    protected ServiceState state = ServiceState.INACTIVE;
    protected Level loggingLevel = Level.INFO;
    private boolean logToConsole;

    private LinkedList<LogRecord> records = new LinkedList<>();

    public SerenaService(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public ServiceState getState() {
        return this.state;
    }

    public Level getLoggingLevel() {
        return loggingLevel;
    }

    public boolean isLogToConsole() {
        return this.logToConsole;
    }

    public void setLogToConsole(boolean logToConsole) {
        this.logToConsole = logToConsole;
    }

    public void addRecord(String message, Level priority, long time) {
        synchronized (this.records) {
            if(this.records.size() == Serena.getInstance().getMaxLogRecordsPerService())
                this.records.removeFirst();
            LogRecord record = new LogRecord(priority, message);
            record.setMillis(time);
            this.records.addLast(record);
            if(this.logToConsole)
                logFormatMessage(message, priority, time);
            Serena.getInstance().updateGui(new GuiUpdateRequest(GuiUpdateRequest.Type.LOGGING_MESSAGE, this));
        }
    }

    public void processRecords(Consumer<LinkedList<LogRecord>> consumer) {
        synchronized (this.records) {
            consumer.accept(this.records);
        }
    }

    public void registerService() {
        Serena.getInstance().registerService(this);
    }

    private void logFormatMessage(String message, Level priority, long time) {
        int spacesAmount = 21 - this.name.length();
        StringBuilder sb = new StringBuilder();
        String prefix = priority.getName();
        if(prefix.length() > 4)
            prefix = prefix.substring(0, 4);
        sb.append(prefix).append("    ").append(this.name);
        for(int i = 0; i < spacesAmount; ++i)
            sb.append(" ");
        System.out.println(sb.append("- ").append(message).toString());
    }

}
