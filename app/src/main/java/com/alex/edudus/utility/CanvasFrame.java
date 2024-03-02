package com.alex.edudus.utility;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;


public class CanvasFrame extends LinearLayout {
    private Context context;
    public LinearLayout layout;

    public SignatureView bDrawl;

    Matrix matrix = new Matrix();
    public CanvasFrame(Context context) {
        super(context);
        this.context = context;
        addWordView();

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);

        setLayoutParams(new LayoutParams(metrics.widthPixels, (int)(metrics.widthPixels*1.414)));
        setWillNotDraw(false);
//        this.setLayerType(View.LAYER_TYPE_NONE, null);
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        //this.setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }
    public CanvasFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setWillNotDraw(false);
    }

    public CanvasFrame(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setWillNotDraw(false);
    }

    private void addWordView() {

//        this.setBackgroundResource(R.drawable.a51);
        bDrawl = new SignatureView(context);
        this.addView(bDrawl);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.concat(matrix);
    }

    @Override
    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                postInvalidate();
            }
        });
    }


}
