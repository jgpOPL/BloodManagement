package com.example.bloodmanagement.utitity;

import com.example.bloodmanagement.domain.BloodStock;
import com.example.bloodmanagement.domain.Donation;
import com.example.bloodmanagement.domain.Users;
import jakarta.persistence.Column;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class StorageHelper {

    public static byte[] getExcelDataFromDonations(List<Donation> donationList, List<Users> usersList, List<BloodStock> bloodStockList)
    {

        try(ByteArrayOutputStream os = new ByteArrayOutputStream(); Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Donation_Details");
            String[] headers= {"DONATION_ID","DONATION_DATE","QUANTITY","DONOR_NAME","BLOOD_GROUP","REMARKS"};

            CellStyle headerStyle = wb.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());

            headerStyle.setFont(headerFont);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);


            Row headerRow = sheet.createRow(0);
            for(int i=0;i< headers.length;i++)
            {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            int i=1;
            for(Donation d : donationList)
            {
                Row row = sheet.createRow(i);
                row.createCell(0).setCellValue(d.getDonationId());
                row.createCell(1).setCellValue(String.valueOf(d.getDonationDate()));
                row.createCell(2).setCellValue(d.getQuantity());
                row.createCell(3).setCellValue(d.getDonor().getName());
                row.createCell(4).setCellValue(String.valueOf(d.getBloodGroup()));
                row.createCell(5).setCellValue(d.getRemarks());
                i++;
            }

            Sheet sheet1 = wb.createSheet("User Details");
            String[] headers1 = {"USER_ID","EMAIL","ROLE","USERNAME"};

            Row headerRow1 = sheet1.createRow(0);
            for( i=0;i<headers1.length;i++)
            {
                Cell cell = headerRow1.createCell(i);
                cell.setCellValue(headers1[i]);
                cell.setCellStyle(headerStyle);
            }
            i=1;
            for(Users user : usersList)
            {
                Row row = sheet1.createRow(i);
                row.createCell(0).setCellValue(user.getId());
                row.createCell(1).setCellValue(user.getEmail());
                row.createCell(2).setCellValue(user.getRole());
                row.createCell(3).setCellValue(user.getUsername());
                i++;
            }
            Sheet sheet2 = wb.createSheet("Blood_Stock");
            String[] headers2 = {"BLOODSTOCK_ID","UNITS","LAST_UPDATED","BLOOD_GROUP"};
            Row headerRow2 = sheet2.createRow(0);

            for (i=0;i<headers2.length;i++)
            {
                Cell cell = headerRow2.createCell(i);
                cell.setCellValue(headers2[i]);
                cell.setCellStyle(headerStyle);
            }
            i=1;
            for(BloodStock bloodStock : bloodStockList)
            {
                Row row = sheet2.createRow(i);
                row.createCell(0).setCellValue(bloodStock.getBloodStockId());
                row.createCell(1).setCellValue(bloodStock.getUnits());
                row.createCell(2).setCellValue(String.valueOf(bloodStock.getLastUpdated()));
                row.createCell(3).setCellValue(String.valueOf(bloodStock.getBloodGroup()));
                i++;
            }
            for(int j=0;j< headers.length;j++)
            {
                sheet.autoSizeColumn(j);
            }
            for(int j=0;j< headers1.length;j++)
            {
                sheet1.autoSizeColumn(j);
            }
            for(int j=0;j< headers2.length;j++)
            {
                sheet2.autoSizeColumn(j);
            }
            wb.write(os);
            return os.toByteArray();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
