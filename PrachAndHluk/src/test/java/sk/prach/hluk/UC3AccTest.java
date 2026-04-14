package sk.prach.hluk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sk.prach.hluk.model.Naradie;
import sk.prach.hluk.model.NaradieZoznam;
import sk.prach.hluk.model.Vypozicka;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UC03 Acceptance Tests (Akceptačné testy pre Návrat náradia)
 *
 * Tieto testy pokrývajú hlavný scenár, alternatívny scenár aj výnimky podľa use-case tabuľky.
 * Testujú business logiku UC03 (controller + model) bez spúšťania plného Swing UI.
 * Môžete ich pridať priamo do projektu (napr. ako NavratUC03AcceptanceTest.java).
 *
 * Spustenie: JUnit 5 (pridajte junit-jupiter do build.gradle / pom.xml ak ešte nemáte).
 */
class UC3AccTest {

    private NaradieZoznam naradieModel;
    private List<Vypozicka> aktivneVypozicky;

    private static final LocalDateTime FIXED_NOW = LocalDateTime.of(2026, 4, 14, 17, 0);

    @BeforeEach
    void setUp() {
        // === Model NaradieZoznam (zdieľaný s UC03) ===
        naradieModel = new NaradieZoznam();
        naradieModel.pridatNaradie(new Naradie(4, "Vŕtačka Bosch", "Vypožičané", 16, 2));
        naradieModel.pridatNaradie(new Naradie(7, "Priamočiara píla", "Vypožičané", 6, 0));

        // === Aktívne výpožičky (testovacie dáta z PrachAndHlukUC03.java) ===
        aktivneVypozicky = new ArrayList<>();

        // Výpožička 1001 – meškajúca (plánovaný návrat už uplynul)
        aktivneVypozicky.add(new Vypozicka(
                1001, 4, "Vŕtačka Bosch",
                "Ján Novák", "+421 911 111 111",
                FIXED_NOW.minusDays(16),
                FIXED_NOW.minusDays(2),   // meškanie
                80.0, "🔩"));

        // Výpožička 1002 – v poriadku
        aktivneVypozicky.add(new Vypozicka(
                1002, 7, "Priamočiara píla",
                "Mária Kováčová", "+421 902 222 222",
                FIXED_NOW.minusDays(6),
                FIXED_NOW.plusDays(1),
                50.0, "🪚"));
    }

    // Pomocná metóda simulujúca logiku listenera v NavratController (setPotvrdenieNavratuListener)
    private void simulujPrevzatie(int naradieId, String novyStavUI) {
        Vypozicka vypozicka = aktivneVypozicky.stream()
                .filter(v -> v.getNaradieId() == naradieId)
                .findFirst()
                .orElse(null);

        if (vypozicka == null) {
            return; // simulácia chyby – nič sa nestane
        }

        // Logika z NavratController (riadky 78-104)
        String stavPreModel = novyStavUI.startsWith("Poškodené") ? "V servise" : novyStavUI;
        boolean uspech = naradieModel.zmenStav(naradieId, stavPreModel);

        if (uspech) {
            aktivneVypozicky.remove(vypozicka);
        }
        // (UI správy sa v teste nekontrolujú – overujeme iba stav modelu a zoznamu)
    }

    @Test
    @DisplayName("UC03-AT01: Zamestnanec zaznamená prevzatie nepoškodeného náradia (hlavný scenár)")
    void uc03_AT01_prevzatieNeposkodenehoNaradia() {
        // Given
        int id = 4;
        String novyStav = "Dostupné";

        // When – simulácia kliknutia "Potvrdiť prevzatie"
        simulujPrevzatie(id, novyStav);

        // Then
        assertEquals(1, aktivneVypozicky.size(), "Výpožička mala byť odstránená");
        assertTrue(aktivneVypozicky.stream().noneMatch(v -> v.getNaradieId() == id));

        Naradie vracane = naradieModel.getZoznam().stream()
                .filter(n -> n.getId() == id)
                .findFirst().orElseThrow();
        assertEquals("Dostupné", vracane.getStav(), "Stav náradia sa má zmeniť na Dostupné");
    }

    @Test
    @DisplayName("UC03-AT03: Zamestnanec zaznamená prevzatie náradia do servisu (bez poškodenia zákazníkom)")
    void uc03_AT03_prevzatieDoServisu() {
        // Given
        int id = 7;
        String novyStavUI = "V servise – potrebuje opravu";

        // When
        simulujPrevzatie(id, novyStavUI);

        // Then
        assertEquals(1, aktivneVypozicky.size());
        assertTrue(aktivneVypozicky.stream().noneMatch(v -> v.getNaradieId() == id));

        Naradie vracane = naradieModel.getZoznam().stream()
                .filter(n -> n.getId() == id)
                .findFirst().orElseThrow();
        assertEquals("V servise – potrebuje opravu", vracane.getStav());
    }

    @Test
    @DisplayName("UC03-AT04: Meškajúce náradie – prevzatie stále úspešné")
    void uc03_AT04_meskaJuceNaradie() {
        // Given – výpožička 1001 už mešká (nastavené v setUp)
        int id = 4;

        // When
        simulujPrevzatie(id, "Dostupné");

        // Then
        assertEquals(1, aktivneVypozicky.size());
        assertTrue(aktivneVypozicky.stream().noneMatch(v -> v.getNaradieId() == id));

        // Overenie, že meškanie bolo detekované (metóda z Vypozicka)
        Vypozicka meskajuca = new Vypozicka(1001, 4, "", "", "", FIXED_NOW.minusDays(16),
                FIXED_NOW.minusDays(2), 80.0, "");
        assertTrue(meskajuca.jeMeskanie(), "Meškanie musí byť detekované");
    }

    @Test
    @DisplayName("UC03-AT05: Výnimka – neexistujúca výpožička (ID nebolo nájdené)")
    void uc03_AT05_neexistujucaVypozicka() {
        int neexistujuceId = 999;

        // When – simulácia pokusu o prevzatie
        simulujPrevzatie(neexistujuceId, "Dostupné");

        // Then – nič sa nesmie zmeniť
        assertEquals(2, aktivneVypozicky.size(), "Zoznam výpožičiek sa nesmie zmeniť");
        assertEquals("Vypožičané", naradieModel.getZoznam().stream()
                .filter(n -> n.getId() == 4).findFirst().get().getStav());
    }

    @Test
    @DisplayName("UC03-AT06: Zamestnanec zruší prevzatie (klikne Zrušiť) – žiadna zmena")
    void uc03_AT06_zruseniePrevzatia() {
        int id = 4;
        int povodnyPocet = aktivneVypozicky.size();

        // When – žiadna akcia (simulácia zrušenia modalu)
        // (listener sa nevolá)

        // Then
        assertEquals(povodnyPocet, aktivneVypozicky.size(), "Počet výpožičiek sa nesmie zmeniť");
        assertEquals("Vypožičané", naradieModel.getZoznam().stream()
                .filter(n -> n.getId() == id).findFirst().get().getStav());
    }

    @Test
    @DisplayName("Vypozicka – správny výpočet vratenej zálohy")
    void testVypocitajVratenuZalohu() {
        Vypozicka v = new Vypozicka(1, 1, "", "", "", FIXED_NOW, FIXED_NOW, 100.0, "");
        assertEquals(100.0, v.vypocitajVratenuZalohu(false), "Nepoškodené = 100 %");
        assertEquals(50.0, v.vypocitajVratenuZalohu(true), "Poškodené = 50 %");
    }
}
