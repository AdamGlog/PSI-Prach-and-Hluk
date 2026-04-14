package sk.prach.hluk.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NaradieZoznam {

    // zoznamNaradia - hlavny zoznam vsetkych kusov naradia
    private List<Naradie> zoznamNaradia;

    // Konstruktor - inicializujeme prazdny zoznam
    public NaradieZoznam() {
        this.zoznamNaradia = new ArrayList<>();
    }

    // getZoznam - vratime cely aktualny zoznam naradia
    public List<Naradie> getZoznam() {
        return zoznamNaradia;
    }

    // pridatNaradie - pridame novy kus naradia do zoznamu (pomocna metoda pre inicializaciu)
    public void pridatNaradie(Naradie naradie) {
        zoznamNaradia.add(naradie);
    }

    // zmenStav - zmenime stav konkretneho naradia podla ID + zvýšime príslušný counter, inak vratim false
    public boolean zmenStav(int id, String novyStav) {
        for (Naradie nar : zoznamNaradia) {
            if (nar.getId() == id) {
                // Zakázaná zmena: z Vypožičané na V servise
                if (nar.getStav().equals("Vypožičané") && novyStav.equals("V servise")) {
                    return false;
                }
                String staryStav = nar.getStav();
                nar.setStav(novyStav);

                // Zvýšenie počítadiel podľa NOVÉHO stavu
                if (novyStav.equals("Vypožičané")) {
                    // Ak sa mení na Vypožičané, zvýšime vypozicaneCount
                    nar.setVypozicaneCount(nar.getVypozicaneCount() + 1);
                } 
                else if (novyStav.equals("V servise") || novyStav.equals("Servis Neskôr")) {
                    nar.setServisovaneCount(nar.getServisovaneCount() + 1);
                }

                return true;
            }
        }
        return false;
    }

    // sortZoznamBy - zoradime zoznam podla zadaneho kriteria a smeru
    public List<Naradie> sortZoznamBy(char kriterium, boolean vzostupne) {
        Comparator<Naradie> komp;
        switch (kriterium) {
            case 'v' -> komp = Comparator.comparingInt(Naradie::getVypozicaneCount);
            case 's' -> komp = Comparator.comparingInt(Naradie::getServisovaneCount);
            default  -> komp = Comparator.comparingInt(Naradie::getVypozicaneCount);
        }
        if (!vzostupne) komp = komp.reversed();
        return zoznamNaradia.stream().sorted(komp).collect(Collectors.toList());
    }

    // filterZoznamBy - prefiltrujeme zoznam podla stavu naradia
    public List<Naradie> filterZoznamBy(String stav) {
        return zoznamNaradia.stream()
                .filter(nar -> nar.getStav().equalsIgnoreCase(stav))
                .collect(Collectors.toList());
    }

    // updateZoznam - odstranime vsetky naradia so stavom Vyradene zo zoznamu
    public void updateZoznam() {
        zoznamNaradia.removeIf(nar -> nar.getStav().equals("Vyradené"));
    }
}