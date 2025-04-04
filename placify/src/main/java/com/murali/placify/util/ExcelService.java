package com.murali.placify.util;

import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelService {
    public List<String> extractMailIds(MultipartFile file) {
        List<String> mailIds = new ArrayList<>();
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                Cell cell = row.getCell(0);
                if (cell != null) {
                    mailIds.add(cell.getStringCellValue().trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mailIds;
    }
}

