package com.dignequipe.hardoiz.ventes;

import com.dignequipe.hardoiz.ventes.dto.VenteRequestDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventes")
@CrossOrigin(origins = "*")
public class VenteControleur {

    private final VenteService venteService;

    public VenteControleur(VenteService venteService) {
        this.venteService = venteService;
    }

    // creation
    @PostMapping
    public Vente creerVente(@RequestBody VenteRequestDTO requestDTO){

        //deleguer la logique metier aau service
        return venteService.creerVente(requestDTO);
    }

    //get ALL
    @GetMapping
    public List<Vente> getAll() {
        return venteService.getAllVentes();
    }

    // get by BOUTIQUE
    @GetMapping("/boutique/{id}")
    public List<Vente> getVenteByBoutique(@PathVariable Long id) {

        return venteService.getVentesByBoutique(id);
    }

    //get par ID
    @GetMapping("/{id}")
    public Vente getById(@PathVariable Long id){
        return venteService.getVenteById(id);
    }

    //supprimer
    @DeleteMapping("/{i}")
    public void supprimer(@PathVariable Long id) {
        venteService.deleteVente(id);
    }
}
