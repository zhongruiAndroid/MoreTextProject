package com.zr;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
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
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatTextView;

public class MoreTextLayout extends ViewGroup {

    private TextView tempView;
    private boolean useAnim = true;
    private int useAnimMaxHeight;
    private int useAnimMinHeight;

    private String ellipsize = "";

    private TextView textView;
    private CharSequence originText;
    private View needExpandView;
    private View needCollapseView;

    private boolean expand = false;
    private int minLine = 3;

    public static final int GRAVITY_RIGHT = 0;
    public static final int GRAVITY_LEFT = 1;

    private int collapseTipsGravity = GRAVITY_RIGHT;
    private boolean collapseTipsHidden = false;
    private boolean collapseTipsNewLine = false;


    /*用于onlayout*/
    private boolean needCollapseTipsNewLine;

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
            ellipsize = "...";
        }
        typedArray.recycle();


        if (tempView == null) {
            tempView = new TextView(getContext());
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (params instanceof MoreTextLayout.LayoutParams) {
            int layoutType = ((LayoutParams) params).layoutType;
            if (layoutType == LayoutParams.TYPE_CONTENT && child instanceof TextView) {
                textView = (TextView) child;
                super.addView(child, index, params);
            } else if (layoutType == LayoutParams.TYPE_EXPAND) {
                needExpandView = child;
                needExpandView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isExpand()) {
                            return;
                        }
                        setExpand(true);
//                        requestLayout();
                    }
                });
                super.addView(child, index, params);
            } else if (layoutType == LayoutParams.TYPE_COLLAPSE) {
                needCollapseView = child;
                needCollapseView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isExpand()) {
                            return;
                        }
                        setExpand(false);
