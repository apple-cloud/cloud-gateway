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

package org.cloud.gateway.bootstrap.zookeeper;

import com.google.common.collect.Maps;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.util.Lists;
import org.cloud.gateway.bootstrap.BaseTest;
import org.cloud.gateway.common.constant.ZkPathConstants;
import org.cloud.gateway.common.dto.convert.DivideUpstream;
import org.cloud.gateway.common.dto.convert.RateLimiterHandle;
import org.cloud.gateway.common.dto.convert.RewriteHandle;
import org.cloud.gateway.common.dto.convert.WafHandle;
import org.cloud.gateway.common.dto.convert.rule.DivideRuleHandle;
import org.cloud.gateway.common.dto.convert.rule.DubboRuleHandle;
import org.cloud.gateway.common.dto.convert.rule.SpringCloudRuleHandle;
import org.cloud.gateway.common.dto.convert.selector.DubboSelectorHandle;
import org.cloud.gateway.common.dto.convert.selector.SpringCloudSelectorHandle;
import org.cloud.gateway.common.dto.zk.AppAuthZkDTO;
import org.cloud.gateway.common.dto.zk.ConditionZkDTO;
import org.cloud.gateway.common.dto.zk.PluginZkDTO;
import org.cloud.gateway.common.dto.zk.RuleZkDTO;
import org.cloud.gateway.common.dto.zk.SelectorZkDTO;
import org.cloud.gateway.common.enums.LoadBalanceEnum;
import org.cloud.gateway.common.enums.MatchModeEnum;
import org.cloud.gateway.common.enums.OperatorEnum;
import org.cloud.gateway.common.enums.ParamTypeEnum;
import org.cloud.gateway.common.enums.PluginEnum;
import org.cloud.gateway.common.enums.SelectorTypeEnum;
import org.cloud.gateway.common.enums.WafEnum;
import org.cloud.gateway.common.utils.JsonUtils;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * The type Zookeeper client test.
 */
