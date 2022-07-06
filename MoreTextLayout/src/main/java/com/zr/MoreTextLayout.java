package com.zr;

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
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;

public class MoreTextLayout extends FrameLayout {

    private float tipsXOffset = 0;
    private float tipsYOffset = 0;
    private float tipsLastXOffset = 0;
    private float tipsLastYOffset = 0;
    private int tipsLastGravity = 0;
    private String ellipsize = "";

    private AppCompatTextView textView;


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


        tipsXOffset = typedArray.getDimensionPixelSize(R.styleable.MoreTextLayout_tipsXOffset, 0);
        tipsYOffset = typedArray.getDimensionPixelSize(R.styleable.MoreTextLayout_tipsYOffset, 0);

        tipsLastXOffset = typedArray.getDimensionPixelSize(R.styleable.MoreTextLayout_tipsLastXOffset, 0);
        tipsLastYOffset = typedArray.getDimensionPixelSize(R.styleable.MoreTextLayout_tipsLastYOffset, 0);
        tipsLastGravity = typedArray.getInt(R.styleable.MoreTextLayout_tipsLastGravity, 0);
        ellipsize = typedArray.getString(R.styleable.MoreTextLayout_ellipsis);
        if (TextUtils.isEmpty(ellipsize)) {
            ellipsize = "";
        }


