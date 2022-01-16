package com.extrato.service.model;

import lombok.Data;

@Data
public class RegistroExcelModel {
    private String data;
    private String descricao;
    private String documento;
    private double valor;
    private boolean entrada;
}
