package com.badri.springsecuritysocialsignin.service;

import com.badri.springsecuritysocialsignin.model.MyUser;
import com.badri.springsecuritysocialsignin.repo.MyUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MyUserService {

    @Autowired
    private MyUserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public MyUser createUser(MyUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.save(user);
    }
}
