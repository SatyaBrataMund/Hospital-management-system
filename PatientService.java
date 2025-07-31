package com.springRest.service;

import com.springRest.DAO.PatientRepository;
import com.springRest.enitity.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    private PatientRepository patientRepository;

    public PatientService() {
    }

    @Autowired
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public void save(Patient patient) {
        patientRepository.save(patient);
    }

    public Optional<Patient> findById(Integer id) {
        return patientRepository.findById(id);
    }

    public void deleteById(Integer id) {
        patientRepository.deleteById(id);
    }

	public void deleteById(Long patientId) {
		
	}

	public Optional<Patient> findById(Long patientId) {
		// TODO Auto-generated method stub
		return null;
	}
}
