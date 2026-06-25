package com.dignequipe.hardoiz.dettes.paiements;

import com.dignequipe.hardoiz.dettes.paiements.dto.PaiementFournisseurRequestDTO;
import com.dignequipe.hardoiz.dettes.paiements.dto.PaiementFournisseurResponseDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paiements-fournisseurs")
public class PaiementFournisseurControleur {
    private final PaiementFournisseurService service;

    public PaiementFournisseurControleur(PaiementFournisseurService service){
        this.service = service;
    }

    @PostMapping
    public PaiementFournisseurResponseDTO creer(@RequestBody PaiementFournisseurRequestDTO dto){
        return service.creer(dto);
    }

    @GetMapping
    public List<PaiementFournisseurResponseDTO> getAll() {
        return service.getALl();
    }

    @GetMapping("/{id}")
    public PaiementFournisseurResponseDTO getById(@PathVariable Long id){
        return service.getById(id);
    }

    @GetMapping("/dette/{detteId}")
    public List<PaiementFournisseurResponseDTO> getByDetteFournisseur(@PathVariable Long detteId){
        return service.getByDetteFournisseur(detteId);
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public List<PaiementFournisseurResponseDTO> getByUtilisateur(@PathVariable Long utilisateurId){
        return service.getByUtilisateur(utilisateurId);
    }
}
