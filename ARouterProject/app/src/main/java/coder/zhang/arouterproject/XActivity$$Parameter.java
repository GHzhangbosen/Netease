package coder.zhang.arouterproject;

import coder.zhang.arouter_api.LoadParameter;

public class XActivity$$Parameter implements LoadParameter {

    @Override
    public void loadParameter(Object target) {
        MainActivity t = (MainActivity) target;

        t.name = t.getIntent().getStringExtra("name");
        t.age = t.getIntent().getIntExtra("age", t.age);
    }
}
