package com.zr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

public class MoreTextLayout extends FrameLayout {
    private String openTips = "展开";
    private ColorStateList openTipsColor;
    private String closeTips = "收缩";
    private ColorStateList closeTipsColor;
    private float tipsSize = -1;
    private String ellipsize="";
    private float tipsSpace = 0;
    private int maxLines = -1;
    private CharSequence text;
    private CharSequence hint;
    private ColorStateList textColor = ColorStateList.valueOf(0xFF000000);
    private int textColorHighlight = 0x6633B5E5;
    private ColorStateList textColorHint;
    private float textSize = -1;
    private float textScaleX = 1f;
    private int typeface = -1;
    private int textStyle = -1;
    //    private float textFontWeight;
    private Typeface fontFamily;
    private ColorStateList textColorLink;
    private boolean textAllCaps;
    private int shadowColor;
    private float shadowDx;
    private float shadowDy;
    private float shadowRadius;
/*    private int elegantTextHeight;
    private int fallbackLineSpacing;
    private int letterSpacing;
    private String fontFeatureSettings;
    private String fontVariationSettings;*/

    private AppCompatTextView textView;


    private CharSequence fullText = "";
    private AppCompatTextView tipsTextView;
    private boolean isOpen=true;

    public MoreTextLayout(@NonNull Context context) {
        super(context);
        init(null);
    }

    public MoreTextLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MoreTextLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MoreTextLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MoreTextLayout);

        openTips = typedArray.getString(R.styleable.MoreTextLayout_openTips);
        if (TextUtils.isEmpty(openTips)) {
            openTips = "展开";
        }
        openTipsColor = typedArray.getColorStateList(R.styleable.MoreTextLayout_openTipsColor);
        closeTips = typedArray.getString(R.styleable.MoreTextLayout_closeTips);
        if (TextUtils.isEmpty(closeTips)) {
            closeTips = "收缩";
        }
        closeTipsColor = typedArray.getColorStateList(R.styleable.MoreTextLayout_closeTipsColor);
        tipsSize = typedArray.getDimension(R.styleable.MoreTextLayout_tipsSize, -1);
        ellipsize = typedArray.getString(R.styleable.MoreTextLayout_ellipsis);
        if (TextUtils.isEmpty(ellipsize)) {
            ellipsize = "";
        }
        tipsSpace = typedArray.getDimension(R.styleable.MoreTextLayout_tipsSpace, 0);
        maxLines = typedArray.getInteger(R.styleable.MoreTextLayout_android_maxLines, -1);
        text = typedArray.getText(R.styleable.MoreTextLayout_android_text);
        if (!TextUtils.isEmpty(text)) {
            fullText = text;
        }
        hint = typedArray.getText(R.styleable.MoreTextLayout_android_hint);
        textColor = typedArray.getColorStateList(R.styleable.MoreTextLayout_android_textColor);
        textColorHighlight = typedArray.getColor(R.styleable.MoreTextLayout_android_textColorHighlight, 0x6633B5E5);
        textColorHint = typedArray.getColorStateList(R.styleable.MoreTextLayout_android_textColorHint);
        textSize = typedArray.getDimensionPixelOffset(R.styleable.MoreTextLayout_android_textSize, -1);
        textScaleX = typedArray.getFloat(R.styleable.MoreTextLayout_android_textScaleX, 1f);
        typeface = typedArray.getInt(R.styleable.MoreTextLayout_android_typeface, -1);
        textStyle = typedArray.getInt(R.styleable.MoreTextLayout_android_textStyle, -1);
