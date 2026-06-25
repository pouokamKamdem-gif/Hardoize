package com.dignequipe.hardoiz.produits;

import com.dignequipe.hardoiz.produits.dto.ProduitRequestDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produits")
public class ProduitControleur {

    private final ProduitService produitService;

    public ProduitControleur(ProduitService produitService) {
        this.produitService = produitService;
    }

    //controlleur de Creation
    @PostMapping
    public Produit creer(@RequestBody ProduitRequestDTO dto) {
        return produitService.creerProduit(dto);
    }

    //controlleur de Lecture
    @GetMapping("/boutique/{id}")
    public List<Produit> lister(@PathVariable Long id){
        return produitService.lister(id);
    }

    // // par ID
        public Produit getByid(@PathVariable Long id, @RequestBody ProduitRequestDTO dto){
            return produitService.modifier(id, dto);
        }

    // controlleur de Modification
    @PutMapping("/{id}")
    public Produit modifier(@PathVariable Long id,
                            @RequestBody ProduitRequestDTO dto){
        return produitService.modifier(id, dto);
    }

    //controlleur de Desactivation
    @DeleteMapping("/{id}")
    public void desactiver(@PathVariable Long id) {
        produitService.desactiver(id);
    }
}
