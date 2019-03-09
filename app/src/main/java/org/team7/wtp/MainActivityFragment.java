package org.team7.wtp;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class MainActivityFragment extends Fragment {

    private MainActivity.GameMode mode;

    ImageView imgView;
    ObscuringImageView obsImageView;
    ObscuringView obsView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        imgView = view.findViewById(R.id.pkmnImageView);
        obsImageView = view.findViewById(R.id.silhouetteImageView);
        obsView = view.findViewById(R.id.obscuringView);

        SeekBar sbar = view.findViewById(R.id.seekBar);
        sbar.setOnSeekBarChangeListener(onSeekBarChangeListener);
        return view;
    }

    private SeekBar.OnSeekBarChangeListener onSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            obsImageView.setImageAlpha(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    public void setMode(MainActivity.GameMode mode) {
        this.mode = mode;
    }

    public MainActivity.GameMode getMode() {
        return this.mode;
    }

    public void updateRegions(SharedPreferences defaultSharedPreferences) {
        //now i trole
    }

    public void reset() {
        obsImageView.reset();
        obsView.reset();

        final Bitmap charm = BitmapFactory.decodeResource(getResources(), R.drawable.charmander);

        // set up the obscuring control depending on game mode
        if (mode == MainActivity.GameMode.CLASSIC) {
            obsImageView.post(new Runnable() {
                @Override
                public void run() {
                    obsImageView.setSilhouetteBitmap(charm);
                }
            });
        } else {
            int pxWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            obsView.setUpBitmap(pxWidth, pxWidth);  // make sure this covers all images
        }
    }
}
