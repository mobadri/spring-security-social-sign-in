package com.badri.springsecuritysocialsignin.util;

import com.badri.springsecuritysocialsignin.model.MyUser;
import com.badri.springsecuritysocialsignin.repo.MyUserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private MyUserRepository userRepository;
    @Autowired
    @Lazy
    private RedirectStrategy redirectStrategy;
    //
//    @Autowired
//    ClientRegistrationRepository clientRegistrationRepository;
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String refreshToken = getRefreshToken(authentication);
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

//                String refreshToken = getRefreshToken(authentication);

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


    // after customizing the facebook connect user
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        if (userRepository.findByUserName(authentication.getName()).isEmpty()) {
//            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
//            Map<String, Object> attributes = token.getPrincipal().getAttributes();
//
//            String firstName = attributes.get("given_name").toString();
//            String lastName = attributes.get("family_name").toString();
//            String email = attributes.get("email").toString();
//
//            MyUser user = new MyUser();
//            user.setUserName(authentication.getName());
//            user.setFirstName(firstName);
//            user.setLastName(lastName);
//            user.setEmail(email);
//            userRepository.save(user);
//        }
//        redirectStrategy.sendRedirect(request, response, "/profile");
//    }


    public String getRefreshToken(Authentication authentication) {
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId(),
                authentication.getName());

        OAuth2AccessToken newOAuth2AccessToken = refreshAccessToken(client.getRefreshToken().getTokenValue(),
                client.getClientRegistration().getClientId(),
                client.getClientRegistration().getClientSecret(),
                client.getClientRegistration().getProviderDetails().getTokenUri());

        updateAccessToken(newOAuth2AccessToken,
                client.getClientRegistration().getRegistrationId(),
                authentication);

        if (client != null && client.getRefreshToken() != null) {
            return client.getRefreshToken().getTokenValue();
        }
//        clientRegistrationRepository.findByRegistrationId("")
        return null;
    }

    public OAuth2AccessToken refreshAccessToken(String refreshToken, String clientId, String clientSecret, String tokenUri) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String body = "grant_type=refresh_token"
                + "&refresh_token=" + refreshToken
                + "&client_id=" + clientId
                + "&client_secret=" + clientSecret;
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(tokenUri, request, Map.class);

        Map<String, Object> responseBody = response.getBody();
        String newAccessToken = (String) responseBody.get("access_token");
        return new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, newAccessToken, null, null);
    }

    public void updateAccessToken(OAuth2AccessToken newAccessToken, String clientRegistrationId, Authentication principal) {
        // Retrieve the current OAuth2AuthorizedClient for the user
        OAuth2AuthorizedClient currentAuthorizedClient = authorizedClientService.loadAuthorizedClient(
                clientRegistrationId, principal.getName());

        if (currentAuthorizedClient == null) {
            throw new IllegalArgumentException("No authorized client found for user");
        }

        // Create a new OAuth2AuthorizedClient with the updated access token
        OAuth2AuthorizedClient updatedAuthorizedClient = new OAuth2AuthorizedClient(
                currentAuthorizedClient.getClientRegistration(),
                currentAuthorizedClient.getPrincipalName(),
                newAccessToken,
                currentAuthorizedClient.getRefreshToken());

        // Update the OAuth2AuthorizedClient in the service
        authorizedClientService.saveAuthorizedClient(updatedAuthorizedClient, principal);
    }

}
