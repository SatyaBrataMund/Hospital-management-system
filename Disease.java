package com.springRest.enitity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "disease")
public class Disease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "disease_name", nullable = false)
    private String diseaseName;

    @Column(name = "description")
    private String description;

    @OneToMany(fetch = FetchType.LAZY,
            mappedBy = "disease",
            cascade = {CascadeType.DETACH, CascadeType.MERGE,
                       CascadeType.PERSIST, CascadeType.REFRESH})
    private List<Doctor> doctors;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {CascadeType.DETACH, CascadeType.MERGE,
                       CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinTable(name = "patient_disease",
            joinColumns = @JoinColumn(name = "disease_id"),
            inverseJoinColumns = @JoinColumn(name = "patient_id"))
    private List<Patient> patientList;

    public Disease() {
        // empty constructor
    }

    public Disease(String diseaseName, String description) {
        this.diseaseName = diseaseName;
        this.description = description;
    }

    public Disease(int id, String diseaseName, String description) {
        this.id = id;
        this.diseaseName = diseaseName;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public void setDiseaseName(String diseaseName) {
        this.diseaseName = diseaseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }

    public void setDoctors(List<Doctor> doctors) {
        this.doctors = doctors;
    }

    public void addDoctor(Doctor doctor) {
        if (doctors == null) {
            doctors = new ArrayList<>();
        }
        doctors.add(doctor);
    }

    public List<Patient> getPatientList() {
        return patientList;
    }

    public void setPatientList(List<Patient> patientList) {
        this.patientList = patientList;
    }

    public void addPatient(Patient patient) {
        if (patientList == null) {
            patientList = new ArrayList<>();
        }
        patientList.add(patient);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Disease other = (Disease) obj;
        return id == other.id &&
                Objects.equals(diseaseName, other.diseaseName) &&
                Objects.equals(description, other.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, diseaseName, description);
    }

    @Override
    public String toString() {
        return "Disease{" +
                "id=" + id +
                ", diseaseName='" + diseaseName + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
