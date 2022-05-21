package com.example.widget.gesturepsw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: created by ZhaoBeibei on 2020-01-10 11:03
 * @describe: 手势小密码盘
 */
public class GesturePswDisplayView extends View {
    private static final String TAG = "GesturePwdDisplayView";
    private Paint mPaint;
    private int mDotCount = 3;
    private int mCircleRadius;
    private int mDotMargin;
    //选中颜色
    private int mDotSelectedColor = Color.YELLOW;
    //未选中时颜色
    private int mDotUnSelectedColor = Color.parseColor("#E6E6E6");
    private List<Dot> mDotList = new ArrayList<>();
    private List<Integer> mAnswerList = new ArrayList<>();

    public GesturePswDisplayView(Context context) {
        this(context, null);
    }

    public GesturePswDisplayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        width = width > height ? height : width;
        setMeasuredDimension(width, width);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCircleRadius = (int) (getWidth() * 2 * 1.0f / (10 * mDotCount + 1));
        mDotMargin = (int) (3 * mCircleRadius);
        for (int i = 0; i < mDotCount * mDotCount; i++) {
            //计算圆心坐标
            float x = i % mDotCount * 2 * mCircleRadius + mCircleRadius + i % mDotCount * mDotMargin + mDotMargin;
            float y = i / mDotCount * 2 * mCircleRadius + mCircleRadius + i / mDotCount * mDotMargin + mDotMargin;
            //初始化点坐标
            mDotList.add(new Dot(x, y, i));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //同步dot状态
        syncAnswerState();
        for (int i = 0; i < mDotList.size(); i++) {
            Dot dot = mDotList.get(i);
            if (dot.isSelected()) {
                mPaint.setColor(mDotSelectedColor);
            } else {
                mPaint.setColor(mDotUnSelectedColor);
            }
            canvas.drawCircle(dot.getX(), dot.getY(), mCircleRadius, mPaint);
        }
    }

    private void syncAnswerState() {
        //先重置所有点的状态
        for (Dot dot : mDotList) {
            dot.setSelected(false);
        }
        //设置答案index的状态
        for (int i = 0; i < mAnswerList.size(); i++) {
            int index = mAnswerList.get(i);
            mDotList.get(index).setSelected(true);
        }
    }

    /**
     * 设置答案
     *
     * @param answer
     */
    public void setAnswer(int... answer) {
        for (int i = 0; i < answer.length; i++) {
            mAnswerList.add(answer[i]);
        }
        postInvalidate();
    }

    /**
     * 设置答案
     *
     * @param list
     */
    public void setAnswer(List<Integer> list) {
        this.mAnswerList = list;
        postInvalidate();
    }

    /**
     * 设置选中颜色
     *
     * @param color
     */
    public void setDotSelectedColor(int color) {
        this.mDotSelectedColor = color;
        postInvalidate();
    }

    /**
     * 设置非选中颜色
     *
     * @param color
     */
    public void setDotUnSelectedColor(int color) {
        this.mDotUnSelectedColor = color;
        postInvalidate();
    }

    /**
     * 设置点的个数
     *
     * @param dotCount
     */
    public void setDotCount(int dotCount) {
        this.mDotCount = dotCount;
    }


    /**
     * 恢复初始未选择的状态
     */
    public void reset() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                for (Dot dot : mDotList) {
                    dot.setSelected(false);
                }

                mAnswerList.clear();
                postInvalidate();
            }
        }, 1000);
    }


    public void setDisplayViewStatus(List<Integer> list, int color) {
        setDotSelectedColor(color);
        setAnswer(list);
    }
}
