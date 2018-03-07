package sexy.kostya.serena.gui;

import sexy.kostya.serena.service.SerenaService;

/**
 * Created by RINES on 07.03.2018.
 */
public class GuiUpdateRequest {

    private final Type type;
    private final SerenaService service;

    public GuiUpdateRequest(Type type, SerenaService service) {
        this.type = type;
        this.service = service;
    }

    public Type getType() {
        return this.type;
    }

    public SerenaService getService() {
        return this.service;
    }

    public enum Type {
        SERVICE_DATA,
        LOGGING_MESSAGE,
        RELOAD,
        SERVICES_REGISTRY
    }

}
