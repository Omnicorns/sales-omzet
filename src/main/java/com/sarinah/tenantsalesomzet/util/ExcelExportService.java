package com.sarinah.tenantsalesomzet.util;



import com.sarinah.tenantsalesomzet.model.dto.TenantOmzetDTO;
import com.sarinah.tenantsalesomzet.model.dto.TenantOmzetReceiptDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelExportService {

    public ByteArrayInputStream exportToExcel(List<TenantOmzetDTO> data, String startDate, String endDate) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Sales Omzet");

            String[] columns = {
                    "No", "Tenant ID", "Tenant Name", "Brand", "Lokasi",
                    "Tanggal", "Hari", "Channel", "Total Omzet",
                    "No. Receipt", "Receipt Date", "Amount", "DPP", "PPN",
                    "Service Charge", "Payment Type"
            };

            // ===== Styles =====
            CreationHelper createHelper = workbook.getCreationHelper();

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            setThinBorder(headerStyle);

            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleStyle.setFont(titleFont);

            CellStyle boldStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);

            CellStyle dateStyle = workbook.createCellStyle();
            dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MMM-yyyy"));
            setThinBorder(dateStyle);

            // Kalau mau format rupiah, bisa pakai: "\"Rp\" #,##0.00"
            CellStyle currencyStyle = workbook.createCellStyle();
            currencyStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0.00"));
            setThinBorder(currencyStyle);

            CellStyle textStyle = workbook.createCellStyle();
            setThinBorder(textStyle);

            // ===== Title Row (merge) =====
            Row titleRow = sheet.createRow(0);
            titleRow.setHeightInPoints(22);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Laporan Sales Omzet");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columns.length - 1));

            // ===== Period Row (merge) =====
            Row periodRow = sheet.createRow(1);
            Cell periodCell = periodRow.createCell(0);
            periodCell.setCellValue("Periode: " + startDate + " s/d " + endDate);
            periodCell.setCellStyle(boldStyle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, columns.length - 1));

            // Empty row
            sheet.createRow(2);

            // ===== Header Row =====
            Row headerRow = sheet.createRow(3);
            headerRow.setHeightInPoints(18);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Freeze panes under header
            sheet.createFreezePane(0, 4);

            // ===== Data Rows =====
            int rowNum = 4;
            int no = 1;

            for (TenantOmzetDTO tenant : data) {
                if (tenant.getReceipts() == null || ObjectUtils.isEmpty(tenant.getReceipts())) {
                    Row row = sheet.createRow(rowNum++);
                    createTenantCells(row, no++, tenant, null, dateStyle, currencyStyle, textStyle);
                } else {
                    for (TenantOmzetReceiptDTO receipt : tenant.getReceipts()) {
                        Row row = sheet.createRow(rowNum++);
                        createTenantCells(row, no, tenant, receipt, dateStyle, currencyStyle, textStyle);
                    }
                    no++;
                }
            }

            // Apply AutoFilter on data range (header + data)
            if (rowNum > 4) {
                sheet.setAutoFilter(new CellRangeAddress(3, rowNum - 1, 0, columns.length - 1));
            }

            // ===== Summary Row =====
            Row summaryRow = sheet.createRow(rowNum + 1);
            Cell summaryLabel = summaryRow.createCell(0);
            summaryLabel.setCellValue("GRAND TOTAL");
            summaryLabel.setCellStyle(boldStyle);

            Cell summaryValue = summaryRow.createCell(8);
            BigDecimal grandTotal = data.stream()
                    .map(TenantOmzetDTO::getTotalOmzet)
                    .filter(v -> v != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            summaryValue.setCellValue(grandTotal.doubleValue());
            summaryValue.setCellStyle(currencyStyle);

            // ===== Columns sizing (opsi) =====
            // autoSize bagus untuk file kecil; kalau datanya banyak, mending setWidth manual.
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            log.error("Error creating Excel file", e);
            throw new RuntimeException("Failed to export data to Excel", e);
        }
    }

    private void createTenantCells(Row row, int no, TenantOmzetDTO tenant,
                                   TenantOmzetReceiptDTO receipt,
                                   CellStyle dateStyle, CellStyle currencyStyle, CellStyle textStyle) {

        int colNum = 0;

        // Tenant data
        createTextCell(row, colNum++, String.valueOf(no), textStyle);
        createTextCell(row, colNum++, safe(tenant.getTenantId()), textStyle);
        createTextCell(row, colNum++, safe(tenant.getTenantName()), textStyle);
        createTextCell(row, colNum++, safe(tenant.getBrandName()), textStyle);
        createTextCell(row, colNum++, safe(tenant.getLotLocation()), textStyle);

        createDateCell(row, colNum++, tenant.getSalesDate(), dateStyle);
        createTextCell(row, colNum++, safe(tenant.getDay()), textStyle);
        createTextCell(row, colNum++, safe(tenant.getChannel()), textStyle);

        createNumberCell(row, colNum++, bd(tenant.getTotalOmzet()), currencyStyle);

        // Receipt data
        if (receipt != null) {
            createTextCell(row, colNum++, safe(receipt.getReceiptNumber()), textStyle);
            createDateCell(row, colNum++, receipt.getReceiptDate(), dateStyle);
            createNumberCell(row, colNum++, bd(receipt.getAmount()), currencyStyle);
            createNumberCell(row, colNum++, bd(receipt.getDpp()), currencyStyle);
            createNumberCell(row, colNum++, bd(receipt.getPpn()), currencyStyle);
            createNumberCell(row, colNum++, bd(receipt.getServiceCharge()), currencyStyle);
            createTextCell(row, colNum, safe(receipt.getPaymentType()), textStyle);
        } else {
            // Biar rapi kolomnya tetap ada (opsional)
            for (int i = colNum; i <= 15; i++) {
                createTextCell(row, i, "", textStyle);
            }
        }
    }

    private static void setThinBorder(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static double bd(BigDecimal v) {
        return v == null ? 0d : v.doubleValue();
    }

    private static void createTextCell(Row row, int col, String value, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value);
        c.setCellStyle(style);
    }

    private static void createNumberCell(Row row, int col, double value, CellStyle style) {
        Cell c = row.createCell(col);
        c.setCellValue(value);
        c.setCellStyle(style);
    }

    private static void createDateCell(Row row, int col, Date date, CellStyle style) {
        Cell c = row.createCell(col);
        if (date != null) {
            c.setCellValue(date);
        } else {
            c.setCellValue("");
        }
        c.setCellStyle(style);
    }
}
