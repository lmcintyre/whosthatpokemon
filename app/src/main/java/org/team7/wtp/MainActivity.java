package org.team7.wtp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    public enum GameMode {
        CLASSIC, ENHANCED
    }

    private GameMode mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mode = GameMode.ENHANCED;

        final ImageView imgView = findViewById(R.id.pkmnImageView);
        final ObscuringImageView obsImageView = findViewById(R.id.silhouetteImageView);
        final ObscuringView obsView = findViewById(R.id.obscuringView);

        Bitmap charm = BitmapFactory.decodeResource(getResources(), R.drawable.charmander);

        if (mode == GameMode.CLASSIC) {
            obsImageView.setSilhouetteBitmap(charm);
        } else {
            int pxWidth = getResources().getSystem().getDisplayMetrics().widthPixels;
            Log.i("test", String.format("%d", imgView.getMaxHeight()));
            obsView.setUpBitmap(pxWidth, pxWidth);
        }

        SeekBar sbar = findViewById(R.id.seekBar);
        sbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                obsImageView.setImageAlpha(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    // gets png's height and width
//    private Pair<Integer, Integer> getRealImgDims() {
//        Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.bulbasaur);
//        BitmapFactory.Options dimensions = new BitmapFactory.Options();
//        dimensions.inJustDecodeBounds = true;
//        Bitmap mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bulbasaur, dimensions);
//        int realImgHeight = dimensions.outHeight;
//        int realImgWidth =  dimensions.outWidth;
//        return new Pair<>(realImgWidth, realImgHeight);
//    }
}
