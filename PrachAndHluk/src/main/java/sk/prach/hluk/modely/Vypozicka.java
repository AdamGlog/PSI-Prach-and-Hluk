package sk.prach.hluk.modely;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Vypozicka - reprezentuje jednu aktivnu vypozicku naradia (UC03).
 * Obsahuje vsetky udaje potrebne pri procese navratenia naradia.
 */
public class Vypozicka implements Model{

    // Atributy vypozicky
    private int vypozickaId;
    private int naradieId;
    private String naradieNazov;
    private String zakaznikMeno;
    private String zakaznikTelefon;
    private LocalDateTime datumVypozicania;
    private LocalDateTime datumPlanovanehNavratu;
    private double zaloha;
    private String stav;       // stav pri vydani
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
        this.stav          = "Vypožičané";
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

    // getStav - vratime aktualny stav naradia
    @Override
    public String getStav() { return stav; }

    // setStav - nastavime novy stav naradia (vyplni zamestnanec pri navrate)
    @Override
    public void setStav(String stav) { this.stav = stav; }

    // delete - vymaže všetky atribúty (nastaví na null / default hodnoty)
    @Override
    public void delete() { 
    this.vypozickaId = 0;
    this.naradieId = 0;
    this.naradieNazov = null;
    this.zakaznikMeno = null;
    this.zakaznikTelefon = null;
    this.datumVypozicania = null;
    this.datumPlanovanehNavratu = null;
    this.zaloha = 0.0;
    this.stav = null;
    this.ikonkaNaradia = null;
    }

    // getIkonkaNaradia - vratime emoji ikonku naradia
    public String getIkonkaNaradia() { return ikonkaNaradia; }

    // getDlzkaPozicaniaDni - vratime dlzku pozicania v dnoch (od vypozicania po dnes)
    public long getDlzkaPozicaniaDni() {
        return ChronoUnit.DAYS.between(datumVypozicania, LocalDateTime.now());
    }

    // jePoskodene - vratime ci je naradie oznacene ako poskodene
    public boolean jePoskodene() {
        return stav.equals("Poškodené");
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
