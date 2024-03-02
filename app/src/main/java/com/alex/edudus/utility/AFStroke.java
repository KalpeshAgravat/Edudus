package com.alex.edudus.utility;


import static com.alex.edudus.utility.Const.VAR.SDKPressureSupport;

import android.graphics.Matrix;
import android.graphics.Path;

import com.afpensdk.pen.mod.AFStrokeBuilder;
import com.afpensdk.pen.mod.AFStrokeOptions;
import com.afpensdk.structure.AFDot;
import com.afpensdk.structure.DotType;

import java.util.ArrayList;


public class AFStroke
{

	public ArrayList<AFDot> dots = null;
    public static class CGPoint{
    public float x;
    public float y;
    public CGPoint(float x, float y){
        this.x = x;
        this.y = y;
        }
    }


    public int lastDrawIdx;
    public Path fullPath;
    ArrayList<CGPoint> pts;
    int drawMethod = 1; //1=stroke 2=fill
    int ctr ;

	public AFStroke()
	{
	    this.dots = new ArrayList<AFDot>();
        pts = new ArrayList<>();
        for(int i=0;i<4;i++){
            pts.add(new CGPoint(0,0));
        }
	}
	public boolean add( AFDot dot )
	{
		this.dots.add( dot );

		return true;
	}

    public void setDots(ArrayList<AFDot> dots) {
        this.dots = dots;
    }

    public boolean addXYP(int x, int y, int type, int pr)
    {
        AFDot d  = new AFDot();
        d.X = (int)(x/4.0);
        d.Y = (int)(y/4.0);
        d.type = type;
        d.reserved1 = pr;
        return true;
    }
	public AFDot get(int index )
	{
		return dots.get( index );
	}

	public ArrayList<AFDot> getDots()
	{
		return dots;
	}

	public static CGPoint BLEPoint2Point(float pw, float ph, float pX,float pY, int refW, int refH){

//        float pw = 4299;
//        float ph = 6070;
        return new CGPoint(pX>=pw?pw:pX*(refW/pw), pY>=ph?ph:pY*(refH/ph));
    }

