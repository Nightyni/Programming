package com.addressbook.service;

import com.addressbook.model.Contact;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class DataExporter {
    public static void exportToExcel(List<Contact> contacts, String filePath) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Contacts");

            // 创建表头
            Row headerRow = sheet.createRow(0);
            createCell(headerRow, 0, "姓名", workbook);
            createCell(headerRow, 1, "电话", workbook);
            createCell(headerRow, 2, "邮箱", workbook);

            // 填充数据
            int rowNum = 1;
            for (Contact contact : contacts) {
                Row row = sheet.createRow(rowNum++);
                createCell(row, 0, contact.getName(), workbook);
                createCell(row, 1, contact.getPhone(), workbook);
                createCell(row, 2, contact.getEmail(), workbook);
            }

            // 写入文件
            try (FileOutputStream outputStream = new FileOutputStream(filePath)) {
                workbook.write(outputStream);
            }
        }
    }

    private static void createCell(Row row, int column, String value, Workbook workbook) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value);
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        cell.setCellStyle(style);
    }
}
