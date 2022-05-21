package com.example.widget.gesturepsw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.widget.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: created by ZhaoBeibei on 2020-01-09 11:21
 * @describe:手势密码控件
 */
public class GesturePasswordView extends View {
    private static final String TAG = "GesturePasswordView";
    /**
     * 格子数
     */
    private static final int POINT_COUNT = 9;
    /**
     * 最少连接的点数，否则提示错误
     */
    private static final int MIN_POINT_COUNT = 4;
    /**
     * 中心点的半径
     */
    private static final int POINT_RADIUS = 30;
    /**
     * 外圈半径
     */
    private static final int CIRCLE_RADIUS = 80;
    /**
     * 错误中心点颜色
     */
    private int errorPointColor = R.color.error_point_color;
    /**
     * 错误中间圆环颜色
     */
    private int errorCircleColor = R.color.error_circle_color;
    /**
     * 错误最外层圆圈边框颜色
     */
    private int errorBorderCircleColor = R.color.error_border_circle_color;
    /**
     * 错误连接线颜色
     */
    private int errorLinkColor = R.color.error_link_color;
    /**
     * 选中的中心点颜色
     */
    private int selectPointColor = R.color.right_point_color;
    /**
     * 选中的中间圆环颜色
     */
    private int selectCircleColor = R.color.right_circle_color;
    /**
     * 选中的最外层圆圈边框颜色
     */
    private int selectBorderCircleColor = R.color.right_border_circle_color;
    /**
     * 选中的连接线颜色
     */
    private int linkColor = R.color.right_point_color;
    /**
     * 未选中的中心点颜色
     */
    private int unSelectPointColor = R.color.default_point_color;
    private Paint mPaint = new Paint();

    /**
     * 记录选择过的点下标
     */
    private List<Integer> selectList = new ArrayList();
    private Map<Integer, int[]> pointMap = new HashMap<>(10);
    private GestureListener listener;
    private boolean mPatternInProgress = false;//当开始画的时候，置为true 抬起变为false
    private float mInProgressX = -1;
    private float mInProgressY = -1;
    private final Path mCurrentPath = new Path();
    private Paint mPathPaint;


    public GesturePasswordView(Context context) {
        this(context, null);
    }

    public GesturePasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mPathPaint = new Paint();
        mPathPaint.setAntiAlias(true); // 防锯齿
        mPathPaint.setDither(true); //防抖动
        mPathPaint.setStyle(Paint.Style.STROKE); //设置画笔的样式，为FILL，FILL_OR_STROKE，或STROKE（空心）
        mPathPaint.setStrokeJoin(Paint.Join.ROUND);//设置绘制时画笔与图形的结合方式，METER\ROUND\BEVEL  平滑效果
        mPathPaint.setStrokeCap(Paint.Cap.ROUND);//当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的图形样式，圆形样式ROUND,或方形样式SQUARE BUTT
        mPathPaint.setStrokeWidth(4); //当画笔样式为STROKE或FILL_OR_STROKE时，设置笔刷的粗细度
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initPointMap();
        for (int i = 0; i < POINT_COUNT; i++) {
            int[] location = getLocation(i);
            if (selectList.contains(i)) {
                //画圆圈
                mPaint.setColor(getResources().getColor(selectCircleColor));
                canvas.drawCircle(location[0], location[1], CIRCLE_RADIUS, mPaint);
                //画圆圈边框
                mPaint.setColor(getResources().getColor(selectBorderCircleColor));
                mPaint.setStrokeWidth(2);
                mPaint.setStyle(Paint.Style.STROKE);
                canvas.drawCircle(location[0], location[1], CIRCLE_RADIUS - 2, mPaint);
                mPaint.setStyle(Paint.Style.FILL);
                //画中心选择态的点
                mPaint.setColor(getResources().getColor(selectPointColor));
                canvas.drawCircle(location[0], location[1], POINT_RADIUS, mPaint);
                if (null != listener) {
                    listener.onDraw(i);
                }
            } else {
                //画未选择的点
                mPaint.setColor(getResources().getColor(unSelectPointColor));
                canvas.drawCircle(location[0], location[1], POINT_RADIUS, mPaint);
            }
        }

