package org.flowable.ui.common.idm.security;

import org.flowable.idm.api.IdmIdentityService;
import org.flowable.ui.common.security.FlowableAppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * @author chen.xing<br>
 * @version 1.0<br>
 * @Desc good study day day up
 * @data 2019<br>
 */
@Component
public class CustomerAuthenticationProvider implements AuthenticationProvider {
    protected org.springframework.security.core.userdetails.UserDetailsService userDetailsService;
    protected IdmIdentityService identityService;

    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    protected GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Autowired
    public CustomerAuthenticationProvider(UserDetailsService userDetailsService, IdmIdentityService identityService) {
        this.userDetailsService = userDetailsService;
        this.identityService = identityService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) authentication;

        boolean authenticated = identityService.checkPassword(authenticationToken.getName(), authenticationToken.getCredentials().toString());
        if (!authenticated) {
            throw new BadCredentialsException(messages.getMessage("LdapAuthenticationProvider.badCredentials", "Bad credentials"));
        }

        FlowableAppUser userDetails = (FlowableAppUser) userDetailsService.loadUserByUsername(authenticationToken.getName());

        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
                userDetails, authenticationToken.getCredentials(),
                authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));
        result.setDetails(authentication.getDetails());

        return result;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
