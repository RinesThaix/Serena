package sexy.kostya.serena.gui;

import sexy.kostya.serena.Serena;
import sexy.kostya.serena.exact.LocalSerena;
import sexy.kostya.serena.exact.RemoteSerena;

import javax.swing.*;
import java.awt.*;

/**
 * Created by RINES on 07.03.2018.
 */
public class GuiFramework {

    private static MainTab mainTab;

    public static void handleUpdateRequest(GuiUpdateRequest request) {
        if(mainTab == null)
            return;
        mainTab.handleUpdateRequest(request);
    }

    private JFrame frame;
    private JTabbedPane pane;

    public void createGui() {
        (this.frame = new JFrame(getTitle())).setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.frame.setBounds(0, 0, 750, 550);
        this.frame.getContentPane().setLayout(new BorderLayout());
        this.pane = new JTabbedPane(1);
        generateMainGui();
        this.frame.getContentPane().add(this.pane, "Center");
        this.frame.show();
    }

    public void generateMainGui() {
        MainGui mainGui = new MainGui();
        JSplitPane splitPane = new JSplitPane(1, mainGui.getMainTab(), new JScrollPane(mainGui.getComponentsList()));
        splitPane.setResizeWeight(1D);
        addTab("Log Center", splitPane);
        mainTab = mainGui.getMainTab();
    }

    private void addTab(String name, Component component) {
        this.pane.add(name, component);
    }

    private String getTitle() {
        Serena serena = Serena.getInstance();
        if(serena == null)
            return "Serena | Connectivity";
        if(serena instanceof LocalSerena)
            return "Serena | Local";
        RemoteSerena remote = (RemoteSerena) serena;
        return "Serena | " + remote.getHostname() + ":" + remote.getPort();
    }

}
