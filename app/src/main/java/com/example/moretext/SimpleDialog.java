package com.example.moretext;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;


public class SimpleDialog extends Dialog {
    public SimpleDialog(Context context) {
        super(context);
        init();
    }

    public SimpleDialog(Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected SimpleDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
        TextView tv=null;

    }

    private void init() {
        View inflate = getLayoutInflater().inflate(R.layout.simple_dialog, null);
        setContentView(inflate);
    }

    public static void showDialog(Activity activity) {
        SimpleDialog dialog = new SimpleDialog(activity);
        dialog.show();
    }
}
