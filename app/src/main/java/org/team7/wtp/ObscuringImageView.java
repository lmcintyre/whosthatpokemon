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
        Bitmap bitmap = b.copy(b.getConfig(), true);

        for (int i = 0; i < b.getWidth(); i++) {
            for (int j = 0; j < b.getHeight(); j++) {
                int pixel = b.getPixel(i, j);
                if (Color.alpha(pixel) != 0)
                    bitmap.setPixel(i, j, Color.BLACK);
            }
        }

        b.recycle();
        this.setImageBitmap(bitmap);
    }

}
