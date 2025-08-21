package com.ellen.sucurity.lab.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ClientController {

    @RequestMapping("/get")
    public ResponseEntity<Map<String, Object>> get() {
        return ResponseEntity.ok(Map.of("message", "hello"));
    }

}
