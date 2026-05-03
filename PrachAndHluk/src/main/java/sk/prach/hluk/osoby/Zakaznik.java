package sk.prach.hluk.osoby;

import sk.prach.hluk.modely.Naradie;

/**
 * Zákazník - podľa class diagramu v EA
 */
public class Zakaznik implements Osoba {

    private int id;
    private String meno;
    private String priezvisko;
    private String email;

    // Konštruktor
    public Zakaznik(int id, String meno, String priezvisko, String email) {
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
    public void prevezmeZmluvu() {
        System.out.println("Zákazník " + meno + " preberá zmluvu.");
    }

    public void odovzdanieNaradia(Naradie naradie) {
        System.out.println("Zákazník odovzdal náradie: " + naradie.getNazov());
    }

    public void podpisanieZmluvy() {
        System.out.println("Zákazník podpísal zmluvu.");
    }

    public void vstupDoPodniku() {
        System.out.println("Zákazník vstúpil do podniku.");
    }

    public void odcudzenieNaradia(Naradie naradie) {
        System.out.println("Zákazník nahlásil odcudzenie náradia: " + naradie.getNazov());
    }

    // toString pre debug
    @Override
    public String toString() {
        return "Zakaznik{" +
                "id=" + id +
                ", meno='" + meno + '\'' +
                ", priezvisko='" + priezvisko + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}