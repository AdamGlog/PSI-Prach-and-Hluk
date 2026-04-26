package sk.prach.hluk.view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import sk.prach.hluk.model.Naradie;

/**
 * VypozickaUI - hlavne okno pre UC02 Vypožičaj náradie.
 * Umožňuje vyhľadávanie a zaevidovanie výdaja náradia.
 */
public class VypozickaUI extends JFrame {

    // === FARBY — zhodné s ostatnými UC ===
    public static final Color ORANGE = new Color(0xFA, 0x6D, 0x04);
    public static final Color LIGHT_GREY = new Color(0xBA, 0xBA, 0xBA);
    public static final Color DARK_GREY = new Color(0x90, 0x90, 0x90);
    public static final Color LIGHT_BLUE = new Color(0xBD, 0xDC, 0xFF);
    public static final Color DARK_BLUE = new Color(0x81, 0xBC, 0xFF);
    public static final Color TABLE_BG = new Color(0xD9, 0xD9, 0xD9);
    public static final Color ROW_WHITE = Color.WHITE;
    public static final Color ROW_GREY = new Color(0xEE, 0xEE, 0xEE);
    public static final Color GREEN_OK = new Color(0x4C, 0xAF, 0x50);
    public static final Color RED_WARN = new Color(0xE5, 0x39, 0x35);

    // === Komponenty ===
    private JTextField searchField;
    private JButton searchBtn;
    private JTable tabulkaNaradia;
    private DefaultTableModel tableModel;

    // Listenery
    private Consumer<String> searchListener;
    private Consumer<Integer> vypozicajListener;
    private BorrowConfirmCallback confirmCallback;

    // UC switch callback
    private Runnable[] ucSwitchCallback;

    public interface BorrowConfirmCallback {
        void onConfirm(int naradieId, String typ, String meno, String telefon, double zaloha, String projekt);
    }

    public VypozickaUI() {
        setTitle("Prach & Hluk – Vypožičaj náradie (UC02)");
        setSize(980, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    private JPanel buildHeader() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Color.WHITE);
        outer.setBorder(BorderFactory.createEmptyBorder(12, 20, 0, 20));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(Color.WHITE);

        JLabel logo = new JLabel("⚙  Prach & Hluk");
        logo.setFont(new Font("Serif", Font.BOLD, 28));
        logo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel prihlaseny = new JLabel("👤 Prihlásený");
        prihlaseny.setFont(new Font("SansSerif", Font.PLAIN, 12));
        prihlaseny.setForeground(DARK_GREY);

        topRow.add(logo, BorderLayout.CENTER);
        topRow.add(prihlaseny, BorderLayout.EAST);

        JPanel nav = new JPanel(new GridLayout(1, 4));
        nav.setBackground(Color.WHITE);
        nav.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        String[] labels = { "UC1", "UC2", "UC3", "UC4" };
        for (int i = 0; i < labels.length; i++) {
            final int idx = i;
            JButton btn = new JButton(labels[i]);
            btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(DARK_GREY));
            btn.setBackground(labels[i].equals("UC2") ? ORANGE : Color.WHITE);
            btn.setForeground(labels[i].equals("UC2") ? Color.WHITE : Color.BLACK);
            btn.addActionListener(e -> {
                if (ucSwitchCallback != null)
                    ucSwitchCallback[idx].run();
            });
            nav.add(btn);
        }

