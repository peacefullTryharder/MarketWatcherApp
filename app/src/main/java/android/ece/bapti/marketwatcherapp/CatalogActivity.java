package android.ece.bapti.marketwatcherapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class CatalogActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.catalog);
        setContentView(R.layout.activity_catalog);
    }
}
