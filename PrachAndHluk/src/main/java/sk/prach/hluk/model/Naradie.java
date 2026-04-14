package sk.prach.hluk.model;

public class Naradie {

    // Atributy triedy Naradie podla class diagramu
    private int id;
    private String nazov;
    private String stav;
    private int vypozicaneCount;
    private int servisovaneCount;

    // Konstruktor - vytvorime novy kus naradia
    public Naradie(int id, String nazov, String stav, int vypozicaneCount, int servisovaneCount) {
        this.id = id;
        this.nazov = nazov;
        this.stav = stav;
        this.vypozicaneCount = vypozicaneCount;
        this.servisovaneCount = servisovaneCount;
    }

    // getId - vratime ID naradia
    public int getId() { return id; }

    // getNazov - vratime nazov naradia
    public String getNazov() { return nazov; }

    // getStav - vratime aktualny stav naradia
    public String getStav() { return stav; }

    // setStav - nastavime novy stav naradia
    public void setStav(String stav) { this.stav = stav; }

    // delete - oznacime naradie ako vyradene, fyzicke odstranenie robi NaradieZoznam
    public void delete() { this.stav = "Vyradené"; }

    // getVypozicaneCount - vratime pocet vypozicani naradia
    public int getVypozicaneCount() { return vypozicaneCount; }

    // getServisovaneCount - vratime pocet servisovani naradia
    public int getServisovaneCount() { return servisovaneCount; }

    // setVypozicaneCount - urcim novy pocet vypozicani naradia
    public void setVypozicaneCount(int newVypozicaneCount) { 
        this.vypozicaneCount = newVypozicaneCount; 
    }

    // setServisovaneCount - urcim novy pocet servisovani naradia
    public void setServisovaneCount(int newServisovaneCount) { 
        this.servisovaneCount = newServisovaneCount; 
    }
}