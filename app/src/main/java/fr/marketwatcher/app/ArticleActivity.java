package fr.marketwatcher.app;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
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

    private String JsonArticleURL, JsonGraphURL;
    private RequestQueue requestQueue;
    private String access_token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1YTc4ODExZjgyNmVmYzdlYzE2M2VlOGUiLCJpYXQiOjE1MTc4NDY5NzUsImV4cCI6MTUxODI3ODk3NX0.o8D3henhAztT-JTuX8ihePR0sWqVL6Sw4OoQENc4jR0";

    private TextView Name = null;
    private TextView Category = null;
    private TextView About = null;
    private TextView Brand = null;
    private TextView MinPrice = null;
    private TextView MaxPrice = null;
    private ImageView ArticleImage = null;
    private ListView MarketPlacesView = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        JsonArticleURL = "http://api.marketwatcher.fr/product/" + getIntent().getStringExtra("googleId");
        JsonGraphURL = "http://api.marketwatcher.fr/product/" + getIntent().getStringExtra("googleId") + "/graph?resolution=365";

        final GraphView graph = (GraphView) findViewById(R.id.graph);
        final List<MarketplaceItem> items = new ArrayList<MarketplaceItem>();

        // Name = (TextView) findViewById(R.id.txtNameArticleActivity);
        Category = (TextView) findViewById(R.id.txtCategoryArticleActivity);
        About = (TextView) findViewById(R.id.txtAboutArticleActivity);
        Brand = (TextView) findViewById(R.id.txtBrandArticleActivity);
        MinPrice = (TextView) findViewById(R.id.txtMinPriceArticleActivity);
        MaxPrice = (TextView) findViewById(R.id.txtMaxPriceArticleActivity);

        ArticleImage = (ImageView) findViewById(R.id.imgArticleOnConsultation);

        MarketPlacesView = (ListView) findViewById(R.id.listArticle);

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

                            setTitle((articleDatas.has("brand") ?
                                            (articleDatas.getString("brand") + " ") : "") +
                                        (articleDatas.has("model") ?
                                            (articleDatas.getString("model") + " ") : "") + "");

                            Category.setText(articleDatas.has("category") ?
                                    articleDatas.getString("category") : "Article");

                            About.setText(articleDatas.has("name") ?
                                    articleDatas.getString("name") : "");

                            Brand.setText(articleDatas.has("brand") ?
                                    articleDatas.getString("brand") : "");

                            new DownloadImageFromInternet(ArticleImage)
                                    .execute(articleDatas.has("image") ?
                                            articleDatas.getString("image") : "");

                            MinPrice.setText(((articleDatas.has("history") ?
                                    (articleDatas.getJSONObject("history").getDouble("min")) : 0) != 0) ?
                                    (
                                            ((Integer.parseInt(("" + articleDatas.getJSONObject("history").getDouble("min")).split("\\.")[1]) == 0) ?
                                                    ("" + articleDatas.getJSONObject("history").getDouble("min")).split("\\.")[0] :
                                                    (("" + articleDatas.getJSONObject("history").getDouble("min")).split("\\.")[0] + ","
                                                            + ("" + articleDatas.getJSONObject("history").getDouble("min")).split("\\.")[1])) + "€"
                                    ) : "");

                            MaxPrice.setText(((articleDatas.has("history") ?
                                    (articleDatas.getJSONObject("history").getDouble("min")) : 0) != 0) ?
                                    (
                                            ((Integer.parseInt(("" + articleDatas.getJSONObject("history").getDouble("max")).split("\\.")[1]) == 0) ?
                                                    ("" + articleDatas.getJSONObject("history").getDouble("max")).split("\\.")[0] :
                                                    (("" + articleDatas.getJSONObject("history").getDouble("max")).split("\\.")[0] + ","
                                                            + ("" + articleDatas.getJSONObject("history").getDouble("max")).split("\\.")[1])) + "€"
                                    ) : "");


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

                            for (int i=0; i<response.length(); i++)
                            {

                                mySeries.add(new LineGraphSeries<>(
                                        getDatasFromJSONArray(response.getJSONObject(i).getJSONArray("data"))));

                                mySeries.get(i).setColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));

                                mySeries.get(i).setThickness(6);
                                graph.addSeries(mySeries.get(i));

                                if (response.getJSONObject(i).getJSONObject("_id").has("marketplace"))
                                {
                                    items.add(new MarketplaceItem(
                                            response.getJSONObject(i).getJSONObject("_id").getString("marketplace"),
                                            response.getJSONObject(i).getJSONArray("data").getJSONObject(response.getJSONObject(i).getJSONArray("data").length()-1).getString("price"),
                                            "http://www.gurret.fr/wp-content/uploads/2016/09/logo-fnac-100x100.png")); // SwitchCaseUrl()
                                }
                            }

                            MarketplaceItemAdapter adapter = new MarketplaceItemAdapter(ArticleActivity.this, items);

                            MarketPlacesView.setAdapter(adapter);

                        }
                        // Try and catch are included to handle any errors due to JSON
                        catch (JSONException e) {
                            // If an error occurs, this prints the error to the log
                            e.printStackTrace();
                            Toast.makeText(ArticleActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
