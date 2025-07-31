package com.springRest.Controller;

import java.util.List;
import java.util.Optional;

import com.springRest.enitity.Doctor;
import com.springRest.enitity.Patient;
import com.springRest.service.DiseaseService;
import com.springRest.service.DoctorService;
import com.springRest.service.PatientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;
    private final DoctorService doctorService;
    private final DiseaseService diseaseService;

    @Autowired
    public PatientController(PatientService patientService,
                             DoctorService doctorService,
                             DiseaseService diseaseService) {
        this.patientService = patientService;
        this.doctorService = doctorService;
        this.diseaseService = diseaseService;
    }

    @GetMapping("/list")
    public String listPatients(Model model) {
        List<Patient> patients = patientService.getAllPatients();
        model.addAttribute("patients", patients);
        return "patients/list-patients";
    }

    @GetMapping("/addPatient")
    public String getPatientForm(Model model) {
        Patient patient = new Patient();
        model.addAttribute("patient", patient);
        model.addAttribute("doctorList", doctorService.getAllDoctors());
        model.addAttribute("diseaseList", diseaseService.getAllDiseases());
        return "patients/addPatient";
    }

    @PostMapping("/save")
    public String savePatient(@ModelAttribute("patient") Patient patient, Model model) {
        if (patient.getDoctor() != null && patient.getDoctor().getId() != null && patient.getDoctor().getId() != 0) {
            Doctor doctor = doctorService.findById(patient.getDoctor().getId());
            patient.setDoctor(doctor);
        } else {
            patient.setDoctor(null);
        }
        patientService.save(patient);
        return "redirect:/patients/list";
    }

    @GetMapping("/showFormForUpdate")
    public String showUpdateForm(@RequestParam("patientId") Long patientId, Model model) {
        if (patientId == null) {
            return "redirect:/patients/list?error=InvalidId";
        }

        Optional<Patient> optionalPatient = patientService.findById(patientId);
        if (optionalPatient.isEmpty()) {
            return "redirect:/patients/list?error=NotFound";
        }

        Patient patient = optionalPatient.get();
        model.addAttribute("patient", patient);
        model.addAttribute("doctorList", doctorService.getAllDoctors());
        model.addAttribute("diseaseList", diseaseService.getAllDiseases());
        return "patients/addPatient";
    }

    @GetMapping("/delete")
    public String deletePatient(@RequestParam("patientId") Long patientId) {
        if (patientId == null) {
            return "redirect:/patients/list?error=InvalidId";
        }

        Optional<Patient> optionalPatient = patientService.findById(patientId);
        if (optionalPatient.isEmpty()) {
            return "redirect:/patients/list?error=NotFound";
        }

        patientService.deleteById(patientId);
        return "redirect:/patients/list";
    }
}
