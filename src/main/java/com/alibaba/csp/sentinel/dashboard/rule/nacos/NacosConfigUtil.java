package com.alibaba.csp.sentinel.dashboard.rule.nacos;

import com.alibaba.csp.sentinel.dashboard.util.JSONUtils;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.RuleEntity;
import com.alibaba.fastjson.JSON;
import com.alibaba.csp.sentinel.slots.block.Rule;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @wiki https://github.com/eacdy/Sentinel-Dashboard-Nacos
 * add by tam
 */
@Component
public class NacosConfigUtil {
    @Autowired
    private NacosConfig.NacosProperties nacosProperties;

    public static final String FLOW_DATA_ID_POSTFIX = "-flow-rules";
    public static final String DEGRADE_DATA_ID_POSTFIX = "-degrade-rules";
    public static final String SYSTEM_DATA_ID_POSTFIX = "-system-rules";
    public static final String PARAM_FLOW_DATA_ID_POSTFIX = "-param-flow-rules";
    public static final String AUTHORITY_DATA_ID_POSTFIX = "-authority-rules";
    public static final String GATEWAY_FLOW_DATA_ID_POSTFIX = "-gateway-flow-rules";
    public static final String GATEWAY_API_DATA_ID_POSTFIX = "-gateway-api-rules";
    public static final String DASHBOARD_POSTFIX = "-dashboard";
    public static final String CLUSTER_MAP_DATA_ID_POSTFIX = "-cluster-map";

    /**
     * cc for `cluster-client`
     */
    public static final String CLIENT_CONFIG_DATA_ID_POSTFIX = "-cc-config";
    /**
     * cs for `cluster-server`
     */
    public static final String SERVER_TRANSPORT_CONFIG_DATA_ID_POSTFIX = "-cs-transport-config";
    public static final String SERVER_FLOW_CONFIG_DATA_ID_POSTFIX = "-cs-flow-config";
    public static final String SERVER_NAMESPACE_SET_DATA_ID_POSTFIX = "-cs-namespace-set";

    /**
     * ?????????????????????JSON??????????????????Nacos server???
     *
     * @param configService nacos config service
     * @param app           ????????????
     * @param postfix       ???????????? eg.NacosConfigUtil.FLOW_DATA_ID_POSTFIX
     * @param rules         ????????????
     * @throws NacosException ??????
     */
    public <T> void setRuleStringToNacos(ConfigService configService, String app, String postfix, List<T> rules) throws NacosException {
        AssertUtil.notEmpty(app, "app name cannot be empty");
        if (rules == null) {
            return;
        }

        List<Rule> ruleForApp = rules.stream()
                .map(rule -> {
                    RuleEntity rule1 = (RuleEntity) rule;
                    //System.out.println(rule1.getClass());
                    Rule rule2 = rule1.toRule();
                    //System.out.println(rule2.getClass());
                    return rule2;
                })
                .collect(Collectors.toList());

        // ???????????????????????????
        String dataId = genDataId(app, postfix);
        configService.publishConfig(
                dataId,
                nacosProperties.getGroupId(),
                JSON.toJSONString(ruleForApp)
        );

        // ???????????????????????????
        configService.publishConfig(
                dataId + DASHBOARD_POSTFIX,
                nacosProperties.getGroupId(),
                JSONUtils.toJSONString(rules)
        );
    }

    /**
     * ???Nacos server??????????????????????????????????????????????????????Rule??????
     *
     * @param configService nacos config service
     * @param appName       ????????????
     * @param postfix       ???????????? eg.NacosConfigUtil.FLOW_DATA_ID_POSTFIX
     * @param clazz         ???
     * @param <T>           ??????
     * @return ??????????????????
     * @throws NacosException ??????
     */
    public <T> List<T> getRuleEntitiesFromNacos(ConfigService configService, String appName, String postfix, Class<T> clazz) throws NacosException {
        String rules = configService.getConfig(
                genDataId(appName, postfix) + DASHBOARD_POSTFIX,
                //genDataId(appName, postfix),
                nacosProperties.getGroupId(),
                3000
        );
        if (StringUtil.isEmpty(rules)) {
            return new ArrayList<>();
        }
        return JSONUtils.parseObject(clazz, rules);
    }

    private static String genDataId(String appName, String postfix) {
        return appName + postfix;
    }
}
