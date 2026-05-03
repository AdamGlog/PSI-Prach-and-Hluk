package sk.prach.hluk.modely;

import java.time.LocalDate;

/**
 * Zmluva - predstavuje zmluvu medzi zákazníkom a podnikom
 */
public class Zmluva {

    // Atributy podľa diagramu
    private int id;
    private LocalDate datum;
    private String obsah;
    private int zaloha;
    private int kvantita;
    private String stav;

    // Konštruktor (povinné parametre)
    public Zmluva(int id, LocalDate datum, String obsah, int zaloha, int kvantita, String stav) {
        this.id = id;
        this.datum = datum;
        this.obsah = obsah;
        this.zaloha = zaloha;
        this.kvantita = kvantita;
        this.stav = stav;
    }

    // Prázdny konštruktor 
    public Zmluva() {
    }

    // Gettery
    public int getId() {
        return id;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public String getObsah() {
        return obsah;
    }

    public int getZaloha() {
        return zaloha;
    }

    public int getKvantita() {
        return kvantita;
    }

    public String getStav() {
        return stav;
    }

    // Settery
    public void setId(int id) {
        this.id = id;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }

    public void setObsah(String obsah) {
        this.obsah = obsah;
    }

    public void setZaloha(int zaloha) {
        this.zaloha = zaloha;
    }

    public void setKvantita(int kvantita) {
        this.kvantita = kvantita;
    }

    public void setStav(String stav) {
        this.stav = stav;
    }

    // Hlavná metóda
    // generate() - vygeneruje alebo finalizuje zmluvu
    public void generate() {
        if (this.datum == null) {
            this.datum = LocalDate.now();
        }
        if (this.stav == null || this.stav.isEmpty()) {
            this.stav = "Aktívna";
        }
        System.out.println("Zmluva " + id + " bola vygenerovaná.");
    }

    // toString pre pohodlné debugovanie
    @Override
    public String toString() {
        return "Zmluva{" +
                "id=" + id +
                ", datum=" + datum +
                ", obsah='" + obsah + '\'' +
                ", zaloha=" + zaloha +
                ", kvantita=" + kvantita +
                ", stav='" + stav + '\'' +
                '}';
    }
}