//        textFontWeight = typedArray.getDimension(R.styleable.MoreTextLayout_android_textFontWeight, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fontFamily = typedArray.getFont(R.styleable.MoreTextLayout_android_fontFamily);
        }
        textColorLink = typedArray.getColorStateList(R.styleable.MoreTextLayout_android_textColorLink);
        textAllCaps = typedArray.getBoolean(R.styleable.MoreTextLayout_android_textAllCaps, false);
        shadowColor = typedArray.getColor(R.styleable.MoreTextLayout_android_shadowColor, 0);
        shadowDx = typedArray.getFloat(R.styleable.MoreTextLayout_android_shadowDx, 0);
        shadowDy = typedArray.getFloat(R.styleable.MoreTextLayout_android_shadowDy, 0);
        shadowRadius = typedArray.getFloat(R.styleable.MoreTextLayout_android_shadowRadius, 0);

       /* elegantTextHeight = typedArray.getDimension(R.styleable.MoreTextLayout_android_elegantTextHeight, 0);
        fallbackLineSpacing = typedArray.getDimension(R.styleable.MoreTextLayout_android_fallbackLineSpacing, 0);
        letterSpacing = typedArray.getDimension(R.styleable.MoreTextLayout_android_letterSpacing, 0);
        fontFeatureSettings = typedArray.getDimension(R.styleable.MoreTextLayout_android_fontFeatureSettings, 0);
        fontVariationSettings = typedArray.getDimension(R.styleable.MoreTextLayout_android_fontVariationSettings, 0);*/


        typedArray.recycle();


    }

    private int normal = 0;
    private int sans = 1;
    private int serif = 2;
    private int monospace = 3;

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        isOpen=true;
        if (maxLines == 0 || TextUtils.isEmpty(text)) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        if (textView == null || textView.getParent() == null) {
            textView = new AppCompatTextView(getContext());
            if (textSize != -1) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
            }
            textView.setTextScaleX(textScaleX);
//            textView.setId(R.id.moreTextId);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            textView.setMaxLines(2);
            if (!TextUtils.isEmpty(text)) {
                textView.setText(text);
            }
            if (!TextUtils.isEmpty(hint)) {
                textView.setHint(hint);
            }
            if (textColor != null) {
                textView.setTextColor(textColor);
            }
            textView.setHighlightColor(textColorHighlight);
            if (textColorHint == null) {
                textView.setHintTextColor(textColorHint);
            }

            int style = Typeface.NORMAL;
            if (textStyle == Typeface.BOLD_ITALIC) {
                style = Typeface.BOLD_ITALIC;
            } else {
                if (textStyle == Typeface.BOLD) {
                    style = Typeface.BOLD;
                } else if (textStyle == Typeface.ITALIC) {
                    style = Typeface.ITALIC;
                }
            }

            if (fontFamily != null) {
                textView.setTypeface(fontFamily, style);
            } else {
                if (typeface == sans) {
                    textView.setTypeface(Typeface.SANS_SERIF, style);
                } else if (typeface == serif) {
                    textView.setTypeface(Typeface.SERIF, style);
                } else if (typeface == monospace) {
                    textView.setTypeface(Typeface.MONOSPACE, style);
                } else {
                    textView.setTypeface(Typeface.DEFAULT, style);
                }
            }
            if (textColorLink != null) {
                textView.setLinkTextColor(textColorLink);
            }
            textView.setAllCaps(textAllCaps);
            if (shadowColor != 0) {
                textView.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor);
            }
//            int makeMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.AT_MOST);
//            textView.measure(getMeasuredWidth(), makeMeasureSpec);

            addView(textView);

        } else if (textView.getId() == View.NO_ID) {
            Layout layout = textView.getLayout();
            if (layout.getLineCount() > maxLines) {
                textView.setId(R.id.moreTextId);
                int lineLeft = layout.getLineStart(maxLines - 1);
                int lineRight = layout.getLineEnd(maxLines - 1);

                CharSequence charSequence = text.subSequence(lineLeft, lineRight);

                Rect rect = new Rect();
                TextPaint paint = textView.getPaint();

                int tempCount=1;

                String tempLineText = ellipsize + openTips + charSequence;

                boolean flag=true;
                while (flag){
                    paint.getTextBounds(tempLineText,0,tempLineText.length()-tempCount,rect);
                    if(rect.width()+tipsSpace>getMeasuredWidth()){
                        tempCount+=1;
                    }else{
                        flag=false;
                    }
                }
                textView.setText(text.subSequence(0,lineRight-tempCount)+ellipsize+openTips);

                tipsTextView = new AppCompatTextView(getContext());
                tipsTextView.setText(openTips);

                isOpen=false;
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onFinishInflate() {
        Log.i("=====", "=====onFinishInflate");
        int childCount = getChildCount();
        if (childCount > 0) {
            View childAt = getChildAt(0);
            if (childAt instanceof AppCompatTextView) {
                textView = (AppCompatTextView) childAt;
            }
        }
        super.onFinishInflate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }
}
