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

    // zmenStav - zmenime stav konkretneho naradia podla ID, vratime false ak zmena nie je povolena
    public boolean zmenStav(int id, String novyStav) {
        for (Naradie n : zoznamNaradia) {
            if (n.getId() == id) {
                if (n.getStav().equals("Vypožičané") && novyStav.equals("V servise")) {
                    return false;
                }
                n.setStav(novyStav);
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
                .filter(n -> n.getStav().equalsIgnoreCase(stav))
                .collect(Collectors.toList());
    }

    // updateZoznam - odstranime vsetky naradia so stavom Vyradene zo zoznamu
    public void updateZoznam() {
        zoznamNaradia.removeIf(n -> n.getStav().equals("Vyradené"));
    }
}