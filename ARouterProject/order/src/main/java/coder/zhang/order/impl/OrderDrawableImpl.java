package coder.zhang.order.impl;

import coder.zhang.arouter.ARouter;
import coder.zhang.common.OrderDrawable;
import coder.zhang.order.R;

@ARouter(path = "/order/getDrawable")
public class OrderDrawableImpl implements OrderDrawable {

    @Override
    public int getDrawable() {
        return R.drawable.ic_backup_black_24dp;
    }
}
