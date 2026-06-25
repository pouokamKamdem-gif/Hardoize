package com.dignequipe.hardoiz.produits.fourniisseurs.achats;

import com.dignequipe.hardoiz.produits.fourniisseurs.achats.dto.AchatRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/achats")
@RequiredArgsConstructor
public class AchatControleur {
    private final AchatService service;

    @PostMapping
    public Achat creer(@RequestBody AchatRequestDTO dto){
        return service.creer(dto);
    }

    @GetMapping
    public List<Achat> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Achat getBxId(@PathVariable Long id){
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        service.delete(id);
    }
}
