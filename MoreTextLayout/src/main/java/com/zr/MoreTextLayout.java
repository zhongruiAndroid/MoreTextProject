package com.zr;

import android.content.Context;
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

public class MoreTextLayout extends ViewGroup {


    private String ellipsize = "";

    private TextView textView;
    private CharSequence originText;
    private View needExpandView;
    private View needCollapseView;

    private boolean expand = false;
    private int minLine = 3;

    public static final int GRAVITY_RIGHT = 0;
    public static final int GRAVITY_LEFT = 1;

    private int collapseTipsGravity = 0;
    private boolean collapseTipsHidden = false;
    private boolean collapseTipsNewLine = false;
    private boolean needCollapseTipsNewLine;

    public int getMinLine() {
        return Math.max(minLine, 1);
    }

    public int getCollapseTipsGravity() {
        return collapseTipsGravity;
    }

    public void setCollapseTipsGravity(int collapseTipsGravity) {
        this.collapseTipsGravity = collapseTipsGravity;
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

        expand = typedArray.getBoolean(R.styleable.MoreTextLayout_expand, false);
        minLine = typedArray.getInteger(R.styleable.MoreTextLayout_minLine, 3);
        ellipsize = typedArray.getString(R.styleable.MoreTextLayout_ellipsis);

        collapseTipsGravity = typedArray.getInt(R.styleable.MoreTextLayout_collapseTipsGravity, 0);
        collapseTipsHidden = typedArray.getBoolean(R.styleable.MoreTextLayout_collapseTipsHidden, false);
        collapseTipsNewLine = typedArray.getBoolean(R.styleable.MoreTextLayout_collapseTipsNewLine, false);

        if (TextUtils.isEmpty(ellipsize)) {
            ellipsize = "";
        }
        typedArray.recycle();
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (params instanceof MoreTextLayout.LayoutParams) {
            int layoutType = ((LayoutParams) params).layoutType;
            if (layoutType == LayoutParams.TYPE_CONTENT && child instanceof TextView) {
                textView = (TextView) child;
                originText = textView.getText();
                super.addView(child, index, params);
            } else if (layoutType == LayoutParams.TYPE_EXPAND) {
                needExpandView = child;
                super.addView(child, index, params);
            } else if (layoutType == LayoutParams.TYPE_COLLAPSE) {
                needCollapseView = child;
                super.addView(child, index, params);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Log.i("=====", "=====tvContentonMeasure");
        int childCount = getChildCount();

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();

        int resultWidth = 0;
        int resultHeight = 0;

        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() == View.GONE) {
                continue;
            }
            measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, 0);

            LayoutParams lp = (LayoutParams) childView.getLayoutParams();
            //子view所占的宽度
            int childViewMeasuredWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            //子view所占的高度
            int childViewMeasuredHeight = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            resultWidth = Math.max(resultWidth, childViewMeasuredWidth);

            int layoutType = lp.layoutType;

            if (layoutType == LayoutParams.TYPE_CONTENT) {
                if (childView instanceof TextView) {
                    originText = ((TextView) childView).getText();
                }
                if (expand) {
                    resultHeight += childViewMeasuredHeight;
                    Log.i("=====", "===1==" + childViewMeasuredHeight);
                } else {
                    if (textView != null) {
                        Layout layout = textView.getLayout();
                        if (layout != null) {
                            int lineCount = layout.getLineCount();

                            /*如果文本总行数小于等于需要显示的行数*/
                            /*if (lineCount <= getMinLine()) {
                                resultHeight = childViewMeasuredHeight;
                                Log.i("=====","===2=="+childViewMeasuredHeight);
                                resultWidth = resultWidth + paddingLeft + paddingRight;
                                resultHeight = resultHeight + getPaddingTop() + getPaddingBottom();
                                Log.i("=====",heightSize+"====="+resultHeight+"========"+(heightMode == MeasureSpec.EXACTLY ? heightSize : resultHeight));
                                setMeasuredDimension(
                                        widthMode == MeasureSpec.EXACTLY ? widthSize : resultWidth,
                                        heightMode == MeasureSpec.EXACTLY ? heightSize : resultHeight);
                                return;
                            } else {

                            }*/
                            if (lineCount > 0) {
                                int tempCount = Math.min(lineCount, getMinLine());
                                /*这里少计算一行高度，是因为还要计算展开view的高度，因为这个高度包含了第minLine行高度*/
                                int minLineHeight =( childView.getMeasuredHeight() - childView.getPaddingTop() - childView.getPaddingBottom()) * (tempCount - 0) / lineCount + lp.topMargin + lp.bottomMargin;
                                resultHeight = resultHeight + (minLineHeight + childView.getPaddingTop() + childView.getPaddingBottom());
                                Log.i("=====", "===3==" + minLineHeight);

                            }
                        }
                    }
                }
            } else if (layoutType == LayoutParams.TYPE_COLLAPSE) {
                needCollapseTipsNewLine = false;
                if (expand) {
                    /*展开时，折叠tips是否隐藏*/
                    if (collapseTipsHidden) {
                        /*如果隐藏，不计算高度*/
                    } else {
                        /*如果不隐藏，是否换行*/
                        if (collapseTipsNewLine) {
                            needCollapseTipsNewLine = true;
                            resultHeight += childViewMeasuredHeight;
                            Log.i("=====", "===4==" + childViewMeasuredHeight);
                        } else {
                            /*如果不设置换行，计算文字内容剩余空间是否不需要换行*/
                            boolean alwaysNewLine = false;
                            if (textView != null) {
                                Layout layout = textView.getLayout();
                                int lineCount = layout.getLineCount();
                                if (lineCount > 0) {
                                    int lineStart = layout.getLineStart(lineCount - 1);
                                    int lineEnd = layout.getLineEnd(lineCount - 1);

                                    CharSequence charSequence = originText.subSequence(lineStart, lineEnd);

                                    Rect rect = new Rect();
                                    TextPaint paint = textView.getPaint();

                                    String tempLineText = ellipsize + charSequence;

                                    paint.getTextBounds(tempLineText, 0, tempLineText.length(), rect);

                                    alwaysNewLine = rect.width() + childViewMeasuredWidth > widthSize - paddingLeft - paddingRight;
                                }
                            }
                            if (alwaysNewLine) {
                                needCollapseTipsNewLine = true;
                                resultHeight += childViewMeasuredHeight;
                                Log.i("=====", "===5==" + childViewMeasuredHeight);
                            } else {

                            }
                        }
                    }
                }
            } else if (layoutType == LayoutParams.TYPE_EXPAND) {
                /*因为是需要展开的view，所以只在折叠时显示并计算宽高*/
                if (!expand) {
                    /*因为展开的view包在内部，这里不计算margin*/
//                    resultHeight +=childView.getMeasuredHeight();
                }
            }
        }

