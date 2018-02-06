package fr.marketwatcher.android;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatalogActivity extends BaseActivity {

    String JsonURL;
    RequestQueue requestQueue;
    String access_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1YTc4ODExZjgyNmVmYzdlYzE2M2VlOGUiLCJpYXQiOjE1MTc4NDY5NzUsImV4cCI6MTUxODI3ODk3NX0.o8D3henhAztT-JTuX8ihePR0sWqVL6Sw4OoQENc4jR0";


    private ListView mListView;
    private EditText catalogSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.catalog);
        setContentView(R.layout.activity_catalog);

        final List<CatalogItem> items = new ArrayList<CatalogItem>();

        mListView = (ListView) findViewById(R.id.listCatalog);
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

                                    items.clear();

                                    JSONObject jsonObject;

                                    for (int i=0; i<response.length(); i++)
                                    {
                                        jsonObject = response.getJSONObject(i);

                                        items.add(new CatalogItem(
                                                jsonObject.getString("name"),
                                                jsonObject.has("image") ? jsonObject.getString("image") : "",
                                                jsonObject.getString("insertedAt"),
                                                jsonObject.getString("googleId"),
                                                jsonObject.has("brand") ? jsonObject.getString("brand") : "",
                                                jsonObject.has("model") ? jsonObject.getString("model") : "",
                                                jsonObject.has("category") ? jsonObject.getString("category") : "",
                                                jsonObject.has("history") ? jsonObject.getJSONObject("history").getJSONObject("min").getDouble("price") : 0,
                                                jsonObject.has("history") ? jsonObject.getJSONObject("history").getJSONObject("max").getDouble("price") : 0));
                                    }

                                    CatalogItemAdapter adapter = new CatalogItemAdapter(CatalogActivity.this, items);
                                    mListView.setAdapter(adapter);
                                }
                                // Try and catch are included to handle any errors due to JSON
                                catch (JSONException e) {
                                    // If an error occurs, this prints the error to the log
                                    Toast.makeText(CatalogActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                ){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("Content-Type", "application/json");
                        headers.put("Authorization", "Bearer " + access_token);
                        return headers;
                    }
                };

                requestQueue.add(arrayReq);

            }
        });
    }
}
