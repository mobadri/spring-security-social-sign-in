package com.badri.springsecuritysocialsignin.repo;

import com.badri.springsecuritysocialsignin.model.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface MyUserRepository extends JpaRepository<MyUser, Long> {

    Optional<MyUser> findByUserName(String userName);
}
