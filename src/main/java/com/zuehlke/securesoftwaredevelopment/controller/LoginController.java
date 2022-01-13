package com.zuehlke.securesoftwaredevelopment.controller;


import com.zuehlke.securesoftwaredevelopment.repository.HashedUserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    private final HashedUserRepository repository;

    LoginController(HashedUserRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

}