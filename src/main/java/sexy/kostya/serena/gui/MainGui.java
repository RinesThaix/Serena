package sexy.kostya.serena.gui;

import sexy.kostya.serena.service.SerenaService;

import javax.swing.*;

/**
 * Created by RINES on 07.03.2018.
 */
public class MainGui {

    private SerenaService selectedService;
    private MainTab mainTab;
    private ComponentsList components;

    public MainGui() {
        this.mainTab = new MainTab(this);
        this.components = new ComponentsList(this);
    }

    MainTab getMainTab() {
        return this.mainTab;
    }

    ComponentsList getComponentsList() {
        return this.components;
    }

    public SerenaService getSelectedService() {
        return selectedService;
    }

    public void setSelectedService(SerenaService selectedService) {
        this.selectedService = selectedService;
        this.mainTab.statisticsPane.updateComponent();
        this.mainTab.loggingPane.onSelectedServiceChange(selectedService);
    }
}
