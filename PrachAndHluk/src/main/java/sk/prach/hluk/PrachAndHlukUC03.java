package sk.prach.hluk;

import javax.swing.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import sk.prach.hluk.controller.NavratController;
import sk.prach.hluk.model.NaradieZoznam;
import sk.prach.hluk.model.Naradie;
import sk.prach.hluk.model.Vypozicka;
import sk.prach.hluk.view.NavratUI;

/**
 * PrachAndHlukUC03 - spustenie UC03 Návrat náradia.
 * Demonstracny main s testovacimi datami.
 */
public class PrachAndHlukUC03 {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // NaradieZoznam model — zdielany s ostatnymi UC
            NaradieZoznam naradieModel = new NaradieZoznam();
            naradieModel.pridatNaradie(new Naradie(4, "Vŕtačka Bosch",      "Vypožičané", 16, 2));
            naradieModel.pridatNaradie(new Naradie(5, "Uhlovka Makita",      "Dostupné",    2, 0));
            naradieModel.pridatNaradie(new Naradie(6, "Jadrový vrták 100mm", "V servise",  12, 1));
            naradieModel.pridatNaradie(new Naradie(7, "Priamočiara píla",    "Vypožičané",  6, 0));
            naradieModel.pridatNaradie(new Naradie(8, "Demolačné kladivo",   "Dostupné",    3, 0));

            // Zoznam aktivnych vypoziciek — tieto su momentalne u zakaznikov
            List<Vypozicka> vypozicky = new ArrayList<>();
            vypozicky.add(new Vypozicka(
                1001, 4, "Vŕtačka Bosch",
                "Ján Novák", "+421 911 111 111",
                LocalDateTime.now().minusDays(16),
                LocalDateTime.now().minusDays(2),   // meskanie!
                80.0, "🔩"
            ));
            vypozicky.add(new Vypozicka(
                1002, 7, "Priamočiara píla",
                "Mária Kováčová", "+421 902 222 222",
                LocalDateTime.now().minusDays(6),
                LocalDateTime.now().plusDays(1),    // v poriadku
                50.0, "🪚"
            ));
            vypozicky.add(new Vypozicka(
                1003, 4, "Demolačné kladivo",
                "Peter Horváth", "+421 944 333 333",
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().plusDays(4),    // v poriadku
                120.0, "🔨"
            ));

            // View — hlavne okno UC03
            NavratUI view = new NavratUI();

            // Controller — prepojime modely a view
            new NavratController(naradieModel, vypozicky, view);

            view.setVisible(true);
        });
    }
}
