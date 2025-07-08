package com.sarinah.tenantsalesomzet.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/tenant-sales")
public class TenantSalesViewController {


    @GetMapping("/entry")
    public String greeting(Model model) {
        return "tenant-sales";  // Mengembalikan halaman greeting
    }
}
