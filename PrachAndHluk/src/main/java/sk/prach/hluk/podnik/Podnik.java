package sk.prach.hluk.podnik;

/**
 * Podnik 
 */
public class Podnik {

    private String lokalita;

    // Konštruktor
    public Podnik(String lokalita) {
        this.lokalita = lokalita;
    }

    // Prázdny konštruktor
    public Podnik() {
    }

    // Getter
    public String getLokalita() {
        return lokalita;
    }

    // Setter
    public void setLokalita(String lokalita) {
        this.lokalita = lokalita;
    }

    // Metóda na pridanie zakaznika do podniku
    public void zaevidovanieZakaznika() {
        System.out.println("Podnik zaevidoval nového zákazníka na lokalite: " + lokalita);
    }

    @Override
    public String toString() {
        return "Podnik{" +
                "lokalita='" + lokalita + '\'' +
                '}';
    }
}