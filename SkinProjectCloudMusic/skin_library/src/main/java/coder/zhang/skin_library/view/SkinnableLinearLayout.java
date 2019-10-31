package coder.zhang.skin_library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.appcompat.widget.AppCompatTextView;

import coder.zhang.skin_library.R;
import coder.zhang.skin_library.SkinManager;
import coder.zhang.skin_library.bean.AttrsBean;

public class SkinnableLinearLayout extends LinearLayout implements ViewMatch {

    private AttrsBean attrsBean;

    public SkinnableLinearLayout(Context context) {
        this(context, null);
    }

    public SkinnableLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SkinnableLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        attrsBean = new AttrsBean();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StyleLinearLayout, defStyleAttr, -1);
        attrsBean.saveViewResources(typedArray, R.styleable.StyleLinearLayout);
        typedArray.recycle();
    }

    @Override
    public void skinnableView() {
        SkinManager skinManager = SkinManager.getInstance();
        String skinPath = skinManager.getSkinPath();
        skinManager.loadSkinResources(skinPath);

        int backgroundId = attrsBean.getViewResource(R.styleable.StyleLinearLayout[R.styleable.StyleLinearLayout_android_background]);
        if (backgroundId > 0) {
            Object backgroundObj = skinManager.getBackgroundOrSrc(backgroundId);
            if (backgroundObj instanceof Integer) {
                setBackgroundColor((Integer) backgroundObj);
            } else {
                setBackgroundDrawable((Drawable) backgroundObj);
            }
        }
    }
}
