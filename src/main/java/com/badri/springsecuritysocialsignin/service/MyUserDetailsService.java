package com.badri.springsecuritysocialsignin.service;

import com.badri.springsecuritysocialsignin.model.MyUser;
import com.badri.springsecuritysocialsignin.repo.MyUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private MyUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<MyUser> myUser = userRepository.findByUserName(username);
        if (myUser.isPresent()) {
            MyUser user = myUser.get();
            return User.builder()
                    .username(user.getUserName())
                    .password(user.getPassword())
                    .roles(getRoles(user))
                    .build();
        } else {
            throw new UsernameNotFoundException(username);
        }
    }

    private String[] getRoles(MyUser user) {
        if (user.getRoles() == null) {
            return new String[]{"USER"};
        } else {
            return user.getRoles().split(",");
        }
    }
}
