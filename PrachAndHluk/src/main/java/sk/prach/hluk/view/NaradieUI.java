package sk.prach.hluk.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.function.BiConsumer;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import sk.prach.hluk.model.Naradie;

public class NaradieUI extends JFrame {

    // === FARBY podla Figma ===
    public static final Color ORANGE       = new Color(0xFA, 0x6D, 0x04);
    public static final Color LIGHT_GREY   = new Color(0xBA, 0xBA, 0xBA);
    public static final Color DARK_GREY    = new Color(0x90, 0x90, 0x90);
    public static final Color LIGHT_BLUE   = new Color(0xBD, 0xDC, 0xFF);
    public static final Color DARK_BLUE    = new Color(0x81, 0xBC, 0xFF);
    public static final Color TABLE_BG     = new Color(0xD9, 0xD9, 0xD9);
    public static final Color ROW_WHITE    = Color.WHITE;
    public static final Color ROW_GREY     = new Color(0xEE, 0xEE, 0xEE);

    // === Komponenty ===
    private JTable tabulkaNaradia;
    private DefaultTableModel tableModel;

    // Listenery - controller ich zaregistruje
    private BiConsumer<Character, Boolean> sortListener;
    private BiConsumer<Integer, String> editStavListener;
    private Runnable[] ucSwitchCallback;

    // Konstruktor - postavime hlavne okno
    public NaradieUI() {
        setTitle("Prach & Hluk");
        setSize(920, 660);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        buildUI();
    }

    // buildUI - poskladame layout podla Figma: header, center, footer
    private void buildUI() {
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    // buildHeader - logo + navigacia UC1-UC4
    private JPanel buildHeader() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Color.WHITE);
        outer.setBorder(BorderFactory.createEmptyBorder(12, 20, 0, 20));

        // Top row: logo vlavo, prihlaseny vpravo
        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(Color.WHITE);

        JLabel logo = new JLabel("⚙  Prach & Hluk");
        logo.setFont(new Font("Serif", Font.BOLD, 28));
        logo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel prihlaseny = new JLabel("👤 Prihlásený");
        prihlaseny.setFont(new Font("SansSerif", Font.PLAIN, 12));
        prihlaseny.setForeground(DARK_GREY);
        prihlaseny.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        topRow.add(logo, BorderLayout.CENTER);
        topRow.add(prihlaseny, BorderLayout.EAST);

