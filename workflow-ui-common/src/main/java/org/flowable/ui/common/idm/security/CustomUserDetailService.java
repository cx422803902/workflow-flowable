package org.flowable.ui.common.idm.security;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * @author chen.xing<br>
 * @version 1.0<br>
 * @Desc good study day day up
 * @data 2019<br>
 */
public interface CustomUserDetailService {

    UserDetails loadByUserId(final String userId);

    UserDetails loadUserByUsername(final String username);

}