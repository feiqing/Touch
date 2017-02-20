package com.vdian.touch.utils;

import java.util.Map;
import java.util.Set;

/**
 * @author jifang
 * @since 2016/11/23 下午3:50.
 */
public class ConfigEntity {

    private Set<String> packages;

    private Set<String> converters;

    private Map<String, Map<String, String>> switcherMap;

    public ConfigEntity(Set<String> converters, Set<String> packages, Map<String, Map<String, String>> switcherMap) {
        this.converters = converters;
        this.packages = packages;
        this.switcherMap = switcherMap;
    }

    public Set<String> getConverters() {
        return converters;
    }

    public Set<String> getPackages() {
        return packages;
    }

    public Map<String, Map<String, String>> getSwitcherMap() {
        return switcherMap;
    }
}
