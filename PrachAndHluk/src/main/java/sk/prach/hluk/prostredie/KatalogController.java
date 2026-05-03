package sk.prach.hluk.prostredie;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import sk.prach.hluk.modely.Naradie;

public class KatalogController {

    private NaradieZoznam naradieModel;
    private KatalogUI katalogUI;

    public KatalogController(NaradieZoznam naradieModel, KatalogUI katalogUI) {
        this.naradieModel = naradieModel;
        this.katalogUI = katalogUI;

        initController();
    }

    public void initController() {
        // Ziskanie vsetkych nastrojov
        List<Naradie> vsetky = naradieModel.getZoznam();

        // Zoskupenie naradia podla kategorie
        Map<String, List<Naradie>> kategorieMap = vsetky.stream()
            .collect(Collectors.groupingBy(Naradie::getKategoria));

        // Odoslanie dat do UI pre zobrazenie
        katalogUI.zobrazKatalog(kategorieMap);
    }
}
