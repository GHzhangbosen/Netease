package coder.zhang.skinprojectcloudmusic.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import coder.zhang.skin_library.SkinManager;
import coder.zhang.skin_library.bean.AttrsBean;
import coder.zhang.skin_library.view.ViewMatch;
import coder.zhang.skinprojectcloudmusic.R;

public class CustomCircleView extends View implements ViewMatch {

    private Paint mTextPain;
    private AttrsBean attrsBean;

    public CustomCircleView(Context context) {
        this(context, null);
    }

    public CustomCircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        attrsBean = new AttrsBean();

        // 根据自定义属性，匹配控件属性的类型集合，如：circleColor
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomCircleView, defStyleAttr, 0);

        int corcleColorResId = typedArray.getResourceId(R.styleable.CustomCircleView_circleColor, 0);

        // 存储到临时JavaBean对象
        attrsBean.saveViewResources(typedArray, R.styleable.CustomCircleView);
        // 这一句回收非常重要！obtainStyledAttributes()有语法提示！！
        typedArray.recycle();

        mTextPain = new Paint();
        mTextPain.setColor(getResources().getColor(corcleColorResId));
        //开启抗锯齿，平滑文字和圆弧的边缘
        mTextPain.setAntiAlias(true);
        //设置文本位于相对于原点的中间
        mTextPain.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 获取宽度一半
        int width = getWidth() / 2;
        // 获取高度一半
        int height = getHeight() / 2;
        // 设置半径为宽或者高的最小值（半径）
        int radius = Math.min(width, height);
        // 利用canvas画一个圆
        canvas.drawCircle(width, height, radius, mTextPain);
    }

    @Override
    public void skinnableView() {
        // 根据自定义属性，获取styleable中的circleColor属性
        int resourceId = attrsBean.getViewResource(R.styleable.CustomCircleView[R.styleable.CustomCircleView_circleColor]);
        if (resourceId > 0) {
            mTextPain.setColor(SkinManager.getInstance().getColor(resourceId));
            invalidate();
        }
    }
}
