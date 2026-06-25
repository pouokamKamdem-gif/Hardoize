package com.dignequipe.hardoiz.produits.dto;

import java.time.LocalDateTime;

public class ProduitResponseDTO {
    public Long id;
    public String nom;
    public String code;

    public double prixVente;
    public double prixAchat;

    public int stock;
    public int seuilAlerte;

    public boolean actif;

    public LocalDateTime dateCreation;
}
