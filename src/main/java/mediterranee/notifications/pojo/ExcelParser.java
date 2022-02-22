package mediterranee.notifications.pojo;

import mediterranee.notifications.model.RDV;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static mediterranee.notifications.controller.MainController.WORKING_DIR;

public class ExcelParser {

    public List<RDV> getParsedFiles() throws IOException, InvalidFormatException {
        final File folder = new File(WORKING_DIR);
        List<File> files = getFilesInFolder(folder);
        List<Workbook> workbooks = generateWorkbooksFromFiles(files);
        List<Sheet> sheets = getSheetsFromWorkbooks(workbooks);
        List<Row> rows = getRowsFromSheets(sheets);

        System.out.println("ROW NUMBERS : " + rows.size());

        List<RDV> rdvs = getRDVs(rows);

        return rdvs;
    }

    private List<RDV> getRDVs(List<Row> rows) {
        List<RDV> rdvs = new ArrayList<>();

        for (Row row : rows) {
            RDV rdv = new RDV();

            if (row.getRowNum() == 0 || row.getRowNum() == 1) {
                continue;
            }

            DataFormatter df = new DataFormatter();
            String cellAsString = "";

            for (Cell cell : row) {
                addCellToRDV(rdv, cell, cellAsString, df);
            }

//            if (rdv.getFirstName() == null || rdv.getFirstName().isEmpty())

            rdvs.add(rdv);
        }

        return rdvs;
    }

    private void addCellToRDV(RDV rdv, Cell cell, String cellAsString, DataFormatter df) {
        switch (cell.getAddress().toString().charAt(0)) {
            case 'A' :
                if (cell.getCellType().toString().equals("STRING")) {
                    rdv.setLastName(cell.getStringCellValue());
                    break;
                } else {
                    throw new ClassCastException("Type / format de données non valide dans la cellule " + cell.getAddress());
                }

            case 'B' :
                if (cell.getCellType().toString().equals("STRING")) {
                    rdv.setFirstName(cell.getStringCellValue());
                    break;
                } else {
                    throw new ClassCastException("Type / format de données non valide dans la cellule " + cell.getAddress());
                }

            case 'C' :
                if (cell.getCellType().toString().equals("STRING") && cell.getStringCellValue().contains("/")) {

                    String numberAsString = cell.getStringCellValue();
                    numberAsString = numberAsString.replaceAll("/", "");
                    numberAsString = numberAsString.replaceAll(" ", "");

                    if (numberAsString.length() == 10 && numberAsString.matches("[0-9]+")) {
                        int phoneNumber = Integer.parseInt(numberAsString);
                        rdv.setPhoneNumber(phoneNumber);
                        break;
                    } else {
                        throw new ClassCastException("Type / format de données non valide dans la cellule " + cell.getAddress());
                    }
                } else {
                    throw new ClassCastException("Type / format de données non valide dans la cellule " + cell.getAddress());
                }

            case 'D' :
                if (cell.getCellType().toString().equals("STRING") && cell.toString().contains("@")) {
                    rdv.setMail(cell.getStringCellValue());
                    break;
                } else if (cell.getCellType().toString().equals("BLANK")) {
                    rdv.setMail("");
                    break;
                } else {
                    throw new ClassCastException("Type / format de données non valide dans la cellule " + cell.getAddress());
                }

            case 'E' :
                if (cell.getCellType().toString().equals("STRING") && cell.toString().length() == 10 && cell.toString().charAt(2) == '/' && cell.toString().charAt(5) == '/') {

                    String dateAsString = cell.getStringCellValue();
                    dateAsString = dateAsString.replaceAll("/", "");

                    if (dateAsString.length() == 8 && dateAsString.matches("[0-9]+")) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                        LocalDate localDate = LocalDate.parse(dateAsString, formatter);
                        rdv.setLetterSentDate(localDate);
                        break;
                    } else {
                        throw new ClassCastException("Type / format de données non valide dans la cellule " + cell.getAddress());
                    }
                } else {
                    throw new ClassCastException("Type / format de données non valide dans la cellule " + cell.getAddress());
                }

            case 'F' :
                if (cell.getCellType().toString().equals("STRING") && cell.toString().length() == 10 && cell.toString().charAt(2) == '/' && cell.toString().charAt(5) == '/') {

                    String dateAsString = cell.getStringCellValue();
                    dateAsString = dateAsString.replaceAll("/", "");

                    if (dateAsString.length() == 8 && dateAsString.matches("[0-9]+")) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                        LocalDate localDate = LocalDate.parse(dateAsString, formatter);
                        rdv.setRDVDate(localDate);
                        break;
                    } else {
                        throw new ClassCastException("Type / format de données non valide dans la cellule " + cell.getAddress());
                    }
                } else {
                    throw new ClassCastException("Type / format de données non valide dans la cellule " + cell.getAddress());
                }

            case 'G' :
                if (cell.getCellType().toString().equals("STRING") && cell.toString().length() == 5 && cell.toString().charAt(2) == 'h') {

                    String timeAsString = cell.getStringCellValue();
                    timeAsString = timeAsString.replaceAll("h", "");

                    if (timeAsString.length() == 4 && timeAsString.matches("[0-9]+")) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HHmm");
                        LocalTime localTime = LocalTime.parse(timeAsString, formatter);
                        rdv.setRDVTime(localTime);
                        break;
                    } else {
                        throw new ClassCastException("Type / format de données non valide dans la cellule " + cell.getAddress());
                    }
                } else {
                    throw new ClassCastException("Type / format de données non valide dans la cellule " + cell.getAddress());
                }

            case 'H' :
                if (cell.getCellType().toString().equals("STRING")) {
                    rdv.setRDVLocation(cell.getStringCellValue());
                    break;
                } else {
                    throw new ClassCastException("Type / format de données non valide dans la cellule " + cell.getAddress());
                }

            case 'I' :
                if (cell.getCellType().toString().equals("STRING")) {
                    rdv.setReferentName(cell.getStringCellValue());
                    break;
                }  else if (cell.getCellType().toString().equals("BLANK")) {
                    rdv.setReferentName("votre référent");
                    break;
                } else {
                    throw new ClassCastException("Type / format de données non valide dans la cellule " + cell.getAddress());
                }

            case 'J' :
                if (cell.getCellType().toString().equals("STRING") && cell.toString().length() <= 4) {

                    if (cell.toString().toUpperCase(Locale.ROOT).contains("IND")) {
                        rdv.setRDVType("IND");
                        break;
                    } else if (cell.toString().toUpperCase(Locale.ROOT).contains("COLL")) {
                        rdv.setRDVType("COLL");
                        break;
                    } else {
                        throw new ClassCastException("Type / format de données non valide dans la cellule " + cell.getAddress());
                    }
                } else {
                    throw new ClassCastException("Type / format de données non valide dans la cellule " + cell.getAddress());
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

        return files;
    }


}
