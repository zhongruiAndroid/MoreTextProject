package com.zr;

import android.graphics.Canvas;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

class MoreTextLayoutUtils {
    public static TextView measureContentView(TextView tempView, TextView originView) {
        if (originView == null) {
            return null;
        }
        if (tempView == null) {
            tempView = new TextView(originView.getContext());
        }
        tempView.setTypeface(originView.getTypeface());
        tempView.setTextColor(originView.getTextColors());
        tempView.setGravity(originView.getGravity());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            tempView.setAllCaps(originView.isAllCaps());
        }
        tempView.setAutoLinkMask(originView.getAutoLinkMask());
        tempView.setBackground(originView.getBackground());
        tempView.setPaintFlags(originView.getPaintFlags());
        tempView.setPadding(originView.getPaddingLeft(), originView.getPaddingTop(), originView.getPaddingRight(), originView.getPaddingBottom());

        MoreTextLayout.LayoutParams tempLayoutParam = new MoreTextLayout.LayoutParams(originView.getLayoutParams());

        tempLayoutParam.height = MoreTextLayout.LayoutParams.WRAP_CONTENT;
        tempView.setLayoutParams(tempLayoutParam);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            tempView.setLineHeight(originView.getLineHeight());
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tempView.setAutoSizeTextTypeWithDefaults(originView.getAutoSizeTextType());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tempView.setAutofillHints(originView.getAutofillHints());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            tempView.setImportantForAutofill(originView.getImportantForAutofill());
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            tempView.setAutofillId(originView.getAutofillId());
        }
        tempView.setLineSpacing(originView.getLineSpacingExtra(), originView.getLineSpacingMultiplier());
        tempView.setIncludeFontPadding(originView.getIncludeFontPadding());
        tempView.setTextSize(TypedValue.COMPLEX_UNIT_PX, originView.getTextSize());
        return tempView;
    }
}