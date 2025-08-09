package org.simu.client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/signup")
    public String signupPage() {
        return "signup"; // Шаблон signup.html
    }

    @GetMapping("/signin")
    public String signinPage() {
        return "signin"; // Шаблон signin.html
    }

    @GetMapping("/home")
    public String homePage() {
        return "home"; // Шаблон home.html
    }
    @GetMapping("/home-page")
    public String homepagePage() {
        return "home-page"; // Шаблон home.html
    }
}