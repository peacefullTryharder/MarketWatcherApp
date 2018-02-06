package fr.marketwatcher.android;

import android.os.Bundle;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.home);
        setContentView(R.layout.activity_main);
    }
}
