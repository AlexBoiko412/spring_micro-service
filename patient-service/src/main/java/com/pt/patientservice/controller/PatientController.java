package com.pt.patientservice.controller;

import com.pt.patientservice.dto.PatientRequestDTO;
import com.pt.patientservice.dto.PatientResponseDTO;
import com.pt.patientservice.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {
    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public ResponseEntity<List<PatientResponseDTO>> getPatients() {
        return ResponseEntity.ok().body(patientService.getPatients());
    }

    @PostMapping
    public ResponseEntity<PatientResponseDTO> setPatients(
            @Valid @RequestBody PatientRequestDTO patientRequestDTO
    ) {
        PatientResponseDTO newPatientResponseDTO = patientService.createPatient(patientRequestDTO);
        return ResponseEntity.ok().body(newPatientResponseDTO);
    }
}
