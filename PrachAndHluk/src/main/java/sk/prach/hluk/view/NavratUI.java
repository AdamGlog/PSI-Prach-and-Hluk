package sk.prach.hluk.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import sk.prach.hluk.model.Vypozicka;

/**
 * NavratUI - hlavne okno pre UC03 Návrat náradia.
 * Stiylovo zladene s NaradieUI (rovnaka farebna paleta, header, footer).
 */
public class NavratUI extends JFrame {

    // === FARBY — rovnake ako v NaradieUI ===
    public static final Color ORANGE     = new Color(0xFA, 0x6D, 0x04);
    public static final Color LIGHT_GREY = new Color(0xBA, 0xBA, 0xBA);
    public static final Color DARK_GREY  = new Color(0x90, 0x90, 0x90);
    public static final Color LIGHT_BLUE = new Color(0xBD, 0xDC, 0xFF);
    public static final Color DARK_BLUE  = new Color(0x81, 0xBC, 0xFF);
    public static final Color TABLE_BG   = new Color(0xD9, 0xD9, 0xD9);
    public static final Color ROW_WHITE  = Color.WHITE;
    public static final Color ROW_GREY   = new Color(0xEE, 0xEE, 0xEE);
    public static final Color GREEN_OK   = new Color(0x4C, 0xAF, 0x50);
    public static final Color RED_WARN   = new Color(0xE5, 0x39, 0x35);

    // === Komponenty ===
    private JTable tabulkaVypoziciek;
    private DefaultTableModel tableModel;

    // Listenery — controller ich zaregistruje
    private Consumer<Integer> navratListener;           // vyvolanie navrat dialogu pre danu vypozicku
    private BiConsumer<Integer, String> potvrdenieNavratu; // id, novyStav

    // UC switch callback — main ho odovzda, pouzivame na prepinanie UC kariet
    private Runnable[] ucSwitchCallback;

    // Konstruktor — postavime hlavne okno
    public NavratUI() {
        setTitle("Prach & Hluk – Návrat náradia (UC03)");
        setSize(980, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.WHITE);
        buildUI();
    }

