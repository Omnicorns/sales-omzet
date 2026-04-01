package com.sarinah.tenantsalesomzet.controller;

import com.sarinah.tenantsalesomzet.model.dto.TenantOmzetDTO;
import com.sarinah.tenantsalesomzet.model.dto.TenantOmzetReceiptDTO;
import com.sarinah.tenantsalesomzet.response.TenantOmzetResponse;
import com.sarinah.tenantsalesomzet.service.GetTenantSalesOmzetTmpService;
import com.sarinah.tenantsalesomzet.util.ExcelExportService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tenant-sales/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {
    private final GetTenantSalesOmzetTmpService getTenantSalesOmzetService;
    private final ExcelExportService excelExportService;





    @GetMapping
    public String showDashboard(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean onlyToday,
            HttpSession session,
            @CookieValue(value = "tenant_username", required = false) String cookieUsername,
            @CookieValue(value = "tenant_brand", required = false) String cookieBrand,
            Model model) {

        String username = (String) session.getAttribute("username");
        String brandName = (String) session.getAttribute("brandName");

        if (username == null) {
            username = cookieUsername;
            brandName = cookieBrand;
            // Restore ke session
            if (username != null) {
                session.setAttribute("username", username);
                session.setAttribute("brandName", brandName);
            }
        }
        if (username == null) {
            return "redirect:/tenant-sales/auth/login";
        }

        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1).toString();
        }
        if (endDate == null) {
            endDate = LocalDate.now().toString();
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);

            Pageable pageable = PageRequest.of(page, size);

            // Panggil service dengan parameter brandName
            Page<TenantOmzetResponse> tenantPage = getTenantSalesOmzetService.execute(
                    start,
                    end,
                    onlyToday,
                    brandName, // ← TAMBAH PARAMETER INI
                    pageable
            );

            // Hitung total omzet
            BigDecimal totalOmzet = tenantPage.getContent().stream()
                    .map(TenantOmzetResponse::getTotalOmzet)
                    .filter(omzet -> omzet != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            model.addAttribute("tenants", tenantPage.getContent());
            model.addAttribute("totalElements", tenantPage.getTotalElements());
            model.addAttribute("totalPages", tenantPage.getTotalPages());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalOmzet", totalOmzet);
            model.addAttribute("startDate", startDate);
            model.addAttribute("endDate", endDate);
            model.addAttribute("onlyToday", onlyToday);
            model.addAttribute("username", username);
            model.addAttribute("brandName", brandName);

        } catch (ParseException e) {
            model.addAttribute("error", "Format tanggal salah: " + e.getMessage());
        } catch (Exception e) {

            model.addAttribute("error", "Gagal memuat data: " + e.getMessage());
        }

        return "dashboard";
    }

    @GetMapping("/export/excel")
    public ResponseEntity<InputStreamResource> exportToExcel(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "false") boolean onlyToday,
            HttpSession session) {

        String username = (String) session.getAttribute("username");
        String brandName = (String) session.getAttribute("brandName");

        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1).toString();
        }
        if (endDate == null) {
            endDate = LocalDate.now().toString();
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);

            // Get ALL data tanpa pagination untuk export
            Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);

            Page<TenantOmzetResponse> tenantPage = getTenantSalesOmzetService.execute(
                    start,
                    end,
                    onlyToday,
                    brandName,
                    pageable
            );

            // Convert ke DTO untuk export
            List<TenantOmzetDTO> tenantDTOs = tenantPage.getContent().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            // Generate Excel
            ByteArrayInputStream excelStream = excelExportService.exportToExcel(
                    tenantDTOs,
                    startDate,
                    endDate
            );

            // Setup response headers
            HttpHeaders headers = new HttpHeaders();
            String filename = String.format("Sales_Omzet_%s_to_%s_%s.xlsx",
                    startDate.replace("-", ""),
                    endDate.replace("-", ""),
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"))
            );

            headers.add("Content-Disposition", "attachment; filename=" + filename);
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Pragma", "no-cache");
            headers.add("Expires", "0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(excelStream));

        } catch (ParseException e) {
            log.error("Error parsing date: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error exporting to Excel", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Helper method untuk convert Response ke DTO
    private TenantOmzetDTO convertToDTO(TenantOmzetResponse response) {
        TenantOmzetDTO dto = new TenantOmzetDTO();
        dto.setTenantId(response.getTenantId());
        dto.setTenantName(response.getTenantName());
        dto.setBrandName(response.getBrandName());
        dto.setLotLocation(response.getLotLocation());
        dto.setSalesDate(response.getSalesDate());
        dto.setDay(response.getDay());
        dto.setChannel(response.getChannel());
        dto.setTotalOmzet(response.getTotalOmzet());

        // Convert receipts
        if (response.getReceipts() != null) {
            List<TenantOmzetReceiptDTO> receiptDTOs = response.getReceipts().stream()
                    .map(this::convertReceiptToDTO)
                    .collect(Collectors.toList());
            dto.setReceipts(receiptDTOs);
        }

        return dto;
    }

    private TenantOmzetReceiptDTO convertReceiptToDTO(Object receipt) {
        // Sesuaikan dengan struktur Receipt response Anda
        TenantOmzetReceiptDTO dto = new TenantOmzetReceiptDTO();

        // Jika receipt adalah Map atau object lain, extract field-nya
        if (receipt instanceof Map) {
            Map<String, Object> receiptMap = (Map<String, Object>) receipt;
            dto.setReceiptNumber((String) receiptMap.get("receiptNumber"));
            dto.setReceiptDate((Date) receiptMap.get("receiptDate"));
            dto.setAmount((BigDecimal) receiptMap.get("amount"));
            dto.setDpp((BigDecimal) receiptMap.get("dpp"));
            dto.setPpn((BigDecimal) receiptMap.get("ppn"));
            dto.setServiceCharge((BigDecimal) receiptMap.get("serviceCharge"));
            dto.setPaymentType((String) receiptMap.get("paymentType"));
        }
        // Atau jika sudah dalam bentuk object Receipt
        // cast dan copy properties

        return dto;
    }
}
