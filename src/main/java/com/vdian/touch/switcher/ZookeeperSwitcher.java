package com.vdian.touch.switcher;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vdian.touch.exceptions.TouchException;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;

/**
 * @author jifang
 * @since 16/10/24 下午4:57.
 */
public class ZookeeperSwitcher implements TouchSwitcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZookeeperSwitcher.class);

    private volatile boolean on;

    @Override
    public void init(Map<String, String> config) {
        String zk = config.get("zookeeper");
        String touchPath = config.get("touchPath");
        final String touchKey = config.get("touchKey");

        // connect zk
        CuratorFramework client = CuratorFrameworkFactory.newClient(zk, new RetryNTimes(10, 5000));
        client.start();

        try {
            // check node exists
            Stat stat = client.checkExists().forPath(touchPath);
            if (stat == null) {
                Map<String, Boolean> json = Collections.singletonMap(touchKey, false);
                client.create().creatingParentsIfNeeded().forPath(touchPath, JSONObject.toJSONString(json).getBytes());
            }

            final NodeCache node = new NodeCache(client, touchPath);
            node.getListenable().addListener(new NodeCacheListener() {
                @Override
                public void nodeChanged() throws Exception {
                    on = toTouchStat(node.getCurrentData().getData(), touchKey);
                    LOGGER.info("zookeeper switcher status: {}", on);
                }
            });
            node.start();
        } catch (Exception e) {
            throw new TouchException(e);
        }
    }

    @Override
    public boolean isSwitchOn(String touchPattern, String queryString) {
        return on;
    }

    private boolean toTouchStat(byte[] zkValue, String key) {
        JSONObject json = (JSONObject) JSON.parse(zkValue);
        return json.getBooleanValue(key);
    }
}
