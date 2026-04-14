package sk.prach.hluk.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Vypozicka - reprezentuje jednu aktivnu vypozicku naradia (UC03).
 * Obsahuje vsetky udaje potrebne pri procese navratenia naradia.
 */
public class Vypozicka {

    // Atributy vypozicky
    private int vypozickaId;
    private int naradieId;
    private String naradieNazov;
    private String zakaznikMeno;
    private String zakaznikTelefon;
    private LocalDateTime datumVypozicania;
    private LocalDateTime datumPlanovanehNavratu;
    private double zaloha;
    private String stavNaradia;       // stav pri vydani
    private String ikonkaNaradia;     // emoji reprezentujuca ikonku náradia

    // Konstruktor - vytvorime novu vypozicku
    public Vypozicka(int vypozickaId, int naradieId, String naradieNazov,
                     String zakaznikMeno, String zakaznikTelefon,
                     LocalDateTime datumVypozicania, LocalDateTime datumPlanovanehNavratu,
                     double zaloha, String ikonkaNaradia) {
        this.vypozickaId          = vypozickaId;
        this.naradieId            = naradieId;
        this.naradieNazov         = naradieNazov;
        this.zakaznikMeno         = zakaznikMeno;
        this.zakaznikTelefon      = zakaznikTelefon;
        this.datumVypozicania     = datumVypozicania;
        this.datumPlanovanehNavratu = datumPlanovanehNavratu;
        this.zaloha               = zaloha;
        this.stavNaradia          = "Vypožičané";
        this.ikonkaNaradia        = ikonkaNaradia;
    }

    // getVypozickaId - vratime ID vypozicky
    public int getVypozickaId() { return vypozickaId; }

    // getNaradieId - vratime ID naradia
    public int getNaradieId() { return naradieId; }

    // getNaradieNazov - vratime nazov naradia
    public String getNaradieNazov() { return naradieNazov; }

    // getZakaznikMeno - vratime meno zakaznika
    public String getZakaznikMeno() { return zakaznikMeno; }

    // getZakaznikTelefon - vratime telefon zakaznika
    public String getZakaznikTelefon() { return zakaznikTelefon; }

    // getDatumVypozicania - vratime datum a cas vypozicania
    public LocalDateTime getDatumVypozicania() { return datumVypozicania; }

    // getDatumPlanovanehNavratu - vratime planovany datum navratu
    public LocalDateTime getDatumPlanovanehNavratu() { return datumPlanovanehNavratu; }

    // getZaloha - vratime vysku zalohy
    public double getZaloha() { return zaloha; }

    // getStavNaradia - vratime aktualny stav naradia
    public String getStavNaradia() { return stavNaradia; }

    // setStavNaradia - nastavime novy stav naradia (vyplni zamestnanec pri navrate)
    public void setStavNaradia(String stavNaradia) { this.stavNaradia = stavNaradia; }

    // getIkonkaNaradia - vratime emoji ikonku naradia
    public String getIkonkaNaradia() { return ikonkaNaradia; }

    // getDlzkaPozicaniaDni - vratime dlzku pozicania v dnoch (od vypozicania po dnes)
    public long getDlzkaPozicaniaDni() {
        return ChronoUnit.DAYS.between(datumVypozicania, LocalDateTime.now());
    }

    // jePoskodene - vratime ci je naradie oznacene ako poskodene
    public boolean jePoskodene() {
        return stavNaradia.equals("Poškodené");
    }

    // jeMeskanie - vratime ci je naradie vrátané po plánovanom dátume
    public boolean jeMeskanie() {
        return LocalDateTime.now().isAfter(datumPlanovanehNavratu);
    }

    // vypocitajVratenuZalohu - vypocitame vysku vratenia zalohy podla stavu
    // Poškodené = strata 50% zálohy, Inak = plná záloha
    public double vypocitajVratenuZalohu(boolean poskodene) {
        if (poskodene) return zaloha * 0.50;
        return zaloha;
    }
}
