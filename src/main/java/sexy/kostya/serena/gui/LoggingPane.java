package sexy.kostya.serena.gui;

import sexy.kostya.serena.service.LocalSerenaService;
import sexy.kostya.serena.service.RemoteSerenaService;
import sexy.kostya.serena.service.SerenaService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Created by RINES on 07.03.2018.
 */
public class LoggingPane extends JPanel implements ChangeListener {

    //OFF -> Severe -> Warning -> Info -> Debug

    private final MainGui gui;
    private final JPanel controls;
    private final LoggingList loggingList;
    private final JSlider slider;
    private final JLabel levelName;

    private final Map<Level, Integer> fromLevels = new HashMap<>();
    private final Map<Integer, Level> toLevels = new HashMap<>();
    private int currentPriority = -1;

    public LoggingPane(MainGui gui) {
        super(new BorderLayout());
        this.gui = gui;
        addLevel(Level.FINE, 0);
        addLevel(Level.INFO, 1);
        addLevel(Level.WARNING, 2);
        addLevel(Level.SEVERE, 3);
        addLevel(Level.OFF, 4);
        this.loggingList = new LoggingList();
        add(new JScrollPane(this.loggingList), "Center");
        this.controls = new JPanel(new FlowLayout(0));
        (this.slider = new JSlider(0, 4, 1)).setPaintTicks(true);
        this.slider.setSnapToTicks(true);
        this.slider.addChangeListener(this);
        this.slider.setMajorTickSpacing(1);
        this.levelName = new JLabel();
        this.controls.add(new JLabel("Threshold: "));
        this.controls.add(this.slider);
        this.controls.add(this.levelName);
        setPriority(Level.INFO, false);
        add(this.controls, "North");
    }

    public void onSelectedServiceChange(SerenaService selectedService) {
        this.loggingList.model.onServiceChange(selectedService);
        if(selectedService != null)
            setPriority(selectedService.getLoggingLevel(), false);
    }

    public void onServiceDataChange(SerenaService service) {
        if(this.gui.getSelectedService() != service)
            return;
        this.loggingList.model.onPriorityChange(service);
        if(service != null)
            setPriority(service.getLoggingLevel(), false);
    }

    public void onNewRecord(LogRecord record) {
        this.loggingList.model.onNewRecord(record);
    }

    private boolean changingManually = false;

    private void setPriority(Level level, boolean changeService) {
        setPriority(this.fromLevels.get(level), changeService);
    }

    private void setPriority(int id, boolean changeService) {
        if(id < 0)
            id = 0;
        else if(id > 4)
            id = 4;
        if(this.currentPriority != id) {
            this.currentPriority = id;
            Level level = this.toLevels.get(id);
            this.levelName.setText(level.getName());
            if(this.slider.getValue() != id) {
                this.changingManually = true;
                this.slider.setValue(id);
                this.changingManually = false;
            }
            SerenaService service = this.gui.getSelectedService();
            if(!changeService || service == null)
                return;
            if(service instanceof LocalSerenaService) {
                LocalSerenaService localService = (LocalSerenaService) service;
                localService.setLoggingLevel(level);
            }else {
                RemoteSerenaService remoteService = (RemoteSerenaService) service;
                remoteService.changeLoggingLevelToServer(level);
            }
        }
    }

    private void addLevel(Level level, int id) {
        this.fromLevels.put(level, id);
        this.toLevels.put(id, level);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        setPriority(this.slider.getValue(), !this.changingManually);
    }

    private static class LoggingList extends JList {

        private static final Color SEVERE_COLOR;
        private static final Color WARN_COLOR;
        private static final Color INFO_COLOR;
        private static final Color DEBUG_COLOR;

        static {
            SEVERE_COLOR = new Color(16711680);
            WARN_COLOR = new Color(16752800);
            INFO_COLOR = new Color(16777215);
            DEBUG_COLOR = new Color(12632319);
        }

        private final Model model;
        private final CellRenderer renderer;

        public LoggingList() {
            setModel(this.model = new Model(this));
            setCellRenderer(this.renderer = new CellRenderer());
            setSelectionMode(0);
            setSelectedIndex(0);
        }

        private static Color getLevelColor(Level level) {
            if(level == Level.SEVERE)
                return SEVERE_COLOR;
            if(level == Level.WARNING)
                return WARN_COLOR;
            if(level == Level.INFO)
                return INFO_COLOR;
            return DEBUG_COLOR;
        }

        private static class Model extends AbstractListModel<LogRecord> {

            private final LoggingList list;
            private SerenaService service;
            private LinkedList<LogRecord> records = new LinkedList<>();

            public Model(LoggingList list) {
                this.list = list;
            }

            public void onServiceChange(SerenaService service) {
                if(service != this.service) {
                    this.service = service;
                    onServiceOrPriorityChange();
                }
            }

            public void onPriorityChange(SerenaService service) {
                if(service != this.service)
                    return;
                onServiceOrPriorityChange();
            }

            private void onServiceOrPriorityChange() {
                synchronized (this.records) {
                    int size = getSize();
                    if(size > 0)
                        fireIntervalRemoved(this, 0, size - 1);
                    this.records.clear();
                    if(this.service == null)
                        return;
                    Level min = this.service.getLoggingLevel();
                    this.service.processRecords(rds -> rds.forEach(rd -> {
                        if(rd.getLevel().intValue() >= min.intValue())
                            this.records.add(0, rd);
                    }));
                    if(!this.records.isEmpty()) {
                        fireIntervalAdded(this, 0, this.records.size() - 1);
                        this.list.setSelectedIndex(this.records.size() - 1);
                    }
                }
            }

            public void onNewRecord(LogRecord record) {
                if(this.service == null || record.getLevel().intValue() < this.service.getLoggingLevel().intValue())
                    return;
                SwingUtilities.invokeLater(() -> {
                    synchronized (this.records) {
                        boolean last = this.list.getSelectedIndex() == this.records.size() - 1;
                        if(this.records.get(0) != record)
                            this.records.add(0, record);
                        fireIntervalAdded(this, 0, 0);
                        if(last)
                            this.list.setSelectedIndex(this.records.size() - 1);
                    }
                });
            }

            @Override
            public int getSize() {
                synchronized (this.records) {
                    return this.records.size();
                }
            }

            @Override
            public LogRecord getElementAt(int index) {
                synchronized (this.records) {
                    return this.records.get(index);
                }
            }
        }

        private static class CellRenderer extends JLabel implements ListCellRenderer<LogRecord> {

            private final LBorder border = new LBorder();

            public CellRenderer() {
                setBorder(new CompoundBorder(this.border, new EmptyBorder(0, 3, 0, 3)));
                setOpaque(true);
            }

            @Override
            public Component getListCellRendererComponent(JList list, LogRecord value, int index, boolean isSelected, boolean cellHasFocus) {
                SimpleDateFormat sf = new SimpleDateFormat("dd-MM HH:mm:ss");
                Date date = new Date(value.getMillis());
                setText(sf.format(date) + "  " + value.getLevel().getName() + "  " + value.getMessage());
                Color color = getLevelColor(value.getLevel());
                setBackground(color);
                this.border.setColor(isSelected ? Color.BLACK : color);
                setForeground(Color.BLACK);
                return this;
            }

            private static class LBorder extends LineBorder {

                public LBorder() {
                    super(Color.black, 1);
                }

                public void setColor(final Color color) {
                    this.lineColor = color;
                }

            }

        }

    }

}