        //画连线
        if (selectList.size() > 0) {
            Path currentPath = mCurrentPath;
            mPathPaint.setColor(getResources().getColor(linkColor));
            float lastX = 0f;
            float lastY = 0f;
            for (int i = 0; i < selectList.size(); i++) {
                float centerX = getLocation(selectList.get(i))[0];
                float centerY = getLocation(selectList.get(i))[1];
                if (i != 0) {
                    currentPath.rewind();
                    currentPath.moveTo(lastX, lastY);
                    currentPath.lineTo(centerX, centerY);
                    canvas.drawPath(currentPath, mPathPaint);
                }
                lastX = centerX;
                lastY = centerY;
            }

            if (mPatternInProgress) {
                currentPath.rewind();
                currentPath.moveTo(lastX, lastY);
                currentPath.lineTo(mInProgressX, mInProgressY);
                canvas.drawPath(currentPath, mPathPaint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (null != listener) {
                    listener.onStart();
                }
            case MotionEvent.ACTION_MOVE:
                int selectIndex = getSelectPoint(event.getX(), event.getY());
                if (selectIndex >= 0 && !selectList.contains(selectIndex)) {
                    selectList.add(selectIndex);
                }
                mPatternInProgress = true;
                mInProgressX = event.getX();
                mInProgressY = event.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mPatternInProgress = false;
                if (getSelectPointCount() < MIN_POINT_COUNT) {
                    showError();
                    if (null != listener) {
                        listener.onError(selectList);
                    }
                } else {
                    if (null != listener) {
                        listener.onFinish(selectList);
                    }
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 获取当前触摸的点
     */
    private int getSelectPoint(float touchX, float touchY) {
        Set<Map.Entry<Integer, int[]>> entries = pointMap.entrySet();
        for (Map.Entry<Integer, int[]> entry : entries) {
            int[] value = entry.getValue();
            boolean xOk = touchX > value[0] - CIRCLE_RADIUS && touchX < value[0] + CIRCLE_RADIUS;
            boolean yOk = touchY > value[1] - CIRCLE_RADIUS && touchY < value[1] + CIRCLE_RADIUS;
            if (xOk && yOk) {
                entry.getKey();
                return entry.getKey();
            }
        }
        return -1;
    }


    /**
     * 保存每个点对应的坐标到Map中
     */
    private void initPointMap() {
        if (pointMap.size() > 0) {
            return;
        }
        for (int i = 0; i < POINT_COUNT; i++) {
            int[] location = getLocation(i);
            pointMap.put(i, location);
        }
    }

    /**
     * 显示错误，比如连结的点过少
     */
    public void showError() {
        setColor(errorPointColor, errorCircleColor, errorBorderCircleColor, errorLinkColor);
        invalidate();
        reset();
    }

    /**
     * 设置点，圈，和连接线的颜色
     */
    public void setColor(int pointColor, int circleColor, int borderCircleColor, int linkColor) {
        if (pointColor > 0) {
            this.selectPointColor = pointColor;
        }
        if (circleColor > 0) {
            this.selectCircleColor = circleColor;
        }
        if (borderCircleColor > 0) {
            this.selectBorderCircleColor = borderCircleColor;
        }
        if (linkColor > 0) {
            this.linkColor = linkColor;
        }
    }

    /**
     * 获取已经选择的点数量
     */
    private int getSelectPointCount() {
        if (null == selectList || selectList.isEmpty()) {
            return 0;
        }
        List<Integer> newList = new ArrayList<>();
        for (int i : selectList) {
            if (!newList.contains(i)) {
                newList.add(i);
            }
        }
        return newList.size();
    }


    /**
     * 获取某个点的具体坐标
     */
    private int[] getLocation(int index) {
        int width = getWidth();
        int height = getHeight();
        int[] location = new int[2];
        switch (index) {
            case 0:
                location[0] = CIRCLE_RADIUS;
                location[1] = CIRCLE_RADIUS;
                break;
            case 1:
                location[0] = width / 2;
                location[1] = CIRCLE_RADIUS;
                break;
            case 2:
                location[0] = width - CIRCLE_RADIUS;
                location[1] = CIRCLE_RADIUS;
                break;
            case 3:
                location[0] = CIRCLE_RADIUS;
                location[1] = height / 2;
                break;
            case 4:
                location[0] = width / 2;
                location[1] = height / 2;
                break;
            case 5:
                location[0] = width - CIRCLE_RADIUS;
                location[1] = height / 2;
                break;
            case 6:
                location[0] = CIRCLE_RADIUS;
                location[1] = height - CIRCLE_RADIUS;
                break;
            case 7:
                location[0] = width / 2;
                location[1] = height - CIRCLE_RADIUS;
                break;
            case 8:
                location[0] = width - CIRCLE_RADIUS;
                location[1] = height - CIRCLE_RADIUS;
                break;
            default:
                break;
        }
        return location;
    }

    /**
     * 设置选中的中心点颜色
     *
     * @param color
     */
    public void setSelectPointColor(int color) {
        this.selectPointColor = color;
    }

    /**
     * 设置选中的中间圆环的颜色
     *
     * @param color
     */
    public void setSelectCircleColor(int color) {
        this.selectCircleColor = color;
    }

    /**
     * 设置选中的圆圈边框颜色
     *
     * @param color
     */
    public void setSelectBorderCircleColor(int color) {
        this.selectBorderCircleColor = color;
    }

    /**
     * 设置连接线颜色
     *
     * @param color
     */
    public void setLinkColor(int color) {
        this.linkColor = color;
    }

    /**
     * 恢复初始未选择的状态
     */
    public void reset() {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                selectList.clear();
                invalidate();
            }
        }, 1000);
    }

    public interface GestureListener {
        void onStart();

        void onDraw(int index);

        void onFinish(List<Integer> list);

        void onError(List<Integer> list);
    }

    public void setListener(GestureListener listener) {
        this.listener = listener;
    }
}
