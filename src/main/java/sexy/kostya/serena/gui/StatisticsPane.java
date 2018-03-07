package sexy.kostya.serena.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by RINES on 07.03.2018.
 */
public class StatisticsPane extends JPanel {

    private final MainGui gui;
    private final JLabel name;
    private final JLabel state;

    public StatisticsPane(MainGui gui) {
        super(new GridBagLayout());
        this.gui = gui;
        this.setBorder(new EmptyBorder(0, 3, 0 , 3));
        GridBagConstraints lconst = new GridBagConstraints();
        GridBagConstraints rconst = new GridBagConstraints();
        lconst.weightx = 0.0;
        rconst.weightx = 1.0;
        lconst.fill = 1;
        rconst.fill = 1;
        lconst.ipadx = 3;
        rconst.ipadx = 3;
        rconst.gridwidth = 0;
        add(createKeyLabel("Name: "), lconst);
        add(this.name = createValueLabel(), rconst);
        add(createKeyLabel("State: "), lconst);
        add(this.state = createValueLabel(), rconst);
    }

    public void updateComponent() {
        if(this.gui.getSelectedService() == null)
            return;
        this.name.setText(this.gui.getSelectedService().getName() + " ");
        this.state.setText(this.gui.getSelectedService().getState().name() + " ");
    }

    private JLabel createKeyLabel(String name) {
        JLabel label = new JLabel(name);
        label.setAlignmentX(0F);
        return label;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel();
        label.setForeground(Color.BLUE);
        label.setAlignmentX(0F);
        return label;
    }

}
