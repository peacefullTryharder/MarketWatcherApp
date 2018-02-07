package fr.marketwatcher.android;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private GraphView graph = null;

    private ArrayList<LineGraphSeries<DataPoint>> mySeries = new ArrayList<LineGraphSeries<DataPoint>>();

    private String MarketplaceMax, MarketplaceMin;

    private LinearLayout.LayoutParams ListViewParams = null;

    View mainArticleInfo;

    // keep track of the number of remaining requests so we can hide the spinner
    private int remainingRequestCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        final int pixels = (int) (50 * scale + 0.5f);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1); // to get previous year add -1
        Date lastYearDate = cal.getTime();
        String lastYearText = cal.get(Calendar.YEAR) + "-" + (1 + cal.get(Calendar.MONTH))
                + "-" + (cal.get(Calendar.DAY_OF_MONTH));

        JsonArticleURL = BaseActivity.API_URL + "/product/" + getIntent().getStringExtra("googleId");
        JsonGraphURL = BaseActivity.API_URL + "/product/" + getIntent().getStringExtra("googleId")
                + "/graph" + "?startDate=" + lastYearText;
        String jsonPredictionURL = BaseActivity.API_URL + "/product/" + getIntent().getStringExtra("googleId") + "/prediction";

        graph = (GraphView) findViewById(R.id.graph);
        final List<MarketplaceItem> items = new ArrayList<>();

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

        mainArticleInfo = findViewById(R.id.articleMainInfo);

        // hide some views before they are loaded
        mainArticleInfo.setVisibility(View.GONE);
        graph.setVisibility(View.GONE);
        findViewById(R.id.articleForecast).setVisibility(View.GONE);

        remainingRequestCount++;
        JsonArrayRequest arrayReq = new JsonArrayRequest(Request.Method.GET, JsonArticleURL,
                // The third parameter Listener overrides the method onResponse() and passes
                //JSONObject as a parameter
                new Response.Listener<JSONArray>() {

                    // Takes the response from the JSON request
                    @Override
                    public void onResponse(JSONArray response) {
                        remainingRequestCount--;
                        try {
                            setArticleContent(response.getJSONObject(0));
                            if (response.getJSONObject(0).has("history")) {
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
                        remainingRequestCount--;
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

        remainingRequestCount++;
        JsonObjectRequest predictionReq = new JsonObjectRequest(Request.Method.GET, jsonPredictionURL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            remainingRequestCount--;
                            displayPredictions(response);
                        } catch (JSONException e) {
                            Log.e("mwArticle", "Encountered a JSONException while parsing forecast: " + e.getMessage());
                        }
                    }
                },
                // The final parameter overrides the method onErrorResponse() and passes VolleyError
                //as a parameter
                new Response.ErrorListener() {
                    @Override
                    // Handles errors that occur due to Volley
                    public void onErrorResponse(VolleyError error) {
                        remainingRequestCount--;
                        Log.e("mwArticle", error.toString());
                    }
                });
        requestQueue.add(predictionReq);

        remainingRequestCount++;
        JsonArrayRequest graphReq = new JsonArrayRequest(Request.Method.GET, JsonGraphURL,
                // The third parameter Listener overrides the method onResponse() and passes
                //JSONObject as a parameter
                new Response.Listener<JSONArray>() {
                    // Takes the response from the JSON request
                    @Override
                    public void onResponse(JSONArray response) {
                        remainingRequestCount--;
                        try {
                            graph.setVisibility(View.VISIBLE);

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
                        remainingRequestCount--;
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

        final ProgressBar spinner = (ProgressBar) findViewById(R.id.articleProgressBar);
        spinner.setVisibility(View.VISIBLE);
        spinner.setIndeterminate(true);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                if (remainingRequestCount <= 0) {
                    spinner.setVisibility(View.GONE);
                }
            }
        });

        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);
    }

    public void displayPredictions(JSONObject response) throws JSONException {
        SparseArray<Object> forecasts = new SparseArray<>();

        JSONArray regressions = response.getJSONArray("regression");

        JSONObject linearRegression = null, polynomialRegression = null;

        for (int i = 0; i < regressions.length(); i++) {
            JSONObject regression = regressions.getJSONObject(i);

            if (regression.getString("type").equals("linear")) {
                linearRegression = regression;
            } else if (regression.getString("type").equals("polynomial")) {
                polynomialRegression = regression;
            }
        }

        // 5 day forecast is the linear regression
        if (linearRegression != null) {
            forecasts.put(5, (int) Math.floor(linearRegression.getDouble("intercept")));
        }

        // The other two are computed using the polynomial regression
        if (polynomialRegression != null) {
            JSONArray paramsArray = polynomialRegression.getJSONArray("coefs");

            double intercept = polynomialRegression.getDouble("intercept");
            double[] params = new double[paramsArray.length()];
            for (int i = 0; i < paramsArray.length(); i++) {
                params[i] = paramsArray.getDouble(i);
            }

            forecasts.put(42, (int) Math.floor(intercept + polynomial(params, 42)));
            forecasts.put(90, (int) Math.floor(intercept + polynomial(params, 90)));
        }

        TextView view = (TextView) findViewById(R.id.articleForecast);

        // fixme: use string builder and i18n
        String text = "Prédiction du prix pour les jours à venir : \n";

        if (forecasts.get(5) != null) {
            text += "· d'ici 5 jours : " + forecasts.get(5) + " €\n";
        }
        if (forecasts.get(42) != null) {
            text += "· dans 42 jours : " + forecasts.get(42) + " €\n";
        }
        if (forecasts.get(90) != null) {
            text += "· dans 3 mois : " + forecasts.get(90) + " €\n";
        }

        view.setText(text);

        if (forecasts.size() > 0) {
            view.setVisibility(View.VISIBLE);
        }

        Log.i("mwArticle", "Computed : " + forecasts.toString());
    }

    /**
     * Computes a polynomial - efficiently! (Horner's algorithm)
     * Based on https://stackoverflow.com/a/39232499
     *
     * @param x x
     * @return y
     */
    private double polynomial(double[] params, double x) {
        double xSquared = x * x;
        double yOdd = 0d, yEven = 0d;

        int index = params.length - 1;
        if (index % 2 == 0) {
            yEven = params[index];
            index -= 1;
        }
        for (; index >= 0; index -= 2) {
            yOdd = params[index] + yOdd * xSquared;
            yEven = params[index - 1] + yEven * xSquared;
        }
        return yEven + yOdd * x;
    }

    public DataPoint[] getDatasFromJSONArray(JSONArray datas) {
        List<DataPoint> values = new ArrayList<DataPoint>();
        DataPoint[] finalValues;

        // k <=> new SimpleDateFormat("yyyy-MM-dd").parse(datas.getJSONObject(k).getString("date").split("T")[0])

        try {


            for (int k = 0; k < datas.length(); k++) {
                values.add(new DataPoint(
                        k,
                        datas.getJSONObject(k).getDouble("price")));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        finalValues = new DataPoint[values.size()];
        finalValues = values.toArray(finalValues);

        return finalValues;
    }

    public void setArticleContent(JSONObject articleDatas) {
        try {
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

            mainArticleInfo.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getMarketplaceImageUrl(String marketplace) {
        switch (marketplace) {
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

    public void checkedMarketplaceHandler(View v) {
        CheckBox checkBox = (CheckBox) v;

        for (int i = 0; i < MarketPlacesView.getChildCount(); i++) {
            if (MarketPlacesView.getChildAt(i) == v.getParent()) {

                if (checkBox.isChecked()) graph.addSeries(mySeries.get(i));
                else graph.removeSeries(mySeries.get(i));
            }
        }
    }
}
