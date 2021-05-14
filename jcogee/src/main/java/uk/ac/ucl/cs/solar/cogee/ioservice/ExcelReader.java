package uk.ac.ucl.cs.solar.cogee.ioservice;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import uk.ac.ucl.cs.solar.cogee.dataset.DataRow;
import uk.ac.ucl.cs.solar.cogee.dataset.Dataset;
import uk.ac.ucl.cs.solar.cogee.dataset.EffortEstimationFold;
import uk.ac.ucl.cs.solar.cogee.exception.CogeeException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader implements DatasetFileReader {

    static Logger logger = Logger.getLogger(ExcelReader.class);

    @Override
    public Dataset read(File file) throws CogeeException {
        Workbook workbook = null;
        try {
            String name = file.getName();
            name = name.substring(0, name.lastIndexOf("train") != -1 ? name.lastIndexOf("train") - 1 : name.length());
            name = name.substring(0, name.lastIndexOf("test") != -1 ? name.lastIndexOf("test") - 1 : name.length());
            name = name.substring(0, name.lastIndexOf(".") != -1 ? name.lastIndexOf(".") : name.length());

            // Creating a Workbook from an Excel file (.xls or .xlsx)
            workbook = WorkbookFactory.create(file);

            // Retrieving the number of sheets in the Workbook
            logger.info(file.getName() + " has " + workbook.getNumberOfSheets() + " Sheets : ");
            Dataset ds = new Dataset(name, workbook.getNumberOfSheets());

            for (Sheet sheet : workbook) {
                EffortEstimationFold fold = new EffortEstimationFold(sheet.getSheetName().replace(" ", ""));
                logger.info("new Fold => " + sheet.getSheetName());

                // Create a DataFormatter to format and get each cell's value as String
                DataFormatter dataFormatter = new DataFormatter();

                Row header = sheet.getRow(sheet.getFirstRowNum());
                List<String> rowHeader = new ArrayList<>();
                int idColumnNumber = 0;
                int effortColumnNumber = 0;
                for (int c = header.getFirstCellNum(); c < header.getLastCellNum(); c++) {
                    if ("id".equals(header.getCell(c).toString().toLowerCase()))
                        idColumnNumber = c;
                    else if ("effort".equals(header.getCell(c).toString().toLowerCase()))
                        effortColumnNumber = c;
                    else
                        rowHeader.add(header.getCell(c).toString());
                }
                fold.setHeader(rowHeader);

                int rowCounter = sheet.getFirstRowNum() + 1;
                while (sheet.getRow(rowCounter) != null && sheet.getRow(rowCounter).getPhysicalNumberOfCells() > 0) {
                    addRow(
                            fold,
                            dataFormatter,
                            idColumnNumber,
                            effortColumnNumber,
                            sheet.getRow(rowCounter));
                    rowCounter++;
                }
                ds.addFold(fold);
                logger.info("number of rows: " + fold.getRows().size());
            }
            // Closing the workbook
            workbook.close();
            return ds;
        } catch (CogeeException cx) {
            logger.error(cx);
        } catch (IOException iox) {
            throw new CogeeException("Couldn't open the File! " + iox.getMessage(), iox);
        } catch (InvalidFormatException ifx) {
            throw new CogeeException("Couldn't read the File!" + ifx.getMessage(), ifx);
        } finally {
            try {
                if (workbook != null)
                    workbook.close();
            } catch (IOException iox) {
                iox.printStackTrace();
                logger.error(iox);
            }
        }
        return null;
    }


    private void addRow(EffortEstimationFold fold, DataFormatter dataFormatter, int idColumnNumber,
                        int effortColumnNumber, Row r) throws CogeeException {
        if (r.getCell(idColumnNumber).toString().trim().isEmpty()) {
            logger.info("No ID value in row: " + fold.getName() + " : " + r.getRowNum());
            logger.info("Not included in the data set!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            return;
        }
        try {
            checkForEmptyCells(r, fold.getName());
            Integer id = Integer.parseInt(
                    dataFormatter.formatCellValue(
                            r.getCell(
                                    idColumnNumber)));

            DataRow<Double> row = new DataRow<>(id);
            for (int c = r.getFirstCellNum(); c < r.getLastCellNum(); c++) {
                if (c == idColumnNumber)
                    continue;
                if (r.getCell(c).toString().trim().isEmpty())
                    continue;
                if (c == effortColumnNumber) {
                    fold.addEffort(
                            id,
                            Double.parseDouble(
                                    dataFormatter.formatCellValue(
                                            r.getCell(effortColumnNumber)))
                    );
                } else
                    row.addValue(
                            Double.parseDouble(
                                    dataFormatter.formatCellValue(
                                            r.getCell(c))));
            }
            fold.addRow(row);
            return;
        }catch (NumberFormatException nfe){
            throw new CogeeException("A value has incorrect number format in fold "+fold.getName()+"! -> row num: " + r.getRowNum());
        }
    }


    private void checkForEmptyCells(Row r, String foldName) throws CogeeException {
        int i = r.getPhysicalNumberOfCells()-1;
        while (i >= 0) {
            if ( r.getCell(i).toString().trim().isEmpty())
                i--;
            else
                break;
        }
        i--;
        while (i >= 0) {
            if (r.getCell(i).toString().trim().isEmpty())
                throw new CogeeException("Dataset contains empty cell at: " + foldName + " : " + r.getRowNum());
            i--;
        }
        return;
    }

}
