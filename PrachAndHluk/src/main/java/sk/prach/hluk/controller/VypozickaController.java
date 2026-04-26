package sk.prach.hluk.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import sk.prach.hluk.model.Naradie;
import sk.prach.hluk.model.NaradieZoznam;
import sk.prach.hluk.model.Vypozicka;
import sk.prach.hluk.view.VypozickaUI;

/**
 * VypozickaController - prepája NaradieZoznam a UC02 View.
 */
public class VypozickaController {

    private NaradieZoznam model;
    private List<Vypozicka> vypozicky;
    private VypozickaUI view;

    public VypozickaController(NaradieZoznam model, List<Vypozicka> vypozicky, VypozickaUI view) {
        this.model = model;
        this.vypozicky = vypozicky;
        this.view = view;
        initView();
        initListeners();
    }

    private void initView() {
        view.zobrazNaradie(model.getZoznam());
    }

    private void initListeners() {
        // Vyhľadávanie
        view.setListeners(
            query -> {
                if (query == null || query.trim().isEmpty()) {
                    view.zobrazNaradie(model.getZoznam());
                } else {
                    String q = query.toLowerCase();
                    List<Naradie> filtered = model.getZoznam().stream()
                        .filter(n -> String.valueOf(n.getId()).contains(q) || n.getNazov().toLowerCase().contains(q))
                        .collect(Collectors.toList());
                    view.zobrazNaradie(filtered);
                }
            },
            id -> {
                Naradie n = najdiNaradie(id);
                if (n != null) {
                    if (!n.getStav().equals("Dostupné")) {
                        view.zobrazSpravu("Náradie nie je dostupné (Stav: " + n.getStav() + ")");
                    } else {
                        view.openBorrowModal(n);
                    }
                }
            },
            (id, typ, meno, telefon, zaloha, projekt) -> {
                Naradie n = najdiNaradie(id);
                if (n == null) return;

                if (typ.equals("Externý")) {
                    // UC02 Krok 6: Zmena stavu na Vypožičané
                    model.zmenStav(id, "Vypožičané");
                    
                    // Pridanie do zoznamu výpožičiek (pre UC03)
                    Vypozicka v = new Vypozicka(
                        1000 + vypozicky.size() + 1,
                        n.getId(), n.getNazov(),
                        meno, telefon,
                        LocalDateTime.now(),
                        LocalDateTime.now().plusDays(7), // Default 7 dní
                        zaloha,
                        getIkonka(n.getNazov())
                    );
                    vypozicky.add(v);
                    view.zobrazSpravu("Náradie bolo vypožičané zákazníkovi: " + meno);
                } else {
                    // UC02 Alternatívny scenár: Interná réžia
                    model.zmenStav(id, "Na internej zákazke");
                    view.zobrazSpravu("Náradie priradené k projektu: " + projekt);
                }
                
                view.zobrazNaradie(model.getZoznam());
            }
        );
    }

    private Naradie najdiNaradie(int id) {
        return model.getZoznam().stream().filter(n -> n.getId() == id).findFirst().orElse(null);
    }

    private String getIkonka(String nazov) {
        if (nazov.contains("Vŕtačka")) return "🔩";
        if (nazov.contains("Píla")) return "🪚";
        if (nazov.contains("Kladivo")) return "🔨";
        if (nazov.contains("vrták")) return "🕳";
        return "🔧";
    }
}
