package com.sarinah.tenantsalesomzet.controller;

import com.sarinah.tenantsalesomzet.response.TenantOmzetResponse;
import com.sarinah.tenantsalesomzet.service.GetTenantSalesOmzetService;
import com.sarinah.tenantsalesomzet.service.GetTenantSalesOmzetTmpService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {
    private final GetTenantSalesOmzetTmpService getTenantSalesOmzetService;


    @GetMapping
    public String showDashboard(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean onlyToday,
            HttpSession session,
            Model model) {

        String username = (String) session.getAttribute("username");
        String brandName = (String) session.getAttribute("brandName");

        if (username == null) {
            return "redirect:/auth/login";
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
}
