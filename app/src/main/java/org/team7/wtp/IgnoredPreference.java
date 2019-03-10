package org.team7.wtp;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * A preference which doesn't appear in the preferences activity.
 */
public class IgnoredPreference extends ListPreference {

    public IgnoredPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IgnoredPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public IgnoredPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        return new View(parent.getContext());
    }
}
