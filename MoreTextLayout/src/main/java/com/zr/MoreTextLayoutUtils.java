package com.zr;

import android.graphics.Canvas;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

class MoreTextLayoutUtils {
    public static int measureContentView(TextView originView, int showLine, int widthMeasureSpec, int heightMeasureSpec) {
        if (originView == null) {
            return 0;
        }
        TextView tempView = new TextView(originView.getContext());
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
        tempView.setLayoutParams(new ViewGroup.LayoutParams(originView.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
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
        tempView.setTextSize(originView.getTextSize());
        tempView.setMaxLines(showLine);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < showLine; i++) {
            sb.append("\n");
        }
        tempView.setText(sb.toString());
        tempView.draw(new Canvas());
        tempView.measure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(heightMeasureSpec, View.MeasureSpec.AT_MOST));
        int measuredHeight = tempView.getMeasuredHeight();
        Log.i("=====", "===measuredHeight==" + measuredHeight);
        return measuredHeight;
    }
}