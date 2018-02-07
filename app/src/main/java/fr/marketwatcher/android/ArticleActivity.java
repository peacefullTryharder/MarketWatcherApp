package fr.marketwatcher.android;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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
    private ScrollView ScrollArticle = null;
    private CheckBox CheckboxSeries = null;

    private String MarketplaceMax, MarketplaceMin;

    private LinearLayout.LayoutParams ListViewParams = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        final int pixels = (int) (50 * scale + 0.5f);

        JsonArticleURL = "http://api.marketwatcher.fr/product/" + getIntent().getStringExtra("googleId");
        JsonGraphURL = "http://api.marketwatcher.fr/product/" + getIntent().getStringExtra("googleId") + "/graph";

        final GraphView graph = (GraphView) findViewById(R.id.graph);
        final List<MarketplaceItem> items = new ArrayList<MarketplaceItem>();

        ScrollArticle = (ScrollView) findViewById(R.id.scrollArticle);
        ScrollArticle.requestDisallowInterceptTouchEvent(true);

        Category = (TextView) findViewById(R.id.txtCategoryArticleActivity);
        About = (TextView) findViewById(R.id.txtAboutArticleActivity);
        Brand = (TextView) findViewById(R.id.txtBrandArticleActivity);
        MinPrice = (TextView) findViewById(R.id.txtMinPriceArticleActivity);
        MaxPrice = (TextView) findViewById(R.id.txtMaxPriceArticleActivity);

        CheckboxSeries = (CheckBox) findViewById(R.id.checkboxSeries);

        ArticleImage = (ImageView) findViewById(R.id.imgArticleOnConsultation);

        MarketPlacesView = (ListView) findViewById(R.id.listArticle);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonArrayRequest arrayReq = new JsonArrayRequest(Request.Method.GET, JsonArticleURL,
                // The third parameter Listener overrides the method onResponse() and passes
                //JSONObject as a parameter
                new Response.Listener<JSONArray>() {

                    // Takes the response from the JSON request
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            setArticleContent(response.getJSONObject(0));
                            if (response.getJSONObject(0).has("history")){
                                MarketplaceMax = response.getJSONObject(0).getJSONObject("history")
                                        .getJSONObject("max").getString("marketplace");
                                MarketplaceMin = response.getJSONObject(0).getJSONObject("history")
                                        .getJSONObject("min").getString("marketplace");
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

        requestQueue.add(arrayReq);

        final ArrayList<LineGraphSeries<DataPoint>> mySeries = new ArrayList<LineGraphSeries<DataPoint>>();

        JsonArrayRequest graphReq = new JsonArrayRequest(Request.Method.GET, JsonGraphURL,
                // The third parameter Listener overrides the method onResponse() and passes
                //JSONObject as a parameter
                new Response.Listener<JSONArray>() {

                    // Takes the response from the JSON request
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Random rnd = new Random();

                            String localMarketplaceTmp;

                            for (int i=0; i<response.length(); i++)
                            {

                                if (response.getJSONObject(i).getJSONObject("_id").has("marketplace"))
                                {

                                    localMarketplaceTmp = response.getJSONObject(i).getJSONObject("_id").getString("marketplace");

                                    items.add(new MarketplaceItem(
                                            (response.getJSONObject(i).getJSONObject("_id").getString("marketplace").length() <= 10) ?
                                                    response.getJSONObject(i).getJSONObject("_id").getString("marketplace")
                                                    : (response.getJSONObject(i).getJSONObject("_id").getString("marketplace").substring(0, 10) + ".."),
                                            response.getJSONObject(i).getJSONArray("data").getJSONObject(response.getJSONObject(i).getJSONArray("data").length()-1).getString("price"),
                                            getMarketplaceImageUrl(response.getJSONObject(i).getJSONObject("_id").getString("marketplace")),
                                            localMarketplaceTmp.equals(MarketplaceMax) || localMarketplaceTmp.equals(MarketplaceMin)));

                                    mySeries.add(new LineGraphSeries<>(
                                            getDatasFromJSONArray(response.getJSONObject(i).getJSONArray("data"))));

                                    // To display default series (min & max)
                                    if (localMarketplaceTmp.equals(MarketplaceMax) || localMarketplaceTmp.equals(MarketplaceMin))
                                    {
                                        mySeries.get(i).setColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
                                        mySeries.get(i).setThickness(6);
                                        graph.addSeries(mySeries.get(i));
                                    }

                                }
                            }

                            ListViewParams = (LinearLayout.LayoutParams) MarketPlacesView.getLayoutParams();
                            ListViewParams.height = pixels * response.length();
                            MarketPlacesView.setLayoutParams(ListViewParams);

                            MarketplaceItemAdapter adapter = new MarketplaceItemAdapter(ArticleActivity.this, items);

                            MarketPlacesView.setAdapter(adapter);

                            ScrollArticle.smoothScrollTo(0,0);

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

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    public DataPoint[] getDatasFromJSONArray(JSONArray datas)
    {
        List<DataPoint> values = new ArrayList<DataPoint>();
        DataPoint[] finalValues;

        // k <=> new SimpleDateFormat("yyyy-MM-dd").parse(datas.getJSONObject(k).getString("date").split("T")[0])

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

    public void setArticleContent(JSONObject articleDatas)
    {
        try
        {
            setTitle(articleDatas.has("name") ?
                    articleDatas.getString("name") : "");

            Category.setText(articleDatas.has("category") ?
                    articleDatas.getString("category") : "Article");

            About.setText((articleDatas.has("brand") ?
                    (articleDatas.getString("brand") + " ") : "") +
                    (articleDatas.has("model") ?
                            (articleDatas.getString("model") + " ") : "") + "");

            Brand.setText(articleDatas.has("brand") ?
                    articleDatas.getString("brand") : "");

            new DownloadImageFromInternet(ArticleImage)
                    .execute(articleDatas.has("image") ?
                            articleDatas.getString("image") : "");

            MinPrice.setText(((articleDatas.has("history") ?
                    (articleDatas.getJSONObject("history").getJSONObject("min").getDouble("price")) : 0) != 0) ?
                    (
                            ((Integer.parseInt(("" + articleDatas.getJSONObject("history").getJSONObject("min").getDouble("price")).split("\\.")[1]) == 0) ?
                                    ("" + articleDatas.getJSONObject("history").getJSONObject("min").getDouble("price")).split("\\.")[0] :
                                    (("" + articleDatas.getJSONObject("history").getJSONObject("min").getDouble("price")).split("\\.")[0] + ","
                                            + ("" + articleDatas.getJSONObject("history").getJSONObject("min").getDouble("price")).split("\\.")[1])) + "€"
                    ) : "");

            MaxPrice.setText(((articleDatas.has("history") ?
                    (articleDatas.getJSONObject("history").getJSONObject("max").getDouble("price")) : 0) != 0) ?
                    (
                            ((Integer.parseInt(("" + articleDatas.getJSONObject("history").getJSONObject("max").getDouble("price")).split("\\.")[1]) == 0) ?
                                    ("" + articleDatas.getJSONObject("history").getJSONObject("max").getDouble("price")).split("\\.")[0] :
                                    (("" + articleDatas.getJSONObject("history").getJSONObject("max").getDouble("price")).split("\\.")[0] + ","
                                            + ("" + articleDatas.getJSONObject("history").getJSONObject("max").getDouble("price")).split("\\.")[1])) + "€"
                    ) : "");
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public String getMarketplaceImageUrl (String marketplace){
        switch (marketplace)
        {
            case "Fnac":
            case "Fnac.com":
                return "http://www.gurret.fr/wp-content/uploads/2016/09/logo-fnac-100x100.png";
            case "EasyLounge":
                return "http://static8.viadeo-static.com/SxUoMWEuUex3z4PBoXM5pmX6tDg=/fit-in/200x200/filters:fill(white)/9460eed72f9c4f718ed0f4d82f0a67a8/1434475018.jpeg";
            case "Cobra":
                return "https://pbs.twimg.com/profile_images/872129592571678720/XB_9tBsy.jpg";
            case "Villatech":
                return "https://www.ecoreuil.fr/m/Store/logo_s/r1q_gCS8.jpg";
            case "Darty":
            case "Darty.com":
                return "https://www.darty.com/static/gdH5/desktop2/common/images/darty_sprite/sprite_darty_logo.png";
            case "Boulanger":
                return "https://media.glassdoor.com/sql/846210/boulanger-squarelogo-1462867429355.png";
            case "Ubaldi":
            case "Ubaldi.com":
                return "https://media.custplace.com/users_pictures/1460726645.png";
            case "iacono":
            case "iacono.fr":
                return "https://www.iacono.fr/boutique/images_boutique/logo/logoPetit.jpg";
            case "Samsung":
            case "Samsung Shop France":
                return "https://arwac.be/files/uploads/2015/03/samsung-logo.jpg";
            default:
                return "";
        }
    }

    interface MarketplaceBooleanChangedListener {
        public void OnBooleanChanged();
    }

    public void checkedMarketplaceHandler(View v) {
        int position;

        for (int i=0; i < MarketPlacesView.getChildCount(); i++)
        {
            if (MarketPlacesView.getChildAt(i) == v.getParent())
            {
                position = i;
            }
        } // In completion
    }
}
