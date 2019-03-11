package org.team7.wtp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

public class ObscuringView extends View {
    private static final float TOUCH_TOLERANCE = 10;

    Bitmap bitmap;
    Paint paint;

    Canvas mCanvas;
    Matrix matrix;
    Paint paintScreen;

    private final Map<Integer, Path> pathMap = new HashMap<>();
    private final Map<Integer, Point> previousPointMap = new HashMap<>();

    public ObscuringView(Context context) {
        super(context);
        setUpDrawing();
    }

    public ObscuringView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpDrawing();
    }

    public void reset() {
        // Dummy bitmap to draw so bitmap isn't null
        bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.TRANSPARENT);
        mCanvas.setBitmap(bitmap);
        mCanvas.drawBitmap(bitmap, 0, 0, paintScreen);
        this.invalidate();
    }

    private void setUpDrawing() {
        mCanvas = new Canvas();
        matrix = new Matrix();

        paintScreen = new Paint();

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.TRANSPARENT);
        paint.setAlpha(0);
        paint.setStrokeWidth(10);
        paint.setStrokeCap(Paint.Cap.ROUND);

        reset();
    }

    public void setUpBitmap(int x, int y) {
        bitmap = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.BLACK);

        mCanvas.setBitmap(bitmap);
        mCanvas.drawBitmap(bitmap, 0, 0, paintScreen);
        this.invalidate();
    }

    // this works
    public double getPercentErased() {
        int w = bitmap.getWidth(), h = bitmap.getHeight();
        int numpx = w * h;
        int numblk = 0;

        int[] pixels = new int[numpx];
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h);

        for (int i = 0; i < numpx; i++)
            if (Color.alpha(pixels[i]) != 0)
                numblk++;

        double pct = ((numblk == 0 ? 1 : numblk) / (double) numpx);
        Log.i("obscuringview", String.format("numblk: %d\npct: %f", numblk, pct));
        return 1.0 - pct;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(bitmap, 0, 0, paintScreen);

        for (Integer key : pathMap.keySet())
            mCanvas.drawPath(pathMap.get(key), paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int actionIndex = event.getActionIndex();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
            touchStarted(event.getX(actionIndex), event.getY(actionIndex),
                    event.getPointerId(actionIndex));
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            touchEnded(event.getPointerId(actionIndex));
        } else {
            touchMoved(event);
        }

        invalidate();
        return true;
    }

    private void touchStarted(float x, float y, int lineID) {
        Path path;
        Point point;
        if (pathMap.containsKey(lineID)) {
            path = pathMap.get(lineID);
            path.reset();
            point = previousPointMap.get(lineID);
        } else {
            path = new Path();
            pathMap.put(lineID, path);
            point = new Point();
            previousPointMap.put(lineID, point);
        }

        path.moveTo(x, y);
        point.x = (int) x;
        point.y = (int) y;
    }

    private void touchMoved(MotionEvent event) {
        for (int i = 0; i < event.getPointerCount(); i++) {
            int pointerID = event.getPointerId(i);
            int pointerIndex = event.findPointerIndex(pointerID);

            if (pathMap.containsKey(pointerID)) {
                float newX = event.getX(pointerIndex);
                float newY = event.getY(pointerIndex);

                Path path = pathMap.get(pointerID);
                Point point = previousPointMap.get(pointerID);

                float deltaX = Math.abs(newX - point.x);
                float deltaY = Math.abs(newY - point.y);

                if (deltaX >= TOUCH_TOLERANCE || deltaY >= TOUCH_TOLERANCE) {
                    path.quadTo(point.x, point.y, (newX + point.x) / 2,
                            (newY + point.y) / 2);

                    point.x = (int) newX;
                    point.y = (int) newY;
                }
            }
        }
    }

    private void touchEnded(int lineID) {
        Path path = pathMap.get(lineID);
        mCanvas.drawPath(path, paint);
        path.reset();
    }
}
