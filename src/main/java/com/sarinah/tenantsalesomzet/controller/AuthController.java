package com.sarinah.tenantsalesomzet.controller;


import com.sarinah.tenantsalesomzet.exception.BusinessException;
import com.sarinah.tenantsalesomzet.request.LoginRequest;
import com.sarinah.tenantsalesomzet.response.PostLoginResponse;
import com.sarinah.tenantsalesomzet.service.PostLoginService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequiredArgsConstructor
@RequestMapping("/tenant-sales/auth")
public class AuthController {
    private final PostLoginService loginService;

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login"; // akan render login.html
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest request,
                        Model model,
                        HttpSession session,
                        RedirectAttributes redirectAttributes) {
        try {
            // Call service untuk login
            PostLoginResponse response = loginService.execute(request);

            // Simpan user info ke session
            session.setAttribute("userId", response.getUserId());
            session.setAttribute("username", response.getUsername());
            session.setAttribute("tenantName", response.getTenantNama());
            session.setAttribute("brandName", response.getTenantBrand());

            redirectAttributes.addFlashAttribute("message", "Login berhasil!");
            return "redirect:/tenant-sales/dashboard";

        } catch (BusinessException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("loginRequest", request);
            return "login";
        }


    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("message", "Anda telah berhasil logout");
        return "redirect:/tenant-sales/auth/login";
    }
}