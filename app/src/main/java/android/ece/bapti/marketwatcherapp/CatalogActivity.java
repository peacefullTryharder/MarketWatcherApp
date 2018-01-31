package android.ece.bapti.marketwatcherapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CatalogActivity extends BaseActivity {

    String JsonURL;
    RequestQueue requestQueue;

    private ListView mListView;
    private EditText catalogSearch;

    Intent ArticleIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.catalog);
        setContentView(R.layout.activity_catalog);

        final List<CatalogItem> items = new ArrayList<CatalogItem>();

        mListView = (ListView) findViewById(R.id.list);
        catalogSearch = (EditText) findViewById(R.id.catalogSearch);

        catalogSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                requestQueue = Volley.newRequestQueue(getApplicationContext());

                JsonURL = "http://api.marketwatcher.fr/product/search?term=" + catalogSearch.getText().toString();

                JsonArrayRequest arrayReq = new JsonArrayRequest(Request.Method.GET, JsonURL,
                        // The third parameter Listener overrides the method onResponse() and passes
                        //JSONObject as a parameter
                        new Response.Listener<JSONArray>() {

                            // Takes the response from the JSON request
                            @Override
                            public void onResponse(JSONArray response) {
                                try {

                                    JSONObject jsonObject;

                                    for (int i=0; i<response.length(); i++)
                                    {
                                        jsonObject = response.getJSONObject(i);

                                        items.add(new CatalogItem(
                                                jsonObject.getString("name"),
                                                jsonObject.getString("image"),
                                                jsonObject.getString("insertedAt"),
                                                jsonObject.getString("googleId")));
                                    }

                                    CatalogItemAdapter adapter = new CatalogItemAdapter(CatalogActivity.this, items);
                                    mListView.setAdapter(adapter);
                                    Toast.makeText(CatalogActivity.this, "PASSAGE", Toast.LENGTH_SHORT).show();
                                }
                                // Try and catch are included to handle any errors due to JSON
                                catch (JSONException e) {
                                    // If an error occurs, this prints the error to the log
                                    e.printStackTrace();
                                }
                            }
                        },
                        // The final parameter overrides the method onErrorResponse() and passes VolleyError
                        //as a parameter
                        new Response.ErrorListener() {
                            @Override
                            // Handles errors that occur due to Volley
                            public void onErrorResponse(VolleyError error) {
                                Log.e("Volley", "Error");
                            }
                        }
                );

                requestQueue.add(arrayReq);

            }
        });

        /* LinearLayout article = (LinearLayout) findViewById(R.id.article);
        article.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                // Pour l'utilisation avec l'API => Transmettre avec des données (ex: un ID pour refaire
                // des appels dans l'activité Article
                ArticleIntent = new Intent(CatalogActivity.this, ArticleActivity.class);
                startActivity(ArticleIntent);

            }
        }); */


        // Adds the JSON object request "obreq" to the request queue
    }
}
