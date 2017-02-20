package com.vdian.touch.helper;

import com.vdian.touch.exceptions.TouchException;
import com.vdian.touch.switcher.TouchSwitcher;

import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author jifang
 * @since 16/10/24 下午2:37.
 */
public class SwitcherHelper extends AbstractHelper {

    private static final String SWITCHER_CONTEXT = "switcher_context";

    public SwitcherHelper(ServletContext servletContext) {
        super(servletContext);
    }

    public SwitcherHelper init(Map<String, Map<String, String>> switcherMap) {
        if (getServletContext().getAttribute(SWITCHER_CONTEXT) == null) {
            Set<TouchSwitcher> switchers = new HashSet<>();
            try {
                if (switcherMap.isEmpty()) {
                    addDefault(switchers);
                } else {
                    for (Map.Entry<String, Map<String, String>> entry : switcherMap.entrySet()) {
                        String switcherName = entry.getKey();
                        Map<String, String> switcherConfig = entry.getValue();
                        TouchSwitcher switcher = (TouchSwitcher) Class.forName(switcherName).newInstance();
                        switcher.init(switcherConfig);
                        switchers.add(switcher);
                    }
                }
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                throw new TouchException(e);
            }

            getServletContext().setAttribute(SWITCHER_CONTEXT, switchers);
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    public boolean isSwitchPassed(String touchPattern, String queryString) {
        Set<TouchSwitcher> switchers = (Set<TouchSwitcher>) getServletContext().getAttribute(SWITCHER_CONTEXT);
        for (TouchSwitcher switcher : switchers) {
            if (!switcher.isSwitchOn(touchPattern, queryString)) {
                return false;
            }
        }
        return true;
    }

    private void addDefault(Set<TouchSwitcher> switchers) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        TouchSwitcher vitaminSwitcher = (TouchSwitcher) Class.forName("com.vdian.touch.switcher.VitaminSwitcher").newInstance();
        vitaminSwitcher.init(Collections.<String, String>emptyMap());
        switchers.add(vitaminSwitcher);
    }
}
