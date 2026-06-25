package com.dignequipe.hardoiz.dettes.paiements;

import com.dignequipe.hardoiz.dettes.paiements.dto.PaiementClientRequestDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paiements-clients")
@CrossOrigin(origins = "*")
public class PaiementClientControleur {
    private final PaiementClientService paiementClientService;

    public PaiementClientControleur(PaiementClientService paiementClientService) {
        this.paiementClientService = paiementClientService;
    }

    @PostMapping("/client/{userId}")
    public PaiementClient payerDetteClient(
            @PathVariable Long userId,
            @RequestBody PaiementClientRequestDTO dto
            ){
        return paiementClientService.payerDetteClient(dto, userId);
    }

    @GetMapping("/dette/{id}")
    public List<PaiementClient> getByDette(@PathVariable Long id){
        return paiementClientService.getByDette(id);
    }
}
