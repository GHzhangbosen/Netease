package coder.zhang.arouterproject.test;

import java.util.HashMap;
import java.util.Map;

import coder.zhang.arouter.bean.RouterBean;
import coder.zhang.arouter_api.ARouterLoadPath;

public class ARouter$$Path$$order implements ARouterLoadPath {

    @Override
    public Map<String, RouterBean> loadPath() {
        Map<String, RouterBean> pathMap = new HashMap<>();
//        pathMap.put("/order/Order_MainActivity", RouterBean.create(RouterBean.Type.ACTIVITY, Order_MainActivity.class, "order", "/order/MainActivity"));
        return pathMap;
    }
}
