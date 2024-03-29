package com.alex.edudus.utility;


import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.View;

import java.lang.reflect.Field;

public class TouchListener implements View.OnTouchListener {

    double minScaleX;
    double minScaleY;

    private Matrix matrix = new Matrix();
    /** 用于记录图片要进行拖拉时候的坐标位置 */
    private Matrix currentMatrix = new Matrix();

    /** 记录是拖拉照片模式还是放大缩小照片模式 */
    private int mode = 0;// 初始状态
    /** 拖拉照片模式 */
    private static final int MODE_DRAG = 1;
    /** 放大缩小照片模式 */
    private static final int MODE_ZOOM = 2;
    /** 用于记录开始时候的坐标位置 */
    private PointF startPoint = new PointF();
    /** 两个手指的开始距离 */
    private float startDis;
    /** 两个手指的中间点 */
    private PointF midPoint;


    private float mMaxScaleRank=300f;
    private float mMaxScale = 1f;
    /** 最小缩放级别 */
    private float mMinScale = 0.21774194f;
    boolean isGetmMinScale = false;
    private boolean isMinScale = false;
    private boolean isMinScale2 = false;
    private boolean isMinScale3 = false;
    int imgWidth ;
    int imgHeight ;
    int intrinsicWidth;
    int intrinsicHeight;
    CanvasFrame canvasFrame;
    public TouchListener(CanvasFrame mCanvasFrame, int imgWidth, int imgHeight, int intrinsicWidth, int intrinsicHeight) {
        this.canvasFrame = mCanvasFrame;
        minScaleX = 1;
        minScaleY = 1;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
        this.intrinsicWidth = intrinsicWidth;
        this.intrinsicHeight = intrinsicHeight;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        /** 通过与运算保留最后八位 MotionEvent.ACTION_MASK = 255 */
        switch (event.getAction() & MotionEvent.ACTION_MASK) {// 单点监听和多点触碰监听
            // 手指压下屏幕
            case MotionEvent.ACTION_DOWN:
                mode = MODE_DRAG;
                // 记录ImageView当前的移动位置
                currentMatrix.set(canvasFrame.getMatrix());
                startPoint.set(event.getX(), event.getY());
                matrix.set(currentMatrix);
                if(!isGetmMinScale){
                    float[] values = new float[9];
                    matrix.getValues(values);
                    mMinScale = values[Matrix.MSCALE_X];
                    mMaxScale = mMinScale*mMaxScaleRank;
                    isGetmMinScale=true;
                }
//                    makeImageViewFit();
                break;
            // 手指在屏幕上移动，改事件会被不断触发
            case MotionEvent.ACTION_MOVE:
                // 拖拉图片
                if (mode == MODE_DRAG) {
                    // System.out.println("ACTION_MOVE_____MODE_DRAG");
                    float dx = event.getX() - startPoint.x; // 得到x轴的移动距离
                    float dy = event.getY() - startPoint.y; // 得到y轴的移动距离
                    // 在没有移动之前的位置上进行移动z
                    matrix.set(currentMatrix);
                    float[] values = new float[9];
                    matrix.getValues(values);
                    dx = checkDxBound(values, dx);
                    dy = checkDyBound(values, dy);
                    matrix.postTranslate(dx, dy);
                }
                // 放大缩小图片
                else if (mode == MODE_ZOOM) {
                    float endDis = distance(event);// 结束距离
                    if (endDis > 10f) { // 两个手指并拢在一起的时候像素大于10
                        float scale = endDis / startDis;// 得到缩放倍数
                        matrix.set(currentMatrix);
                        if(scale>=1)
                            isMinScale3=false;
                        else
                            isMinScale3=true;

                        float[] values = new float[9];
                        matrix.getValues(values);

                        scale = checkFitScale(scale, values);

                        matrix.postScale(scale, scale, midPoint.x, midPoint.y);
                        matrix.getValues(values);

//                                float valuesX = Float.parseFloat(String.format("%.7f",values[Matrix.MSCALE_X]));
//                                float valuesY = Float.parseFloat(String.format("%.7f",values[Matrix.MSCALE_Y]));
                        double valuesX = Math.floor(values[Matrix.MSCALE_X]*1000000)/1000000.0;
                        double valuesY = Math.floor(values[Matrix.MSCALE_Y]*1000000)/1000000.0;


//                                DecimalFormat df = new DecimalFormat("##.00000");
//                                float valuesx = Float.parseFloat(df.format(values[Matrix.MSCALE_X]));
//                                float valuesy = Float.parseFloat(df.format(values[Matrix.MSCALE_Y]));
                        if(valuesX==minScaleX && valuesY==minScaleY)
                            isMinScale2 = true;

                        if(isMinScale && isMinScale2 && isMinScale3){
                            matrix.getValues(values);
                            makeImgCenter(values);
                        }
                    }
                }
                break;
            // 手指离开屏幕
            case MotionEvent.ACTION_UP:
//                    setDoubleTouchEvent(event);
                if(isMinScale && isMinScale2 && isMinScale3)
                    isMinScale2=false;
                isMinScale=false;
                isMinScale3=false;
                //onClick與onTouch監聽區分
                if (Math.abs(event.getX() - startPoint.x) < 10 && Math.abs(event.getY() - startPoint.y) < 10) {
                    try {
                        Field field = View.class.getDeclaredField("mListenerInfo");
                        field.setAccessible(true);
                        Object object = field.get(v);
                        field = object.getClass().getDeclaredField("mOnClickListener");
                        field.setAccessible(true);
                        object = field.get(object);
                        if (object != null && object instanceof View.OnClickListener) {
                            ((View.OnClickListener) object).onClick(v);
                        }
                    } catch (Exception e) {

                    }
                }

            case MotionEvent.ACTION_POINTER_UP:
                // System.out.println("ACTION_POINTER_UP");
                mode = 0;
                // matrix.set(currentMatrix);
                float[] values = new float[9];
                matrix.getValues(values);
                makeImgCenter(values);
                break;
            // 当屏幕上已经有触点(手指)，再有一个触点压下屏幕
            case MotionEvent.ACTION_POINTER_DOWN:
                // System.out.println("ACTION_POINTER_DOWN");
                mode = MODE_ZOOM;
                /** 计算两个手指间的距离 */
                startDis = distance(event);
                /** 计算两个手指间的中间点 */
                if (startDis > 10f) { // 两个手指并拢在一起的时候像素大于10
                    midPoint = mid(event);
                    // 记录当前ImageView的缩放倍数
                    currentMatrix.set(canvasFrame.getMatrix());
                }
                break;
        }
        canvasFrame.setMatrix(matrix);
        return true;
    }

