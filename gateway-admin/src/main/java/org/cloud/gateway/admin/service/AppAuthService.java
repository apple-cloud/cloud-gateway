/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 */

package org.cloud.gateway.admin.service;

import org.cloud.gateway.admin.dto.AppAuthDTO;
import org.cloud.gateway.admin.page.CommonPager;
import org.cloud.gateway.admin.query.AppAuthQuery;
import org.cloud.gateway.admin.vo.AppAuthVO;

import java.util.List;

/**
 * this is application authority service.
 *
 * @author xiaoyu(Myth)
 */
public interface AppAuthService {

    /**
     * create or update application authority.
     *
     * @param appAuthDTO {@linkplain AppAuthDTO}
     * @return rows
     */
    int createOrUpdate(AppAuthDTO appAuthDTO);

    /**
     * delete application authorities.
     *
     * @param ids primary key.
     * @return rows
     */
    int delete(List<String> ids);

    /**
     * find application authority by id.
     *
     * @param id primary key.
     * @return {@linkplain AppAuthVO}
     */
    AppAuthVO findById(String id);

    /**
     * find page of application authority by query.
     *
     * @param appAuthQuery {@linkplain AppAuthQuery}
     * @return {@linkplain CommonPager}
     */
    CommonPager<AppAuthVO> listByPage(AppAuthQuery appAuthQuery);
}
