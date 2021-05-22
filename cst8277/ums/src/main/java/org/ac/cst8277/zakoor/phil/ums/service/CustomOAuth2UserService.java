package org.ac.cst8277.zakoor.phil.ums.service;

import org.ac.cst8277.zakoor.phil.ums.dao.UmsRepository;
import org.ac.cst8277.zakoor.phil.ums.dtos.GitHubUser;
import org.ac.cst8277.zakoor.phil.ums.dtos.Roles;
import org.ac.cst8277.zakoor.phil.ums.dtos.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService  {

    @Autowired
    private UmsRepository umsRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User user =  super.loadUser(userRequest);

        try {
            return processOidcUser(userRequest, user);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOidcUser(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        GitHubUser gitHubUser = new GitHubUser(oAuth2User);

        User user = umsRepository.findUserByEmail(gitHubUser.getEmail());
        if (user.getId() == null) {
            user.setEmail(gitHubUser.getEmail());
            user.setName(gitHubUser.getName());

            Map<String, Roles> roles = umsRepository.findAllRoles();
            List<Roles> userRoles = new ArrayList<>();
            userRoles.add(roles.get(Roles.Role.SUBSCRIBER));

            umsRepository.createUser(user);
            user = umsRepository.findUserByEmail(gitHubUser.getEmail());
        }
        umsRepository.createLogin(user.getId());

        return gitHubUser;
    }

}
