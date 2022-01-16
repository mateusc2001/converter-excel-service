package com.extrato.service.service;

import com.extrato.service.model.RegistroExcelModel;
import org.apache.commons.codec.binary.Base64;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class SantanderService {

    public List<RegistroExcelModel> converterSantanderFile(String base64Value) {
        try {
            return lerExcel(getSheet(base64Value), Arrays.asList(0, 2, 3, 4), (byte) 4);
        } catch (Exception err) {
            System.out.println(err.getMessage());
        }
        return new ArrayList<>();
    }

    public List<RegistroExcelModel> converterSicredFile(String base64Value) {
        try {
            return lerExcel(getSheet(base64Value), Arrays.asList(0, 1, 2, 3), (byte) 0);
        } catch (Exception err) {
            System.out.println(err.getMessage());
        }
        return new ArrayList<>();
    }

    public List<RegistroExcelModel> converterOutroBanco(String base64Value) {
        try {
            return lerExcelOutroBanco(getSheet(base64Value));
        } catch (Exception err) {
            System.out.println(err.getMessage());
        }
        return new ArrayList<>();
    }

    public List<RegistroExcelModel> lerExcel(Sheet sheet, List<Integer> indexCollunList, byte startLine) {
        List<RegistroExcelModel> registroExcelModelList = new ArrayList<>();
        sheet.forEach(row -> {
            if (row.getRowNum() >= startLine) {
                RegistroExcelModel registroExcelModel = new RegistroExcelModel();
                row.forEach(cell -> {
                    if (cell.getColumnIndex() == indexCollunList.get(0)) {
                        registroExcelModel.setData(cell.getStringCellValue().trim());
                    } else if (cell.getColumnIndex() == indexCollunList.get(1)) {
                        registroExcelModel.setDescricao(cell.getStringCellValue());
                    } else if (cell.getColumnIndex() == indexCollunList.get(2)) {
                        registroExcelModel.setDocumento(cell.getStringCellValue());
                    } else if (cell.getColumnIndex() == indexCollunList.get(3)) {
                        double valor = getValorFromCell(cell);
                        registroExcelModel.setEntrada(valor > 0);
                        registroExcelModel.setValor(valor);
                        //registroExcelModel.setValor(Math.abs(valor));
                    }
                });
                registroExcelModelList.add(registroExcelModel);
            }
        });
        return registroExcelModelList;
    }

    public List<RegistroExcelModel> lerExcelOutroBanco(Sheet sheet) {
        AtomicBoolean continuar = new AtomicBoolean(true);
        List<RegistroExcelModel> registroExcelModelList = new ArrayList<>();
        sheet.forEach(row -> {
            RegistroExcelModel registroExcelModel = new RegistroExcelModel();
            row.forEach(cell -> {
                if (continuar.get() && cell.getColumnIndex() == 0 && cell.getStringCellValue().toUpperCase().contains("lançamentos".toUpperCase())) {
                    continuar.set(false);
                }
                if (continuar.get()) {
                    if (cell.getColumnIndex() == 0) {
                        registroExcelModel.setData(cell.getStringCellValue().trim());
                    } else if (cell.getColumnIndex() == 1) {
                        registroExcelModel.setDescricao(cell.getStringCellValue().trim());
                    } else if (cell.getColumnIndex() == 2) {
                        registroExcelModel.setDocumento(cell.getStringCellValue().trim());
                    } else if (cell.getColumnIndex() == 3) {
                        double valor = getValorFromCell(cell);
                        registroExcelModel.setEntrada(valor > 0);
                        //registroExcelModel.setValor(Math.abs(valor));
                        registroExcelModel.setValor(valor);
                    }
                }
            });
            if (continuar.get()) registroExcelModelList.add(registroExcelModel);
        });
        return registroExcelModelList;
    }

    public double getValorFromCell(Cell cell) {
        switch (cell.getCellType()) {
            case 0:
                return cell.getNumericCellValue();
            case 1:
                return cell.getStringCellValue().isEmpty() ? 0 : Double.parseDouble(cell.getStringCellValue().replace(".", "").replace(",", "."));
            default:
                return 0;
        }
    }

    public Sheet getSheet(String base64Value) {
        try {
            byte[] byteArray = Base64.decodeBase64(base64Value);
            InputStream inputStream = new ByteArrayInputStream(byteArray);
            Workbook workbook = WorkbookFactory.create(inputStream);
            return workbook.getSheetAt(0);
        } catch (Exception err) {
            return null;
        }
    }

    public Workbook getWorkbook(InputStream inputStream) {
        try {
            String inputFilename = "";

            switch (inputFilename.substring(inputFilename.lastIndexOf(".") + 1, inputFilename.length())) {
                case "xls":
                    return new HSSFWorkbook(inputStream);
                case "xlsx":
                    return new XSSFWorkbook(inputStream);
                default:
                    return null;
            }
        } catch (Exception err) {

        }
        return null;
    }
}