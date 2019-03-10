package org.team7.wtp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;

public class ObscuringImageView extends android.support.v7.widget.AppCompatImageView {
    public ObscuringImageView(Context context) {
        super(context);
    }

    public ObscuringImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSilhouetteBitmap(Bitmap b) {
        int w = b.getWidth(), h = b.getHeight();

        int[] pixels = new int[w * h];
        b.getPixels(pixels, 0, w, 0, 0, w, h);

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (Color.alpha(pixels[h * i + j]) != 0)
                    pixels[h * i + j] = Color.BLACK;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(pixels, w, h, Bitmap.Config.ARGB_8888);
        this.setImageBitmap(bitmap);
    }

    public void reset() {
        Bitmap b = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        b.eraseColor(Color.TRANSPARENT);
        this.setImageBitmap(b);
    }
}
