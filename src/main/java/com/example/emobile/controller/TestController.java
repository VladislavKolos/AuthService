package com.example.emobile.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    /**
     * Test endpoint for verifying access to protected resources.
     *
     * @return A confirmation message indicating successful access to a protected endpoint.
     */
    @GetMapping("/endpoint")
    @ResponseStatus(HttpStatus.OK)
    public String protectedEndpoint() {
        return "You have accessed a protected endpoint!";
    }
}