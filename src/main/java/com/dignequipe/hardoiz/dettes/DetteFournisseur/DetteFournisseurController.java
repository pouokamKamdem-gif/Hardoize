package com.dignequipe.hardoiz.dettes.DetteFournisseur;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public class DetteFournisseurController {

    private final DetteFournisseurService detteFournisseurService;

    public DetteFournisseurController(DetteFournisseurService detteFournisseurService) {
        this.detteFournisseurService = detteFournisseurService;
    }

    //creer dette fournisseur
    @PostMapping("/{fournisseurId}")
    public DetteFournisseur creer(@PathVariable Long fournisseurId,
                                  @RequestParam Double montant) {
        return detteFournisseurService.creerDette(fournisseurId, montant);
    }

    //dettes fournisseur
    @GetMapping("/{fournisseur}")
    public List<DetteFournisseur> getByFournisseur(@PathVariable Long fournisseurId) {
        return detteFournisseurService.getByFournisseur(fournisseurId);
    }

    //paiement dette fournisseur
    @PostMapping("/payer/{detteId}")
    public DetteFournisseur payer(@PathVariable Long detteId,
                                  @RequestParam Double montant) {
        return detteFournisseurService.reduireDette(detteId, montant);
    }
}