    public static float getWidthByLevel (int lvl){

        float w = 16.5f;
        float outw = w;
        if (lvl == 1)
            outw = w * 0.6f;
        else if (lvl == 2)
            outw = w * 0.8f;
        else if (lvl == 3)
            outw = w * 1f;
        else if (lvl == 4)
            outw = w * 1.3f;
        else if (lvl == 5)
            outw = w * 1.5f;
        return outw;
    }
    public void buildBezierPath(float pw, float ph, int frameW,int frameH, int fromIdx, int toIdx){
//        float pw = 3307;
//        float ph = 4843;
        if(SDKPressureSupport){

            double distance = 0;
//            for(int i=1;i<dots.size();i++) {
//                distance+=Math.sqrt(Math.pow(this.dots.get(i).X - this.dots.get(i-1).X, 2) + Math.pow(this.dots.get(i).Y - this.dots.get(i-1).Y, 2));
//                if(distance>100)
//                    break;
//            }
////            if(this.dots.get(this.dots.size()-1).type==2) {
////                Log.e("buildBezierPath", distance + "");
////            }
//            if(distance<=100||dots.size()<=5 ){
//                this.fullPath = new Path();
//                drawMethod = 1;
//                AFDot penPoint = getDots().get(0);
//                CGPoint p = BLEPoint2Point(pw, ph, penPoint.X, penPoint.Y, frameW, frameH);
//                fullPath.moveTo(p.x, p.y);
//
//                for (int i = 1; i < this.getDots().size(); i++) {
//                    penPoint = getDots().get(i);
//                    p = BLEPoint2Point(pw, ph, penPoint.X, penPoint.Y, frameW, frameH);
//                    this.fullPath.lineTo(p.x, p.y);
//                }
//            }else {
                AFStrokeOptions mAFStrokeOptions = new AFStrokeOptions();
//            mAFStrokeOptions.dotscale = 0.25;
//                mAFStrokeOptions.size = getWidthByLevel(Glo.getInstance().lineWidthLv);
//            mAFStrokeOptions.size = 3.2;
            mAFStrokeOptions.size = Glo.getInstance().lineWidthByLevel_PressureUse(Glo.getInstance().lineWidthLv);
                AFStrokeBuilder builder = new AFStrokeBuilder();
                builder.setOptions(mAFStrokeOptions);
//                for(int i=0 ;i<this.dots.size();i++){
//            for(int i=0 ;i<this.dots.size();i++){
//                    AFDot dot =  this.dots.get(i);
//                    String s2 = String.format("X=%d Y=%d t=%d p=%d pr=%d\n", dot.X, dot.Y, dot.type, dot.page, dot.getFieldRv1());
//
//                    Log.d("AFStroke",s2);
//                }

                ArrayList<ArrayList<Double>> points = builder.getStrokePoints(this.dots);

                if(points!=null) {
                    Path buildPath;
                    buildPath = builder.buildPath(points);
                    Matrix mx = new Matrix();
                    float r_h = frameH / (float) ph;
                    float r_w = frameW / (float) pw;
                    float r = Math.max(r_w, r_h);
//                    mx.setScale(r_w*4.0f, r_h*4.0f);
                    mx.setScale(r_w, r_h);
                    buildPath.transform(mx);
                    fullPath = buildPath;
                    drawMethod = 2;
                }
//            }
        }else{
            drawMethod = 1;
            try {
                if (frameW != 0 && frameH != 0 && getDots()!=null && getDots().size()>0) {

                    if (this.fullPath == null) {
                        this.fullPath = new Path();//[UIBezierPath bezierPath];
                        ctr = 0;
                    }
                    if(true) {
                        if (this.dots.size() < 4 && this.dots.get(this.dots.size() - 1).type == DotType.PEN_ACTION_UP.getValue()) {
                            AFDot penPoint = getDots().get(0);
                            CGPoint p = BLEPoint2Point(pw, ph, penPoint.X, penPoint.Y, frameW, frameH);
                            fullPath.moveTo(p.x, p.y);

                            for (int i = 1; i < this.getDots().size(); i++) {
                                penPoint = getDots().get(i);
                                p = BLEPoint2Point(pw, ph, penPoint.X, penPoint.Y, frameW, frameH);
                                this.fullPath.lineTo(p.x, p.y);
                            }
                            return;
                        } else {
                            for (int i = fromIdx; i < toIdx; i++) {
                                AFDot penPoint = getDots().get(i);
            //                    RLMPenAction *penPoint=self.points[i];
            //                    CGPoint p = [GlobalUtil BLEPoint2Point:CGPointMake(penPoint.sX, penPoint.sY) refSize:frameSize];
                                CGPoint p = BLEPoint2Point(pw, ph, penPoint.X, penPoint.Y, frameW, frameH);
                                if (i == 0) {
                                    pts.set(0, p);
                                    continue;
                                }
                                ctr++;

                                pts.set(ctr, p);
                                if (ctr == 3) {
                                    pts.set(2, new CGPoint((float) ((pts.get(1).x + pts.get(3).x) / 2.0), (float) ((pts.get(1).y + pts.get(3).y) / 2.0)));
                                    this.fullPath.moveTo(pts.get(0).x, pts.get(0).y);
                                    this.fullPath.quadTo(pts.get(1).x, pts.get(1).y, pts.get(2).x, pts.get(2).y);

                                    pts.set(0, pts.get(2));
                                    pts.set(1, pts.get(3));
                                    ctr = 1;
                                }

                                if ((i == (toIdx - 1) && getDots().get(i).type == DotType.PEN_ACTION_UP.getValue()) && (ctr > 0) && (ctr < 3)) {

                                    CGPoint ctr1;
                                    CGPoint ctr2;

                                    if (ctr == 1)
                                        this.fullPath.lineTo(pts.get(ctr).x, pts.get(ctr).y);
                                    else {
                                        ctr1 = ctr2 = pts.get(ctr - 1);
                                        this.fullPath.cubicTo(ctr1.x, ctr1.y, ctr2.x, ctr2.y, pts.get(ctr).x, pts.get(ctr).y);
                                    }
                                }
                            }
                        }
                    }else{
                        for (int i = fromIdx; i < toIdx; i++) {
                            AFDot penPoint = getDots().get(i);
                            CGPoint p = BLEPoint2Point(pw, ph, penPoint.X, penPoint.Y, frameW, frameH);

                            if(i==0)
                                fullPath.moveTo(p.x, p.y);
                            else
                                this.fullPath.lineTo(p.x, p.y);
                        }
                    }
                    this.lastDrawIdx = toIdx;
                }
            //        return self;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
	public void buildBezierPath(float pw, float ph, int frameW,int frameH){
        buildBezierPath(pw, ph, frameW, frameH, this.lastDrawIdx, dots.size());
    }
}
