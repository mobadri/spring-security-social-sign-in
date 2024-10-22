package com.badri.springsecuritysocialsignin.controller;

import com.badri.springsecuritysocialsignin.model.MyUser;
import com.badri.springsecuritysocialsignin.repo.MyUserRepository;
import com.badri.springsecuritysocialsignin.service.MyUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.Optional;

@Controller
public class ContentController {

    @Autowired
    private MyUserService userService;

    @Autowired
    private MyUserRepository repository;

    @GetMapping("/login")
    public String login() {
        return "custom-login";
    }

    @GetMapping("/register")
    public String register() {
        return "custom-register";
    }

    @PostMapping("/register")
    public String registerNewUser(@RequestParam String username,
                                  @RequestParam String firstname,
                                  @RequestParam String lastname,
                                  @RequestParam String email,
                                  @RequestParam String password,
                                  @RequestParam String roles,
                                  Model model) {
        MyUser user = new MyUser();
        user.setUserName(username);
        user.setFirstName(firstname);
        user.setLastName(lastname);
        user.setEmail(email);
        user.setPassword(password);
        user.setRoles(roles);
        Optional<MyUser> optionalUser = repository.findByUserName(username);
        if (optionalUser.isPresent()) {
            model.addAttribute("error", "Username is already taken");
            return "custom-register";
        }
        userService.createUser(user);
        return "redirect:/login";
    }

//    @GetMapping("/profile")
//    public String userProfile(OAuth2AuthenticationToken token, Model model) {
//        DefaultOidcUser oidcToken = (DefaultOidcUser) token.getPrincipal();
//
//        String username = oidcToken.getSubject();
//        String firstname = oidcToken.getGivenName();
//        String lastname = oidcToken.getFamilyName();
//        String email = oidcToken.getEmail();
//
//        model.addAttribute("username", username);
//        model.addAttribute("firstname", firstname);
//        model.addAttribute("lastname", lastname);
//        model.addAttribute("email", email);
//        return "user-profile";
//    }

    @GetMapping("/profile")
    public String userProfile(Authentication authentication, Model model) {
        Object principal = authentication.getPrincipal();
        String username = "";
        String firstname = "";
        String lastname = "";
        String email = "";

        if (principal instanceof User) { // username & password authentication
            MyUser user = null;
            username = authentication.getName();
            Optional<MyUser> optionalUser = repository.findByUserName(username);
            if (optionalUser.isPresent()) {
                user = optionalUser.get();
            }
            username = user.getUserName();
            firstname = user.getFirstName();
            lastname = user.getLastName();
            email = user.getEmail();
        } else if (principal instanceof DefaultOAuth2User) {
            Map<String, Object> attributes = ((DefaultOAuth2User) principal).getAttributes();
            if (principal instanceof DefaultOidcUser) {
                username = attributes.get("sub").toString();
                firstname = attributes.get("given_name").toString();
                lastname = attributes.get("family_name").toString();
                email = attributes.get("email").toString();
            } else if (((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId()
                    .equals("facebook")) {
                // I don't use facebook
                String name = attributes.get("name").toString();
                firstname = name.split(" ")[0];
                lastname = name.split(" ")[1];
            }

        }


        model.addAttribute("username", username);
        model.addAttribute("firstname", firstname);
        model.addAttribute("lastname", lastname);
        model.addAttribute("email", email);
        return "user-profile";
    }
}
