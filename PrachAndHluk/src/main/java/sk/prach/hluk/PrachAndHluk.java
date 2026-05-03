package sk.prach.hluk;

import java.awt.CardLayout;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import sk.prach.hluk.modely.Naradie;
import sk.prach.hluk.modely.Vypozicka;
import sk.prach.hluk.modely.Zmluva;
import sk.prach.hluk.osoby.Zakaznik;
import sk.prach.hluk.osoby.Zamestnanec;
import sk.prach.hluk.podnik.InternyProjekt;         
import sk.prach.hluk.podnik.Podnik;
import sk.prach.hluk.prostredie.KatalogController;
import sk.prach.hluk.prostredie.KatalogUI;
import sk.prach.hluk.prostredie.NaradieController;
import sk.prach.hluk.prostredie.NaradieUI;
import sk.prach.hluk.prostredie.NaradieZoznam;
import sk.prach.hluk.prostredie.NavratController;
import sk.prach.hluk.prostredie.NavratUI;
import sk.prach.hluk.prostredie.VypozickaController;
import sk.prach.hluk.prostredie.VypozickaUI;

public class PrachAndHluk {

    public static void main(String[] args) {
        // === NOVÉ OBJEKTY PODĽA OBJEKTOVÝCH DIAGRAMOV ===
        System.out.println("=== Inicializácia business objektov ===");

        // 1. Podnik
        Podnik podnik = new Podnik("Snina");
        
        // 2. Interný projekt
        InternyProjekt projekt = new InternyProjekt();
        projekt.pridatProjekt();

        // 3. Zamestnanci
        Zamestnanec janNovak = new Zamestnanec(1, "Ján", "Novák", "jan.novak@prach.sk");
        Zamestnanec mariaKovacova = new Zamestnanec(2, "Mária", "Kováčová", "maria.kovacova@prach.sk");
        Zamestnanec peterHorvath = new Zamestnanec(3, "Peter", "Horváth", "peter.horvath@prach.sk");

        janNovak.prihlasit(1, "Ján", "Novák");

        // 4. Zákazníci (podľa diagramov)
        Zakaznik zakaznik1 = new Zakaznik(101, "Ján", "Novák", "jan.novak@email.sk");
        podnik.zaevidovanieZakaznika();
        Zakaznik zakaznik2 = new Zakaznik(102, "Mária", "Kováčová", "maria@email.sk");
        podnik.zaevidovanieZakaznika();
        Zakaznik zakaznik3 = new Zakaznik(103, "Peter", "Horváth", "peter.h@email.sk");
        podnik.zaevidovanieZakaznika();

        
        zakaznik1.vstupDoPodniku();
        zakaznik2.vstupDoPodniku();
        zakaznik3.vstupDoPodniku();

        // 5. Zmluvy (podľa diagramov)
        Zmluva zmluva1 = new Zmluva(1001, LocalDate.now(), "Zmluva na vŕtačku", 80, 1, "Vytvorená");
        Zmluva zmluva2 = new Zmluva(1002, LocalDate.now(), "Zmluva na uhlovku", 50, 1, "Vytvorená");

        zmluva1.generate();
        zmluva2.generate();

        // 6. Výpožičky (podľa diagramov)
        List<Vypozicka> vypozicky = new ArrayList<>();
        vypozicky.add(new Vypozicka(1001, 4, "Vŕtačka Bosch", 
                "Ján Novák", "+421 911 111 111", 
                LocalDateTime.now().minusDays(16), 
                LocalDateTime.now().minusDays(2), 80.0, "🔩"));
        vypozicky.add(new Vypozicka(1002, 7, "Priamočiara píla", 
                "Mária Kováčová", "+421 902 222 222", 
                LocalDateTime.now().minusDays(6), 
                LocalDateTime.now().plusDays(1), 50.0, "🪚"));

        // === ZDIELANÝ MODEL — rovnake naradie pre vsetky UC ===
        NaradieZoznam naradieModel = new NaradieZoznam();
        naradieModel.pridatNaradie(new Naradie(4, "Vŕtačka Bosch",       "Vŕtanie", "Vypožičané", 16, 2));
        naradieModel.pridatNaradie(new Naradie(5, "Uhlovka Makita",      "Rezanie", "Dostupné",    2, 0));
        naradieModel.pridatNaradie(new Naradie(6, "Jadrový vrták 100mm", "Vŕtanie", "V servise",  12, 1));
        naradieModel.pridatNaradie(new Naradie(7, "Priamočiara píla",    "Rezanie", "Vypožičané",  6, 0));
        naradieModel.pridatNaradie(new Naradie(8, "Demolačné kladivo",   "Búranie", "Dostupné",    3, 0));

        // === HLAVNÉ OKNO s CardLayout ===
        SwingUtilities.invokeLater(() -> {
            JFrame mainFrame = new JFrame("Prach & Hluk");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(980, 680);
            mainFrame.setLocationRelativeTo(null);

            CardLayout cardLayout = new CardLayout();
            JPanel cardPanel = new JPanel(cardLayout);
            // === UC1 VIEW + CONTROLLER ===
            KatalogUI uc1View = new KatalogUI();
            new KatalogController(naradieModel, uc1View);

            // === UC2, UC3, UC4 ===
            VypozickaUI uc2View = new VypozickaUI();
            new VypozickaController(naradieModel, vypozicky, uc2View);

            NavratUI uc3View = new NavratUI();
            new NavratController(naradieModel, vypozicky, uc3View);

            NaradieUI uc4View = new NaradieUI();
            new NaradieController(naradieModel, uc4View);

            // Extractujeme content pane z kazdeho JFrame a pridame do CardLayout
            // JFrame.getContentPane() vrati hlavny panel s headerom, centerom a footrom
            JPanel uc1Panel = (JPanel) uc1View.getContentPane();
            JPanel uc2Panel = (JPanel) uc2View.getContentPane();
            JPanel uc3Panel = (JPanel) uc3View.getContentPane();
            JPanel uc4Panel = (JPanel) uc4View.getContentPane();

            cardPanel.add(uc1Panel, "UC1");
            cardPanel.add(uc2Panel, "UC2");
            cardPanel.add(uc3Panel, "UC3");
            cardPanel.add(uc4Panel, "UC4");

            Runnable[] switchToUC = new Runnable[4];
            switchToUC[0] = () -> cardLayout.show(cardPanel, "UC1");
            switchToUC[1] = () -> cardLayout.show(cardPanel, "UC2");
            switchToUC[2] = () -> cardLayout.show(cardPanel, "UC3");
            switchToUC[3] = () -> cardLayout.show(cardPanel, "UC4");

            // Odovzdame switch callback do UC1, UC2, UC3 a UC4 view
            uc1View.setUcSwitchCallback(switchToUC);
            uc2View.setUcSwitchCallback(switchToUC);
            uc3View.setUcSwitchCallback(switchToUC);
            uc4View.setUcSwitchCallback(switchToUC);

            // Spustime na UC1 ako default (mozno zmenit)
            cardLayout.show(cardPanel, "UC1");

            mainFrame.setContentPane(cardPanel);
            mainFrame.setVisible(true);
        });
    }

}