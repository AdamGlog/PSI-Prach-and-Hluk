package sk.prach.hluk;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import sk.prach.hluk.controller.NaradieController;
import sk.prach.hluk.model.Naradie;
import sk.prach.hluk.model.NaradieZoznam;
import sk.prach.hluk.view.NaradieUI;

class UC4AccTest {

    private NaradieZoznam model;
    private NaradieUI view;
    private NaradieController controller;

    @BeforeEach
    void setUp() {
        // Vytvoríme čistý model pre každý test
        model = new NaradieZoznam();

        // Testovacie dáta podľa tvojho mainu
        model.pridatNaradie(new Naradie(4, "Vŕtačka Bosch",      "Vypožičané", 16, 2));
        model.pridatNaradie(new Naradie(5, "Uhlovka Makita",      "Dostupné",     2, 0));
        model.pridatNaradie(new Naradie(6, "Jadrový vrták 100mm", "V servise",   12, 1));
        model.pridatNaradie(new Naradie(7, "Priamočiara píla",    "Vypožičané",   6, 0));
        model.pridatNaradie(new Naradie(8, "Demolačné kladivo",   "Dostupné",     3, 0));

        view = new NaradieUI();
        controller = new NaradieController(model, view);
    }

    @Test
    @DisplayName("UC04 - Hlavný scenár: Zobrazenie, zoradenie, zmena stavu na 'V servise' a vyradenie")
    void testHlavnyScenar() {
        // 1. + 2. Systém zobrazí zoznam vrátane štatistík
        assertEquals(5, model.getZoznam().size());

        // 3. + 4. Zamestnanec zoradí podľa vypožičaní (zostupne)
        var zoradeny = model.sortZoznamBy('v', false);
        assertEquals(4, zoradeny.get(0).getId());   // Vŕtačka Bosch má najviac (16)

        // 5. Zamestnanec označí náradie stavom „V servise“
        boolean uspech = model.zmenStav(5, "V servise");   // Uhlovka Makita
        assertTrue(uspech);
        assertEquals("V servise", getNaradieById(5).getStav());

        // Overíme zvýšenie servisovaneCount
        assertEquals(1, getNaradieById(5).getServisovaneCount());

        // 7. Zamestnanec označí náradie stavom „Vyradené“
        model.zmenStav(8, "Vyradené");     // Demolačné kladivo

        // 8. Systém odstráni vyradené náradie refreshom (tvoj spôsob)
        model.updateZoznam();

        // Overíme, že bolo odstránené
        assertEquals(4, model.getZoznam().size());
        assertNull(getNaradieById(8));     // už by nemalo existovať
    }

    @Test
    @DisplayName("UC04 - Alternatíva: Zamestnanec neurobí žiadnu zmenu")
    void testAlternativaZiadnaZmena() {
        int povodnyPocet = model.getZoznam().size();

        // Žiadna akcia → nič sa nemá zmeniť
        assertEquals(povodnyPocet, model.getZoznam().size());
    }

    @Test
    @DisplayName("UC04 - Exception: Pokus zmeniť z 'Vypožičané' na 'V servise'")
    void testExceptionNepovolenaZmena() {
        // id=4 je "Vypožičané"
        boolean uspech = model.zmenStav(4, "V servise");

        assertFalse(uspech, "Zmena z Vypožičané na V servise musí byť zakázaná");

        // Stav sa nesmie zmeniť
        assertEquals("Vypožičané", getNaradieById(4).getStav());
    }

    // Pomocná metóda na jednoduchšie vyhľadávanie náradia podľa ID
    private Naradie getNaradieById(int id) {
        return model.getZoznam().stream()
                .filter(n -> n.getId() == id)
                .findFirst()
                .orElse(null);
    }
}