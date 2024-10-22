package com.badri.springsecuritysocialsignin.userdetails;

public interface MyUserAuthenticatedPrincipal {

    String getFirstName();

    String getLastName();

    String getFirstAndLastName();

    String getEmail();
}
