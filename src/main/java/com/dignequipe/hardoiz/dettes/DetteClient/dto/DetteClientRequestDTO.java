package com.dignequipe.hardoiz.dettes.DetteClient.dto;

import lombok.Data;

@Data
public class DetteClientRequestDTO {

    private Long ClientId;

    private Long utilisateurId;

    private Double montantInitial;
}
