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
package org.flowable.ui.common.idm.conf;

import org.flowable.ui.common.idm.properties.FlowableIdmAppProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(FlowableIdmAppProperties.class)
@ComponentScan(basePackages = {
        "org.flowable.ui.common.idm.conf",
        "org.flowable.ui.common.idm.security",
        "org.flowable.ui.common.idm.service"})
public class IdmApplicationConfiguration {
}