    /** 计算两个手指间的距离 */
    private float distance(MotionEvent event) {
        float dx = event.getX(1) - event.getX(0);
        float dy = event.getY(1) - event.getY(0);
        /** 使用勾股定理返回两点之间的距离 */
        return (float)Math.sqrt(dx * dx + dy * dy);
    }

    /** 计算两个手指间的中间点 */
    private PointF mid(MotionEvent event) {
        float midX = (event.getX(1) + event.getX(0)) / 2;
        float midY = (event.getY(1) + event.getY(0)) / 2;
        return new PointF(midX, midY);
    }

    /**
     * 和当前矩阵对比，检验dy，使图像移动后不会超出ImageView边界
     *
     * @param values
     * @param dy
     * @return
     */
    private float checkDyBound(float[] values, float dy) {

        float height = imgHeight;
        if (intrinsicHeight * values[Matrix.MSCALE_Y] < height)
            return 0;
        if (values[Matrix.MTRANS_Y] + dy > 0)
            dy = -values[Matrix.MTRANS_Y];
        else if (values[Matrix.MTRANS_Y] + dy < -(intrinsicHeight
                * values[Matrix.MSCALE_Y] - height))
            dy = -(intrinsicHeight * values[Matrix.MSCALE_Y] - height)
                    - values[Matrix.MTRANS_Y];
        return dy;
    }

    /**
     * 和当前矩阵对比，检验dx，使图像移动后不会超出ImageView边界
     *
     * @param values
     * @param dx
     * @return
     */
    private float checkDxBound(float[] values, float dx) {

        float width = imgWidth;
        if (intrinsicWidth * values[Matrix.MSCALE_X] < width)
            return 0;
        if (values[Matrix.MTRANS_X] + dx > 0)
            dx = -values[Matrix.MTRANS_X];
        else if (values[Matrix.MTRANS_X] + dx < -(intrinsicWidth
                * values[Matrix.MSCALE_X] - width))
            dx = -(intrinsicWidth * values[Matrix.MSCALE_X] - width)
                    - values[Matrix.MTRANS_X];
        return dx;
    }

    /**
     * MSCALE用于处理缩放变换
     * MSKEW用于处理错切变换
     * MTRANS用于处理平移变换
     */

    /**
     * 检验scale，使图像缩放后不会超出最大倍数
     *
     * @param scale
     * @param values
     * @return
     */
    private float checkFitScale(float scale, float[] values) {
        if (scale * values[Matrix.MSCALE_X] > mMaxScale)
            scale = mMaxScale / values[Matrix.MSCALE_X];
        if (scale * values[Matrix.MSCALE_X] < mMinScale) {
            scale = mMinScale / values[Matrix.MSCALE_X];
            isMinScale=true;
        }
        return scale;
    }

    /**
     * 促使图片居中
     *
     * @param values
     *            (包含着图片变化信息)
     */
    private void makeImgCenter(float[] values) {

        // 缩放后图片的宽高
        float zoomY = intrinsicHeight * values[Matrix.MSCALE_Y];
        float zoomX = intrinsicWidth * values[Matrix.MSCALE_X];
        // 图片左上角Y坐标
        float leftY = values[Matrix.MTRANS_Y];
        // 图片左上角X坐标
        float leftX = values[Matrix.MTRANS_X];
        // 图片右下角Y坐标
        float rightY = leftY + zoomY;
        // 图片右下角X坐标
        float rightX = leftX + zoomX;

        // 使图片垂直居中
        if (zoomY < imgHeight) {
            float marY = (imgHeight - zoomY) / 2.0f;
            matrix.postTranslate(0, marY - leftY);
        }

        // 使图片水平居中
        if (zoomX < imgWidth) {

            float marX = (imgWidth - zoomX) / 2.0f;
            matrix.postTranslate(marX - leftX, 0);

        }

        // 使图片缩放后上下不留白（即当缩放后图片的大小大于imageView的大小，但是上面或下面留出一点空白的话，将图片移动占满空白处）
        if (zoomY >= imgHeight) {
            if (leftY > 0) {// 判断图片上面留白
                matrix.postTranslate(0, -leftY);
            }
            if (rightY < imgHeight) {// 判断图片下面留白
                matrix.postTranslate(0, imgHeight - rightY);
            }
        }

        // 使图片缩放后左右不留白
        if (zoomX >= imgWidth) {
            if (leftX > 0) {// 判断图片左边留白
                matrix.postTranslate(-leftX, 0);
            }
            if (rightX < imgWidth) {// 判断图片右边不留白
                matrix.postTranslate(imgWidth - rightX, 0);
            }
        }
    }
}

