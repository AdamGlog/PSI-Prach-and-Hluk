package sk.prach.hluk.controller;

import sk.prach.hluk.model.NaradieZoznam;
import sk.prach.hluk.model.Vypozicka;
import sk.prach.hluk.view.NavratUI;

import java.util.List;

/**
 * NavratController - controller pre UC03 Návrat náradia.
 * Prepaja model (NaradieZoznam + zoznam Vypoziciek) s NavratUI.
 *
 * Hlavny scenar UC03:
 *   1. Zakaznik pride na pobocku s vypozicanym naradim
 *   2. Zamestnanec si najde ID pozicky v databaze (tabulka vypoziciek)
 *   3. Zamestnanec skontroluje stav naradia
 *   4. Zamestnanec prevezme naradie od zakaznika
 *   5. Zamestnanec vrati zakaznikovi zalohu
 *   6. System zaznaci prevzatie naradia (updateZoznam + zmena stavu)
 *
 * Alternativny scenar 4.1 — naradie mierne poskodene:
 *   -> Zamestnanec nastaví stav "Poškodené – servis", zaloha sa krati o 50%
 *
 * Vynimka 1.1 — zakaznik nevrati naradie:
 *   -> Vypozicka zostava v zozname (zamestnanec okno zatvori bez potvrdenia)
 */
public class NavratController {

    // Model a View
    private NaradieZoznam naradieModel;
    private List<Vypozicka> vypozicky;
    private NavratUI view;

    // Konstruktor — prepojime modely a view
    public NavratController(NaradieZoznam naradieModel, List<Vypozicka> vypozicky, NavratUI view) {
        this.naradieModel = naradieModel;
        this.vypozicky    = vypozicky;
        this.view         = view;
        initView();
        initListeners();
    }

    // initView — naplnime view pociatocnymi datami (zoznam aktivnych vypoziciek)
    private void initView() {
        view.zobrazVypozicky(vypozicky);
    }

    // initListeners — zaregistrujeme listenery pre navrat a potvrdenie
    private void initListeners() {

        // setNavratListener — zamestnanec klikol "Prevziať" pri konkretnej vypozicke
        // UC03 Krok 2: Zamestnanec si najde ID pozicky a otvorime detail modal
        view.setNavratListener(naradieId -> {
            Vypozicka vypozicka = najdiVypozickuPodlaId(naradieId);
            if (vypozicka == null) {
                view.zobrazSpravu("Výpožička pre náradie ID " + naradieId + " nebola nájdená.");
                return;
            }
            // UC03 Krok 3+4: Zamestnanec skontroluje stav a prevezme naradie
            view.openNavratModal(vypozicka);
        });

        // setPotvrdenieNavratuListener — zamestnanec potvrdil prevzatie v modalnom okne
        // UC03 Krok 5-8: Vyucet zalohy, aktualizacia stavu, ulozenie do skladu
        view.setPotvrdenieNavratuListener((naradieId, novyStav) -> {
            Vypozicka vypozicka = najdiVypozickuPodlaId(naradieId);
            if (vypozicka == null) return;

            String zakaznikMeno = vypozicka.getZakaznikMeno();
            String naradieNazov = vypozicka.getNaradieNazov();

            // UC03 Krok 7a: Aktualizujeme stav v modeli naradia
            // "Poškodené – servis" = naradie ide do servisu (stav "V servise" v NaradieZoznam)
            String stavPreNaradieModel = novyStav.startsWith("Poškodené") ? "V servise" : novyStav;
            boolean uspech = naradieModel.zmenStav(naradieId, stavPreNaradieModel);
            if (!uspech) {
                // Toto by nemalo nastat — vypozicane naradie moze ist len na Dostupne alebo V servise
                view.zobrazSpravu("Chyba pri zmene stavu náradia. Kontaktujte administrátora.");
                return;
            }

            // UC03 Krok 7b: Zaevidujeme prevzatie — odstranime z aktivnych vypoziciek
            vypozicky.remove(vypozicka);

            // Alternativny scenar 4.1: Naradie poskodene — informujeme zamestnanca
            if (novyStav.startsWith("Poškodené")) {
                view.zobrazSpravu(
                    "⚠ Náradie „" + naradieNazov + " je poškodené \n"
                    + "Zákazníkovi " + zakaznikMeno + " bola vrátená len 50% zálohy.\n"
                    + "Náradie bolo odoslané do servisu.");
            }

            // UC03 Krok 8: Naradie bolo ulozene spat do skladu — potvrdenie
            view.zobrazUspech(naradieNazov, zakaznikMeno);

            // UC03 Krok 7: Zobrazime aktualizovany zoznam vypoziciek
            view.zobrazVypozicky(vypozicky);
        });
    }

    // najdiVypozickuPodlaId — pomocna metoda, najdeme vypozicku podla ID naradia
    private Vypozicka najdiVypozickuPodlaId(int naradieId) {
        return vypozicky.stream()
                .filter(v -> v.getNaradieId() == naradieId)
                .findFirst()
                .orElse(null);
    }

    // getAktivneVypozicky — vratime aktualizovany zoznam aktivnych vypoziciek (pre integraciu)
    public List<Vypozicka> getAktivneVypozicky() {
        return vypozicky;
    }
}
