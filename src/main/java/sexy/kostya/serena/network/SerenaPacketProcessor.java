package sexy.kostya.serena.network;

import org.inmine.network.Packet;
import org.inmine.network.PacketProcessor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Created by RINES on 07.03.2018.
 */
public abstract class SerenaPacketProcessor extends PacketProcessor {

    private final static Executor packetsExecutor = Executors.newFixedThreadPool(getExecutorThreadsAmount(), new ThreadFactory() {

        private AtomicInteger id = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("PacketProcessor-" + this.id.incrementAndGet());
            return thread;
        }
    });

    private static int getExecutorThreadsAmount() {
        return SerenaNetworkServer.getInstance() == null ? 4 : 16;
    }

    @Override
    protected <T extends Packet> void addHandler(Class<T> packet, Consumer<T> handler) {
        super.addHandler(packet, p -> packetsExecutor.execute(() -> {
            try {
                handler.accept(p);
            }catch(Throwable t) {
                new Exception("Can not handle packet " + packet.getSimpleName(), t).printStackTrace();
            }
        }));
    }

}
