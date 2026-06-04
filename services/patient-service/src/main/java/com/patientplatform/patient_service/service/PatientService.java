package com.patientplatform.patient_service.service;

import com.patientplatform.patient_service.dto.PatientRequest;
import com.patientplatform.patient_service.dto.PatientResponse;
import com.patientplatform.patient_service.exception.CustomException;
import com.patientplatform.patient_service.model.Gender;
import com.patientplatform.patient_service.model.Patient;
import com.patientplatform.patient_service.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PatientRepository patientRepository;

    // Create patient profile
    public PatientResponse createPatient(PatientRequest request) {
        if (patientRepository.existsByUserId(request.getUserId())) {
            throw new CustomException("Patient profile already exists for this user");
        }

        Patient patient = new Patient();
        mapRequestToPatient(request, patient);

        Patient saved = patientRepository.save(patient);
        return mapPatientToResponse(saved);
    }

    // Get patient by ID
    public PatientResponse getPatientById(Long id) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new CustomException("Patient not found"));
        return mapPatientToResponse(patient);
    }

    // Get patient by userId
    public PatientResponse getPatientByUserId(Long userId) {
        Patient patient = patientRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException("Patient profile not found"));
        return mapPatientToResponse(patient);
    }

    // Get all patients
    public List<PatientResponse> getAllPatients() {
        return patientRepository.findAll()
                .stream()
                .map(this::mapPatientToResponse)
                .collect(Collectors.toList());
    }

    // Update patient
    public PatientResponse updatePatient(Long id, PatientRequest request) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new CustomException("Patient not found"));

        mapRequestToPatient(request, patient);
        Patient updated = patientRepository.save(patient);
        return mapPatientToResponse(updated);
    }

    // Delete patient
    public void deletePatient(Long id) {
        if (!patientRepository.existsById(id)) {
            throw new CustomException("Patient not found");
        }
        patientRepository.deleteById(id);
    }

    // ── Helpers ──────────────────────────────────────────

    private void mapRequestToPatient(PatientRequest request, Patient patient) {
        patient.setUserId(request.getUserId());
        patient.setFirstName(request.getFirstName());
        patient.setLastName(request.getLastName());
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setPhone(request.getPhone());
        patient.setAddress(request.getAddress());
        patient.setBloodGroup(request.getBloodGroup());
        patient.setEmergencyContact(request.getEmergencyContact());
        patient.setEmergencyPhone(request.getEmergencyPhone());

        if (request.getGender() != null) {
            try {
                patient.setGender(Gender.valueOf(request.getGender().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new CustomException("Invalid gender. Must be MALE, FEMALE, or OTHER");
            }
        }
    }

    private PatientResponse mapPatientToResponse(Patient patient) {
        PatientResponse response = new PatientResponse();
        response.setId(patient.getId());
        response.setUserId(patient.getUserId());
        response.setFirstName(patient.getFirstName());
        response.setLastName(patient.getLastName());
        response.setDateOfBirth(patient.getDateOfBirth());
        response.setGender(patient.getGender() != null ? patient.getGender().name() : null);
        response.setPhone(patient.getPhone());
        response.setAddress(patient.getAddress());
        response.setBloodGroup(patient.getBloodGroup());
        response.setEmergencyContact(patient.getEmergencyContact());
        response.setEmergencyPhone(patient.getEmergencyPhone());
        response.setCreatedAt(patient.getCreatedAt());
        return response;
    }
}
