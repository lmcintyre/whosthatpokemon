package org.team7.wtp;

import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class MainActivityFragment extends Fragment {

    private static final String TAG = "WTP";

    private List<Pair<Integer, Integer>> regions;
    private MainActivity.GameMode mode;

    private ImageView imgView;
    private ObscuringImageView obsImageView;
    private ObscuringView obsView;
    private List<Button> buttons;
    private TextView progressTextView;
    private TextView correctTextView;

    private List<String> pokePaths;
    private List<String> pokes;

    private SecureRandom random;
    private Handler handler;

    private String correctPoke;
    private int numQuestions;
    private int totalGuesses;
    private int correctAnswers;

    // Just make the controls spin or do something to show they got it wrong
    // A shake like flag quiz might make us seem like a rip-off
    private Animation spinAnimation;

    // Spin and reveal? Or maybe just reveal like the real WTP
    private Animation revealAnimation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        numQuestions = 10;

        buttons = new ArrayList<>();
        regions = new ArrayList<>();
        pokePaths = new ArrayList<>();
        pokes = new ArrayList<>();

        random = new SecureRandom();
        handler = new Handler();

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        imgView = view.findViewById(R.id.pkmnImageView);
        obsImageView = view.findViewById(R.id.silhouetteImageView);
        obsView = view.findViewById(R.id.obscuringView);

        progressTextView = view.findViewById(R.id.progressTextView);
        correctTextView = view.findViewById(R.id.correctTextView);

        buttons.add(view.findViewById(R.id.guessButton1));
        buttons.add(view.findViewById(R.id.guessButton2));
        buttons.add(view.findViewById(R.id.guessButton3));
        buttons.add(view.findViewById(R.id.guessButton4));

        for (Button b : buttons)
            b.setOnClickListener(guessButtonListener);

        progressTextView.setText(getString(R.string.quiz_progress, 1, numQuestions));

        return view;
    }

    public void setMode(MainActivity.GameMode mode) {
        this.mode = mode;
    }

    // need spinner preference? or edit text preference?
    public void updateNumQuestions(int num) {
        this.numQuestions = num;
    }

    public MainActivity.GameMode getMode() {
        return this.mode;
    }

    // FlagQuiz simply keeps a Set<String> of regions
    // We need to keep track of the dex ranges
    public void updateRegions(SharedPreferences defaultSharedPreferences) {

        regions.clear();

        Set<String> regionSet = defaultSharedPreferences.getStringSet(MainActivity.REGIONS, null);

        Pair<Integer, Integer> kanto = new Pair<>(1, 151);
        Pair<Integer, Integer> johto = new Pair<>(152, 251);
        Pair<Integer, Integer> hoenn = new Pair<>(252, 386);
        Pair<Integer, Integer> sinnoh = new Pair<>(387, 493);
        Pair<Integer, Integer> unova = new Pair<>(494, 649);
        Pair<Integer, Integer> kalos = new Pair<>(650, 721);
        Pair<Integer, Integer> alola = new Pair<>(651, 809);

        if (regionSet.contains("Kanto"))
            regions.add(kanto);
        if (regionSet.contains("Johto"))
            regions.add(johto);
        if (regionSet.contains("Hoenn"))
            regions.add(hoenn);
        if (regionSet.contains("Sinnoh"))
            regions.add(sinnoh);
        if (regionSet.contains("Unova"))
            regions.add(unova);
        if (regionSet.contains("Kalos"))
            regions.add(kalos);
        if (regionSet.contains("Alola"))
            regions.add(alola);
    }

    private boolean pokeInRegions(int dex) {

        for (Pair<Integer, Integer> range : regions) {
            int low = range.first, high = range.second;

            if (dex >= low && dex <= high)
                return true;
        }

        return false;
    }

    private String getPokeName(String filename) {

        String[] spl = filename.split("_");
        String name = spl[1];

        if (name.equals("type null"))
            return "Type:Null";

        String splitOn = "";

        if (name.contains("-"))
            splitOn = "-";
        else if (name.contains(" "))
            splitOn = " ";

        StringBuilder actualName = new StringBuilder();

        if (!splitOn.equals("")) {

            String[] spaceSpl = name.split(splitOn);

            for (String aSpaceSpl : spaceSpl) {
                String sb = aSpaceSpl.substring(0, 1).toUpperCase();
                sb += aSpaceSpl.substring(1);

                actualName.append(sb);
                actualName.append(splitOn);
            }

            actualName.replace(actualName.length() - 1, actualName.length(), "");
        } else {
            String sb = name.substring(0, 1).toUpperCase();
            sb += name.substring(1);

            actualName.append(sb);
        }

        return actualName.toString();
    }

    private int getPokeDexNum(String filename) {
        String[] spl = filename.split("_");
        return Integer.parseInt(spl[0]);
    }

    public void reset() {
        initPokeList();
        loadNextPoke();
    }

    private void initPokeList() {

        AssetManager assets = getActivity().getAssets();
        pokePaths.clear();

        try {
            String[] paths = assets.list("pokemon");

            for (String path : paths) {
                int dex = getPokeDexNum(path);
                if (pokeInRegions(dex))
                    pokePaths.add(path.replace(".png", ""));
            }

        } catch (IOException e) {
            Log.e(TAG, "Error loading images!", e);
        }

        correctAnswers = 0;
        totalGuesses = 0;
        pokes.clear();

        int counter = 1;
        int totalPokes = pokePaths.size();

        while (counter <= numQuestions) {
            int index = random.nextInt(totalPokes);

            String filename = pokePaths.get(index);
            if (!pokes.contains(filename)) {
                pokes.add(filename);
                counter++;
            }
        }
    }

    private void loadNextPoke() {

        obsImageView.reset();
        obsView.reset();
        correctTextView.setText("");

        String next = pokes.remove(0);
        correctPoke = next;

        progressTextView.setText(getString(R.string.quiz_progress, correctAnswers + 1, numQuestions));

        AssetManager assets = getActivity().getAssets();

        try (InputStream is = assets.open("pokemon/" + next + ".png")) {
            Bitmap b = BitmapFactory.decodeStream(is);
            imgView.setImageBitmap(b);

            // set up the obscuring control depending on game mode
            if (mode == MainActivity.GameMode.CLASSIC) {
                // This used to be posted... but it would flash the Pokemon un-obscured, then obscure it
                obsImageView.setSilhouetteBitmap(b);
            } else {
                final int pxWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
                obsView.setUpBitmap(pxWidth, pxWidth);
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to load image!", e);
        }

        Collections.shuffle(pokePaths);

        int correctIndex = pokePaths.indexOf(correctPoke);
        pokePaths.add(pokePaths.remove(correctIndex));

        for (int i = 0; i < buttons.size(); i++) {
            buttons.get(i).setEnabled(true);
            buttons.get(i).setText(getPokeName(pokePaths.get(i)));
        }

        int correctGuess = random.nextInt(buttons.size());
        buttons.get(correctGuess).setText(getPokeName(correctPoke));
    }

    private View.OnClickListener guessButtonListener = (v) -> {
        Button thisButton = (Button) v;
        String guessedPoke = thisButton.getText().toString();
        String correctAnswer = getPokeName(correctPoke);

        totalGuesses++;

        if (guessedPoke.equals(correctAnswer)) {
            correctAnswers++;

            correctTextView.setText(getString(R.string.poke_correct, correctAnswer));
            // text color?

            for (Button b : buttons)
                b.setEnabled(false);

            if (correctAnswers == numQuestions) {
                //display results
            } else {
                //next question, need anim
                handler.postDelayed(this::loadNextPoke, 2000);
            }
        } else {
            //incorrect anim

            correctTextView.setText(R.string.incorrect);
            //text color?

            thisButton.setEnabled(false);
        }

    };

}
