package coder.zhang.skin_library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import coder.zhang.skin_library.R;
import coder.zhang.skin_library.SkinManager;
import coder.zhang.skin_library.bean.AttrsBean;

public class SkinnableButton extends AppCompatButton implements ViewMatch {

    private AttrsBean attrsBean;

    public SkinnableButton(Context context) {
        this(context, null);
    }

    public SkinnableButton(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SkinnableButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        attrsBean = new AttrsBean();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StyleButton, defStyleAttr, -1);
        attrsBean.saveViewResources(typedArray, R.styleable.StyleButton);
        typedArray.recycle();
    }

    @Override
    public void skinnableView() {
        SkinManager skinManager = SkinManager.getInstance();
        String skinPath = skinManager.getSkinPath();
        skinManager.loadSkinResources(skinPath);

        int backgroundId = attrsBean.getViewResource(R.styleable.StyleButton[R.styleable.StyleButton_android_background]);
        if (backgroundId > 0) {
            Object backgroundObj = skinManager.getBackgroundOrSrc(backgroundId);
            if (backgroundObj instanceof Integer) {
                setBackgroundColor((Integer) backgroundObj);
            } else {
                setBackgroundDrawable((Drawable) backgroundObj);
            }
        }

        int textColorId = attrsBean.getViewResource(R.styleable.StyleButton[R.styleable.StyleButton_android_textColor]);
        if (textColorId > 0) {
            setTextColor(skinManager.getColor(textColorId));
        }
    }
}
