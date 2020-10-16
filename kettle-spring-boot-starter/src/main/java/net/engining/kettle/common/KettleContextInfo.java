package net.engining.kettle.common;

import java.util.Map;

/**
 * 配置内容
 *
 * @author 陈宝
 * @version 1.0
 * @date 2020/9/23 18:23
 * @since 1.0
 */
public class KettleContextInfo {
    /**
     * kettle 名称
     */
    private String name;
    /**
     * 路径  repo需要
     */
    private String subdirectory;
    /**
     * 参数
     */
    Map<String, String> params;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubdirectory() {
        return subdirectory;
    }

    public void setSubdirectory(String subdirectory) {
        this.subdirectory = subdirectory;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