    // buildUI — poskladame layout: header, center, footer
    private void buildUI() {
        setLayout(new BorderLayout());
        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);
    }

    // buildHeader — logo + navigacia UC1-UC4 (UC3 je zvyraznene oranžovo)
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
        prihlaseny.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        topRow.add(logo, BorderLayout.CENTER);
        topRow.add(prihlaseny, BorderLayout.EAST);

        // Nav: UC1-UC4 — UC3 je aktivny
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
            btn.setBackground(labels[i].equals("UC3") ? ORANGE : Color.WHITE);
            btn.setForeground(labels[i].equals("UC3") ? Color.WHITE : Color.BLACK);
            btn.addActionListener(e -> {
                if (ucSwitchCallback != null) ucSwitchCallback[idx].run();
            });
            nav.add(btn);
        }

        outer.add(topRow, BorderLayout.NORTH);
        outer.add(nav, BorderLayout.SOUTH);
        return outer;
    }

    // buildCenter — zoznam aktivnych vypoziciek s tlacidlom "Prevziať"
    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(LIGHT_GREY);
        center.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(TABLE_BG);
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Titulok
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(TABLE_BG);
        titleBar.setBorder(BorderFactory.createEmptyBorder(0, 4, 8, 4));

        JLabel titleLbl = new JLabel("Aktívne výpožičky – Prevzatie vráteného náradia");
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleBar.add(titleLbl, BorderLayout.WEST);

        // Info badge
        JLabel badge = new JLabel("UC03");
        badge.setFont(new Font("SansSerif", Font.BOLD, 12));
        badge.setForeground(Color.WHITE);
        badge.setBackground(ORANGE);
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        titleBar.add(badge, BorderLayout.EAST);

        card.add(titleBar, BorderLayout.NORTH);

        // Tabulka: Náradie | ID | Zákazník | Dátum výpožičky | Plán. návrat | Záloha | Stav | Akcia
        String[] cols = {"", "Náradie", "ID", "Zákazník", "Vypožičané od", "Plán. návrat", "Záloha (€)", "Stav", ""};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tabulkaVypoziciek = new JTable(tableModel);
        tabulkaVypoziciek.setRowHeight(52);
        tabulkaVypoziciek.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabulkaVypoziciek.setShowGrid(false);
        tabulkaVypoziciek.setIntercellSpacing(new Dimension(0, 3));
        tabulkaVypoziciek.setBackground(TABLE_BG);
        tabulkaVypoziciek.setSelectionBackground(DARK_BLUE);
        tabulkaVypoziciek.setSelectionForeground(Color.BLACK);

        // Header styling
        JTableHeader hdr = tabulkaVypoziciek.getTableHeader();
        hdr.setBackground(TABLE_BG);
        hdr.setFont(new Font("SansSerif", Font.PLAIN, 12));
        hdr.setForeground(Color.DARK_GRAY);
        ((DefaultTableCellRenderer) hdr.getDefaultRenderer()).setHorizontalAlignment(SwingConstants.LEFT);

        // Stlpce — sirky
        tabulkaVypoziciek.getColumnModel().getColumn(0).setMaxWidth(52);  // ikonka
        tabulkaVypoziciek.getColumnModel().getColumn(0).setCellRenderer(new IkonkaRenderer());
        tabulkaVypoziciek.getColumnModel().getColumn(2).setMaxWidth(40);  // ID
        tabulkaVypoziciek.getColumnModel().getColumn(8).setMaxWidth(110); // akcia btn
        tabulkaVypoziciek.getColumnModel().getColumn(8).setCellRenderer(new NavratBtnRenderer());

        // Stav stlpec — farebny renderer
        tabulkaVypoziciek.getColumnModel().getColumn(7).setCellRenderer(new StavRenderer());

        // Alternujuce farby riadkov pre ostatne stlpce
        DefaultTableCellRenderer rowRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, sel, foc, r, c);
                if (!sel) comp.setBackground(r % 2 == 0 ? ROW_WHITE : ROW_GREY);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return comp;
            }
        };
        for (int i = 1; i <= 6; i++) {
            tabulkaVypoziciek.getColumnModel().getColumn(i).setCellRenderer(rowRenderer);
        }

        // Klik na "Prevziať" tlacidlo v poslednom stlpci
        tabulkaVypoziciek.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int col = tabulkaVypoziciek.columnAtPoint(e.getPoint());
                int row = tabulkaVypoziciek.rowAtPoint(e.getPoint());
                if (col == 8 && row >= 0) {
                    int id = Integer.parseInt(tableModel.getValueAt(row, 2).toString());
                    if (navratListener != null) navratListener.accept(id);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tabulkaVypoziciek);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(TABLE_BG);
        card.add(scroll, BorderLayout.CENTER);

        center.add(card, BorderLayout.CENTER);
        return center;
    }

    // buildFooter — rovnaka paticka ako v NaradieUI
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

        JPanel contact = new JPanel(new GridLayout(4, 1, 0, 2));
        contact.setBackground(DARK_GREY);
        for (String s : new String[]{"📞 +421 900 123 456", "✉ info@prachahluk.sk", "📍 Trnava, Slovensko", ""}) {
            JLabel l = new JLabel(s);
            l.setForeground(Color.WHITE);
            l.setFont(new Font("SansSerif", Font.PLAIN, 11));
            contact.add(l);
        }

        JLabel copy = new JLabel("© 2025 Prach & Hluk s.r.o.", SwingConstants.RIGHT);
        copy.setForeground(Color.LIGHT_GRAY);
        copy.setFont(new Font("SansSerif", Font.PLAIN, 10));

        footer.add(links, BorderLayout.WEST);
        footer.add(contact, BorderLayout.CENTER);
        footer.add(copy, BorderLayout.SOUTH);
        return footer;
    }

    // ===== VEREJNE METODY =====

    // zobrazVypozicky — naplnime tabulku zoznamom aktivnych vypoziciek
    public void zobrazVypozicky(List<Vypozicka> zoznam) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        tableModel.setRowCount(0);
        for (Vypozicka v : zoznam) {
            String meska = v.jeMeskanie() ? " ⚠" : "";
            tableModel.addRow(new Object[]{
                v.getIkonkaNaradia(),
                v.getNaradieNazov(),
                v.getNaradieId(),
                v.getZakaznikMeno(),
                v.getDatumVypozicania().format(fmt) + "\n(" + v.getDlzkaPozicaniaDni() + " dní)",
                v.getDatumPlanovanehNavratu().format(fmt) + meska,
                String.format("%.2f", v.getZaloha()),
                v.getStavNaradia(),
                "Prevziať"
            });
        }
    }

    // openNavratModal — otvorime modal pre potvrdenie navratenia konkretnej vypozicky
    public void openNavratModal(Vypozicka vypozicka) {
        JDialog dlg = new JDialog(this, "Prevzatie náradia – UC03", true);
        dlg.setSize(480, 560);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
        dlg.getContentPane().setBackground(Color.WHITE);

        // --- Hlavicka modalneho okna ---
        JPanel modalHeader = new JPanel(new BorderLayout());
        modalHeader.setBackground(ORANGE);
        modalHeader.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        JLabel modalTitle = new JLabel("Prevzatie vráteného náradia");
        modalTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        modalTitle.setForeground(Color.WHITE);
        JLabel modalUC = new JLabel("UC03");
        modalUC.setFont(new Font("SansSerif", Font.BOLD, 14));
        modalUC.setForeground(Color.WHITE);
        modalHeader.add(modalTitle, BorderLayout.WEST);
        modalHeader.add(modalUC, BorderLayout.EAST);
        dlg.add(modalHeader, BorderLayout.NORTH);

        // --- Telo modalneho okna ---
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(14, 20, 8, 20));

        // Ikonka + nazov naradia
        JPanel ikonkaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        ikonkaPanel.setBackground(Color.WHITE);
        JLabel ikonkaLbl = new JLabel(vypozicka.getIkonkaNaradia());
        ikonkaLbl.setFont(new Font("SansSerif", Font.PLAIN, 42));
        JPanel nazovPanel = new JPanel(new GridLayout(2, 1));
        nazovPanel.setBackground(Color.WHITE);
        JLabel nazovLbl = new JLabel(vypozicka.getNaradieNazov());
        nazovLbl.setFont(new Font("SansSerif", Font.BOLD, 17));
        JLabel idLbl = new JLabel("ID výpožičky: " + vypozicka.getVypozickaId()
                + "  |  ID náradia: " + vypozicka.getNaradieId());
        idLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        idLbl.setForeground(DARK_GREY);
        nazovPanel.add(nazovLbl);
        nazovPanel.add(idLbl);
        ikonkaPanel.add(ikonkaLbl);
        ikonkaPanel.add(nazovPanel);
        body.add(ikonkaPanel);
        body.add(Box.createVerticalStrut(12));

        // Separator
        JSeparator sep1 = new JSeparator();
        sep1.setForeground(LIGHT_GREY);
        body.add(sep1);
        body.add(Box.createVerticalStrut(10));

        // Udaje o zakaznikovi a vypozicke — format karty
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        JPanel infoGrid = new JPanel(new GridLayout(0, 2, 6, 6));
        infoGrid.setBackground(new Color(0xF7, 0xF7, 0xF7));
        infoGrid.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(LIGHT_GREY),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));

        addInfoRow(infoGrid, "👤 Zákazník:",       vypozicka.getZakaznikMeno());
        addInfoRow(infoGrid, "📞 Telefón:",         vypozicka.getZakaznikTelefon());
        addInfoRow(infoGrid, "📅 Vypožičané:",      vypozicka.getDatumVypozicania().format(fmt));
        addInfoRow(infoGrid, "📅 Plán. návrat:",    vypozicka.getDatumPlanovanehNavratu().format(fmt));
        addInfoRow(infoGrid, "⏱ Dni požičania:",    vypozicka.getDlzkaPozicaniaDni() + " dní");

        // Meškanie — upozornenie
        if (vypozicka.jeMeskanie()) {
            JLabel meskaLabel = new JLabel("⚠ Meškanie s vrátením!");
            meskaLabel.setForeground(RED_WARN);
            meskaLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            infoGrid.add(new JLabel(""));
            infoGrid.add(meskaLabel);
        }

        body.add(infoGrid);
        body.add(Box.createVerticalStrut(12));

        // Separator 2
        JSeparator sep2 = new JSeparator();
        sep2.setForeground(LIGHT_GREY);
        body.add(sep2);
        body.add(Box.createVerticalStrut(10));

        // Stav naradia — zamestnanec vyberie po kontrole
        JLabel stavTitle = new JLabel("Stav vráteného náradia (vyplní zamestnanec):");
        stavTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        body.add(stavTitle);
        body.add(Box.createVerticalStrut(6));

        ButtonGroup stavGroup = new ButtonGroup();
        JRadioButton rbDostupne  = makeRadio("✅  Dostupné – bez poškodenia",  stavGroup, true);
        JRadioButton rbServis    = makeRadio("🔧  V servise – potrebuje opravu", stavGroup, false);
        JRadioButton rbPoskodene = makeRadio("⚠  Poškodené – zákazník zodpovedá", stavGroup, false);

        for (JRadioButton rb : new JRadioButton[]{rbDostupne, rbServis, rbPoskodene}) {
            body.add(rb);
            body.add(Box.createVerticalStrut(3));
        }

        body.add(Box.createVerticalStrut(10));

        // Zaloha panel — dynamicky sa prepocitava podla stavu
        JPanel zalohaPanel = new JPanel(new GridLayout(3, 2, 6, 4));
        zalohaPanel.setBackground(new Color(0xF0, 0xF8, 0xFF));
        zalohaPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DARK_BLUE),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel zalohaTitle = new JLabel("💰 Záloha:");
        zalohaTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        JLabel zalohaHodnota = new JLabel(String.format("%.2f €", vypozicka.getZaloha()));
        zalohaHodnota.setFont(new Font("SansSerif", Font.PLAIN, 13));

        JLabel vracaneTitle = new JLabel("↩ Vrátená zákazníkovi:");
        vracaneTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        JLabel vracaneHodnota = new JLabel(String.format("%.2f €", vypozicka.getZaloha()));
        vracaneHodnota.setFont(new Font("SansSerif", Font.BOLD, 14));
        vracaneHodnota.setForeground(GREEN_OK);

        JLabel zadrzanaTitle = new JLabel("✂ Zadržaná suma:");
        zadrzanaTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        zadrzanaTitle.setForeground(DARK_GREY);
        JLabel zadrzanaHodnota = new JLabel("0.00 €");
        zadrzanaHodnota.setFont(new Font("SansSerif", Font.PLAIN, 12));
        zadrzanaHodnota.setForeground(DARK_GREY);

        zalohaPanel.add(zalohaTitle);   zalohaPanel.add(zalohaHodnota);
        zalohaPanel.add(vracaneTitle);  zalohaPanel.add(vracaneHodnota);
        zalohaPanel.add(zadrzanaTitle); zalohaPanel.add(zadrzanaHodnota);
        body.add(zalohaPanel);

        // Dynamicky prepocet zalohy pri zmene stavu
        ActionListener zalohaUpdater = e -> {
            boolean poskodene = rbPoskodene.isSelected();
            double vratiť = vypozicka.vypocitajVratenuZalohu(poskodene);
            double zadrzat = vypozicka.getZaloha() - vratiť;
            vracaneHodnota.setText(String.format("%.2f €", vratiť));
            zadrzanaHodnota.setText(String.format("%.2f €", zadrzat));
            vracaneHodnota.setForeground(poskodene ? RED_WARN : GREEN_OK);
        };
        rbDostupne.addActionListener(zalohaUpdater);
        rbServis.addActionListener(zalohaUpdater);
        rbPoskodene.addActionListener(zalohaUpdater);

        JScrollPane bodyScroll = new JScrollPane(body);
        bodyScroll.setBorder(BorderFactory.createEmptyBorder());
        dlg.add(bodyScroll, BorderLayout.CENTER);

        // --- Paticka modalneho okna: tlacidla ---
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnRow.setBackground(Color.WHITE);
        btnRow.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, LIGHT_GREY));

        JButton cancelBtn = new JButton("Zrušiť");
        cancelBtn.setBackground(LIGHT_GREY);
        cancelBtn.setForeground(Color.BLACK);
        cancelBtn.setFocusPainted(false);
        cancelBtn.addActionListener(e -> dlg.dispose());

        JButton potvrditBtn = new JButton("✔  Potvrdiť prevzatie");
        potvrditBtn.setBackground(ORANGE);
        potvrditBtn.setForeground(Color.WHITE);
        potvrditBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        potvrditBtn.setFocusPainted(false);
        potvrditBtn.addActionListener(e -> {
            String novyStav = rbServis.isSelected()    ? "V servise"
                            : rbPoskodene.isSelected() ? "Poškodené – servis"
                            : "Dostupné";
            if (potvrdenieNavratu != null)
                potvrdenieNavratu.accept(vypozicka.getNaradieId(), novyStav);
            dlg.dispose();
        });

        btnRow.add(cancelBtn);
        btnRow.add(potvrditBtn);
        dlg.add(btnRow, BorderLayout.SOUTH);

        dlg.setVisible(true);
    }

    // zobrazSpravu — zobrazi upozornenie alebo potvrdenie
    public void zobrazSpravu(String sprava) {
        JOptionPane.showMessageDialog(this, sprava, "Informácia", JOptionPane.INFORMATION_MESSAGE);
    }

    // zobrazUspech — zobrazi zelene potvrdenie uspesneho navratu
    public void zobrazUspech(String naradieNazov, String zakaznik) {
        JOptionPane.showMessageDialog(this,
            "✅  Náradie „" + naradieNazov + "bolo úspešne prevzaté od zákazníka" + zakaznik + ".\n"
            + "Záloha bola vyúčtovaná. Stav náradia bol aktualizovaný.",
            "Prevzatie úspešné", JOptionPane.INFORMATION_MESSAGE);
    }

    // setNavratListener — controller zaregistruje listener pre otvorenie navrat dialogu
    public void setNavratListener(Consumer<Integer> l) { this.navratListener = l; }

    // setPotvrdenieNavratuListener — controller zaregistruje listener pre potvrdenie navratu
    public void setPotvrdenieNavratuListener(BiConsumer<Integer, String> l) { this.potvrdenieNavratu = l; }

    // ===== POMOCNE METODY =====

    // addInfoRow — pridame riadok s labelom a hodnotou do info panelu
    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        JLabel val = new JLabel(value);
        val.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(lbl);
        panel.add(val);
    }

    // makeRadio — pomocna metoda pre RadioButton
    private JRadioButton makeRadio(String text, ButtonGroup group, boolean selected) {
        JRadioButton rb = new JRadioButton(text, selected);
        rb.setBackground(Color.WHITE);
        rb.setFont(new Font("SansSerif", Font.PLAIN, 13));
        group.add(rb);
        return rb;
    }

    // ===== RENDERERY =====

    // IkonkaRenderer — renderer pre emoji ikonku naradia (prvy stlpec)
    private class IkonkaRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int r, int c) {
            JLabel lbl = new JLabel(v != null ? v.toString() : "🔧");
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 26));
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setOpaque(true);
            lbl.setBackground(sel ? DARK_BLUE : (r % 2 == 0 ? ROW_WHITE : ROW_GREY));
            return lbl;
        }
    }

    // NavratBtnRenderer — renderer pre "Prevziať" tlacidlo v poslednom stlpci
    private class NavratBtnRenderer extends JButton implements TableCellRenderer {
        NavratBtnRenderer() {
            setOpaque(true);
            setFont(new Font("SansSerif", Font.BOLD, 12));
            setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        }
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int r, int c) {
            setText("↩ Prevziať");
            setBackground(sel ? DARK_BLUE : ORANGE);
            setForeground(Color.WHITE);
            return this;
        }
    }

    // StavRenderer — renderer pre stav naradia s farebnymi badges
    private class StavRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object v,
                boolean sel, boolean foc, int r, int c) {
            String stav = v != null ? v.toString() : "";
            JLabel lbl = new JLabel(" " + stav + " ");
            lbl.setOpaque(true);
            lbl.setFont(new Font("SansSerif", Font.BOLD, 11));
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            switch (stav) {
                case "Vypožičané" -> { lbl.setBackground(LIGHT_BLUE); lbl.setForeground(new Color(0x00, 0x50, 0xA0)); }
                case "V servise"  -> { lbl.setBackground(new Color(0xFF, 0xF0, 0xCC)); lbl.setForeground(new Color(0x8B, 0x60, 0x00)); }
                default           -> { lbl.setBackground(sel ? DARK_BLUE : (r % 2 == 0 ? ROW_WHITE : ROW_GREY)); lbl.setForeground(Color.DARK_GRAY); }
            }
            return lbl;
        }
    }

    // setUcSwitchCallback - main odovzda pole Runnable pre prepinanie UC kariet
    public void setUcSwitchCallback(Runnable[] callbacks) { this.ucSwitchCallback = callbacks; }
}
