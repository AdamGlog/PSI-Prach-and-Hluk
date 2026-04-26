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
 * UC02 Acceptance Tests (Akceptačné testy pre Vypožičaj náradie)
 */
class UC2AccTest {

    private NaradieZoznam naradieModel;
    private List<Vypozicka> aktivneVypozicky;

    @BeforeEach
    void setUp() {
        naradieModel = new NaradieZoznam();
        naradieModel.pridatNaradie(new Naradie(5, "Uhlovka Makita",      "Dostupné",    2, 0));
        naradieModel.pridatNaradie(new Naradie(6, "Jadrový vrták 100mm", "V servise",  12, 1));
        naradieModel.pridatNaradie(new Naradie(4, "Vŕtačka Bosch",      "Vypožičané", 16, 2));

        aktivneVypozicky = new ArrayList<>();
    }

    // Pomocná metóda simulujúca logiku confirmCallback v VypozickaController
    private void simulujVydaj(int id, String typ, String meno, String telefon, double zaloha, String projekt) {
        Naradie n = naradieModel.getZoznam().stream()
                .filter(nar -> nar.getId() == id)
                .findFirst()
                .orElse(null);
        
        if (n == null || !n.getStav().equals("Dostupné")) {
            return; // Simulácia neúspechu (výnimky)
        }

        if (typ.equals("Externý")) {
            naradieModel.zmenStav(id, "Vypožičané");
            Vypozicka v = new Vypozicka(
                1000 + aktivneVypozicky.size() + 1,
                n.getId(), n.getNazov(),
                meno, telefon,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7),
                zaloha,
                "🔧"
            );
            aktivneVypozicky.add(v);
        } else {
            naradieModel.zmenStav(id, "Na internej zákazke");
        }
    }

    @Test
    @DisplayName("UC02-AT01: Úspešný externý nájom náradia (hlavný scenár)")
    void uc02_AT01_uspesnyExternyNajom() {
        // Given
        int id = 5; // Uhlovka Makita (Dostupné)
        String meno = "Jozef Mrkva";
        double zaloha = 60.0;

        // When
        simulujVydaj(id, "Externý", meno, "0900111222", zaloha, "");

        // Then
        Naradie n = naradieModel.getZoznam().stream().filter(x -> x.getId() == id).findFirst().get();
        assertEquals("Vypožičané", n.getStav(), "Stav náradia musí byť Vypožičané");
        assertEquals(3, n.getVypozicaneCount(), "Počet vypožičaní sa musí zvýšiť");
        
        assertEquals(1, aktivneVypozicky.size(), "Vypožička musí byť pridaná do zoznamu");
        assertEquals(meno, aktivneVypozicky.get(0).getZakaznikMeno());
        assertEquals(zaloha, aktivneVypozicky.get(0).getZaloha());
    }

    @Test
    @DisplayName("UC02-AT02: Úspešná interná réžia (alternatívny scenár)")
    void uc02_AT02_uspesnaInternaRezia() {
        // Given
        int id = 5;
        String projekt = "Stavba A";

        // When
        simulujVydaj(id, "Interný", "", "", 0, projekt);

        // Then
        Naradie n = naradieModel.getZoznam().stream().filter(x -> x.getId() == id).findFirst().get();
        assertEquals("Na internej zákazke", n.getStav());
        assertEquals(0, aktivneVypozicky.size(), "Interná réžia sa nepridáva do zoznamu pre externý návrat");
    }

    @Test
    @DisplayName("UC02-AT03: Výnimka - náradie nie je dostupné (V servise)")
    void uc02_AT03_naradieVServise() {
        // Given
        int id = 6; // Jadrový vrták (V servise)

        // When
        simulujVydaj(id, "Externý", "Test", "123", 50, "");

        // Then
        Naradie n = naradieModel.getZoznam().stream().filter(x -> x.getId() == id).findFirst().get();
        assertEquals("V servise", n.getStav(), "Stav sa nesmie zmeniť");
        assertEquals(0, aktivneVypozicky.size());
    }

    @Test
    @DisplayName("UC02-AT04: Výnimka - náradie nie je dostupné (Vypožičané)")
    void uc02_AT04_naradieUzVypozicane() {
        // Given
        int id = 4; // Vŕtačka Bosch (Vypožičané)

        // When
        simulujVydaj(id, "Externý", "Test", "123", 50, "");

        // Then
        Naradie n = naradieModel.getZoznam().stream().filter(x -> x.getId() == id).findFirst().get();
        assertEquals("Vypožičané", n.getStav());
        assertEquals(0, aktivneVypozicky.size());
    }

    @Test
    @DisplayName("UC02-AT05: Výnimka - neexistujúce ID")
    void uc02_AT05_neexistujuceId() {
        // When
        simulujVydaj(999, "Externý", "Test", "123", 50, "");

        // Then
        assertEquals(0, aktivneVypozicky.size());
    }
}
