package com.example.bankcards.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OpenApiController {

  @GetMapping("/api-docs.yaml")
  public ResponseEntity<Resource> getOpenApiYaml() {
    try {
      Resource resource = new FileSystemResource("docs/openapi.yaml");
      if (resource.exists()) {
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType("application/yaml"))
            .body(resource);
      } else {
        return ResponseEntity.notFound().build();
      }
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }
}