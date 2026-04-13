package sk.prach.hluk;

import javax.swing.*;
import sk.prach.hluk.controller.NaradieController;
import sk.prach.hluk.model.Naradie;
import sk.prach.hluk.model.NaradieZoznam;
import sk.prach.hluk.view.NaradieUI;

public class PrachAndHluk {

    public static void main(String[] args) {

        // SwingUtilities - spustime UI v spravnom vlakne pre Swing
        SwingUtilities.invokeLater(() -> {

            // Model - vytvorime zoznam naradia a naplnime testovacimi datami
            NaradieZoznam model = new NaradieZoznam();
            model.pridatNaradie(new Naradie(4, "Vŕtačka Bosch",      "Vypožičané", 16, 2));
            model.pridatNaradie(new Naradie(5, "Uhlovka Makita",      "Dostupné",    2, 0));
            model.pridatNaradie(new Naradie(6, "Jadrový vrták 100mm", "V servise",  12, 1));
            model.pridatNaradie(new Naradie(7, "Priamočiara píla",    "Vypožičané",  6, 0));
            model.pridatNaradie(new Naradie(8, "Demolačné kladivo",   "Dostupné",    3, 0));

            // View - vytvorime hlavne okno aplikacie
            NaradieUI view = new NaradieUI();

            // Controller - prepojime model a view
            new NaradieController(model, view);

            // setVisible - zobrazime okno
            view.setVisible(true);
        });
    }
}