package com.ellen.security.lab.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
public class HelloController {

    @RequestMapping("/hello")
    public String hello(@RequestParam("date") LocalDate date, String name) {
        String formattedDate = date.format(DateTimeFormatter.ISO_DATE);
        return name + " says hello on " + formattedDate;
    }

    @RequestMapping("/ping")
    public String ping() {
        return "ok";
    }


}
