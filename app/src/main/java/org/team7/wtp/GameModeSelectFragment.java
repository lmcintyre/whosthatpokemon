package org.team7.wtp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Button;

public class GameModeSelectFragment extends DialogFragment {

    private final String MODE = "pref_mode";

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final View gameModeView = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_game_mode, null
        );

        builder.setView(gameModeView);
        builder.setTitle(R.string.select_game_mode);

        Button classicButton = gameModeView.findViewById(R.id.classicButton);
        Button enhancedButton = gameModeView.findViewById(R.id.enhancedButton);

        final DialogFragment dialog = this;

        classicButton.setOnClickListener(v -> {
            updatePrefsAndGame(getString(R.string.classic).toUpperCase());
            dialog.dismiss();
        });

        enhancedButton.setOnClickListener(v -> {
            updatePrefsAndGame(getString(R.string.enhanced).toUpperCase());
            dialog.dismiss();
        });

        return builder.create();
    }

    private void updatePrefsAndGame(String mode) {

        MainActivityFragment pkmnFragment = getPkmnFragment();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        if (!pkmnFragment.getMode().toString().equals(mode)) {

            // Edit the preferences
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(MODE, mode);
            editor.apply();

            if (MainActivity.GameMode.valueOf(mode) == MainActivity.GameMode.CLASSIC)
                pkmnFragment.setMode(MainActivity.GameMode.CLASSIC);
            else
                pkmnFragment.setMode(MainActivity.GameMode.ENHANCED);
            pkmnFragment.reset();
        }
    }

    private MainActivityFragment getPkmnFragment() {
        return (MainActivityFragment) getFragmentManager().findFragmentById(R.id.whosThatFragment);
    }

    private MainActivity.GameMode getCurrentMode() {
        MainActivity.GameMode mode = MainActivity.GameMode.valueOf(
                PreferenceManager.getDefaultSharedPreferences(this.getContext())
                        .getString(MODE, MainActivity.GameMode.CLASSIC.toString()));
        return mode;
    }

}
