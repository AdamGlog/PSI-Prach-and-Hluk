package sk.prach.hluk;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import sk.prach.hluk.controller.NaradieController;
import sk.prach.hluk.controller.NavratController;
import sk.prach.hluk.model.Naradie;
import sk.prach.hluk.model.NaradieZoznam;
import sk.prach.hluk.model.Vypozicka;
import sk.prach.hluk.view.NaradieUI;
import sk.prach.hluk.view.NavratUI;

public class PrachAndHluk {

    public static void main(String[] args) {
        // SwingUtilities - spustime UI v spravnom vlakne pre Swing
        SwingUtilities.invokeLater(() -> {

            // === ZDIELANY MODEL — rovnake naradie pre vsetky UC ===
            NaradieZoznam naradieModel = new NaradieZoznam();
            naradieModel.pridatNaradie(new Naradie(4, "Vŕtačka Bosch",      "Vypožičané", 16, 2));
            naradieModel.pridatNaradie(new Naradie(5, "Uhlovka Makita",      "Dostupné",    2, 0));
            naradieModel.pridatNaradie(new Naradie(6, "Jadrový vrták 100mm", "V servise",  12, 1));
            naradieModel.pridatNaradie(new Naradie(7, "Priamočiara píla",    "Vypožičané",  6, 0));
            naradieModel.pridatNaradie(new Naradie(8, "Demolačné kladivo",   "Dostupné",    3, 0));

            // === ZDIELANY MODEL — aktivne vypozicky pre UC03 ===
            List<Vypozicka> vypozicky = new ArrayList<>();
            vypozicky.add(new Vypozicka(
                1001, 4, "Vŕtačka Bosch",
                "Ján Novák", "+421 911 111 111",
                LocalDateTime.now().minusDays(16),
                LocalDateTime.now().minusDays(2),
                80.0, "🔩"
            ));
            vypozicky.add(new Vypozicka(
                1002, 7, "Priamočiara píla",
                "Mária Kováčová", "+421 902 222 222",
                LocalDateTime.now().minusDays(6),
                LocalDateTime.now().plusDays(1),
                50.0, "🪚"
            ));
            vypozicky.add(new Vypozicka(
                1003, 8, "Demolačné kladivo",
                "Peter Horváth", "+421 944 333 333",
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().plusDays(4),
                120.0, "🔨"
            ));

            // === HLAVNE OKNO s CardLayout pre prepinanie UC ===
            JFrame mainFrame = new JFrame("Prach & Hluk");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(980, 680);
            mainFrame.setLocationRelativeTo(null);

            // CardLayout - kazda karta je jeden UC panel
            CardLayout cardLayout = new CardLayout();
            JPanel cardPanel = new JPanel(cardLayout);

            // === UC3 VIEW + CONTROLLER ===
            NavratUI uc3View = new NavratUI();
            new NavratController(naradieModel, vypozicky, uc3View);

            // === UC4 VIEW + CONTROLLER ===
            NaradieUI uc4View = new NaradieUI();
            new NaradieController(naradieModel, uc4View);

            // Extractujeme content pane z kazdeho JFrame a pridame do CardLayout
            // JFrame.getContentPane() vrati hlavny panel s headerom, centerom a footrom
            JPanel uc1Panel = buildPlaceholderPanel("UC1 – Prehliadanie katalógu", "Bude implementované v UC01");
            JPanel uc2Panel = buildPlaceholderPanel("UC2 – Výpožička náradia",     "Bude implementované v UC02");
            JPanel uc3Panel = (JPanel) uc3View.getContentPane();
            JPanel uc4Panel = (JPanel) uc4View.getContentPane();

            cardPanel.add(uc1Panel, "UC1");
            cardPanel.add(uc2Panel, "UC2");
            cardPanel.add(uc3Panel, "UC3");
            cardPanel.add(uc4Panel, "UC4");

            // === UC SWITCH CALLBACK — odovzdame obom view aby vedeli prepinat ===
            // Ked kliknes na UC tlacidlo v headeri, prepneme kartu v CardLayout
            Runnable[] switchToUC = new Runnable[4];
            switchToUC[0] = () -> cardLayout.show(cardPanel, "UC1");
            switchToUC[1] = () -> cardLayout.show(cardPanel, "UC2");
            switchToUC[2] = () -> cardLayout.show(cardPanel, "UC3");
            switchToUC[3] = () -> cardLayout.show(cardPanel, "UC4");

            // Odovzdame switch callback do UC3 a UC4 view
            uc3View.setUcSwitchCallback(switchToUC);
            uc4View.setUcSwitchCallback(switchToUC);

            // Spustime na UC4 ako default (mozno zmenit)
            cardLayout.show(cardPanel, "UC4");

            mainFrame.setContentPane(cardPanel);
            mainFrame.setVisible(true);
        });
    }

    // buildPlaceholderPanel - pomocna metoda pre UC ktore este nie su implementovane
    private static JPanel buildPlaceholderPanel(String nazov, String popis) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0xBA, 0xBA, 0xBA));

        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(new Color(0xD9, 0xD9, 0xD9));
        center.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titleLbl = new JLabel(nazov);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel opisLbl = new JLabel(popis);
        opisLbl.setFont(new Font("SansSerif", Font.PLAIN, 15));
        opisLbl.setForeground(new Color(0x90, 0x90, 0x90));
        opisLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalStrut(10));
        card.add(titleLbl);
        card.add(Box.createVerticalStrut(10));
        card.add(opisLbl);
        card.add(Box.createVerticalStrut(10));

        center.add(card);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }
}