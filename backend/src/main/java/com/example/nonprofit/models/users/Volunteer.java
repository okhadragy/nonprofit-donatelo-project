package com.example.nonprofit.models.users;

import com.example.nonprofit.models.VolunteerManagement.Role;
import com.example.nonprofit.models.VolunteerManagement.Qualification;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

@Entity
@DiscriminatorValue("VOLUNTEER")
public class Volunteer extends User {

    private String status;

    protected Volunteer() {
    }

    public Volunteer(
            String name,
            String email,
            String username,
            String password) {
        super(name, email, username, password, "VOLUNTEER");
        this.status = "PENDING";
    }

    @ManyToMany
    @JoinTable(name = "volunteer_preferred_roles", joinColumns = @JoinColumn(name = "volunteer_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> preferredRoles = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "volunteer_qualifications", joinColumns = @JoinColumn(name = "volunteer_id"), inverseJoinColumns = @JoinColumn(name = "qualification_id"))
    private List<Qualification> qualifications = new ArrayList<>();

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Role> getPreferredRoles() {
        return preferredRoles;
    }

    public void setPreferredRoles(List<Role> preferredRoles) {
        this.preferredRoles = preferredRoles;
    }

    public List<Qualification> getQualifications() {
        return qualifications;
    }

    public void setQualifications(List<Qualification> qualifications) {
        this.qualifications = qualifications;
    }
}