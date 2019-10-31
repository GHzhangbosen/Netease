package coder.zhang.skin_package.inflater;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.app.AppCompatViewInflater;

import coder.zhang.skin_package.view.SkinnableButton;
import coder.zhang.skin_package.view.SkinnableLinearLayout;
import coder.zhang.skin_package.view.SkinnableTextView;

public class CustomAppCompatViewInflater extends AppCompatViewInflater {

    private Context context;

    public CustomAppCompatViewInflater(Context context) {
        this.context = context;
    }

    public View autoMatch(String name, AttributeSet attrs) {
        View view = null;
        switch (name) {
            case "LinearLayout":
                view = new SkinnableLinearLayout(context, attrs);
                break;

            case "TextView":
                view = new SkinnableTextView(context, attrs);
                break;

            case "Button":
                view = new SkinnableButton(context, attrs);
                break;
        }
        return view;
    }
}
