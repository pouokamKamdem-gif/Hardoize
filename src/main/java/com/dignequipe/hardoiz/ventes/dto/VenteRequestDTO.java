package com.dignequipe.hardoiz.ventes.dto;

import com.dignequipe.hardoiz.ventes.LigneVente;
import com.dignequipe.hardoiz.ventes.TypeVente;
import lombok.Data;

import java.util.List;

@Data
public class VenteRequestDTO {
    public Long utilisateurId;
    public Long pointDeVenteId;
    public Long clientId;

    public String typeVente; // CASH ou CREDIT

    public List<LigneVenteDTO> lignes;
}
