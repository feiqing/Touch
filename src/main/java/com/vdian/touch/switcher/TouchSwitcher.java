package com.vdian.touch.switcher;

import java.util.Map;

/**
 * @author jifang
 * @since 16/10/24 下午1:17.
 */
public interface TouchSwitcher {
    /**
     * init when new TouchSwitcher instance.
     *
     * @param config in touch.xml <switcher><config ... /> ...</switcher>
     */
    void init(Map<String, String> config);

    boolean isSwitchOn(String touchPattern, String queryString);
}
