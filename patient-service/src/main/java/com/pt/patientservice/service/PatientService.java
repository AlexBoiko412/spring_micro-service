package com.pt.patientservice.service;

import com.pt.patientservice.dto.PatientRequestDTO;
import com.pt.patientservice.dto.PatientResponseDTO;
import com.pt.patientservice.exception.EmailAlreadyExistsException;
import com.pt.patientservice.exception.PatientNotFoundException;
import com.pt.patientservice.mapper.PatientMapper;
import com.pt.patientservice.model.Patient;
import com.pt.patientservice.repository.PatientRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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
            "A patient with this email already exists: "
                    + patientRequestDTO.getEmail()
            );
        }

        Patient newPatient = patientRepository.save(PatientMapper.toModel(patientRequestDTO));

        return PatientMapper.toDto(newPatient);
    }

    public PatientResponseDTO updatePatient(
            UUID id,
            PatientRequestDTO patientRequestDTO) {
        Patient patient = patientRepository.findById(id).orElseThrow(
                () -> new PatientNotFoundException(
                        "Patient doesnt exist with ID: " + id)
                );

        if(patientRepository.existsByEmailAndIdNot(patientRequestDTO.getEmail(), id)) {
            throw new EmailAlreadyExistsException(
                    "Another patient with this email already exists: "
                            + patientRequestDTO.getEmail()
            );
        }

        patient.setEmail(patientRequestDTO.getEmail());
        patient.setName(patientRequestDTO.getName());
        patient.setAddress(patientRequestDTO.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDTO.getDateOfBirth()));

        Patient updatedPatient = patientRepository.save(patient);

        return PatientMapper.toDto(updatedPatient);
    }

    public void deletePatient(UUID id) {
        patientRepository.deleteById(id);
    }
}