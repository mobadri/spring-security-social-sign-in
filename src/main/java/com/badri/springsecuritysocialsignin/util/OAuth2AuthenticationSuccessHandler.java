package com.badri.springsecuritysocialsignin.util;

import com.badri.springsecuritysocialsignin.model.MyUser;
import com.badri.springsecuritysocialsignin.repo.MyUserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private MyUserRepository userRepository;
    @Autowired
    @Lazy
    private RedirectStrategy redirectStrategy;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (userRepository.findByUserName(authentication.getName()).isEmpty()) {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
            Map<String, Object> attributes = token.getPrincipal().getAttributes();
            String firstName = null;
            String lastName = null;
            String email = null;
            if (token.getAuthorizedClientRegistrationId().equals("facebook")) { // since facebook is not OIDC provider
                String name = attributes.get("name").toString();
                firstName = name.split(" ")[0];
                lastName = name.split(" ")[1];
                email = attributes.get("email").toString();
            } else if (token.getPrincipal() instanceof DefaultOidcUser) { // OIDC provider user
//                OidcIdToken idToken = ((DefaultOidcUser) token.getPrincipal()).getIdToken();
                DefaultOidcUser oidcToken = (DefaultOidcUser) token.getPrincipal();
                firstName = oidcToken.getGivenName();
                lastName = oidcToken.getFamilyName();
                email = oidcToken.getEmail();
            }
            MyUser user = new MyUser();
            user.setUserName(authentication.getName());
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            userRepository.save(user);
        }
        redirectStrategy.sendRedirect(request, response, "/profile");
    }
}
