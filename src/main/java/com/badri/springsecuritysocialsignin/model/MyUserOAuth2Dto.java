package com.badri.springsecuritysocialsignin.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MyUserOAuth2Dto {

    private final String firstname;
    private final String lastname;
    private final String username;
    private final String email;
}
