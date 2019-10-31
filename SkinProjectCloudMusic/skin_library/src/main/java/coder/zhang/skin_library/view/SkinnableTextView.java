package coder.zhang.skin_library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import java.io.File;
import java.lang.reflect.Field;

import coder.zhang.skin_library.R;
import coder.zhang.skin_library.SkinManager;
import coder.zhang.skin_library.bean.AttrsBean;
import coder.zhang.skin_library.utils.L;

public class SkinnableTextView extends AppCompatTextView implements ViewMatch {

    private AttrsBean attrsBean;

    public SkinnableTextView(Context context) {
        this(context, null);
    }

    public SkinnableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SkinnableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        attrsBean = new AttrsBean();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StyleTextView, defStyleAttr, 0);
        attrsBean.saveViewResources(typedArray, R.styleable.StyleTextView);
        typedArray.recycle();
    }

    @Override
    public void skinnableView() {
        SkinManager skinManager = SkinManager.getInstance();
        String skinPath = skinManager.getSkinPath();
        skinManager.loadSkinResources(skinPath);

        int backgroundId = attrsBean.getViewResource(R.styleable.StyleTextView[R.styleable.StyleTextView_android_background]);
        if (backgroundId > 0) {
            Object backgroundObj = skinManager.getBackgroundOrSrc(backgroundId);
            if (backgroundObj instanceof Integer) {
                setBackgroundColor((Integer) backgroundObj);
            } else {
                setBackgroundDrawable((Drawable) backgroundObj);
            }
        }

        int textColorId = attrsBean.getViewResource(R.styleable.StyleTextView[R.styleable.StyleTextView_android_textColor]);
        if (textColorId > 0) {
            setTextColor(skinManager.getColor(textColorId));
        }

        int textId = attrsBean.getViewResource(R.styleable.StyleTextView[R.styleable.StyleTextView_android_text]);
        if (textId > 0) {
            String text = skinManager.getString(textId);
            if (!TextUtils.isEmpty(text)) {
                setText(text);
            }
        }
    }
}