        resultWidth = resultWidth + paddingLeft + paddingRight;
        resultHeight = resultHeight + getPaddingTop() + getPaddingBottom();
        Log.i("=====", heightSize + "=====" + resultHeight + "========" + (heightMode == MeasureSpec.EXACTLY ? heightSize : resultHeight));
        setMeasuredDimension(
                widthMode == MeasureSpec.EXACTLY ? widthSize : resultWidth,
                heightMode == MeasureSpec.EXACTLY ? heightSize : resultHeight);


        if (textView != null && !expand && !TextUtils.isEmpty(originText)) {
            if (TextUtils.isEmpty(ellipsize)) {
                ellipsize = "";
            }
            /*如果是折叠状态，则开始设置折叠时的文本*/
            Layout layout = textView.getLayout();
            if (layout != null) {
                int lineCount = layout.getLineCount();
                if (lineCount >= getMinLine()) {
                    int lineStart = layout.getLineStart(getMinLine() - 1);
                    int lineEnd = layout.getLineEnd(getMinLine() - 1);

                    CharSequence charSequence = originText.subSequence(lineStart, lineEnd);

                    Rect rect = new Rect();
                    TextPaint paint = textView.getPaint();

                    String tempLineText = ellipsize + charSequence;
                    boolean flag = true;
                    int step = 0;
                    int spaceWidth = getMeasuredWidth() - paddingLeft - paddingRight;
                    int tipsWidth = 0;
                    if (needExpandView != null) {
                        LayoutParams layoutParams = (LayoutParams) needExpandView.getLayoutParams();
                        tipsWidth = needExpandView.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
                    }
                    while (flag) {
                        paint.getTextBounds(tempLineText, 0, tempLineText.length() - step, rect);
                        if (rect.width() + tipsWidth > spaceWidth) {
                            step += 1;
                        } else {
                            flag = false;
                        }
                    }
                    CharSequence startText = originText.subSequence(0, lineStart);
                    String showText = charSequence.subSequence(0, charSequence.length() - step) + ellipsize;
                    textView.setText(startText + showText);
                }
            }
        }
    }


    public void test() {
        if (textView != null) {
            Layout layout = textView.getLayout();
            int lineCount = layout.getLineCount();
            int lineStart = layout.getLineStart(lineCount - 1);
            int lineEnd = layout.getLineEnd(lineCount - 1);

            CharSequence charSequence = originText.subSequence(lineStart, lineEnd);

            Rect rect = new Rect();
            TextPaint paint = textView.getPaint();

            String tempLineText = ellipsize + charSequence;
            boolean flag = true;
            int stepCount = 0;
            while (flag) {
                paint.getTextBounds(tempLineText, 0, tempLineText.length() - stepCount, rect);
//                if (rect.width() + childViewMeasuredWidth > widthSize - paddingLeft - paddingRight) {
//                    stepCount += 2;
//                } else {
//                    flag = false;
//                }
            }
            String showText = originText.subSequence(0, lineEnd - stepCount) + ellipsize;
            // TODO: 2022/7/12 是否又会触发测量
            textView.setText(showText);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int measuredWidth = getMeasuredWidth();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() == View.GONE) {
                continue;
            }
            LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
            int layoutType = layoutParams.layoutType;
            if (layoutType == LayoutParams.TYPE_CONTENT) {
                childView.layout(paddingLeft + layoutParams.leftMargin, paddingTop + layoutParams.topMargin, paddingLeft + layoutParams.leftMargin + childView.getMeasuredWidth(), paddingTop + layoutParams.topMargin + childView.getMeasuredHeight());
            } else if (layoutType == LayoutParams.TYPE_EXPAND) {
                if (!expand) {
                    int viewRight = getMeasuredWidth() - paddingRight - layoutParams.rightMargin;
                    int viewBottom = getMeasuredHeight() - paddingBottom - layoutParams.bottomMargin;
                    int viewLeft = viewRight - childView.getMeasuredWidth();
                    int viewTop = viewBottom - childView.getMeasuredHeight();
                    childView.layout(viewLeft, viewTop, viewRight, viewBottom);
                }
            } else if (layoutType == LayoutParams.TYPE_COLLAPSE) {
                if (collapseTipsHidden) {
                    continue;
                }
                if (expand) {
                    /*是否需要换行*/
                    if (needCollapseTipsNewLine) {
                        if (getCollapseTipsGravity() == GRAVITY_RIGHT) {
                            /*居右显示*/
                            int viewRight = getMeasuredWidth() - paddingRight - layoutParams.rightMargin;
                            int viewBottom = getMeasuredHeight() - paddingBottom - layoutParams.bottomMargin;
                            int viewLeft = viewRight - childView.getMeasuredWidth();
                            int viewTop = viewBottom - childView.getMeasuredHeight();
                            childView.layout(viewLeft, viewTop, viewRight, viewBottom);
                        } else {
                            /*居左显示*/
                            int viewLeft = paddingLeft + layoutParams.leftMargin;
                            int viewBottom = getMeasuredHeight() - paddingBottom - layoutParams.bottomMargin;
                            int viewRight = viewLeft + childView.getMeasuredWidth();
                            int viewTop = viewBottom - childView.getMeasuredHeight();
                            childView.layout(viewLeft, viewTop, viewRight, viewBottom);
                        }
                    } else {
                        /*居右显示*/
                        int viewRight = getMeasuredWidth() - paddingRight - layoutParams.rightMargin;
                        int viewBottom = getMeasuredHeight() - paddingBottom - layoutParams.bottomMargin;
                        int viewLeft = viewRight - childView.getMeasuredWidth();
                        int viewTop = viewBottom - childView.getMeasuredHeight();
                        childView.layout(viewLeft, viewTop, viewRight, viewBottom);
                    }
                }
            }
        }
    }

    @Override
    public MoreTextLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MoreTextLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof MoreTextLayout.LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp instanceof MoreTextLayout.LayoutParams) {
            return new MoreTextLayout.LayoutParams((MoreTextLayout.LayoutParams) lp);
        } else if (lp instanceof MarginLayoutParams) {
            return new MoreTextLayout.LayoutParams((MarginLayoutParams) lp);
        }
        return new MoreTextLayout.LayoutParams(lp);
    }

    public static class LayoutParams extends MarginLayoutParams {
        public static final int TYPE_CONTENT = 1;
        /*需要展开的tipsViewType*/
        public static final int TYPE_EXPAND = 2;
        /*需要折叠的tipsViewType*/
        public static final int TYPE_COLLAPSE = 3;
        public int layoutType = 0;

        public LayoutParams(@NonNull Context c, @Nullable AttributeSet attrs) {
            super(c, attrs);
            TypedArray array = c.obtainStyledAttributes(attrs, R.styleable.MoreTextLayout_Layout);
            layoutType = array.getInt(R.styleable.MoreTextLayout_Layout_layout_type, 0);
            array.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(@NonNull ViewGroup.LayoutParams source) {
            super(source);
        }

        public LayoutParams(@NonNull MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(@NonNull MoreTextLayout.LayoutParams source) {
            super(source);
        }
    }


    public void expand() {
        expand = true;
        expandView();
    }

    public void collapse() {
        expand = false;
//        collapseView();
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

       /* CharSequence charSequence = text.subSequence(lineStart, lineEnd);

        Rect rect = new Rect();
        TextPaint paint = textView.getPaint();

        int tempCount = 1;

        String tempLineText = closeTips.toString() + charSequence;
        paint.getTextBounds(tempLineText, 0, tempLineText.length() - tempCount, rect);
        *//*如果最后一行的宽度+tips大于父view宽度*//*
        if (rect.width() + tipsSpace > getMeasuredWidth()) {
            //tips显示在最后一行的下一行
            textView.setText(text);
            if (expandTipsGravity == 0) {
                setTipsView(0, lineTop, lineBottom, true);
            } else {
                setTipsView(getMeasuredWidth() - closeTips.length() * tipsSize, lineTop, lineBottom, true);
            }

        } else {
            //tips显示在最后一行
            textView.setText(text);
            setTipsView(lineRight, lineTop, lineBottom);
        }*/
    }

//    private void collapseView() {
//        if (textView == null || textView.getParent() == null) {
//            return;
//        }
//        Layout layout = textView.getLayout();
//        //收缩时，如果显示行数大于设置的最大行数
//        if (layout.getLineCount() > maxLines) {
//            int lineStart = layout.getLineStart(maxLines - 1);
//            int lineEnd = layout.getLineEnd(maxLines - 1);
//            float lineRight = layout.getLineRight(maxLines - 1);
//            float lineTop = layout.getLineTop(maxLines - 1);
//            float lineBottom = layout.getLineBottom(maxLines - 1);
//
//
//            CharSequence charSequence = text.subSequence(lineStart, lineEnd);
//
//            Rect rect = new Rect();
//            TextPaint paint = textView.getPaint();
//
//            int tempCount = 1;
//
//            final String tempLineText = ellipsize + charSequence;
//            boolean flag = true;
//            float tipsLength = openTips.length() * tipsSize;
//            while (flag) {
//                paint.getTextBounds(tempLineText, 0, tempLineText.length() - tempCount, rect);
//                /*最后一行文字+。。。+提示 长度需要小于父view宽度*/
//                if (rect.width() + tipsSpace + tipsLength + dp2px(12) > getMeasuredWidth()) {
//                    tempCount += 1;
//                } else {
//                    flag = false;
//                }
//            }
//            String showText = text.subSequence(0, lineEnd - tempCount) + ellipsize;
//            textView.setText(showText);
//
//
//            setTipsView(rect.width() + tipsSpace, lineTop, lineBottom);
//        } else {
//            textView.setText(text);
//            removeView(tipsTextView);
//        }
//    }

    private int dp2px(int dp) {
        return (int) (getResources().getDisplayMetrics().density * dp);
    }

    private Rect offsetRect = new Rect();

    private void setTipsView(float lineRight, float lineTop, float lineBottom) {
        setTipsView(lineRight, lineTop, lineBottom, false);
    }

    private void setTipsView(float lineRight, float lineTop, float lineBottom, boolean showLastLine) {

//        if (offsetRect != null) {
//            offsetRect.setEmpty();
//        }
//
//        float offsetX;
//        float offsetY;
//        if (showLastLine) {
//            /*展开状态，显示的是closetips*/
//            offsetX = tipsLastXOffset + lineRight;
//            offsetY = lineBottom + tipsLastYOffset;
//        } else {
//            offsetX = lineRight + tipsSpace + tipsXOffset;
//            offsetY = tipsYOffset + lineBottom + (tipsTextView.getPaint().getFontMetrics().top - tipsTextView.getPaint().getFontMetrics().bottom) + 1;
//        }
//        MarginLayoutParams layoutParams = (MarginLayoutParams) tipsTextView.getLayoutParams();
//        if (layoutParams == null) {
//            layoutParams = new MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        }
//        layoutParams.topMargin = (int) offsetY;
//        layoutParams.leftMargin = (int) offsetX;
//        tipsTextView.setLayoutParams(layoutParams);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        initContentView();
    }

//    private void initContentView() {
//        if (maxLines == 0 || TextUtils.isEmpty(text)) {
//            expand = true;
//            return;
//        }
//        if (textView == null) {
//            textView = new AppCompatTextView(getContext());
//        }
//        if (textSize != -1) {
//            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
//        }
//        textView.setTextScaleX(textScaleX);
//        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        if (!TextUtils.isEmpty(text)) {
//            textView.setText(text);
//        }
//        if (!TextUtils.isEmpty(hint)) {
//            textView.setHint(hint);
//        }
//        if (textColor != null) {
//            textView.setTextColor(textColor);
//        }
//        textView.setHighlightColor(textColorHighlight);
//        if (textColorHint != null) {
//            textView.setHintTextColor(textColorHint);
//        }
//
//        int style = Typeface.NORMAL;
//        if (textStyle == Typeface.BOLD_ITALIC) {
//            style = Typeface.BOLD_ITALIC;
//        } else {
//            if (textStyle == Typeface.BOLD) {
//                style = Typeface.BOLD;
//            } else if (textStyle == Typeface.ITALIC) {
//                style = Typeface.ITALIC;
//            }
//        }
//
//        if (fontFamily != null) {
//            textView.setTypeface(fontFamily, style);
//        } else {
//            if (typeface == sans) {
//                textView.setTypeface(Typeface.SANS_SERIF, style);
//            } else if (typeface == serif) {
//                textView.setTypeface(Typeface.SERIF, style);
//            } else if (typeface == monospace) {
//                textView.setTypeface(Typeface.MONOSPACE, style);
//            } else {
//                textView.setTypeface(Typeface.DEFAULT, style);
//            }
//        }
//        if (textColorLink != null) {
//            textView.setLinkTextColor(textColorLink);
//        }
//        textView.setAllCaps(textAllCaps);
//        if (shadowColor != 0) {
//            textView.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor);
//        }
//        if (textView.getParent() == null) {
//            addView(textView);
//        }
//        textView.post(new Runnable() {
//            @Override
//            public void run() {
//                if (expand) {
//                    expandView();
//                } else {
//                    collapseView();
//                }
//            }
//        });
//    }

//    public void complete() {
//        if (expand) {
//            expandView();
//        } else {
//            collapseView();
//        }
//    }
    //////////////////////////////////////////////////////////////////////////////////////////

}
