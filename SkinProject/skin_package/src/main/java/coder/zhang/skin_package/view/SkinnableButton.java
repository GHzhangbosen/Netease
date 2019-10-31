package coder.zhang.skin_package.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import coder.zhang.skin_package.R;
import coder.zhang.skin_package.bean.AttrBean;

public class SkinnableButton extends AppCompatButton implements SkinChange {

    private AttrBean attrBean;

    public SkinnableButton(Context context) {
        this(context, null);
    }

    public SkinnableButton(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SkinnableButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        attrBean = new AttrBean();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StyleButton, defStyleAttr, 0);
        attrBean.saveViewResources(typedArray, R.styleable.StyleButton);
        typedArray.recycle();
    }

    @Override
    public void changeSkin() {
        int key = R.styleable.StyleButton[R.styleable.StyleButton_android_background];
        int resourceId = attrBean.getViewResource(key);
        if (resourceId > 0) {
            Drawable drawable = ContextCompat.getDrawable(getContext(), resourceId);
            // 控件自带api，这里不用setBackgroundColor()因为在9.0测试不通过
            // setBackgroundDrawable本来过时了，但是兼容包重写了方法
            setBackgroundDrawable(drawable);
        }

        key = R.styleable.StyleButton[R.styleable.StyleButton_android_textColor];
        resourceId = attrBean.getViewResource(key);
        if (resourceId > 0) {
            ColorStateList color = ContextCompat.getColorStateList(getContext(), resourceId);
            setTextColor(color);
        }
    }
}
