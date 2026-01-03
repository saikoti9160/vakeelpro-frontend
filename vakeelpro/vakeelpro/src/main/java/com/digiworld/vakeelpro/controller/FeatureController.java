package com.digiworld.vakeelpro.controller;

import java.util.Map;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digiworld.vakeelpro.service.impl.FeatureAssignmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/features")
@RequiredArgsConstructor
public class FeatureController {

    private final FeatureAssignmentService featureAssignmentService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Set<String>>> getUserFeatures(@PathVariable Long userId) {
        Map<String, Set<String>> features = featureAssignmentService.getUserFeatures(userId);
        return ResponseEntity.ok(features);
    }
}
