package sexy.kostya.serena.gui;

import sexy.kostya.serena.Serena;
import sexy.kostya.serena.service.SerenaService;
import sexy.kostya.serena.service.ServiceState;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by RINES on 07.03.2018.
 */
public class ComponentsList extends JList<SerenaService> {

    private final MainGui gui;
    private final Model model;

    public ComponentsList(MainGui gui) {
        this.gui = gui;
        this.model = new Model();
        setModel(model);
        addListSelectionListener(model);
        setCellRenderer(new CellRenderer());
        setSelectionMode(0);
        setSelectedIndex(0);
    }

    public void updateServices() {
        this.model.updateServices();
    }

    private class Model extends AbstractListModel<SerenaService> implements ListSelectionListener {

        private final LinkedList<SerenaService> services = new LinkedList<>();

        {
            updateServices();
        }

        public void updateServices() {
            List<SerenaService>[] sorted = new List[1];
            Serena.getInstance().processServices(ss -> {
                sorted[0] = ((Collection<SerenaService>) ((Map) ss).values()).stream()
                        .sorted((a, b) -> a.getName().compareTo(b.getName()))
                        .collect(Collectors.toList());
            });
            SwingUtilities.invokeLater(() -> {
                synchronized (this.services) {
                    int size = this.services.size();
                    if(size > 0)
                        fireIntervalRemoved(this, 0, size - 1);
                    this.services.clear();
                    this.services.addAll(sorted[0]);
                    size = this.services.size();
                    if(size > 0)
                        fireIntervalAdded(this, 0, size - 1);
                }
            });
        }

        @Override
        public int getSize() {
            synchronized (this.services) {
                return this.services.size();
            }
        }

        @Override
        public SerenaService getElementAt(int index) {
            synchronized (this.services) {
                return this.services.get(index);
            }
        }

        @Override
        public void valueChanged(ListSelectionEvent e) {
            int index = getSelectedIndex();
            SerenaService service;
            synchronized (this.services) {
                if(index < 0 || index >= this.services.size())
                    return;
                service = this.services.get(index);
            }
            gui.setSelectedService(service);
        }

    }

    private static class CellRenderer extends JLabel implements ListCellRenderer<SerenaService> {

        private final LBorder border;

        public CellRenderer() {
            this.border = new LBorder();
            setBorder(new CompoundBorder(this.border, new EmptyBorder(0, 3, 0, 3)));
            setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends SerenaService> list, SerenaService value, int index, boolean isSelected, boolean cellHasFocus) {
            setText(value.getName());
            Color color =
                    value.getState() == ServiceState.ACTIVE ? new Color(10551200)
                            : value.getState() == ServiceState.INACTIVE ? new Color(16728128)
                            : Color.ORANGE;
            setBackground(color);
            this.border.setColor(isSelected ? Color.BLACK : color);
            setForeground(Color.BLACK);
            return this;
        }

        private static class LBorder extends LineBorder {

            public LBorder() {
                super(Color.BLACK, 1);
            }

            public void setColor(final Color color) {
                this.lineColor = color;
            }

        }

    }

}
