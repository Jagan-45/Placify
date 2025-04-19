package com.murali.placify.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

@Component
public class ExcelExporter {
    private final RootPath rootPath;

    public ExcelExporter(RootPath rootPath) {
        this.rootPath = rootPath;
    }

    public <T> void writeToExcel(List<T> dataList, String contestId) throws IOException, IllegalAccessException {
        String filePath = rootPath.getRootPath() + "/placify/leaderboard_files/leaderboard_" + contestId + ".xlsx";

        if (dataList == null || dataList.isEmpty()) {
            throw new IllegalArgumentException("Data list is empty or null.");
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Data");

        T sample = dataList.get(0);
        Field[] fields = sample.getClass().getDeclaredFields();
        Row headerRow = sheet.createRow(0);
        int headerCol = 0;
        for (Field field : fields) {
            field.setAccessible(true);
            Cell cell = headerRow.createCell(headerCol++);
            cell.setCellValue(field.getName());
        }

        int rowIdx = 1;
        for (T item : dataList) {
            Row row = sheet.createRow(rowIdx++);
            int colIdx = 0;
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(item);
                Cell cell = row.createCell(colIdx++);
                if (value != null) {
                    cell.setCellValue(value.toString());
                } else {
                    cell.setCellValue("");
                }
            }
        }

        for (int i = 0; i < fields.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            workbook.write(fos);
        }

        workbook.close();
    }

    public File getLeaderboardFile(String contestId) {
        File file = new File(rootPath.getRootPath() + "/placify/leaderboard_files/leaderboard_" + contestId + ".xlsx");

        if (!fileExists(contestId)) {
            throw new RuntimeException("File does not exists try again");
        }

        return file;
    }

    public boolean fileExists(String contestId) {

        File file = new File(rootPath.getRootPath() + "/placify/leaderboard_files/leaderboard_" + contestId + ".xlsx");

        return file.exists();
    }
}

