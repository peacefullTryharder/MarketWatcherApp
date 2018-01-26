package android.ece.bapti.marketwatcherapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class CatalogActivity extends BaseActivity {

    Intent ArticleIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.catalog);
        setContentView(R.layout.activity_catalog);

        LinearLayout article = (LinearLayout) findViewById(R.id.article);
        article.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                // Pour l'utilisation avec l'API => Transmettre avec des données (ex: un ID pour refaire
                // des appels dans l'activité Article
                ArticleIntent = new Intent(CatalogActivity.this, ArticleActivity.class);
                startActivity(ArticleIntent);

            }
        });
    }
}
