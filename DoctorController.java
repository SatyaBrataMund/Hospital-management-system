package com.springRest.Controller;

import com.springRest.enitity.Doctor;
import com.springRest.service.DiseaseService;
import com.springRest.service.DoctorService;
import com.springRest.service.PatientService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
@RequestMapping("/doctors")
public class DoctorController {

    private DoctorService doctorService;
    private PatientService patientService;
    private DiseaseService diseaseService;

    public DoctorController(DoctorService doctorService,
                            PatientService patientService,
                            DiseaseService diseaseService) {
        this.doctorService = doctorService;
        this.patientService = patientService;
        this.diseaseService = diseaseService;
    }

    @GetMapping("/list")
    public String listDoctors(Model theModel) {
        List<Doctor> theDoctors = doctorService.getAllDoctors();
        theModel.addAttribute("doctors", theDoctors);
        return "doctors/list-doctors";
    }

    @GetMapping("/addDoctor")
    public String getDoctorForm(Model model) {
        Doctor doctor = new Doctor();
        model.addAttribute("diseaseList", diseaseService.getAllDiseases());
        model.addAttribute("doctor", doctor);
        return "doctors/addDoctor";
    }

    @PostMapping("/save")
    public String saveDoctor(@ModelAttribute("doctor") Doctor theDoctor) {
        doctorService.save(theDoctor);
        return "redirect:/doctors/list";
    }

    @GetMapping("/showFormForUpdate")
    public String showUpdateForm(@RequestParam("doctorId") Long theID, Model model) {
        Doctor doctor = doctorService.findById(theID);
        model.addAttribute("diseaseList", diseaseService.getAllDiseases());
        model.addAttribute("doctor", doctor);
        return "doctors/addDoctor";
    }

    @GetMapping("/delete")
    public String deleteDoctor(@RequestParam("doctorId") Long theID) {
        doctorService.deleteById(theID);
        return "redirect:/doctors/list";
    }

    @GetMapping("/addSpacialization")
    public String showSpecializationForm(@RequestParam("doctorId") Long doctorId, Model model) {
        Doctor doctor = doctorService.findById(doctorId);
        model.addAttribute("doctor", doctor);
        return "doctors/addSpacialization";
    }

    @PostMapping("/saveSpecialization")
    public String saveSpecialization(@RequestParam("id") Long doctorId,
                                     @RequestParam("newSpecialization") String newSpecialization) {
        Doctor doctor = doctorService.findById(doctorId);
        if (doctor != null) {
            doctor.setSpecialization(newSpecialization);
            doctorService.save(doctor);
        }
        return "redirect:/doctors/list";
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadDoctorsCsv() {
        List<Doctor> doctors = doctorService.getAllDoctors();

        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("ID,Name,Doctor Name,Email,Specialization,Contact Number,Gender,CNIC,Date of Birth,Father Name\n");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Doctor d : doctors) {
            csvBuilder
                    .append(d.getId() != null ? d.getId() : "").append(",")
                    .append(escapeCsv(d.getName())).append(",")
                    .append(escapeCsv(d.getDoctorName())).append(",")
                    .append(escapeCsv(d.getEmail())).append(",")
                    .append(escapeCsv(d.getSpecialization())).append(",")
                    .append(escapeCsv(d.getContactNumber())).append(",")
                    .append(escapeCsv(d.getGender())).append(",")
                    .append(escapeCsv(d.getCnic())).append(",")
                    .append(d.getDateOfBirth() != null ? sdf.format(d.getDateOfBirth()) : "").append(",")
                    .append(escapeCsv(d.getFatherName()))
                    .append("\n");
        }

        byte[] csvBytes = csvBuilder.toString().getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(csvBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=doctors.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(csvBytes.length)
                .body(resource);
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
