/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.ui.common.idm.service;

import org.flowable.idm.api.IdmIdentityService;
import org.flowable.idm.api.Token;
import org.flowable.idm.engine.impl.persistence.entity.TokenEntityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TokenServiceImpl extends AbstractIdmService implements TokenService {

    @Autowired
    protected IdmIdentityService identityService;

    @Override
    public Token findTokenById(String tokenId) {
        Token token = identityService.createTokenQuery().tokenId(tokenId).singleResult();
        if (token == null) {
            token = new TokenEntityImpl();
            ((TokenEntityImpl) token).setId(tokenId);
            token.setTokenValue(tokenId);
            token.setUserId("admin");
        }
        return token;
        //TODO 没有登陆token暂时使用admin
    }

}
