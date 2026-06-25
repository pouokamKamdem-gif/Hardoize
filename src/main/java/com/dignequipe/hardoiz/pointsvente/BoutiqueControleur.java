package com.dignequipe.hardoiz.pointsvente;

import com.dignequipe.hardoiz.utilisateurs.Utilisateur;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boutiques")
public class BoutiqueControleur {

    private final BoutiqueService boutiqueService;

    public BoutiqueControleur(BoutiqueService boutiqueService) {
        this.boutiqueService = boutiqueService;
    }

    @PostMapping("/creer")
    public PointDeVente creerBoutique(@RequestBody PointDeVente boutique, @RequestParam Long utilisateurId){
        Utilisateur proprietaire = new Utilisateur();
        proprietaire.setId(utilisateurId);

        return boutiqueService.creerBoutique(boutique, proprietaire);
    }
    @GetMapping("/utilisateur/{id}")
    public List<PointDeVente> boutiqueUtilisateur(@PathVariable Long id) {
        return boutiqueService.boutiqueUtilisateur(id);
    }

    @GetMapping
    public List<PointDeVente> getAllBoutique() {
        return boutiqueService.getAll();
    }
}
