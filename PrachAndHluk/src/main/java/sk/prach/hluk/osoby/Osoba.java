package sk.prach.hluk.osoby;

/**
 * Interface pre všetky osoby v systéme (Zamestnanec aj Zákazník)
 */
public interface Osoba {

    int getId();
    String getMeno();
    String getPriezvisko();
    String getEmail();

}