//                        requestLayout();
                    }
                });
                super.addView(child, index, params);
            }
        }
    }

    private boolean setTextFlag;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        Log.i("=====", "=====onMeasure");
        int childCount = getChildCount();

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (textView != null) {
            if (!setTextFlag && !isAnimRunning()) {
                originText = textView.getText();
            }
            setTextFlag = false;


            if (!isAnimRunning()) {
                /*计算最高和最低高度,用于执行动画*/
                tempView = MoreTextLayoutUtils.measureContentView(tempView, textView);
                tempView.setText(originText);
                tempView.setMaxLines(Integer.MAX_VALUE);
                measureChildWithMargins(tempView, widthMeasureSpec, 0, heightMeasureSpec, 0);

                MarginLayoutParams tempViewLayoutParams = (MarginLayoutParams) tempView.getLayoutParams();
                useAnimMaxHeight = tempView.getMeasuredHeight() + tempViewLayoutParams.topMargin + tempViewLayoutParams.bottomMargin;

                Layout layout = textView.getLayout();
                int lineCount = getMinLine();
                if (layout != null) {
                    lineCount = Math.min(lineCount, layout.getLineCount());
                }
                tempView.setMaxLines(lineCount);
                measureChildWithMargins(tempView, widthMeasureSpec, 0, heightMeasureSpec, 0);

                useAnimMinHeight = tempView.getMeasuredHeight() + tempViewLayoutParams.topMargin + tempViewLayoutParams.bottomMargin;

                Log.i("=====", "=====childViewMeasuredHeight1=" + useAnimMinHeight);
                Log.i("=====", "=====childViewMeasuredHeight2=" + useAnimMaxHeight);
            }

        }
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
                if (expand) {
                    resultHeight += childViewMeasuredHeight;
                    Log.i("=====", "===1==" + childViewMeasuredHeight);
                } else {
                    if (isAnimRunning()) {
                        resultHeight = childViewMeasuredHeight;
                    } else {
                        if (textView != null) {
                            Layout layout = textView.getLayout();
                            if (layout != null) {
                                int lineCount = layout.getLineCount();
                                if (lineCount <= getMinLine()) {
                                    resultHeight = childViewMeasuredHeight;
                                } else {
                                    textView.setMaxLines(getMinLine());
                                    textView.measure(widthMeasureSpec, heightMeasureSpec);
                                    resultHeight = lp.topMargin + lp.bottomMargin + textView.getMeasuredHeight();
                                }
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
                                    int length = originText.length();
                                    lineEnd = Math.min(lineEnd, length);
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


        if (!isAnimRunning() && textView != null && !expand && !TextUtils.isEmpty(textView.getText())) {
            if (TextUtils.isEmpty(ellipsize)) {
                ellipsize = "";
            }
            /*如果是折叠状态，则开始设置折叠时的文本*/
            Layout layout = textView.getLayout();
            if (layout != null) {
                int lineCount = layout.getLineCount();
                if (lineCount > getMinLine()) {
                    int lineStart = layout.getLineStart(getMinLine() - 1);
                    int lineEnd = layout.getLineEnd(getMinLine() - 1);
                    CharSequence text = textView.getText();
                    CharSequence charSequence = text.subSequence(lineStart, lineEnd);

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
                    CharSequence startText = text.subSequence(0, lineStart);
                    String showText = charSequence.subSequence(0, charSequence.length() - step) + ellipsize;
                    textView.setText(startText + showText);
                    setTextFlag = true;
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        int measuredWidth = getMeasuredWidth();
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

    private boolean isAnimRunning() {
        return valueAnimator != null && valueAnimator.isRunning();
    }

    private ValueAnimator valueAnimator;

    private void updateHeight( int animStartHeight, final int animEndHeight) {
        Log.i("=====", animStartHeight + "==updateHeight===" + animEndHeight);
        valueAnimator = ValueAnimator.ofInt(animStartHeight, animEndHeight);
        valueAnimator.setDuration(1500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (textView == null) {
                    return;
                }
                ViewGroup.LayoutParams layoutParams = textView.getLayoutParams();
                layoutParams.height = (int) animation.getAnimatedValue();
                textView.setLayoutParams(layoutParams);

            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Log.i("=====", "=====onMeasureonAnimationEnd");
            }
        });
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.start();
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

    /***********************************************************************************************/
    public String getEllipsize() {
        return ellipsize;
    }

    public MoreTextLayout setEllipsize(String ellipsize) {
        this.ellipsize = ellipsize;
        return this;
    }

    public boolean isExpand() {
        return expand;
    }

    public MoreTextLayout setExpand(boolean expand) {
        if (this.expand == expand) {
            return this;
        }
        this.expand = expand;
        if (useAnim) {
            updateHeight(expand?useAnimMinHeight:useAnimMaxHeight, expand?useAnimMaxHeight:useAnimMinHeight);
        }
        if (expand) {
            if (textView != null) {
                textView.setMaxLines(Integer.MAX_VALUE);
                textView.setText(originText);
            }
            if (needExpandView != null) {
                needExpandView.setVisibility(GONE);
            }
            if (needCollapseView != null) {
                needCollapseView.setVisibility(VISIBLE);
            }
        } else {
            if (needExpandView != null) {
                needExpandView.setVisibility(VISIBLE);
            }
            if (needCollapseView != null) {
                needCollapseView.setVisibility(GONE);
            }
        }

        return this;
    }

    public MoreTextLayout setMinLine(int minLine) {
        this.minLine = minLine;
        return this;
    }

    public boolean isCollapseTipsHidden() {
        return collapseTipsHidden;
    }

    public MoreTextLayout setCollapseTipsHidden(boolean collapseTipsHidden) {
        this.collapseTipsHidden = collapseTipsHidden;
        return this;
    }

    public boolean isCollapseTipsNewLine() {
        return collapseTipsNewLine;
    }

    public MoreTextLayout setCollapseTipsNewLine(boolean collapseTipsNewLine) {
        this.collapseTipsNewLine = collapseTipsNewLine;
        return this;
    }


    public int getMinLine() {
        return Math.max(minLine, 1);
    }

    public int getCollapseTipsGravity() {
        return collapseTipsGravity;
    }

    public MoreTextLayout setCollapseTipsGravity(int collapseTipsGravity) {
        this.collapseTipsGravity = collapseTipsGravity;
        return this;
    }

    public CharSequence getText() {
        if (textView != null) {
            return textView.getText();
        }
        return "";
    }

    public CharSequence getOriginText() {
        if (TextUtils.isEmpty(originText)) {
            return "";
        }
        return originText;
    }

    public MoreTextLayout setText(CharSequence originText) {
        this.originText = originText;
        if (textView != null) {
            textView.setText(originText);
        }
        return this;
    }
    /***********************************************************************************************/

}
