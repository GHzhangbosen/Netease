package coder.zhang.skin_library.inflater;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.app.AppCompatViewInflater;

import coder.zhang.skin_library.view.SkinnableButton;
import coder.zhang.skin_library.view.SkinnableLinearLayout;
import coder.zhang.skin_library.view.SkinnableRelativeLayout;
import coder.zhang.skin_library.view.SkinnableTextView;

public class CustomAppCompatViewInflater extends AppCompatViewInflater {

    private Context context;

    public CustomAppCompatViewInflater(Context context) {
        this.context = context;
    }

    public View autoCreateView(String name, AttributeSet attrs) {
        View view = null;
        switch (name) {
            case "TextView":
                view = new SkinnableTextView(context, attrs);
                break;

            case "Button":
                view = new SkinnableButton(context, attrs);
                break;

            case "LinearLayout":
                view = new SkinnableLinearLayout(context, attrs);
                break;

            case "RelativeLayout":
                view = new SkinnableRelativeLayout(context, attrs);
                break;
        }
        return view;
    }
}
