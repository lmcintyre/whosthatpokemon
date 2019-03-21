package org.team7.wtp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.ArraySet;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static final String REGIONS = "pref_regions";
    public static final String MODE = "pref_mode";

    private MainActivityFragment pkmnFragment;

    public enum GameMode {
        CLASSIC, ENHANCED;

        public String toString() {
            if (this == CLASSIC)
                return "CLASSIC";
            else
                return "ENHANCED";
        }
    }

    private boolean preferencesChanged = true;

    @Override
    protected void onStart() {
        super.onStart();

        pkmnFragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.whosThatFragment);
        GameMode mode = GameMode.valueOf(
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .getString(MODE, GameMode.CLASSIC.toString()));
        pkmnFragment.setMode(mode);

        if (preferencesChanged) {
            pkmnFragment.updateRegions(PreferenceManager.getDefaultSharedPreferences(this));
            pkmnFragment.reset();
            preferencesChanged = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(
                        (sharedPreferences, key) -> {
                            preferencesChanged = true;

                            if (key.equals(REGIONS)) {
                                Set<String> regions = sharedPreferences.getStringSet(REGIONS, null);

                                if (regions != null & regions.size() > 0) {
                                    pkmnFragment.updateRegions(sharedPreferences);
                                    pkmnFragment.reset();
                                } else {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    regions.add(getString(R.string.region_default));
                                    editor.putStringSet(REGIONS, regions);
                                    editor.apply();
                                }
                            }
                            Toast.makeText(MainActivity.this, getString(R.string.quiz_restart), Toast.LENGTH_SHORT).show();
                        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent preferencesIntent = new Intent(this, SettingsActivity.class);
                startActivity(preferencesIntent);
                return true;
            case R.id.action_gamemode:
                GameModeSelectFragment frag = new GameModeSelectFragment();
                frag.show(getSupportFragmentManager(), "game mode select");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
