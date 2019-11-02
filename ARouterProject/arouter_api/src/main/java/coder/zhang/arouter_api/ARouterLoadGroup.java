package coder.zhang.arouter_api;

import java.util.Map;

public interface ARouterLoadGroup {

    Map<String, Class<? extends ARouterLoadPath>> loadGroup();
}