        typedArray.recycle();


    }



    public void expand() {
        expand = true;
        expandView();
    }

    public void collapse() {
        expand = false;
        collapseView();
    }

    private void expandView() {
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

        String tempLineText = closeTips.toString() + charSequence;
        paint.getTextBounds(tempLineText, 0, tempLineText.length() - tempCount, rect);
        /*如果最后一行的宽度+tips大于父view宽度*/
        if (rect.width() + tipsSpace > getMeasuredWidth()) {
            //tips显示在最后一行的下一行
            textView.setText(text);
            if (tipsLastGravity == 0) {
                setTipsView(0, lineTop, lineBottom, true);
            } else {
                setTipsView(getMeasuredWidth() - closeTips.length() * tipsSize, lineTop, lineBottom, true);
            }

        } else {
            //tips显示在最后一行
            textView.setText(text);
            setTipsView(lineRight, lineTop, lineBottom);
        }
    }

    private void collapseView() {
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

            final String tempLineText = ellipsize + charSequence;
            boolean flag = true;
            float tipsLength = openTips.length() * tipsSize;
            while (flag) {
                paint.getTextBounds(tempLineText, 0, tempLineText.length() - tempCount, rect);
                /*最后一行文字+。。。+提示 长度需要小于父view宽度*/
                if (rect.width() + tipsSpace + tipsLength + dp2px(12) > getMeasuredWidth()) {
                    tempCount += 1;
                } else {
                    flag = false;
                }
            }
            String showText = text.subSequence(0, lineEnd - tempCount) + ellipsize;
            textView.setText(showText);


            setTipsView(rect.width() + tipsSpace, lineTop, lineBottom);
        } else {
            textView.setText(text);
            removeView(tipsTextView);
        }
    }

    private int dp2px(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }

    private Rect offsetRect = new Rect();

    private void setTipsView(float lineRight, float lineTop, float lineBottom) {
        setTipsView(lineRight, lineTop, lineBottom, false);
    }

    private void setTipsView(float lineRight, float lineTop, float lineBottom, boolean showLastLine) {
        if (tipsTextView == null) {
            tipsTextView = new AppCompatTextView(getContext());
        }

        tipsTextView.setText(expand ? closeTips : openTips);
        if (openTipsColor != null) {
            /*展开状态，显示的是closetips*/
            tipsTextView.setTextColor(expand ? closeTipsColor : openTipsColor);
        }
        if (tipsSize != -1) {
            tipsTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, tipsSize);
        }
        if (offsetRect != null) {
            offsetRect.setEmpty();
        }
        tipsTextView.getPaint().getTextBounds(openTips, 0, openTips.length(), offsetRect);

        float offsetX;
        float offsetY;
        if (showLastLine) {
            /*展开状态，显示的是closetips*/
            offsetX = tipsLastXOffset + lineRight;
            offsetY = lineBottom + tipsLastYOffset;
        } else {
            offsetX = lineRight + tipsSpace + tipsXOffset;
            offsetY = tipsYOffset + lineBottom + (tipsTextView.getPaint().getFontMetrics().top - tipsTextView.getPaint().getFontMetrics().bottom) + 1;
        }
        MarginLayoutParams layoutParams = (MarginLayoutParams) tipsTextView.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        layoutParams.topMargin = (int) offsetY;
        layoutParams.leftMargin = (int) offsetX;
        tipsTextView.setLayoutParams(layoutParams);
        /*控制tips  x轴偏移*/
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
                    expandView();
                } else {
                    if (openTipsColor != null) {
                        tipsTextView.setTextColor(openTipsColor);
                    }
                    collapseView();
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

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initContentView();
    }

    private void initContentView() {
        if (maxLines == 0 || TextUtils.isEmpty(text)) {
            expand = true;
            return;
        }
        if (textView == null) {
            textView = new AppCompatTextView(getContext());
        }
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
        if (textView.getParent() == null) {
            addView(textView);
        }
        textView.post(new Runnable() {
            @Override
            public void run() {
                if (expand) {
                    expandView();
                } else {
                    collapseView();
                }
            }
        });
    }

    public void complete() {
        if (expand) {
            expandView();
        } else {
            collapseView();
        }
    }
    //////////////////////////////////////////////////////////////////////////////////////////

    public CharSequence getOpenTips() {
        return openTips;
    }

    public void setOpenTips(CharSequence openTips) {
        this.openTips = openTips;
    }


    public void setOpenTipsColor(@ColorInt int color) {
        this.openTipsColor = ColorStateList.valueOf(color);
    }

    public CharSequence getCloseTips() {
        return closeTips;
    }

    public void setCloseTips(CharSequence closeTips) {
        this.closeTips = closeTips;
    }


    public void setCloseTipsColor(@ColorInt int color) {
        this.closeTipsColor = ColorStateList.valueOf(color);
    }

    public float getTipsSize() {
        return tipsSize;
    }

    public void setTipsSize(float tipsSize) {
        this.tipsSize = tipsSize;
    }

    public float getTipsSpace() {
        return tipsSpace;
    }

    public void setTipsSpace(float tipsSpace) {
        this.tipsSpace = tipsSpace;
    }

    public float getTipsXOffset() {
        return tipsXOffset;
    }

    public void setTipsXOffset(float tipsXOffset) {
        this.tipsXOffset = tipsXOffset;
    }

    public float getTipsYOffset() {
        return tipsYOffset;
    }

    public void setTipsYOffset(float tipsYOffset) {
        this.tipsYOffset = tipsYOffset;
    }

    public float getTipsLastXOffset() {
        return tipsLastXOffset;
    }

    public void setTipsLastXOffset(float tipsLastXOffset) {
        this.tipsLastXOffset = tipsLastXOffset;
    }

    public float getTipsLastYOffset() {
        return tipsLastYOffset;
    }

    public void setTipsLastYOffset(float tipsLastYOffset) {
        this.tipsLastYOffset = tipsLastYOffset;
    }

    public int getTipsLastGravity() {
        return tipsLastGravity;
    }

    public void setTipsLastGravity(int tipsLastGravity) {
        this.tipsLastGravity = tipsLastGravity;
    }

    public String getEllipsize() {
        return ellipsize;
    }

    public void setEllipsize(String ellipsize) {
        this.ellipsize = ellipsize;
    }

    public int getMaxLines() {
        return maxLines;
    }

    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
    }

    public CharSequence getText() {
        return text;
    }

    public void setText(CharSequence text) {
        this.text = text;
    }

    public CharSequence getHint() {
        return hint;
    }

    public void setHint(CharSequence hint) {
        this.hint = hint;
    }


    public void setTextColor(@ColorInt int Color) {
        this.textColor = ColorStateList.valueOf(Color);
    }

    public int getTextColorHighlight() {
        return textColorHighlight;
    }

    public void setTextColorHighlight(@ColorInt int textColorHighlight) {
        this.textColorHighlight = textColorHighlight;
    }


    public void setTextColorHint(@ColorInt int Color) {
        this.textColorHint = ColorStateList.valueOf(Color);
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getTextScaleX() {
        return textScaleX;
    }

    public void setTextScaleX(float textScaleX) {
        this.textScaleX = textScaleX;
    }

    public int getTypeface() {
        return typeface;
    }

    public void setTypeface(int typeface) {
        this.typeface = typeface;
    }

    public int getTextStyle() {
        return textStyle;
    }

    public void setTextStyle(int textStyle) {
        this.textStyle = textStyle;
    }

    public Typeface getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(Typeface fontFamily) {
        this.fontFamily = fontFamily;
    }

    public void setTextColorLink(@ColorInt int color) {
        this.textColorLink = ColorStateList.valueOf(color);
    }

    public boolean isTextAllCaps() {
        return textAllCaps;
    }

    public void setTextAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
    }

    public int getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(@ColorInt int shadowColor) {
        this.shadowColor = shadowColor;
    }

    public float getShadowDx() {
        return shadowDx;
    }

    public void setShadowDx(float shadowDx) {
        this.shadowDx = shadowDx;
    }

    public float getShadowDy() {
        return shadowDy;
    }

    public void setShadowDy(float shadowDy) {
        this.shadowDy = shadowDy;
    }

    public float getShadowRadius() {
        return shadowRadius;
    }

    public void setShadowRadius(float shadowRadius) {
        this.shadowRadius = shadowRadius;
    }

    public boolean isExpand() {
        return expand;
    }

}
