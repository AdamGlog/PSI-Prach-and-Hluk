package sk.prach.hluk.prostredie;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import sk.prach.hluk.modely.Naradie;

public class KatalogUI extends JFrame {

    // === FARBY podla Figma ===
    public static final Color ORANGE       = new Color(0xFA, 0x6D, 0x04);
    public static final Color LIGHT_GREY   = new Color(0xBA, 0xBA, 0xBA);
    public static final Color DARK_GREY    = new Color(0x90, 0x90, 0x90);
    public static final Color TABLE_BG     = new Color(0xD9, 0xD9, 0xD9);

    private JPanel katalogPanel;
    private Runnable[] ucSwitchCallback;
    private java.awt.CardLayout centerCardLayout;
    private JPanel centerCardPanel;

    public KatalogUI() {
        setTitle("Prach & Hluk");
        setSize(920, 660);
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
        prihlaseny.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        topRow.add(logo, BorderLayout.CENTER);
        topRow.add(prihlaseny, BorderLayout.EAST);

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
            btn.setBackground(labels[i].equals("UC1") ? ORANGE : Color.WHITE);
            btn.setForeground(labels[i].equals("UC1") ? Color.WHITE : Color.BLACK);
            btn.addActionListener(e -> {
                if (ucSwitchCallback != null) ucSwitchCallback[idx].run();
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

        // Tab Row
        JPanel tabRow = new JPanel(new GridLayout(1, 2, 0, 0));
        tabRow.setBackground(LIGHT_GREY);
        tabRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JButton btnNaradie = new JButton("NÁRADIE");
        JButton btnSluzby = new JButton("SLUŽBY");
        
        btnNaradie.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnSluzby.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnNaradie.setFocusPainted(false);
        btnSluzby.setFocusPainted(false);
        btnNaradie.setBackground(ORANGE);
        btnNaradie.setForeground(Color.WHITE);
        btnSluzby.setBackground(Color.WHITE);
        btnSluzby.setForeground(Color.BLACK);
        
        centerCardLayout = new java.awt.CardLayout();
        centerCardPanel = new JPanel(centerCardLayout);
        centerCardPanel.setBackground(TABLE_BG);
        
        btnNaradie.addActionListener(e -> {
            btnNaradie.setBackground(ORANGE);
            btnNaradie.setForeground(Color.WHITE);
            btnSluzby.setBackground(Color.WHITE);
            btnSluzby.setForeground(Color.BLACK);
            centerCardLayout.show(centerCardPanel, "NARADIE");
        });
        
        btnSluzby.addActionListener(e -> {
            btnSluzby.setBackground(ORANGE);
            btnSluzby.setForeground(Color.WHITE);
            btnNaradie.setBackground(Color.WHITE);
            btnNaradie.setForeground(Color.BLACK);
            centerCardLayout.show(centerCardPanel, "SLUZBY");
        });
        
        tabRow.add(btnNaradie);
        tabRow.add(btnSluzby);

        // Panel NARADIE
        JPanel panelNaradie = buildNaradiePanel();
        
        // Panel SLUZBY
        JPanel panelSluzby = buildSluzbyPanel();
        
        centerCardPanel.add(panelNaradie, "NARADIE");
        centerCardPanel.add(panelSluzby, "SLUZBY");
        
        center.add(tabRow, BorderLayout.NORTH);
        center.add(centerCardPanel, BorderLayout.CENTER);
        return center;
    }

    private JPanel buildNaradiePanel() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(TABLE_BG);
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(TABLE_BG);
        titleBar.setBorder(BorderFactory.createEmptyBorder(0, 4, 8, 4));

        JLabel titleLbl = new JLabel("Katalóg náradia");
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleBar.add(titleLbl, BorderLayout.WEST);

        card.add(titleBar, BorderLayout.NORTH);

        katalogPanel = new JPanel();
        katalogPanel.setLayout(new BoxLayout(katalogPanel, BoxLayout.Y_AXIS));
        katalogPanel.setBackground(TABLE_BG);

        JScrollPane scroll = new JScrollPane(katalogPanel);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(TABLE_BG);
        // Zvýšenie rýchlosti scrollovania
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildSluzbyPanel() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(TABLE_BG);
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(TABLE_BG);
        titleBar.setBorder(BorderFactory.createEmptyBorder(0, 4, 8, 4));

        JLabel titleLbl = new JLabel("Cenník: Jadrové vŕtanie");
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleBar.add(titleLbl, BorderLayout.WEST);
        
        JButton btnAvail = new JButton("Overenie dostupnosti");
        btnAvail.setBackground(ORANGE);
        btnAvail.setForeground(Color.WHITE);
        btnAvail.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnAvail.setFocusPainted(false);
        btnAvail.addActionListener(e -> openSluzbyAvailabilityModal());
        titleBar.add(btnAvail, BorderLayout.EAST);

        card.add(titleBar, BorderLayout.NORTH);

        // Price list table
        String[] cols = {"Priemer vrtáka (mm)", "Cena za cm s DPH", "Typ muriva"};
        Object[][] data = {
            {"50 mm", "0.80 €", "Tehla / Porobetón"},
            {"50 mm", "1.20 €", "Železobetón"},
            {"80 mm", "1.00 €", "Tehla / Porobetón"},
            {"80 mm", "1.50 €", "Železobetón"},
            {"100 mm", "1.30 €", "Tehla / Porobetón"},
            {"100 mm", "1.80 €", "Železobetón"},
            {"150 mm", "1.80 €", "Tehla / Porobetón"},
            {"150 mm", "2.50 €", "Železobetón"},
            {"200 mm", "2.50 €", "Tehla / Porobetón"},
            {"200 mm", "3.20 €", "Železobetón"}
        };
        
        javax.swing.JTable table = new javax.swing.JTable(data, cols);
        table.setRowHeight(35);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setBackground(LIGHT_GREY);
        table.setShowGrid(true);
        table.setGridColor(new Color(0xDD, 0xDD, 0xDD));
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0xDD, 0xDD, 0xDD)));
        
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    private void openSluzbyAvailabilityModal() {
        javax.swing.JDialog dlg = new javax.swing.JDialog(this, "Dostupnosť - Jadrové vŕtanie", true);
        dlg.setSize(420, 220);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new java.awt.Insets(5, 5, 5, 5);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        JLabel title = new JLabel("Dostupnosť (najbližších 7 dní):");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        body.add(title, g);

        g.gridy = 1;
        JPanel calPanel = new JPanel(new GridLayout(1, 7, 4, 0));
        calPanel.setBackground(Color.WHITE);
        
        java.time.LocalDate dnes = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter formatDna = java.time.format.DateTimeFormatter.ofPattern("dd.MM");
        
        for (int i = 0; i < 7; i++) {
            JPanel dayBox = new JPanel(new BorderLayout());
            dayBox.setBorder(BorderFactory.createLineBorder(new Color(0xDD, 0xDD, 0xDD)));
            
            JLabel dayLbl = new JLabel(dnes.plusDays(i).format(formatDna));
            dayLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
            dayLbl.setHorizontalAlignment(SwingConstants.CENTER);
            dayLbl.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
            
            JPanel colorBox = new JPanel();
            // Mock logic: busy today and tomorrow, available rest of the week
            if (i < 2) {
                colorBox.setBackground(new Color(0xEF, 0x9A, 0x9A)); // cervena
                colorBox.setToolTipText("Obsadené");
            } else {
                colorBox.setBackground(new Color(0xA5, 0xD6, 0xA7)); // zelena
                colorBox.setToolTipText("Dostupné");
            }
            
            dayBox.add(dayLbl, BorderLayout.NORTH);
            dayBox.add(colorBox, BorderLayout.CENTER);
            dayBox.setPreferredSize(new Dimension(45, 45));
            calPanel.add(dayBox);
        }
        body.add(calPanel, g);
        
        dlg.add(body, BorderLayout.CENTER);
        
        JPanel btnRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        btnRow.setBackground(Color.WHITE);
        btnRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 15));
        
        JButton btnClose = new JButton("Zatvoriť");
        btnClose.setBackground(LIGHT_GREY);
        btnClose.setForeground(Color.BLACK);
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> dlg.dispose());
        
        btnRow.add(btnClose);
        dlg.add(btnRow, BorderLayout.SOUTH);
        
        // Fix pre zatvaranie modalnych okien aby sa neglitchovalo pozadie
        dlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                getContentPane().revalidate();
                getContentPane().repaint();
            }
        });

        dlg.setVisible(true);
    }

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

    // Metoda pre zobrazenie kategorii a ich naradia
    public void zobrazKatalog(Map<String, List<Naradie>> kategorieMap) {
        katalogPanel.removeAll();

        for (Map.Entry<String, List<Naradie>> entry : kategorieMap.entrySet()) {
            String kategoria = entry.getKey();
            List<Naradie> naradieList = entry.getValue();

            // Hlavička kategórie
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(TABLE_BG);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
            JLabel katLabel = new JLabel(kategoria);
            katLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            katLabel.setForeground(new Color(0x33, 0x33, 0x33));
            headerPanel.add(katLabel, BorderLayout.WEST);

            katalogPanel.add(headerPanel);

            // Mriežka (Grid) pre položky v kategórii
            JPanel gridPanel = new JPanel();
            // Pouzijeme mriezku, 3 stlpce
            int rows = (int) Math.ceil(naradieList.size() / 3.0);
            gridPanel.setLayout(new GridLayout(rows, 3, 10, 10));
            gridPanel.setBackground(TABLE_BG);

            for (Naradie n : naradieList) {
                JPanel itemCard = new JPanel(new BorderLayout());
                itemCard.setBackground(Color.WHITE);
                itemCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0xDD, 0xDD, 0xDD)),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15)
                ));

                // Nazov naradia
                JLabel nameLabel = new JLabel(n.getNazov());
                nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
                nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

                // Dostupnost (Stav)
                JLabel statusLabel = new JLabel(n.getStav());
                statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
                statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
                statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

                // Farba podla stavu
                if ("Dostupné".equalsIgnoreCase(n.getStav())) {
                    statusLabel.setForeground(new Color(0x2E, 0x7D, 0x32)); // Zelena
                } else if ("Vypožičané".equalsIgnoreCase(n.getStav())) {
                    statusLabel.setForeground(new Color(0xC6, 0x28, 0x28)); // Cervena
                } else {
                    statusLabel.setForeground(new Color(0xEF, 0x6C, 0x00)); // Oranzova
                }

                itemCard.add(nameLabel, BorderLayout.CENTER);
                itemCard.add(statusLabel, BorderLayout.SOUTH);

                // Pridanie MouseListenera na zobrazenie detailu
                itemCard.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                itemCard.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        openDetailModal(n);
                    }
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        itemCard.setBackground(new Color(0xF5, 0xF5, 0xF5)); // Hover efekt
                    }
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        itemCard.setBackground(Color.WHITE);
                    }
                });

                gridPanel.add(itemCard);
            }

            // Ak nie su vyplnene vsetky bunky v mriezke, pridame prazdne panely
            int remaining = (rows * 3) - naradieList.size();
            for (int i = 0; i < remaining; i++) {
                JPanel empty = new JPanel();
                empty.setBackground(TABLE_BG);
                gridPanel.add(empty);
            }

            katalogPanel.add(gridPanel);
            katalogPanel.add(Box.createVerticalStrut(20));
        }

        katalogPanel.revalidate();
        katalogPanel.repaint();
    }

    // openDetailModal - modal okno pre zobrazenie detailov a dostupnosti
    public void openDetailModal(Naradie n) {
        javax.swing.JDialog dlg = new javax.swing.JDialog(this, "Detail náradia - " + n.getNazov(), true);
        dlg.setSize(420, 360);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel body = new JPanel(new GridBagLayout());
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new java.awt.Insets(5, 5, 5, 5);
        g.anchor = GridBagConstraints.WEST;

        // Názov
        g.gridx = 0; g.gridy = 0; g.gridwidth = 2;
        JLabel title = new JLabel(n.getNazov());
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        body.add(title, g);

        // Kategória
        g.gridy = 1; g.gridwidth = 1;
        JLabel katLbl = new JLabel("Kategória:");
        katLbl.setForeground(DARK_GREY);
        body.add(katLbl, g);
        g.gridx = 1;
        JLabel katVal = new JLabel(n.getKategoria());
        katVal.setFont(new Font("SansSerif", Font.PLAIN, 14));
        body.add(katVal, g);

        // Stav
        g.gridx = 0; g.gridy = 2;
        JLabel stavLbl = new JLabel("Aktuálny stav:");
        stavLbl.setForeground(DARK_GREY);
        body.add(stavLbl, g);
        g.gridx = 1;
        JLabel statusVal = new JLabel(n.getStav());
        statusVal.setFont(new Font("SansSerif", Font.BOLD, 14));
        if ("Dostupné".equalsIgnoreCase(n.getStav())) {
            statusVal.setForeground(new Color(0x2E, 0x7D, 0x32)); // Zelena
        } else if ("Vypožičané".equalsIgnoreCase(n.getStav())) {
            statusVal.setForeground(new Color(0xC6, 0x28, 0x28)); // Cervena
        } else {
            statusVal.setForeground(new Color(0xEF, 0x6C, 0x00)); // Oranzova
        }
        body.add(statusVal, g);

        // Dostupnosť (Mock kalendár)
        g.gridx = 0; g.gridy = 3; g.gridwidth = 2;
        g.insets = new java.awt.Insets(15, 5, 5, 5);
        JLabel calTitle = new JLabel("Dostupnosť (najbližších 7 dní):");
        calTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        body.add(calTitle, g);

        g.gridy = 4;
        g.insets = new java.awt.Insets(0, 5, 5, 5);
        JPanel calPanel = new JPanel(new GridLayout(1, 7, 4, 0));
        calPanel.setBackground(Color.WHITE);
        
        java.time.LocalDate dnes = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter formatDna = java.time.format.DateTimeFormatter.ofPattern("dd.MM");
        
        for (int i = 0; i < 7; i++) {
            JPanel dayBox = new JPanel(new BorderLayout());
            dayBox.setBorder(BorderFactory.createLineBorder(new Color(0xDD, 0xDD, 0xDD)));
            
            JLabel dayLbl = new JLabel(dnes.plusDays(i).format(formatDna));
            dayLbl.setFont(new Font("SansSerif", Font.PLAIN, 10));
            dayLbl.setHorizontalAlignment(SwingConstants.CENTER);
            dayLbl.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
            
            JPanel colorBox = new JPanel();
            // Jednoduchá mock logika
            if ("Dostupné".equalsIgnoreCase(n.getStav())) {
                colorBox.setBackground(new Color(0xA5, 0xD6, 0xA7)); // svetlo zelena
            } else if ("Vypožičané".equalsIgnoreCase(n.getStav())) {
                colorBox.setBackground(i < 3 ? new Color(0xEF, 0x9A, 0x9A) : new Color(0xA5, 0xD6, 0xA7)); 
            } else {
                colorBox.setBackground(new Color(0xFF, 0xCC, 0x80)); // oranzova (servis)
            }
            
            dayBox.add(dayLbl, BorderLayout.NORTH);
            dayBox.add(colorBox, BorderLayout.CENTER);
            dayBox.setPreferredSize(new Dimension(45, 45));
            calPanel.add(dayBox);
        }
        body.add(calPanel, g);

        dlg.add(body, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        btnRow.setBackground(Color.WHITE);
        btnRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 15));
        
        JButton btnClose = new JButton("Zatvoriť");
        btnClose.setBackground(LIGHT_GREY);
        btnClose.setForeground(Color.BLACK);
        btnClose.setFocusPainted(false);
        btnClose.addActionListener(e -> dlg.dispose());
        
        
        btnRow.add(btnClose);
        dlg.add(btnRow, BorderLayout.SOUTH);

        // Fix pre zatvaranie modalnych okien aby sa neglitchovalo pozadie
        dlg.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                getContentPane().revalidate();
                getContentPane().repaint();
            }
        });

        dlg.setVisible(true);
    }

    public void setUcSwitchCallback(Runnable[] callbacks) {
        this.ucSwitchCallback = callbacks;
    }
}
