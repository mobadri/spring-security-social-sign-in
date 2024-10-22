package com.badri.springsecuritysocialsignin.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class MyUser {

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

}
