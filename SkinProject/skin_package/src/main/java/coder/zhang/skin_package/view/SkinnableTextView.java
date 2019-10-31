package coder.zhang.skin_package.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import coder.zhang.skin_package.R;
import coder.zhang.skin_package.bean.AttrBean;

public class SkinnableTextView extends AppCompatTextView implements SkinChange {

    private AttrBean attrBean;

    public SkinnableTextView(Context context) {
        this(context, null);
    }

    public SkinnableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SkinnableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        attrBean = new AttrBean();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StyleTextView, defStyleAttr, 0);
        attrBean.saveViewResources(typedArray, R.styleable.StyleTextView);
        typedArray.recycle();
    }

    @Override
    public void changeSkin() {
        int key = R.styleable.StyleTextView[R.styleable.StyleTextView_android_background];
        int resourceId = attrBean.getViewResource(key);
        if (resourceId > 0) {
            Drawable drawable = ContextCompat.getDrawable(getContext(), resourceId);
            // 控件自带api，这里不用setBackgroundColor()因为在9.0测试不通过
            // setBackgroundDrawable本来过时了，但是兼容包重写了方法
            setBackgroundDrawable(drawable);
        }

        key = R.styleable.StyleTextView[R.styleable.StyleTextView_android_textColor];
        resourceId = attrBean.getViewResource(key);
        if (resourceId > 0) {
            ColorStateList color = ContextCompat.getColorStateList(getContext(), resourceId);
            setTextColor(color);
        }
    }
}
