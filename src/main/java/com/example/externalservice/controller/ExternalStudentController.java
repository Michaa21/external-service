package com.example.externalservice.controller;

import com.example.externalservice.dto.ExternalStudentResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/external/students")
public class ExternalStudentController {

    @GetMapping("/{id}")
    public ExternalStudentResponse getExtra(@PathVariable String id) {
        return new ExternalStudentResponse(id, "extra-info-for-" + id);
    }

    @PostMapping
    public String create() {
        return "created";
    }
}