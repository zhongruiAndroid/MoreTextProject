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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;

public class MoreTextLayout extends FrameLayout {
    private String openTips = "展开";
    private ColorStateList openTipsColor;
    private String closeTips = "收缩";
    private ColorStateList closeTipsColor;
    private float tipsSize = -1;
    private float tipsXOffset = 0;
    private float tipsYOffset = 0;
    private String ellipsize = "";
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
    private boolean expand = false;


    private OnTouchListener tipsTouchListener;
    private OnTipsClickListener onTipsClickListener;

    public void setTipsTouchListener(OnTouchListener tipsTouchListener) {
        this.tipsTouchListener = tipsTouchListener;
    }

    public void setOnTipsClickListener(OnTipsClickListener onTipsClickListener) {
        this.onTipsClickListener = onTipsClickListener;
    }

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
        if (closeTipsColor == null) {
            closeTipsColor = openTipsColor;
        }
        tipsSize = typedArray.getDimensionPixelSize(R.styleable.MoreTextLayout_tipsSize, -1);
        tipsXOffset = typedArray.getDimensionPixelOffset(R.styleable.MoreTextLayout_tipsXOffset, 0);
        tipsYOffset = typedArray.getDimensionPixelOffset(R.styleable.MoreTextLayout_tipsYOffset, 0);
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
        textSize = typedArray.getDimensionPixelSize(R.styleable.MoreTextLayout_android_textSize, -1);
        if (tipsSize == -1) {
            tipsSize = textSize;
        }
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (maxLines == 0 || TextUtils.isEmpty(text)) {
            expand = true;
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        if (textView == null || textView.getParent() == null) {
            textView = new AppCompatTextView(getContext());
            if (textSize != -1) {
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }
            textView.setTextScaleX(textScaleX);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
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
            if (textColorHint != null) {
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
            addView(textView);
        } else {
            if (expand) {
                expand();
            } else {
                noExpand();
            }

        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void expand() {
        if (textView == null || textView.getParent() == null) {
            return;
        }
        Layout layout = textView.getLayout();
        int lineCount = layout.getLineCount();
        //收缩时，如果显示行数大于设置的最大行数
        int lineStart = layout.getLineStart(lineCount - 1);
        int lineEnd = layout.getLineEnd(lineCount - 1);
        float lineRight = layout.getLineRight(lineCount - 1);
        float lineTop = layout.getLineTop(lineCount - 1);
        float lineBottom = layout.getLineBottom(lineCount - 1);

        CharSequence charSequence = text.subSequence(lineStart, lineEnd);

        Rect rect = new Rect();
        TextPaint paint = textView.getPaint();

        int tempCount = 1;

        final String tempLineText = openTips + charSequence;
        paint.getTextBounds(tempLineText, 0, tempLineText.length() - tempCount, rect);
        /*如果最后一行的宽度+tips大于父view宽度*/
        if (rect.width() + tipsSpace > getMeasuredWidth()) {
            //tips显示在下一行
            /*最后一个高度*/
            lineBottom = lineBottom + (lineBottom - lineTop);

            textView.setText(text);
            // TODO: 2021/10/12
            //显示在左边
            setTipsView(0, lineBottom);

        } else {
            //tips显示在最后一行

            textView.setText(text);
            // TODO: 2021/10/12
            //显示在 右边
            setTipsView(lineRight, lineBottom);
        }


    }

    private void noExpand() {
        if (textView == null || textView.getParent() == null) {
            return;
        }
        Layout layout = textView.getLayout();
        //收缩时，如果显示行数大于设置的最大行数
        if (layout.getLineCount() > maxLines) {
            int lineStart = layout.getLineStart(maxLines - 1);
            int lineEnd = layout.getLineEnd(maxLines - 1);
            float lineRight = layout.getLineRight(maxLines - 1);
            float lineTop = layout.getLineTop(maxLines - 1);
            float lineBottom = layout.getLineBottom(maxLines - 1);


            CharSequence charSequence = text.subSequence(lineStart, lineEnd);

            Rect rect = new Rect();
            TextPaint paint = textView.getPaint();

            int tempCount = 1;

            final String tempLineText = ellipsize + openTips + charSequence;

            boolean flag = true;
            while (flag) {
                paint.getTextBounds(tempLineText, 0, tempLineText.length() - tempCount, rect);
                /*最后一行文字+。。。+提示 长度需要小于父view宽度*/
                if (rect.width() + tipsSpace > getMeasuredWidth()) {
                    tempCount += 1;
                } else {
                    flag = false;
                }
            }
            textView.setText(text.subSequence(0, lineEnd - tempCount) + ellipsize);

            setTipsView(lineRight-tempCount*textSize, lineBottom);
        } else {
            textView.setText(text);
            removeView(tipsTextView);
        }
    }

    private Rect offsetRect = new Rect();

    private void setTipsView(float lineRight, float lineBottom) {
        if (tipsTextView == null) {
            tipsTextView = new AppCompatTextView(getContext());
        }

        tipsTextView.setText(expand ? closeTips : openTips);
        if (openTipsColor != null) {
            tipsTextView.setTextColor(openTipsColor);
        }
        if (tipsSize != -1) {
            tipsTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, tipsSize);
        }
        if (offsetRect != null) {
            offsetRect.setEmpty();
        }
        tipsTextView.getPaint().getTextBounds(openTips, 0, openTips.length(), offsetRect);

        float offsetX = tipsXOffset + lineRight /*- offsetRect.width()*/ + tipsSpace;
        float offsetY = tipsYOffset + lineBottom + (tipsTextView.getPaint().getFontMetrics().top - tipsTextView.getPaint().getFontMetrics().bottom);

        /*控制tips  x轴偏移*/
        tipsTextView.setX(offsetX);
        /*控制tips  y轴偏移*/
        tipsTextView.setY(offsetY);
        tipsTextView.setOnTouchListener(tipsTouchListener);
        tipsTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                expand = !expand;
                tipsTextView.setText(expand ? closeTips : openTips);
                if (expand) {
                    textView.setText(text);
                    if (closeTipsColor != null) {
                        tipsTextView.setTextColor(closeTipsColor);
                    }
                } else {
                    if (openTipsColor != null) {
                        tipsTextView.setTextColor(openTipsColor);
                    }
                }
                /*重新测量时会自动设置展开或者收缩，所以这里不需要手动调用expand或者noExpand方法*/
                requestLayout();
                if (onTipsClickListener != null) {
                    onTipsClickListener.onClick(tipsTextView, expand);
                }
            }
        });
        if (tipsTextView.getParent() == null) {
            addView(tipsTextView);
        }
    }
}
