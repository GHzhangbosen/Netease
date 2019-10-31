package coder.zhang.skin_library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatTextView;

import coder.zhang.skin_library.R;
import coder.zhang.skin_library.SkinManager;
import coder.zhang.skin_library.bean.AttrsBean;

public class SkinnableRelativeLayout extends RelativeLayout implements ViewMatch {

    private AttrsBean attrsBean;

    public SkinnableRelativeLayout(Context context) {
        this(context, null);
    }

    public SkinnableRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SkinnableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        attrsBean = new AttrsBean();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StyleRelativeLayout, defStyleAttr, -1);
        attrsBean.saveViewResources(typedArray, R.styleable.StyleRelativeLayout);
        typedArray.recycle();
    }

    @Override
    public void skinnableView() {
        SkinManager skinManager = SkinManager.getInstance();
        String skinPath = skinManager.getSkinPath();
        skinManager.loadSkinResources(skinPath);

        int backgroundId = attrsBean.getViewResource(R.styleable.StyleRelativeLayout[R.styleable.StyleRelativeLayout_android_background]);
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
