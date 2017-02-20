package com.vdian.touch.switcher;
/*
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vdian.vitamin.client.VitaminClient;
import com.vdian.vitamin.client.listener.HandleListener;
import com.vdian.vitamin.common.model.NodeDO;

import java.util.List;
import java.util.Map;
*/
/**
 * @author jifang
 * @since 16/10/24 下午4:57.
 */
/*
public class VitaminSwitcher implements TouchSwitcher {

    private static final String TOUCH_VITAMIN_GROUP_ID = "touch";

    private static final String TOUCH_VITAMIN_SERVICE_ID = "touch-switcher";

    private static final String TOUCH_VITAMIN_NODE_KEY = "touch.switcher";

    private static final String TOUCH_VITAMIN_JSON_KEY = "touch_open";

    private volatile boolean on;

    @Override
    public void init(Map<String, String> config) {
        Map<String, String> nodes = VitaminClient.lookup(TOUCH_VITAMIN_GROUP_ID, TOUCH_VITAMIN_SERVICE_ID, new HandleListener() {

            @Override
            public void handle(List<NodeDO> list) {
                for (NodeDO node : list) {
                    if (TOUCH_VITAMIN_NODE_KEY.equals(node.getNodeKey())) {
                        JSONObject json = JSON.parseObject(node.getNodeValue());
                        on = json.getBoolean(TOUCH_VITAMIN_JSON_KEY);
                    }
                }
            }
        });

        JSONObject json = JSON.parseObject(nodes.get(TOUCH_VITAMIN_NODE_KEY));
        on = json.getBooleanValue(TOUCH_VITAMIN_JSON_KEY);
    }

    @Override
    public boolean isSwitchOn(String touchPattern, String queryString) {
        return on;
    }
}
*/
