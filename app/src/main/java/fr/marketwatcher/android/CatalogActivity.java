package fr.marketwatcher.android;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
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

    String JsonURL, JsonFirstCallURL;
    RequestQueue requestQueue;
    String access_token;


    private ListView mListView;
    private EditText catalogSearch;
    private RecyclerView menu;

    private final List<CatalogItem> items = new ArrayList<>();

    // Fast-coding: never dissociate this two List
    private final List<String> menuItems = new ArrayList<>();
    private final List<Boolean> menuItemsIsChecked = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.catalog);
        setContentView(R.layout.activity_catalog);

        access_token = getToken();


        mListView = (ListView) findViewById(R.id.listCatalog);
        catalogSearch = (EditText) findViewById(R.id.catalogSearch);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        // Category menu handled by this RecyclerView
        JsonFirstCallURL = BaseActivity.API_URL + "/product/";

        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        menu = (RecyclerView) findViewById(R.id.categoryMenuCatalog);
        menu.setLayoutManager(layoutManager);

        JsonArrayRequest menuReq = new JsonArrayRequest(Request.Method.GET, JsonFirstCallURL,
                // The third parameter Listener overrides the method onResponse() and passes
                //JSONObject as a parameter
                new Response.Listener<JSONArray>() {

                    // Takes the response from the JSON request
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for(int i=0; i<response.length(); i++)
                            {
                                if (!menuItems.contains(response.getJSONObject(i).getString("category")))
                                {
                                    menuItems.add(response.getJSONObject(i).getString("category"));
                                    menuItemsIsChecked.add(false);
                                }
                            }

                            menu.setAdapter(new CatalogMenuItemAdapter(menuItems, R.layout.column_catalog_menu));
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

        requestQueue.add(menuReq);

        updateCatalogList();

        // Fin recycler

        catalogSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateCatalogList();
            }
        });
    }

    public void checkedCategoryMenu(View v) {
        CheckBox checkBox = (CheckBox) v;

        for (int i=0; i<menuItems.size(); i++)
        {
            if (menuItems.get(i).equals(checkBox.getText().toString()))
            {
                menuItemsIsChecked.set(i, !menuItemsIsChecked.get(i));
                updateCatalogList();
            }
        }
    }

    public void updateCatalogList()
    {

        JsonURL = BaseActivity.API_URL + "/product/" +
                (catalogSearch.getText().toString().length() >=3 ?
                        "search?term=" + catalogSearch.getText().toString() :
                        "");

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
                            CatalogItemAdapter adapter;

                            if (!menuItemsIsChecked.contains(true))
                            {
                                for (int i=0; i<response.length(); i++)
                                {
                                    jsonObject = response.getJSONObject(i);

                                    items.add(new CatalogItem(
                                            jsonObject.has("name") ? jsonObject.getString("name") : "",
                                            jsonObject.has("image") ? jsonObject.getString("image") : "",
                                            jsonObject.has("insertedAt") ? jsonObject.getString("insertedAt") : "",
                                            jsonObject.has("googleId") ? jsonObject.getString("googleId") : "",
                                            jsonObject.has("brand") ? jsonObject.getString("brand") : "",
                                            jsonObject.has("model") ? jsonObject.getString("model") : "",
                                            jsonObject.has("category") ? jsonObject.getString("category") : "",
                                            jsonObject.has("history") ? jsonObject.getJSONObject("history").getJSONObject("min").getDouble("price") : 0,
                                            jsonObject.has("history") ? jsonObject.getJSONObject("history").getJSONObject("max").getDouble("price") : 0));
                                }

                                adapter = new CatalogItemAdapter(CatalogActivity.this, items);
                                mListView.setAdapter(adapter);
                            }
                            else
                            {

                                for (int i=0; i<response.length(); i++)
                                {
                                    jsonObject = response.getJSONObject(i);

                                    for (int j=0; j<menuItemsIsChecked.size(); j++)
                                    {
                                        if (menuItemsIsChecked.get(j) && (menuItems.get(j).equals(jsonObject.getString("category"))))
                                        {
                                            items.add(new CatalogItem(
                                                    jsonObject.has("name") ? jsonObject.getString("name") : "",
                                                    jsonObject.has("image") ? jsonObject.getString("image") : "",
                                                    jsonObject.has("insertedAt") ? jsonObject.getString("insertedAt") : "",
                                                    jsonObject.has("googleId") ? jsonObject.getString("googleId") : "",
                                                    jsonObject.has("brand") ? jsonObject.getString("brand") : "",
                                                    jsonObject.has("model") ? jsonObject.getString("model") : "",
                                                    jsonObject.has("category") ? jsonObject.getString("category") : "",
                                                    jsonObject.has("history") ? jsonObject.getJSONObject("history").getJSONObject("min").getDouble("price") : 0,
                                                    jsonObject.has("history") ? jsonObject.getJSONObject("history").getJSONObject("max").getDouble("price") : 0));
                                        }
                                    }

                                    adapter = new CatalogItemAdapter(CatalogActivity.this, items);
                                    mListView.setAdapter(adapter);
                                }
                            }
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
}