        // Nav: UC1-UC4
        JPanel nav = new JPanel(new GridLayout(1, 4));
        nav.setBackground(Color.WHITE);
        nav.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        String[] labels = {"UC1", "UC2", "UC3", "UC4"};
        for (int i = 0; i < labels.length; i++) {
            final int idx = i;
            JButton btn = new JButton(labels[i]);
            btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(DARK_GREY));
            btn.setBackground(labels[i].equals("UC4") ? ORANGE : Color.WHITE);
            btn.setForeground(labels[i].equals("UC4") ? Color.WHITE : Color.BLACK);
            btn.addActionListener(e -> {
                if (ucSwitchCallback != null) ucSwitchCallback[idx].run();
            });
            nav.add(btn);
        }

        outer.add(topRow, BorderLayout.NORTH);
        outer.add(nav, BorderLayout.SOUTH);
        return outer;
    }

    // buildCenter - sekcia so zoznamom naradia a tabulkou
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(LIGHT_GREY);
        center.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(TABLE_BG);
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Titulok + sort/filter button
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(TABLE_BG);
        titleBar.setBorder(BorderFactory.createEmptyBorder(0, 4, 8, 4));

        JLabel titleLbl = new JLabel("Zoznam náradia");
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleBar.add(titleLbl, BorderLayout.WEST);

        JButton sfBtn = makeSfButton();
        sfBtn.addActionListener(e -> openSortFilterModal());
        titleBar.add(sfBtn, BorderLayout.EAST);

        card.add(titleBar, BorderLayout.NORTH);

        // Tabulka
        String[] cols = {"Názov náradia", "ID", "Vypožičané (v dňoch)", "Servisované", "Stav", ""};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabulkaNaradia = new JTable(tableModel);
        tabulkaNaradia.setRowHeight(42);
        tabulkaNaradia.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabulkaNaradia.setShowGrid(false);
        tabulkaNaradia.setIntercellSpacing(new Dimension(0, 3));
        tabulkaNaradia.setBackground(TABLE_BG);
        tabulkaNaradia.setSelectionBackground(DARK_BLUE);
        tabulkaNaradia.setSelectionForeground(Color.BLACK);

        // Header
        JTableHeader hdr = tabulkaNaradia.getTableHeader();
        hdr.setBackground(TABLE_BG);
        hdr.setFont(new Font("SansSerif", Font.PLAIN, 12));
        hdr.setForeground(Color.DARK_GRAY);
        ((DefaultTableCellRenderer) hdr.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        // Edit button stlpec - maly
        tabulkaNaradia.getColumnModel().getColumn(5).setMaxWidth(48);
        tabulkaNaradia.getColumnModel().getColumn(5).setCellRenderer(new EditBtnRenderer());

        // Klik na edit tlacidlo
        tabulkaNaradia.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int col = tabulkaNaradia.columnAtPoint(e.getPoint());
                int row = tabulkaNaradia.rowAtPoint(e.getPoint());
                if (col == 5 && row >= 0) {
                    int id  = Integer.parseInt(tableModel.getValueAt(row, 1).toString());
                    String stav = tableModel.getValueAt(row, 4).toString();
                    openEditModal(id, stav);
                }
            }
        });

        // Alternujuce farby riadkov
        tabulkaNaradia.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) comp.setBackground(r % 2 == 0 ? ROW_WHITE : ROW_GREY);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return comp;
            }
        });

        JScrollPane scroll = new JScrollPane(tabulkaNaradia);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(TABLE_BG);
        card.add(scroll, BorderLayout.CENTER);

        center.add(card, BorderLayout.CENTER);
        return center;
    }

    // buildFooter - paticka s odkazmi a kontaktnymi udajmi
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(DARK_GREY);
        footer.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JPanel links = new JPanel(new GridLayout(4, 1, 0, 2));
        links.setBackground(DARK_GREY);
        for (String s : new String[]{"O Nás", "Info o Rezerváciách", "Ochrana osobných údajov", "Obchodné podmienky"}) {
            JLabel l = new JLabel("<html><u>" + s + "</u></html>");
            l.setForeground(Color.WHITE);
            l.setFont(new Font("SansSerif", Font.PLAIN, 11));
            links.add(l);
        }

        JPanel info = new JPanel(new GridLayout(3, 1, 0, 2));
        info.setBackground(DARK_GREY);
        for (String s : new String[]{
                "Prach & Hluk, Kukučínova 2155/5, Snina 069 01",
                "Kontakt: +421 944 123 456, prach@hluk.com",
                "© 2026 Prach & Hluk. Všetky práva vyhradené."}) {
            JLabel l = new JLabel(s, SwingConstants.RIGHT);
            l.setForeground(Color.WHITE);
            l.setFont(new Font("SansSerif", Font.PLAIN, 11));
            info.add(l);
        }

        footer.add(links, BorderLayout.WEST);
        footer.add(info, BorderLayout.EAST);
        return footer;
    }

    // makeSfButton - helper pre sort/filter ikonu v rohu tabulky
    private JButton makeSfButton() {
        JButton btn = new JButton("≡");
        btn.setFont(new Font("SansSerif", Font.BOLD, 18));
        btn.setBackground(LIGHT_GREY);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(DARK_GREY));
        btn.setPreferredSize(new Dimension(40, 36));
        return btn;
    }

    // openSortFilterModal - modal okno pre zoradenie a filtrovanie zoznamu
    public void openSortFilterModal() {
        JDialog dlg = new JDialog(this, "Zoradiť & Filtrovať", true);
        dlg.setSize(330, 280);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(14, 16, 8, 16));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0; g.gridy = 0; g.gridwidth = 3;
        body.add(new JLabel("Zoradiť & Filtrovať"), g);
        g.gridwidth = 1;

        // Vypožičané
        g.gridx = 0; g.gridy = 1; body.add(new JLabel("Vypožičané"), g);
        JButton vAsc = makeSortArrow("↑");
        JButton vDesc = makeSortArrow("↓");
        vDesc.setBackground(ORANGE); vDesc.setForeground(Color.WHITE);
        g.gridx = 1; body.add(vAsc, g);
        g.gridx = 2; body.add(vDesc, g);
        togglePair(vAsc, vDesc);

        // Servisované
        g.gridx = 0; g.gridy = 2; body.add(new JLabel("Servisované"), g);
        JButton sAsc = makeSortArrow("↑");
        JButton sDesc = makeSortArrow("↓");
        g.gridx = 1; body.add(sAsc, g);
        g.gridx = 2; body.add(sDesc, g);
        togglePair(sAsc, sDesc);

        // Checkboxy filtrov
        g.gridx = 0; g.gridy = 3; body.add(new JLabel("Stav:"), g);
        JCheckBox cbD = new JCheckBox("Dostupné", true);
        JCheckBox cbV = new JCheckBox("Vypožičané");
        JCheckBox cbS = new JCheckBox("V Servise");
        JCheckBox cbN = new JCheckBox("Servis neskôr");
        JPanel cbPanel = new JPanel(new GridLayout(2, 2, 4, 2));
        cbPanel.setBackground(Color.WHITE);
        for (JCheckBox cb : new JCheckBox[]{cbD, cbV, cbS, cbN}) {
            cb.setBackground(Color.WHITE);
            cb.setFont(new Font("SansSerif", Font.PLAIN, 12));
            cbPanel.add(cb);
        }
        g.gridx = 1; g.gridwidth = 2; body.add(cbPanel, g);

        dlg.add(body, BorderLayout.CENTER);

        JButton applyBtn = new JButton("Aplikovať");
        applyBtn.setBackground(ORANGE);
        applyBtn.setForeground(Color.WHITE);
        applyBtn.addActionListener(e -> {
            // Zistíme, ktorý sort bol označený
            char kriterium = 'v'; // default
            boolean vzostupne = false;

            if (vAsc.getBackground().equals(ORANGE) || vDesc.getBackground().equals(ORANGE)) {
                kriterium = 'v';
                vzostupne = vAsc.getBackground().equals(ORANGE);
            } else if (sAsc.getBackground().equals(ORANGE) || sDesc.getBackground().equals(ORANGE)) {
                kriterium = 's';
                vzostupne = sAsc.getBackground().equals(ORANGE);
            }

            if (sortListener != null) {
                sortListener.accept(kriterium, vzostupne);
            }
            dlg.dispose();
        });

        JPanel btnRow = new JPanel(); btnRow.setBackground(Color.WHITE);
        btnRow.add(applyBtn);
        dlg.add(btnRow, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // openEditModal - modal okno pre zmenu stavu konkretneho kusu naradia
        public void openEditModal(int naradieId, String aktualnyStav) {
        JDialog dlg = new JDialog(this, "Upraviť náradie", true);
        dlg.setSize(280, 220);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(14, 16, 8, 16));
        GridBagConstraints g = new GridBagConstraints();
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(6, 4, 6, 4);

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        body.add(new JLabel("Upraviť náradie ID: " + naradieId), g);

        g.gridy = 1; g.gridwidth = 1;
        body.add(new JLabel("Nový stav:"), g);

        ButtonGroup group = new ButtonGroup();

        JRadioButton rbServis     = new JRadioButton("V servise");
        JRadioButton rbVyradene   = new JRadioButton("Vyradené");
        JRadioButton rbServisNeskor = new JRadioButton("Servis Neskôr");

        // Predvyplnenie
        if (aktualnyStav.equals("V servise")) rbServis.setSelected(true);
        else if (aktualnyStav.equals("Vyradené")) rbVyradene.setSelected(true);
        else if (aktualnyStav.equals("Servis Neskôr")) rbServisNeskor.setSelected(true);

        group.add(rbServis);
        group.add(rbVyradene);
        group.add(rbServisNeskor);

        JPanel radioPanel = new JPanel(new GridLayout(3, 1, 0, 6));
        radioPanel.setBackground(Color.WHITE);
        radioPanel.add(rbServis);
        radioPanel.add(rbVyradene);
        radioPanel.add(rbServisNeskor);

        g.gridx = 1; g.gridy = 1;
        body.add(radioPanel, g);

        dlg.add(body, BorderLayout.CENTER);

        JButton ok = new JButton("Potvrdiť");
        ok.setBackground(ORANGE);
        ok.setForeground(Color.WHITE);
        ok.setFocusPainted(false);
        ok.addActionListener(e -> {
            String novyStav = null;
            if (rbServis.isSelected()) novyStav = "V servise";
            else if (rbVyradene.isSelected()) novyStav = "Vyradené";
            else if (rbServisNeskor.isSelected()) novyStav = "Servis Neskôr";

            if (novyStav != null && editStavListener != null) {
                editStavListener.accept(naradieId, novyStav);
            }
            dlg.dispose();
            // vynutenie repaint tabulky, kedze sa to glitchovalo
            SwingUtilities.invokeLater(() -> {
                tabulkaNaradia.revalidate();
                tabulkaNaradia.repaint();
            });
        });

        JPanel btnRow = new JPanel(); btnRow.setBackground(Color.WHITE);
        btnRow.add(ok);
        dlg.add(btnRow, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // makeSortArrow - pomocna metoda pre male sort tlacidla so sipkami
    private JButton makeSortArrow(String text) {
        JButton b = new JButton(text);
        b.setPreferredSize(new Dimension(36, 28));
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setBackground(LIGHT_GREY); b.setForeground(Color.BLACK);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createLineBorder(DARK_GREY));
        return b;
    }

    // togglePair - prepneme highlight medzi asc/desc parom
    private void togglePair(JButton asc, JButton desc) {
        asc.addActionListener(e -> {
            asc.setBackground(ORANGE); asc.setForeground(Color.WHITE);
            desc.setBackground(LIGHT_GREY); desc.setForeground(Color.BLACK);
        });
        desc.addActionListener(e -> {
            desc.setBackground(ORANGE); desc.setForeground(Color.WHITE);
            asc.setBackground(LIGHT_GREY); asc.setForeground(Color.BLACK);
        });
    }

    // zobrazZoznamNaradia - naplnime tabulku zoznamom naradia
    public void zobrazZoznamNaradia(List<Naradie> zoznam) {
        tableModel.setRowCount(0);
        for (Naradie nar : zoznam)
            tableModel.addRow(new Object[]{nar.getNazov(), nar.getId(),
                nar.getVypozicaneCount(), nar.getServisovaneCount(), nar.getStav(), "✎"});
    }

    // zobrazZoradenyZoznam - naplnime tabulku zoradenim zoznamom
    public void zobrazZoradenyZoznam(List<Naradie> zoznam) { zobrazZoznamNaradia(zoznam); }

    // filterZoznamByStav - zobrazime prefiltrovany zoznam
    public void filterZoznamByStav(List<Naradie> f) { zobrazZoznamNaradia(f); }

    // zavriZoznam - zatvorime okno
    public void zavriZoznam() { dispose(); }

    // zobrazSpravu - zobrazime upozornenie
    public void zobrazSpravu(String s) {
        JOptionPane.showMessageDialog(this, s, "Upozornenie", JOptionPane.WARNING_MESSAGE);
    }

    // editStav - vstupny bod z UI, logiku handleuje controller
    public void editStav(int id, String stav) {}

    // pytajSaServisNeskor - dialog pre potvrdenie Servis Neskor
    public boolean pytajSaServisNeskor() {
        return JOptionPane.showConfirmDialog(this,
            "Chcete pridať príznak 'Servis Neskôr'?", "Servis Neskôr",
            JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
    }

    // setSortListener - controller zaregistruje listener pre sort
    public void setSortListener(BiConsumer<Character, Boolean> l) { this.sortListener = l; }

    // setEditStavListener - controller zaregistruje listener pre edit stavu
    public void setEditStavListener(BiConsumer<Integer, String> l) { this.editStavListener = l; }

    // getVybraneNaradieId - vratime ID vybratého riadku
    public int getVybraneNaradieId() {
        int r = tabulkaNaradia.getSelectedRow();
        return r == -1 ? -1 : Integer.parseInt(tableModel.getValueAt(r, 1).toString());
    }

    // EditBtnRenderer - renderer pre edit ikonku v poslednom stlpci
    private class EditBtnRenderer extends JButton implements TableCellRenderer {
        EditBtnRenderer() {
            setOpaque(true);
            setFont(new Font("SansSerif", Font.PLAIN, 15));
            setBorder(BorderFactory.createLineBorder(DARK_BLUE));
        }
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int r, int c) {
            setText("✎");
            setBackground(sel ? DARK_BLUE : LIGHT_BLUE);
            setForeground(Color.DARK_GRAY);
            return this;
        }
    }

    // setUcSwitchCallback - main odovzda pole Runnable pre prepinanie UC kariet
    public void setUcSwitchCallback(Runnable[] callbacks) { this.ucSwitchCallback = callbacks; }
}