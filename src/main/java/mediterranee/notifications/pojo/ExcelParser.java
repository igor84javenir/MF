package mediterranee.notifications.pojo;

import mediterranee.notifications.model.RDV;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static mediterranee.notifications.controller.MainController.WORKING_DIR;

public class ExcelParser {

    public List<RDV> getParsedFiles() throws IOException, InvalidFormatException {
        final File folder = new File(WORKING_DIR);
        List<File> files = getFilesInFolder(folder);
        List<Workbook> workbooks = generateWorkbooksFromFiles(files);
        List<Sheet> sheets = getSheetsFromWorkbooks(workbooks);
        List<Row> rows = getRowsFromSheets(sheets);
        List<RDV> rdvs = getRDVs(rows);

        System.out.println(rdvs);

        return rdvs;
    }

    private List<RDV> getRDVs(List<Row> rows) {
        List<RDV> rdvs = new ArrayList<>();

        for (Row row : rows) {
            RDV rdv = new RDV();

            if (row.getRowNum() == 0) {
                continue;
            }

            DataFormatter df = new DataFormatter();
            String cellAsString = "";

            for (Cell cell : row) {
                addCellToRDV(rdv, cell, cellAsString, df);
            }
            rdvs.add(rdv);
        }

        return rdvs;
    }

    private void addCellToRDV(RDV rdv, Cell cell, String cellAsString, DataFormatter df) {
        switch (cell.getAddress().toString().charAt(0)) {
            case 'A':
                if (cell.getCellType().toString().equals("STRING")) {
                    rdv.setFirstName(cell.getStringCellValue());
                    break;
                } else {
                    throw new ClassCastException("Wrong data type/format in cell " + cell.getAddress());
                }

            case 'B' :
                if (cell.getCellType().toString().equals("STRING")) {
                    rdv.setLastName(cell.getStringCellValue());
                    break;
                } else {
                    throw new ClassCastException("Wrong data type/format in cell " + cell.getAddress());
                }

            case 'C' :
                if (cell.getCellType().toString().equals("STRING")) {
                    rdv.setAddress1(cell.getStringCellValue());
                    break;
                } else {
                    throw new ClassCastException("Wrong data type/format in cell " + cell.getAddress());
                }

            case 'D' :
                if (cell.getCellType().toString().equals("NUMERIC")) {
                    cellAsString = df.formatCellValue(cell);
                    rdv.setAddress2(cellAsString);
                    break;
                } else if ((cell.getCellType().toString().equals("STRING"))) {
                    rdv.setAddress2(cell.getStringCellValue());
                    break;
                } else {
                    throw new ClassCastException("Wrong data type/format in cell " + cell.getAddress());
                }

            case 'E' : if (cell.getCellType().toString().equals("STRING")) {
                rdv.setAddress3(cell.toString());
                break;
            } else {
                throw new ClassCastException("Wrong data type/format in cell " + cell.getAddress());
            }

            case 'F' :
                if (cell.getCellType().toString().equals("NUMERIC")) {
                    cellAsString = df.formatCellValue(cell);
                    if (cellAsString.length() == 10) {
                        rdv.setPhoneNumber(cellAsString);
                        break;
                    } else {
                        throw new ClassCastException("Wrong data in cell " + cell.getAddress());
                    }
                } else if (cell.getCellType().toString().equals("STRING") && cell.toString().contains(".")) {
                    cellAsString = cell.getStringCellValue();
                    cellAsString = cellAsString.replaceAll("\\.", "");
                    rdv.setPhoneNumber(cellAsString);
                    break;
                } else if (cell.getCellType().toString().equals("STRING") && cell.toString().contains(" ")) {
                    cellAsString = cell.getStringCellValue();
                    cellAsString = cellAsString.replaceAll(" ", "");
                    rdv.setPhoneNumber(cellAsString);
                    break;
                } else {
                    throw new ClassCastException("Wrong data type/format in cell " + cell.getAddress());
                }

            case 'G' : if (cell.getCellType().toString().equals("STRING") && cell.getStringCellValue().contains("@")) {
                rdv.setMail(cell.getStringCellValue());
                break;
            } else {
                throw new ClassCastException("Wrong data type/format in cell " + cell.getAddress());
            }

            case 'H' :
                if (cell.getCellType().toString().equals("STRING") && cell.getStringCellValue().contains("/")) {
                    rdv.setRDVDate(cell.toString());
                    break;
                } else {
                    throw new ClassCastException("Wrong data type/format in cell " + cell.getAddress());
                }

            case 'I' : if (cell.getCellType().toString().equals("STRING") && cell.getStringCellValue().contains("h")) {
                rdv.setRDVTime(cell.toString());
                break;
            } else {
                throw new ClassCastException("Wrong data type/format in cell " + cell.getAddress());
            }

            case 'J' : if (cell.getCellType().toString().equals("STRING")) {
                rdv.setRDVLocation(cell.toString());
                break;
            } else {
                throw new ClassCastException("Wrong data type/format in cell " + cell.getAddress());
            }
        }
    }

    private List<Row> getRowsFromSheets(List<Sheet> sheets) throws FileNotFoundException {
        List<Row> rows = new ArrayList<>();

        for (Sheet sheet : sheets) {
            for (Row row : sheet) {
                rows.add(row);
            }
        }

        if (rows.isEmpty()) {
            throw new FileNotFoundException("No rows found in sheet(s) " + sheets);
        }

        return rows;
    }

    private List<Sheet> getSheetsFromWorkbooks(List<Workbook> workbooks) throws FileNotFoundException {
        List<Sheet> sheets = new ArrayList<>();

        for (Workbook workbook : workbooks) {
            sheets.add(workbook.getSheetAt(0));
        }

        if (sheets.isEmpty()) {
            throw new FileNotFoundException("No sheets found in workbook(s) " + workbooks);
        }

        System.out.println("Found sheets are : " + sheets);

        return sheets;
    }

    private List<Workbook> generateWorkbooksFromFiles(List<File> files) throws IOException, InvalidFormatException {
        List<Workbook> workbooks = new ArrayList<>();
        for (File file : files) {
            Workbook workbook = new XSSFWorkbook(file);
            workbooks.add(workbook);
        }

        if (workbooks.isEmpty()) {
            throw new FileNotFoundException("No workbooks found in file(s) " + files);
        }

        System.out.println("Found workbooks are : " + workbooks);

        return workbooks;
    }

    private List<File> getFilesInFolder(final File folder) throws FileNotFoundException {
        List<File> files = new ArrayList<>();
        for (final File fileEntry : folder.listFiles()) {
            if (!fileEntry.isDirectory() && fileEntry.getName().endsWith(".xlsx")) {
                files.add(fileEntry);
            }
        }

        if (files.isEmpty()) {
            throw new FileNotFoundException("No .xlsx files found at path " + Path.of(WORKING_DIR).toAbsolutePath());
        }

        System.out.println("Found files are : " + files);

        return files;
    }


}
