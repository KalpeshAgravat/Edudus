package com.alex.edudus.utility;





import static com.alex.edudus.utility.Const.VAR.SDKPressureSupport;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.afpensdk.structure.AFDot;
import com.afpensdk.structure.DotType;
import com.alex.edudus.AppController;


import java.util.ArrayList;
import java.util.List;

public class SignatureView extends View {

//    private float mSignatureWidth = 2.5f/14f;
    private float mSignatureWidth = 1f;
    private Bitmap mSignature = null;

    private static final boolean GESTURE_RENDERING_ANTIALIAS = true;
    private static final boolean DITHER_FLAG = true;

    private Paint mPaint = new Paint();
    private Path mPath = new Path();

    public ArrayList<AFStroke> strokes = new ArrayList<>();
    public AFStroke curStroke = new AFStroke();

    void curStrokeBuildPath(){
        int pw = PenClientCtrl.getInstance(AppController.context).paper_w;
        int ph = PenClientCtrl.getInstance(AppController.context).paper_h;
        curStroke.buildBezierPath(
                pw,
                ph,
                getWidth(),
                (int) (getWidth()/(pw/(float)ph))
        );
    }
    public void addDot(AFDot dot )
    {
//        Log.e("addDot",dot.reserved1+"");
//        dot.X /= 4.0;
//        dot.Y /= 4.0;
        curStroke.add(dot);
//        curStroke.addXYP(dot.X,dot.Y,dot.type,dot.reserved1);
        curStrokeBuildPath();
        if(dot.type != DotType.PEN_ACTION_UP.getValue()){

        }else{
            strokes.add(curStroke);
            curStroke = new AFStroke();
        }
        invalidate();
    }
    public void addDots(List<AFDot> dots )
    {
        if(curStroke==null || curStroke.getDots().size()==0)
            curStroke = new AFStroke();
        for(int i=0;i<dots.size();i++){
            AFDot dot = dots.get(i);
            curStroke.add(dot);
            if(dot.type != 2){

            }else{
                curStrokeBuildPath();
                strokes.add(curStroke);
                curStroke = new AFStroke();
            }
        }
        postInvalidate();
    }
    public void clear(){
        strokes = new ArrayList<>();
        curStroke = new AFStroke();
        postInvalidate();
    }

    public SignatureView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public void setSignatureBitmap(Bitmap signature) {
        mSignature = signature;
        invalidate();
    }

    public SignatureView(Context context) {
        super(context);
        init(context);
    }

    public SignatureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);

        mPaint.setAntiAlias(GESTURE_RENDERING_ANTIALIAS);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setDither(DITHER_FLAG);

        if(SDKPressureSupport){
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setStrokeWidth(1);
        }else{
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(mSignatureWidth);

        }
        mPath.reset();
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        if(mSignature==null){
//            mSignature = BitmapFactory.decodeResource(getResources(), R.mipmap.notebk1420);
//        }
//        if (mSignature != null) {
//            canvas.drawBitmap(mSignature, null, new Rect(0, 0, getWidth(), getHeight()), null);
//        }

        for(int i=0;i<strokes.size();i++){
//        for(int i=0;i<56;i++){
            if(strokes.get(i).fullPath!=null) {
                if(strokes.get(i).drawMethod==1) {
                    mPaint.setStyle(Paint.Style.STROKE);
                    mPaint.setStrokeWidth(Glo.getInstance().lineWidthByLevel2(Glo.getInstance().lineWidthLv));
                }else if(strokes.get(i).drawMethod==2) {
                    mPaint.setStyle(Paint.Style.FILL);
                    mPaint.setStrokeWidth(0f);
                }
//                strokes.get(i).fullPath.setFillType(Path.FillType.INVERSE_EVEN_ODD);
                canvas.drawPath(strokes.get(i).fullPath, mPaint);
            }
        }
        if(curStroke.fullPath!=null) {
            if(curStroke.drawMethod==1) {
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setStrokeWidth(Glo.getInstance().lineWidthByLevel2(Glo.getInstance().lineWidthLv));
            }else if(curStroke.drawMethod==2) {
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setStrokeWidth(1);
            }
            canvas.drawPath(curStroke.fullPath, mPaint);
        }

    }
}