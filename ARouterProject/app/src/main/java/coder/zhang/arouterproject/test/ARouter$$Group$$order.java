package coder.zhang.arouterproject.test;

import java.util.HashMap;
import java.util.Map;

import coder.zhang.arouter_api.ARouterLoadGroup;
import coder.zhang.arouter_api.ARouterLoadPath;

public class ARouter$$Group$$order implements ARouterLoadGroup {

    @Override
    public Map<String, Class<? extends ARouterLoadPath>> loadGroup() {
        Map<String, Class<? extends ARouterLoadPath>> groupMap = new HashMap<>();
        groupMap.put("order", ARouter$$Path$$order.class);
        return groupMap;
    }
}
