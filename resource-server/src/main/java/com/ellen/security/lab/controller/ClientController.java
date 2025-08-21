package com.ellen.security.lab.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
public class ClientController {

    @RequestMapping("/hello")
    public String hello(@RequestParam("date") LocalDate date, String name) {
        String formattedDate = date.format(DateTimeFormatter.ISO_DATE);
        return name + " says hello on " + formattedDate;
    }

    @RequestMapping(value = "/ping", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("ok");
    }
}
