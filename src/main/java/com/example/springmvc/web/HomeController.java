package com.example.springmvc.web;

import com.example.springmvc.security.UserPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home(@AuthenticationPrincipal UserPrincipal principal, Model model) {
        if (principal != null) {
            model.addAttribute("displayName", principal.getUser().getFullName());
            model.addAttribute("email", principal.getUsername());
        }
        return "home";
    }
}
