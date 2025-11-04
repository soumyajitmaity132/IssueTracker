package com.example.demo.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import com.example.demo.Entity.Employee;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelHelper {

    /**
     * Parse Excel (.xlsx) file to Employee list
     */
    public static List<Employee> excelToEmployees(InputStream is) throws IOException {
        List<Employee> employees = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            int rowNumber = 0;

            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // Skip header row
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Employee emp = new Employee();

                // Column Mapping:
                // EMPLOYEE ID | FULL NAME | Email Address | Password | Department | Reporting Manager Id | ROLE

                Cell empIdCell = currentRow.getCell(0);
                if (empIdCell != null) emp.setEmpId(getCellValueAsString(empIdCell));

                Cell nameCell = currentRow.getCell(1);
                if (nameCell != null) emp.setName(getCellValueAsString(nameCell));

                Cell emailCell = currentRow.getCell(2);
                if (emailCell != null) emp.setEmail(getCellValueAsString(emailCell));

                Cell passwordCell = currentRow.getCell(3);
                if (passwordCell != null) emp.setPassword(getCellValueAsString(passwordCell));

                Cell deptCell = currentRow.getCell(4);
                if (deptCell != null) emp.setDepartment(getCellValueAsString(deptCell));

                Cell managerCell = currentRow.getCell(5);
                if (managerCell != null) emp.setReportingManager(getCellValueAsString(managerCell));

                Cell roleCell = currentRow.getCell(6);
                if (roleCell != null) emp.setRole(getCellValueAsString(roleCell));

                employees.add(emp);
            }
        }
        return employees;
    }

    /**
     * Parse CSV (from Google Sheet export) to Employee list
     */
    public static List<Employee> csvToEmployees(BufferedReader reader) {
    List<Employee> employees = new ArrayList<>();
    try (CSVParser csvParser = new CSVParser(reader, 
            CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {
        
        for (CSVRecord record : csvParser) {
            Employee emp = new Employee(
                record.get("EMPLOYEE ID"),          // 0
                record.get("FULL NAME"),            // 1
                record.get("Email Address"),        // 2
                record.get("Department"),           // 4
                record.get("Reporting Manager Id"), // 5
                record.get("ROLE"),                 // 6
                record.get("Password")              // 3
            );
            employees.add(emp);
        }
    } catch (IOException e) {
        throw new RuntimeException("Failed to parse CSV: " + e.getMessage());
    }
    return employees;
}



    /**
     * Convert Excel cell value to String safely
     */
    private static String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue()); // avoids decimals
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }
}
