package sk.prach.hluk.controller;

import sk.prach.hluk.model.Naradie;
import sk.prach.hluk.model.NaradieZoznam;
import sk.prach.hluk.view.NaradieUI;

public class NaradieController {

    // Model a View - hlavne komponenty MVC
    private NaradieZoznam model;
    private NaradieUI view;

    // Konstruktor - prepojime model a view cez controller
    public NaradieController(NaradieZoznam model, NaradieUI view) {
        this.model = model;
        this.view  = view;
        initView();
        initListeners();
    }

    // initView - naplnime view pociatocnymi datami
    private void initView() {
        view.zobrazZoznamNaradia(model.getZoznam());
    }

    // initListeners - zaregistrujeme listenery pre sort a edit akcie
    private void initListeners() {

        // setSortListener - zoradime zoznam podla kriterialneho znaku a smeru
        view.setSortListener((kriterium, vzostupne) -> {
            var zoradeny = model.sortZoznamBy(kriterium, vzostupne);
            view.zobrazZoradenyZoznam(zoradeny);
        });

        // setEditStavListener - spracujeme zmenu stavu naradia z edit modalu
        view.setEditStavListener((id, novyStav) -> {
            if (novyStav.equals("Vyradené")) {
                // Vyradenie - zavolame delete() na naradii a updateZoznam na zozname
                model.getZoznam().stream()
                        .filter(n -> n.getId() == id)
                        .findFirst()
                        .ifPresent(Naradie::delete);
                model.updateZoznam();
            } else {
                // Zmena stavu - pokusime sa zmenit stav, spracujeme vynimku
                boolean uspech = model.zmenStav(id, novyStav);
                if (!uspech) {
                    view.zobrazSpravu("Náradie je Vypožičané. Zmena na 'V servise' nie je možná.");
                    return;
                }
            }
            view.zobrazZoznamNaradia(model.getZoznam());
        });
    }
}