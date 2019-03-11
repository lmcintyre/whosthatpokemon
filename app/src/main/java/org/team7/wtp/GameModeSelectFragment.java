package org.team7.wtp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

public class GameModeSelectFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final View gameModeView = getActivity().getLayoutInflater().inflate(
                R.layout.fragment_game_mode, null
        );

        builder.setView(gameModeView);

        ImageButton classicButton = gameModeView.findViewById(R.id.classicButton);
        ImageButton enhancedButton = gameModeView.findViewById(R.id.enhancedButton);

        final DialogFragment dialog = this;

        classicButton.setOnClickListener(v -> {
            updatePrefsAndGame(getString(R.string.classic).toUpperCase());
            Toast.makeText(dialog.getContext(), getString(R.string.classic_selected), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        enhancedButton.setOnClickListener(v -> {
            updatePrefsAndGame(getString(R.string.enhanced).toUpperCase());
            Toast.makeText(dialog.getContext(), getString(R.string.enhanced_selected), Toast.LENGTH_SHORT).show();
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
            editor.putString(MainActivity.MODE, mode);
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
                        .getString(MainActivity.MODE, MainActivity.GameMode.CLASSIC.toString()));
        return mode;
    }

}
