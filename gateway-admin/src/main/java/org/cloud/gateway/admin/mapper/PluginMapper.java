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

package org.cloud.gateway.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.cloud.gateway.admin.entity.PluginDO;
import org.cloud.gateway.admin.query.PluginQuery;

import java.util.List;

/**
 * PluginMapper.
 *
 * @author jiangxiaofeng(Nicholas)
 */
@Mapper
public interface PluginMapper {

    /**
     * select plugin by id.
     *
     * @param id primary key.
     * @return {@linkplain PluginDO}
     */
    PluginDO selectById(String id);

    /**
     * select plugin by query.
     *
     * @param pluginQuery {@linkplain PluginQuery}
     * @return {@linkplain List}
     */
    List<PluginDO> selectByQuery(PluginQuery pluginQuery);

    /**
     * count plugin by query.
     *
     * @param pluginQuery {@linkplain PluginQuery}
     * @return {@linkplain Integer}
     */
    Integer countByQuery(PluginQuery pluginQuery);

    /**
     * insert plugin.
     *
     * @param pluginDO {@linkplain PluginDO}
     * @return rows
     */
    int insert(PluginDO pluginDO);

    /**
     * insert selective plugin.
     *
     * @param pluginDO {@linkplain PluginDO}
     * @return rows
     */
    int insertSelective(PluginDO pluginDO);

    /**
     * update plugin.
     *
     * @param pluginDO {@linkplain PluginDO}
     * @return rows
     */
    int update(PluginDO pluginDO);

    /**
     * update selective plugin.
     *
     * @param pluginDO {@linkplain PluginDO}
     * @return rows
     */
    int updateSelective(PluginDO pluginDO);

    /**
     * delete plugin.
     *
     * @param id primary key.
     * @return rows
     */
    int delete(String id);
}