        outer.add(topRow, BorderLayout.NORTH);
        outer.add(nav, BorderLayout.SOUTH);
        return outer;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(LIGHT_GREY);
        center.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setBackground(LIGHT_GREY);
        searchField = new JTextField(20);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchBtn = new JButton("🔍 Vyhľadať náradie");
        searchBtn.setBackground(ORANGE);
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.addActionListener(e -> {
            if (searchListener != null)
                searchListener.accept(searchField.getText());
        });
        searchPanel.add(new JLabel("Hľadať (ID/Názov):"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(TABLE_BG);
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(TABLE_BG);
        titleBar.setBorder(BorderFactory.createEmptyBorder(0, 4, 8, 4));

        JLabel titleLbl = new JLabel("Sklad náradia – Výdaj do prenájmu");
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleBar.add(titleLbl, BorderLayout.WEST);

        JLabel badge = new JLabel("UC02");
        badge.setFont(new Font("SansSerif", Font.BOLD, 12));
        badge.setForeground(Color.WHITE);
        badge.setBackground(ORANGE);
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        titleBar.add(badge, BorderLayout.EAST);

        card.add(titleBar, BorderLayout.NORTH);

        String[] cols = { "ID", "Názov", "Stav", "Výpožičky", "Servisy", "Akcia" };
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tabulkaNaradia = new JTable(tableModel);
        tabulkaNaradia.setRowHeight(40);
        tabulkaNaradia.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabulkaNaradia.setShowGrid(false);
        tabulkaNaradia.setIntercellSpacing(new Dimension(0, 3));
        tabulkaNaradia.setBackground(TABLE_BG);

        JTableHeader hdr = tabulkaNaradia.getTableHeader();
        hdr.setBackground(TABLE_BG);
        hdr.setFont(new Font("SansSerif", Font.PLAIN, 12));

        tabulkaNaradia.getColumnModel().getColumn(5).setCellRenderer(new VypozicajBtnRenderer());

        tabulkaNaradia.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int col = tabulkaNaradia.columnAtPoint(e.getPoint());
                int row = tabulkaNaradia.rowAtPoint(e.getPoint());
                if (col == 5 && row >= 0) {
                    int id = (int) tableModel.getValueAt(row, 0);
                    if (vypozicajListener != null)
                        vypozicajListener.accept(id);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabulkaNaradia);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(TABLE_BG);
        card.add(scroll, BorderLayout.CENTER);

        center.add(searchPanel, BorderLayout.NORTH);
        center.add(card, BorderLayout.CENTER);
        return center;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(DARK_GREY);
        footer.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel copy = new JLabel("© 2025 Prach & Hluk s.r.o.", SwingConstants.RIGHT);
        copy.setForeground(Color.LIGHT_GRAY);
        copy.setFont(new Font("SansSerif", Font.PLAIN, 10));

        footer.add(new JLabel("📍 Systém pre správu výpožičiek"), BorderLayout.WEST);
        footer.add(copy, BorderLayout.SOUTH);
        return footer;
    }

    public void zobrazNaradie(List<Naradie> zoznam) {
        tableModel.setRowCount(0);
        for (Naradie n : zoznam) {
            tableModel.addRow(new Object[] {
                    n.getId(),
                    n.getNazov(),
                    n.getStav(),
                    n.getVypozicaneCount(),
                    n.getServisovaneCount(),
                    n.getStav().equals("Dostupné") ? "Vypožičať" : "Nedostupné"
            });
        }
    }

    public void openBorrowModal(Naradie n) {
        JDialog dlg = new JDialog(this, "Nová výpožička – UC02", true);
        dlg.setSize(450, 520);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        body.setBackground(Color.WHITE);

        JLabel title = new JLabel("Výdaj náradia: " + n.getNazov());
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        body.add(title);
        body.add(Box.createVerticalStrut(20));

        ButtonGroup typeGroup = new ButtonGroup();
        JRadioButton rbExterny = new JRadioButton("Externý nájom (Zákazník)", true);
        JRadioButton rbInterny = new JRadioButton("Interná réžia (Firma)");
        typeGroup.add(rbExterny);
        typeGroup.add(rbInterny);
        body.add(rbExterny);
        body.add(rbInterny);
        body.add(Box.createVerticalStrut(20));

        // Fields for External
        JPanel extPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        extPanel.setBackground(Color.WHITE);
        extPanel.setBorder(BorderFactory.createTitledBorder("Údaje zákazníka"));
        JTextField nameFld = new JTextField();
        JTextField phoneFld = new JTextField();
        JTextField depositFld = new JTextField("50.00");
        extPanel.add(new JLabel("Meno zákazníka:"));
        extPanel.add(nameFld);
        extPanel.add(new JLabel("Telefón:"));
        extPanel.add(phoneFld);
        extPanel.add(new JLabel("Záloha (€):"));
        extPanel.add(depositFld);

        // Fields for Internal
        JPanel intPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        intPanel.setBackground(Color.WHITE);
        intPanel.setBorder(BorderFactory.createTitledBorder("Interný projekt"));
        JTextField projectFld = new JTextField();
        intPanel.add(new JLabel("Názov projektu:"));
        intPanel.add(projectFld);
        intPanel.setVisible(false);

        body.add(extPanel);
        body.add(intPanel);

        rbExterny.addActionListener(e -> {
            extPanel.setVisible(true);
            intPanel.setVisible(false);
        });
        rbInterny.addActionListener(e -> {
            extPanel.setVisible(false);
            intPanel.setVisible(true);
        });

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.setBackground(Color.WHITE);
        JButton cancel = new JButton("Zrušiť");
        JButton confirm = new JButton("Potvrdiť výdaj");
        confirm.setBackground(ORANGE);
        confirm.setForeground(Color.WHITE);

        confirm.addActionListener(e -> {
            String type = rbExterny.isSelected() ? "Externý" : "Interný";
            try {
                double zaloha = type.equals("Externý") ? Double.parseDouble(depositFld.getText()) : 0;
                if (confirmCallback != null) {
                    confirmCallback.onConfirm(n.getId(), type, nameFld.getText(), phoneFld.getText(), zaloha,
                            projectFld.getText());
                }
                dlg.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, "Neplatná suma zálohy.");
            }
        });

        cancel.addActionListener(e -> dlg.dispose());
        btns.add(cancel);
        btns.add(confirm);

        dlg.add(body, BorderLayout.CENTER);
        dlg.add(btns, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    public void zobrazSpravu(String s) {
        JOptionPane.showMessageDialog(this, s);
    }

    public void setListeners(Consumer<String> s, Consumer<Integer> v, BorrowConfirmCallback c) {
        this.searchListener = s;
        this.vypozicajListener = v;
        this.confirmCallback = c;
    }

    public void setUcSwitchCallback(Runnable[] callbacks) {
        this.ucSwitchCallback = callbacks;
    }

    private class VypozicajBtnRenderer extends JButton implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            setText(v.toString());
            setBackground(v.toString().equals("Vypožičať") ? ORANGE : Color.LIGHT_GRAY);
            setForeground(Color.WHITE);
            return this;
        }
    }
}
