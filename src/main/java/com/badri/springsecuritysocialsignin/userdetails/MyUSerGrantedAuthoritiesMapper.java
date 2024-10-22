package com.badri.springsecuritysocialsignin.userdetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class MyUSerGrantedAuthoritiesMapper implements GrantedAuthoritiesMapper {

    // GrantedAuthoritiesMapper called after the OAuth2 user object is retrieved by the user service
    // by default the user service will add the role USER and also scopes to the granted authorities
    // the mapper allows to adjust the authorities.
    // authentication process not yet complete -> there won't be an authentication object in the security context
    // and to give you access to the token and claims, the user service add either an OidcUserAuthority
    // or an OAuth2UserAuthority to the GrantedAuthorities (Collection<? extends GrantedAuthority> authorities)
    // which you can then retrieve and use to map claims to authorities (Set<GrantedAuthority> mappedAuthorities)
    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
        authorities.forEach(authority -> {
            if (authority instanceof OidcUserAuthority) {
                OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;
                OidcIdToken idToken = oidcUserAuthority.getIdToken();
                OidcUserInfo userInfo = oidcUserAuthority.getUserInfo();

                Map<String, Object> userAttributes = oidcUserAuthority.getAttributes();

            } else if (authority instanceof OAuth2UserAuthority) {
                OAuth2UserAuthority oAuth2UserAuthority = (OAuth2UserAuthority) authority;
                Map<String, Object> userAttributes = oAuth2UserAuthority.getAttributes();
            }
            mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        });
        return mappedAuthorities;
    }
}
