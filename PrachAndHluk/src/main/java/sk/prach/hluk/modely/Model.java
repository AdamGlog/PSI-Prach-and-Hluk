package sk.prach.hluk.modely;

/**
 * Generický interface pre všetky "modelové" zoznamy / kolekcie entít
 * (Náradie a Výpozicky)
 */
public interface Model<T> {

    void delete();
    String getStav();
    void setStav(String s);

}