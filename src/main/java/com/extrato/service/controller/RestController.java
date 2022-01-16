package com.extrato.service.controller;


import com.extrato.service.model.RegistroExcelModel;
import com.extrato.service.service.SantanderService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @Autowired
    private SantanderService santanderService;


    @PostMapping("/santander/base")
    @CrossOrigin(origins = "http://localhost:4200")
    List<RegistroExcelModel> converterSantanderFile(@RequestBody SantanderRequest santanderRequest) {
        return santanderService.converterSantanderFile(santanderRequest.getBase64());
    }

    @PostMapping("/sicredi/base")
    @CrossOrigin(origins = "http://localhost:4200")
    List<RegistroExcelModel> converterSicredFile(@RequestBody SantanderRequest santanderRequest) {
        return santanderService.converterSicredFile(santanderRequest.getBase64());
    }

    @PostMapping("/outro-banco/base")
    @CrossOrigin(origins = "http://localhost:4200")
    List<RegistroExcelModel> converterTerceiroBancoFile(@RequestBody SantanderRequest santanderRequest) {
        return santanderService.converterOutroBanco(santanderRequest.getBase64());
    }

    @GetMapping("/teste-origem")
    @CrossOrigin(origins = "http://192.168.1.102:4200")
    String teste() {
        return "santanderService.converterOutroBanco(santanderRequest.getBase64())";
    }
}

@Data
class SantanderRequest {
    private String base64;
}
