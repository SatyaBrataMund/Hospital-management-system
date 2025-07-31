package com.springRest.Controller;

import com.springRest.enitity.Medicine;
import com.springRest.enitity.Patient;
import com.springRest.service.MedicineService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;

@Controller
@RequestMapping("/medicines")
public class MedicineController {

    private final MedicineService medicineService;

    @Autowired
    private EntityManager em;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping("/list")
    public String listmedicines(Model theModel) {
        List<Medicine> themedicines = medicineService.getAllmedicines();
        theModel.addAttribute("medicines", themedicines);
        return "medicine/list-medicines";
    }

    @GetMapping("/addMedicine")
    public String getmedicineForm(Model model) {
        Medicine themedicine = new Medicine();
        model.addAttribute("medicine", themedicine);
        return "medicine/addMedicine";
    }

    @PostMapping("/save")
    public String savemedicine(@ModelAttribute("medicine") Medicine themedicine) {
        medicineService.save(themedicine);
        return "redirect:/medicines/list";
    }

    @GetMapping("/showFormForUpdate")
    public String showUpdateForm(@RequestParam("medicineId") int theID, Model model) {
        Medicine medicine = medicineService.findById(theID);
        model.addAttribute("medicine", medicine);
        return "medicine/addMedicine";
    }

    @GetMapping("/delete")
    public String deletemedicine(@RequestParam("medicineId") int theID) {
        Medicine a = em.find(Medicine.class, theID);
        for (Patient b : a.getPatientList()) {
            if (b.getMedicineList().size() == 1) {
                em.remove(b);
            } else {
                b.getMedicineList().remove(a);
            }
        }
        em.remove(a);
        medicineService.deleteById(theID);
        return "redirect:/medicines/list";
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadMedicinesCsv() throws IOException {
        List<Medicine> medicines = medicineService.getAllmedicines();

        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("ID,Medicine Name,Company Name,Manufacture Date,Expiry Date,Type\n");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (Medicine med : medicines) {
            csvBuilder
                    .append(med.getId()).append(",")
                    .append(escapeCsv(med.getMedicineName())).append(",")
                    .append(escapeCsv(med.getCompanyName())).append(",")
                    .append(med.getManufactureDate() != null ? sdf.format(med.getManufactureDate()) : "").append(",")
                    .append(med.getExpiryDate() != null ? sdf.format(med.getExpiryDate()) : "").append(",")
                    .append(escapeCsv(med.getType())).append("\n");
        }

        byte[] csvBytes = csvBuilder.toString().getBytes(StandardCharsets.UTF_8);
        ByteArrayResource resource = new ByteArrayResource(csvBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=medicines.csv")
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
