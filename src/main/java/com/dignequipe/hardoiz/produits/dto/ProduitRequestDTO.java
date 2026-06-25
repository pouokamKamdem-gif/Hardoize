package com.dignequipe.hardoiz.produits.dto;


import java.util.List;

public class ProduitRequestDTO {
    public String nom;
    public String code;

    public double prixVente;
    public double prixAchat;

    public int stock;
    public int seuilAlerte;

    public Long pointDeVenteId;

}
