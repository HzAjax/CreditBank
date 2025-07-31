package ru.volodin.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/deal")
    public ResponseEntity<String> dealFallback() {
        return ResponseEntity.ok("Fallback: Deal service is unavailable.");
    }

    @GetMapping("/statement")
    public ResponseEntity<String> statementFallback() {
        return ResponseEntity.ok("Fallback: Statement service is unavailable.");
    }
}
