package com.dignequipe.hardoiz.dettes.DetteClient;

import org.springframework.web.bind.annotation.*;

import java.util.List;

public class DetteClientControleur {
    private final DetteClientService service;

    public DetteClientControleur(DetteClientService service){
        this.service = service;
    }

    @GetMapping
    public List<DetteClient> getAll(){
        return service.getAll();
    }

    @GetMapping("/{id}")
    public DetteClient getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/client/{clientId}")
    public List<DetteClient> getByClient(@PathVariable Long clientId) {
        return service.getByClient(clientId);
    }

    @PutMapping("/{id}")
    public DetteClient update(@PathVariable Long id, @RequestBody DetteClient dto) {
        return service.update(id,dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
