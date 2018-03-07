package sexy.kostya.serena.service;

import java.util.logging.Level;

/**
 * Created by RINES on 07.03.2018.
 */
public class LocalRepeatingSerenaService extends LocalSerenaService {

    private long lastCall;

    public LocalRepeatingSerenaService(String name, long maxTimeInMillisWithoutCalling) {
        super(name);
        startWatcher(maxTimeInMillisWithoutCalling);
    }

    public LocalRepeatingSerenaService(String name, ServiceState state, Level loggingLevel, long maxTimeInMillisWithoutCalling) {
        super(name, state, loggingLevel);
        startWatcher(maxTimeInMillisWithoutCalling);
    }

    public LocalRepeatingSerenaService(String name, ServiceState state, Level loggingLevel, boolean logToConsole, long maxTimeInMillisWithoutCalling) {
        super(name, state, loggingLevel, logToConsole);
        startWatcher(maxTimeInMillisWithoutCalling);
    }

    public void call() {
        this.lastCall = System.currentTimeMillis();
        if(getState() == ServiceState.INACTIVE)
            setState(ServiceState.ACTIVE);
    }

    private void startWatcher(long maxTimeInMillisWithoutCalling) {
        Thread t = new Thread(() -> {
            while(true) {
                if(getState() == ServiceState.ACTIVE && System.currentTimeMillis() - this.lastCall > maxTimeInMillisWithoutCalling)
                    setState(ServiceState.INACTIVE);
                try {
                    Thread.sleep(maxTimeInMillisWithoutCalling / 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        t.setName("SerenaService-Watcher#" + getClass().getSimpleName());
        t.setDaemon(true);
        t.start();
    }

}
