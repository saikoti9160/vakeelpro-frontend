package com.digiworld.vakeelpro.entities;

import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "organizations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String lawFirmData; // JSON or additional fields for analytics

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    private Set<User> staff; // All staff under this organization

    @OneToMany(mappedBy = "organization", fetch = FetchType.LAZY)
    private Set<Case> cases; // Cases tied to this organization

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Organization)) return false;
        Organization org = (Organization) o;
        return id != null && id.equals(org.id);
    }
}