@SuppressWarnings("all")
public class ZookeeperClientTest extends BaseTest {


    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperClientTest.class);

    @Autowired
    private ZkClient zkClient;

    private static final String ROOT_PATH = "/xiaoyu";

    private static final String PLUGIN = "/soul/plugin";

    private static Map<String, PluginZkDTO> pluginZkDTOMap = Maps.newConcurrentMap();

    /**
     * Test.
     */
    @Test
    public void test() {

        if (!zkClient.exists(ROOT_PATH)) {
            zkClient.createPersistent(ROOT_PATH, true);
        }
        zkClient.writeData(ROOT_PATH, new PluginZkDTO("1", PluginEnum.DIVIDE.getName(), Boolean.TRUE));
        final Object o = zkClient.readData(ROOT_PATH);
        System.out.println(o.toString());
    }

    /**
     * Test insert app auth.
     */
    @Test
    public void testInsertAppAuth() {
        AppAuthZkDTO appAuthZkDTO = new AppAuthZkDTO();
        appAuthZkDTO.setAppKey("gateway");
        appAuthZkDTO.setAppSecret("123456");
        appAuthZkDTO.setEnabled(true);

        final String path = ZkPathConstants.buildAppAuthPath(appAuthZkDTO.getAppKey());

        if (!zkClient.exists(path)) {
            zkClient.createPersistent(path, true);
        }
        zkClient.writeData(path, appAuthZkDTO);

        final Object o = zkClient.readData(path);
        System.out.println(o.toString());

    }


    /**
     * Test write divide selector.
     */
    @Test
    public void testWriteDivideSelector() {

        final SelectorZkDTO selectorZkDTO = buildSelectorZkDTO("eee", "eeex", PluginEnum.DIVIDE.getName());

        writeSelector(selectorZkDTO);

    }


    /**
     * Test write dubbo selector and rule.
     */
    @Test
    public void testWriteDubboSelectorAndRule() {

        writeSelectorAndRule(PluginEnum.DUBBO.getName());
    }


    /**
     * Test write rate limiter selector.
     */
    @Test
    public void testWriteRateLimiterSelector() {

        writeSelectorAndRule(PluginEnum.RATE_LIMITER.getName());
    }


    /**
     * Test write waf selector.
     */
    @Test
    public void testWriteWafSelector() {
        writeSelectorAndRule(PluginEnum.WAF.getName());
    }


    /**
     * Test write rewrite selector.
     */
    @Test
    public void testWriteRewriteSelector() {
        writeSelectorAndRule(PluginEnum.REWRITE.getName());
    }

    private void writeSelectorAndRule(String pluginName) {
        final SelectorZkDTO selectorZkDTO = buildSelectorZkDTO("aaa", "aaa", pluginName);

        writeSelector(selectorZkDTO);

        final RuleZkDTO ruleZkDTO = buildRuleDTO("xxx", selectorZkDTO.getId(), selectorZkDTO.getPluginName());

        final String rulePath = ZkPathConstants
                .buildRulePath(selectorZkDTO.getPluginName(), ruleZkDTO.getSelectorId(), ruleZkDTO.getId());
        writeRule(rulePath, ruleZkDTO);
    }


    /**
     * Test insert rule.
     */
    @Test
    public void testInsertRule() {
        final RuleZkDTO ruleZkDTO = buildRuleDTO("aaa", "eee", PluginEnum.DIVIDE.getName());
        final String rulePath = ZkPathConstants
                .buildRulePath(PluginEnum.DIVIDE.getName(), ruleZkDTO.getSelectorId(), ruleZkDTO.getId());
        if (!zkClient.exists(rulePath)) {
            zkClient.createPersistent(rulePath, true);
        }

        zkClient.writeData(rulePath, ruleZkDTO);

        final RuleZkDTO zkDTO = zkClient.readData(rulePath);
        LOGGER.info(zkDTO.toString());

    }

    private void writeRule(String rulePath, RuleZkDTO ruleZkDTO) {
        if (!zkClient.exists(rulePath)) {
            zkClient.createPersistent(rulePath, true);
        }

        zkClient.writeData(rulePath, ruleZkDTO);
    }


    private void writeSelector(SelectorZkDTO selectorZkDTO) {
        final String selectorRealPath =
                ZkPathConstants.buildSelectorRealPath(selectorZkDTO.getPluginName(), selectorZkDTO.getId());
        if (!zkClient.exists(selectorRealPath)) {
            zkClient.createPersistent(selectorRealPath, true);
        }
        zkClient.writeData(selectorRealPath, selectorZkDTO);
    }


    private SelectorZkDTO buildSelectorZkDTO(String id, String name, String pluginName) {
        SelectorZkDTO selectorZkDTO = new SelectorZkDTO();
        selectorZkDTO.setId(id);
        selectorZkDTO.setName(name);
        selectorZkDTO.setSort(1);
        selectorZkDTO.setContinued(Boolean.TRUE);
        selectorZkDTO.setLoged(Boolean.TRUE);
        selectorZkDTO.setEnabled(Boolean.TRUE);
        selectorZkDTO.setPluginName(pluginName);
        selectorZkDTO.setType(SelectorTypeEnum.FULL_FLOW.getCode());
        selectorZkDTO.setMatchMode(MatchModeEnum.AND.getCode());
        final ConditionZkDTO conditionZkDTO = buildConditionZkDTO();
        selectorZkDTO.setConditionZkDTOList(Collections.singletonList(conditionZkDTO));
        return selectorZkDTO;
    }

    private RuleZkDTO buildRuleDTO(String id, String selectorId, String pluginName) {
        RuleZkDTO dto1 = new RuleZkDTO();
        dto1.setId(id);
        dto1.setSelectorId(selectorId);
        dto1.setName("宇测试");
        dto1.setConditionZkDTOList(Collections.singletonList(buildConditionZkDTO()));
        dto1.setEnabled(true);
        dto1.setLoged(Boolean.TRUE);
        dto1.setMatchMode(MatchModeEnum.AND.getCode());
        if (PluginEnum.DIVIDE.getName().equals(pluginName)) {
            final String jsonStr = JsonUtils.toJson(buildDivideHandle().getRight());
            dto1.setHandle(jsonStr);
        } else if (PluginEnum.RATE_LIMITER.getName().equals(pluginName)) {
            final String jsonStr = JsonUtils.toJson(buildRateLimiterHandle());
            dto1.setHandle(jsonStr);
        } else if (PluginEnum.WAF.getName().equals(pluginName)) {
            dto1.setHandle(JsonUtils.toJson(buildWafHandle()));
        } else if (PluginEnum.REWRITE.getName().equals(pluginName)) {
            dto1.setHandle(JsonUtils.toJson(buildRewriteHandle()));
        } else if (PluginEnum.DUBBO.getName().equals(pluginName)) {
            dto1.setHandle(JsonUtils.toJson(buildDubboHandle()));
        }
        dto1.setSort(120);
        return dto1;
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        System.out.println(JsonUtils.toJson(buildSpringCloudHandle()));
    }

    private static Pair<List<DivideUpstream>, DivideRuleHandle> buildDivideHandle() {

        DivideRuleHandle ruleHandle = new DivideRuleHandle();

        ruleHandle.setLoadBalance(LoadBalanceEnum.ROUND_ROBIN.getName());
        ruleHandle.setCommandKey("PDM");
        ruleHandle.setGroupKey("pdm");

        ruleHandle.setTimeout(1000);

        return new ImmutablePair<>(buildUpstreamList(), ruleHandle);
    }

    private static Pair<DubboSelectorHandle, DubboRuleHandle> buildDubboHandle() {
        DubboSelectorHandle selectorHandle = new DubboSelectorHandle();
        selectorHandle.setAppName("local");
        selectorHandle.setRegistry("zookeeper://localhost:2181");

        DubboRuleHandle ruleHandle = new DubboRuleHandle();

        ruleHandle.setTimeout(3000);
        ruleHandle.setGroupKey("xiaoyu");
        ruleHandle.setCommandKey("xiaoyu");

        return new ImmutablePair<>(selectorHandle, ruleHandle);
    }

    private static RateLimiterHandle buildRateLimiterHandle() {
        RateLimiterHandle rateLimiterHandle = new RateLimiterHandle();
        rateLimiterHandle.setBurstCapacity(1);
        rateLimiterHandle.setReplenishRate(1);
        return rateLimiterHandle;
    }


    private static WafHandle buildWafHandle() {
        WafHandle wafHandle = new WafHandle();
        wafHandle.setPermission(WafEnum.ALLOW.getName());
        wafHandle.setStatusCode("403");
        return wafHandle;
    }

    private static RewriteHandle buildRewriteHandle() {
        RewriteHandle rewriteHandle = new RewriteHandle();
        rewriteHandle.setRewriteURI("rewrite");
        return rewriteHandle;
    }


    private static Pair<SpringCloudSelectorHandle, SpringCloudRuleHandle> buildSpringCloudHandle() {

        SpringCloudSelectorHandle selectorHandle = new SpringCloudSelectorHandle();
        selectorHandle.setServiceId("xiaoyu");

        SpringCloudRuleHandle ruleHandle = new SpringCloudRuleHandle();

        ruleHandle.setPath("/xiaoyu");
        return new ImmutablePair<>(selectorHandle, ruleHandle);
    }

    private static List<DivideUpstream> buildUpstreamList() {
        List<DivideUpstream> upstreams = Lists.newArrayList();
        DivideUpstream upstream = new DivideUpstream();
        upstream.setUpstreamHost("localhost");
        upstream.setUpstreamUrl("http://localhost:8081");
        upstream.setWeight(90);
        upstreams.add(upstream);
        return upstreams;
    }

    /**
     * Test delete.
     */
    @Test
    public void testDelete() {
        final String dividePath = ZkPathConstants.buildSelectorParentPath(PluginEnum.DIVIDE.getName());
        zkClient.delete(dividePath);
    }


    private ConditionZkDTO buildConditionZkDTO() {
        ConditionZkDTO condition = new ConditionZkDTO();
        condition.setOperator(OperatorEnum.EQ.getAlias());
        condition.setParamName("module");
        condition.setParamValue("pdm");
        condition.setParamType(ParamTypeEnum.POST.getName());
        return condition;
    }


    /**
     * Test write plugin.
     */
    @Test
    public void testWritePlugin() {

        Arrays.stream(PluginEnum.values()).forEach(pluginEnum -> {
            String pluginPath = ZkPathConstants.buildPluginPath(pluginEnum.getName());
            if (!zkClient.exists(pluginPath)) {
                zkClient.createPersistent(pluginPath, true);
            }
            zkClient.writeData(pluginPath, buildByName(pluginEnum.getName()));
            PluginZkDTO data = zkClient.readData(pluginPath);
            LOGGER.debug(data.toString());
        });


    }

    /**
     * Test load plugin data.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void testLoadPluginData() throws InterruptedException {
        Arrays.stream(PluginEnum.values()).forEach(pluginEnum -> {
            String pluginPath = PLUGIN + "/" + pluginEnum.getName();
            PluginZkDTO data = zkClient.readData(pluginPath);
            pluginZkDTOMap.put(pluginEnum.getName(), data);

            zkClient.subscribeDataChanges(pluginPath, new IZkDataListener() {
                @Override
                public void handleDataChange(String dataPath, Object data) {
                    LOGGER.info("node data changed!");
                    LOGGER.info("path=>" + dataPath);
                    LOGGER.info("data=>" + data);
                    final String key = dataPath
                            .substring(dataPath.lastIndexOf("/") + 1, dataPath.length());
                    pluginZkDTOMap.put(key, (PluginZkDTO) data);
                }

                @Override
                public void handleDataDeleted(String dataPath) {

                }
            });

        });

        LOGGER.info("ready!");

        //junit测试时，防止线程退出
        while (true) {
            TimeUnit.SECONDS.sleep(5);
        }

    }

    /**
     * Test update plugin.
     */
    @Test
    public void testUpdatePlugin() {
        String divide = PLUGIN + "/" + PluginEnum.DIVIDE.getName();
        PluginZkDTO divideDTO = new PluginZkDTO("3", PluginEnum.DIVIDE.getName(), false);
        zkClient.writeData(divide, divideDTO);

        String global = PLUGIN + "/" + PluginEnum.GLOBAL.getName();
        PluginZkDTO globalDTO = new PluginZkDTO("4", PluginEnum.GLOBAL.getName(), false);
        zkClient.writeData(global, globalDTO);


    }


    private PluginZkDTO buildByName(String name) {
        PluginZkDTO pluginZkDTO = new PluginZkDTO();
        pluginZkDTO.setEnabled(true);
        pluginZkDTO.setId("1");
        pluginZkDTO.setName(name);
        return pluginZkDTO;
    }


    /**
     * Test plugin.
     */
    @Test
    public void testPlugin() {
        if (!zkClient.exists(PLUGIN)) {
            zkClient.createPersistent(PLUGIN, true);
        }
        zkClient.writeData(PLUGIN, buildMap());
        Map<String, PluginZkDTO> resultMap = zkClient.readData(PLUGIN);
        resultMap.forEach((k, v) -> LOGGER.debug(k + ":" + v.toString()));

    }

    /**
     * Dispose.
     */
    @After
    public void dispose() {
        zkClient.close();
        LOGGER.info("zkclient closed!");
    }

    /**
     * Test plugin update.
     */
    @Test
    public void testPluginUpdate() {
        final Map<String, PluginZkDTO> map = buildMap();
        map.put(PluginEnum.DIVIDE.getName(), new PluginZkDTO("2", PluginEnum.DIVIDE.getName(), Boolean.FALSE));
        zkClient.writeData(PLUGIN, map);
    }

    /**
     * Test listener.
     *
     * @throws InterruptedException the interrupted exception
     */
    @Test
    public void testListener() throws InterruptedException {
        //监听指定节点的数据变化
        zkClient.subscribeDataChanges(PLUGIN, new IZkDataListener() {
            @Override
            public void handleDataChange(String path, Object o) {
                LOGGER.info("node data changed!");
                LOGGER.info("path=>" + path);
                LOGGER.info("data=>" + o);
                Map<String, PluginZkDTO> map;
                map = (Map<String, PluginZkDTO>) o;
                LOGGER.info(map.toString());
                LOGGER.info("--------------");
            }

            @Override
            public void handleDataDeleted(String path) {
                LOGGER.info("node data deleted!");
                LOGGER.info("path=>" + path);
                LOGGER.info("--------------");

            }
        });

        LOGGER.info("ready!");

        //junit测试时，防止线程退出
        while (true) {
            TimeUnit.SECONDS.sleep(5);
        }
    }


    /**
     * Test update config.
     */
    @Test
    public void testUpdateConfig() {
        if (!zkClient.exists(ROOT_PATH)) {
            zkClient.createPersistent(ROOT_PATH);
        }
        zkClient.writeData(ROOT_PATH, "1");
        zkClient.writeData(ROOT_PATH, "2");
        zkClient.delete(ROOT_PATH);
        zkClient.delete(ROOT_PATH);//删除一个不存在的node，并不会报错
    }

    private Map<String, PluginZkDTO> buildMap() {
        Map<String, PluginZkDTO> pluginMap = Maps.newHashMap();
        pluginMap.put(PluginEnum.DIVIDE.getName(), new PluginZkDTO("6", PluginEnum.DIVIDE.getName(), Boolean.TRUE));
        pluginMap.put(PluginEnum.GLOBAL.getName(), new PluginZkDTO("7", PluginEnum.GLOBAL.getName(), Boolean.TRUE));
        pluginMap.put(PluginEnum.MONITOR.getName(), new PluginZkDTO("8", PluginEnum.MONITOR.getName(), Boolean.TRUE));
        return pluginMap;
    }
}
