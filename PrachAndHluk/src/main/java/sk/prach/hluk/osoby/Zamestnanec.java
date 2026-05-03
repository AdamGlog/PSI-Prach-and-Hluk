package sk.prach.hluk.osoby;

import sk.prach.hluk.modely.Naradie;

/**
 * Zamestnanec 
 */
public class Zamestnanec implements Osoba {

    private int id;
    private String meno;
    private String priezvisko;
    private String email;

    // Konštruktor
    public Zamestnanec(int id, String meno, String priezvisko, String email) {
        this.id = id;
        this.meno = meno;
        this.priezvisko = priezvisko;
        this.email = email;
    }

    // Gettery z interface Osoba
    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getMeno() {
        return meno;
    }

    @Override
    public String getPriezvisko() {
        return priezvisko;
    }

    @Override
    public String getEmail() {
        return email;
    }

    // Špecifické metódy 
    public void vyhladaAlternativu() {
        System.out.println("Zamestnanec " + meno + " vyhľadáva alternatívu náradia.");
    }

    public void evidovanieUkoncenia() {
        System.out.println("Zamestnanec eviduje ukončenie výpožičky.");
    }

    public void tlacZmluvy() {
        System.out.println("Zamestnanec tlačí zmluvu.");
    }

    public void ponechanieZalohy() {
        System.out.println("Zamestnanec ponecháva zálohu.");
    }

    public void ulozenieNaradia(Naradie naradie) {
        System.out.println("Zamestnanec uložil náradie: " + naradie.getNazov());
    }

    public void prevzatieNaradia(Naradie naradie) {
        System.out.println("Zamestnanec prevzal náradie: " + naradie.getNazov());
    }

    public void evidovanieNavratuNaradia(Naradie naradie) {
        System.out.println("Zamestnanec eviduje návrat náradia: " + naradie.getNazov());
    }

    public String kontrolaStavuNaradia(Naradie naradie) {
        return "Stav náradia " + naradie.getNazov() + ": " + naradie.getStav();
    }

    public int prehliadanieDatabazy() {
        System.out.println("Zamestnanec prehliada databázu.");
        return 42; // placeholder
    }

    public void prihlasit(int id, String meno, String priezvisko) {
        System.out.println("Zamestnanec " + meno + " sa prihlásil.");
    }

    public void odhlasit(int id) {
        System.out.println("Zamestnanec s ID " + id + " sa odhlásil.");
    }

    // toString pre debug
    @Override
    public String toString() {
        return "Zamestnanec{" +
                "id=" + id +
                ", meno='" + meno + '\'' +
                ", priezvisko='" + priezvisko + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}