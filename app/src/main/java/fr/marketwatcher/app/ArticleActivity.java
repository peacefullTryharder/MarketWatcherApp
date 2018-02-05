package fr.marketwatcher.app;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.Series;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ArticleActivity extends BaseActivity {

    String JsonArticleURL, JsonGraphURL;
    RequestQueue requestQueue;
    String access_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1YTcxZGFlOGY2ZWZhZTEzYzVmY2Q1NWMiLCJpYXQiOjE1MTc0MTEzMDgsImV4cCI6MTUxNzg0MzMwOH0.wcxs9twlGeWN8To-C2FGTzd82TrxzNnGgRgTCKDq7RQ";

    TextView Name = null;
    TextView Category = null;
    TextView About = null;
    TextView Brand = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Article");
        setContentView(R.layout.activity_article);

        JsonArticleURL = "http://api.marketwatcher.fr/product/" + getIntent().getStringExtra("googleId");
        JsonGraphURL = "http://api.marketwatcher.fr/product/" + getIntent().getStringExtra("googleId") + "/graph";

        final GraphView graph = (GraphView) findViewById(R.id.graph);

        Name = (TextView) findViewById(R.id.txtNameArticleActivity);
        Category = (TextView) findViewById(R.id.txtCategoryArticleActivity);
        About = (TextView) findViewById(R.id.txtAboutArticleActivity);
        Brand = (TextView) findViewById(R.id.txtBrandArticleActivity);

        Calendar calendar = Calendar.getInstance();

        Toast.makeText(this, "" + calendar.getTime(), Toast.LENGTH_SHORT).show();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonArrayRequest arrayReq = new JsonArrayRequest(Request.Method.GET, JsonArticleURL,
                // The third parameter Listener overrides the method onResponse() and passes
                //JSONObject as a parameter
                new Response.Listener<JSONArray>() {

                    // Takes the response from the JSON request
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject articleDatas = response.getJSONObject(0);

                            Name.setText(articleDatas.has("name") ?
                                    articleDatas.getString("name") : "NaN");
                            Category.setText(articleDatas.has("category") ?
                                    articleDatas.getString("category") : "NaN");
                            About.setText("dunno what to do");
                            Brand.setText(articleDatas.has("brand") ?
                                    articleDatas.getString("brand") : "NaN");

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
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + access_token);
                return headers;
            }
        };

        requestQueue.add(arrayReq);


        JsonArrayRequest graphReq = new JsonArrayRequest(Request.Method.GET, JsonGraphURL,
                // The third parameter Listener overrides the method onResponse() and passes
                //JSONObject as a parameter
                new Response.Listener<JSONArray>() {

                    // Takes the response from the JSON request
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Random rnd = new Random();

                            ArrayList<LineGraphSeries<DataPoint>> mySeries = new ArrayList<LineGraphSeries<DataPoint>>();

                            for (int i=0; i<3; i++)
                            {

                                mySeries.add(new LineGraphSeries<>(
                                        getDatasFromJSONArray(response.getJSONObject(i).getJSONArray("data"))));

                                mySeries.get(i).setColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));

                                graph.addSeries(mySeries.get(i));
                            }
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
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + access_token);
                return headers;
            }
        };

        requestQueue.add(graphReq);

        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
    }

    public DataPoint[] getDatasFromJSONArray(JSONArray datas)
    {
        List<DataPoint> values = new ArrayList<DataPoint>();
        DataPoint[] finalValues;

        try
        {
            for (int k=datas.length()-365; k<datas.length(); k++)
            {
                values.add(new DataPoint(
                        k,
                        datas.getJSONObject(k).getDouble("price")));
           }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        finalValues = new DataPoint[values.size()];
        finalValues = values.toArray(finalValues);

        return finalValues;
    }
}
