package com.dignequipe.hardoiz.ventes.dto;

import com.dignequipe.hardoiz.produits.Produit;
import lombok.Data;

@Data
public class LigneVenteDTO {
    public Long produitId;
    public int quantite;
    public double prixUnitaire;
}
