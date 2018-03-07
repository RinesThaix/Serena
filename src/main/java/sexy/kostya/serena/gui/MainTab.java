package sexy.kostya.serena.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by RINES on 07.03.2018.
 */
public class MainTab extends JPanel {

    private final MainGui gui;
    final LoggingPane loggingPane;
    final StatisticsPane statisticsPane;

    public MainTab(MainGui gui) {
        super(new BorderLayout());
        this.gui = gui;
        this.loggingPane = new LoggingPane(gui);
        this.statisticsPane = new StatisticsPane(gui);
        add(this.statisticsPane, "North");
        add(this.loggingPane, "Center");
    }

    public void handleUpdateRequest(GuiUpdateRequest request) {
        switch(request.getType()) {
            case RELOAD: {
                this.statisticsPane.updateComponent();
                this.loggingPane.onSelectedServiceChange(null);
                break;
            }case SERVICE_DATA: {
                this.statisticsPane.updateComponent();
                this.loggingPane.onServiceDataChange(request.getService());
                this.gui.getComponentsList().updateServices();
                break;
            }case SERVICES_REGISTRY: {
                this.gui.getComponentsList().updateServices();
                break;
            }default: {
                if(request.getService() == this.gui.getSelectedService())
                    request.getService().processRecords(records -> this.loggingPane.onNewRecord(records.getLast()));
                break;
            }
        }
    }

}
