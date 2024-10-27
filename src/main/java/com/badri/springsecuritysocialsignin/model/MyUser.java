package com.badri.springsecuritysocialsignin.model;

import com.badri.springsecuritysocialsignin.userdetails.MyUserAuthenticatedPrincipal;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class MyUser implements MyUserAuthenticatedPrincipal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String roles;

    @Override
    public String getFirstAndLastName() {
        return firstName + " " + lastName;
    }
}
