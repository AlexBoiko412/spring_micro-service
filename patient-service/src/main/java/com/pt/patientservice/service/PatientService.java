package com.pt.patientservice.service;

import com.pt.patientservice.dto.PatientRequestDTO;
import com.pt.patientservice.dto.PatientResponseDTO;
import com.pt.patientservice.exception.EmailAlreadyExistsException;
import com.pt.patientservice.mapper.PatientMapper;
import com.pt.patientservice.model.Patient;
import com.pt.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {
    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<PatientResponseDTO> getPatients() {
        List<Patient> patients = patientRepository.findAll();
        return patients
                .stream().map(PatientMapper::toDto).toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException(
            "A patient with this enail already exists: "
                    + patientRequestDTO.getEmail()
            );
        }

        Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));

        return PatientMapper.toDto(newPatient);
    }
}