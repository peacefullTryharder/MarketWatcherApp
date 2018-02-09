package fr.marketwatcher.android;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
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
import com.jjoe64.graphview.series.OnDataPointTapListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.lang.Math; 
    
public class ArticleActivity extends BaseActivity {

    private String JsonArticleURL, JsonGraphURL;
    private RequestQueue requestQueue;
    private String access_token;

    private TextView Name = null;
    private TextView Category = null;
    private TextView About = null;
    private TextView MinPrice = null;
    private TextView MaxPrice = null;
    private ImageView ArticleImage = null;
    private ListView MarketPlacesView = null;
    private ScrollView ScrollArticle = null;
    private GraphView graph = null;
    private ImageButton mShareView = null;

    private String productName = null;
    private String productBrand = null;
    private String productModel = null;
    private String productAuthor = null;
    private String productPriceMax = null;
    private String productPriceMin = null;
    private String productPrediction = null;
    private String nameUser = null;

    private String[] graphColors =
    {
            "#e6194b",
            "#3cb44b",
            "#ffe119",
            "#0082c8",
            "#f58231",
            "#911eb4",
            "#46f0f0",
            "#f032e6",
            "#d2f53c",
            "#fabebe",
            "#008080",
            "#e6beff",
            "#aa6e28",
            "#fffac8",
            "#800000",
            "#aaffc3",
            "#808000",
            "#ffd8b1",
            "#000080",
            "#808080"
    };

    String offerUrl;

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

        access_token = getToken();

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

        Name = (TextView) findViewById(R.id.txtNameArticleActivity);
        Category = (TextView) findViewById(R.id.txtCategoryArticleActivity);
        About = (TextView) findViewById(R.id.txtAboutArticleActivity);
        MinPrice = (TextView) findViewById(R.id.txtMinPriceArticleActivity);
        MaxPrice = (TextView) findViewById(R.id.txtMaxPriceArticleActivity);

        mShareView = (ImageButton) findViewById(R.id.share);

        ArticleImage = (ImageView) findViewById(R.id.imgArticleOnConsultation);

        MarketPlacesView = (ListView) findViewById(R.id.listArticle);

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        mainArticleInfo = findViewById(R.id.articleMainInfo);

        mShareView.setOnClickListener(shareListener);

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
                            offerUrl = response.getJSONObject(0).has("offerUrl") ?
                                    response.getJSONObject(0).getString("offerUrl") : "";

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
                            int k = 0;

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
                                            ((k<2) ? localMarketplaceTmp.equals(MarketplaceMax) || localMarketplaceTmp.equals(MarketplaceMin) : false)));

                                    mySeries.add(new LineGraphSeries<>(
                                            getDatasFromJSONArray(response.getJSONObject(i).getJSONArray("data"))));

                                    // To display default series (min & max)
                                    if (localMarketplaceTmp.equals(MarketplaceMax) || localMarketplaceTmp.equals(MarketplaceMin))
                                    {
                                         // Blindage immonde qui est faux ^^
                                        if (k<2){
                                            mySeries.get(i).setColor(Color.parseColor(graphColors[i]));
                                            mySeries.get(i).setThickness(6);
                                            graph.addSeries(mySeries.get(i));
                                            k++;
                                        }
                                    }




                                    graph.getViewport().setMinX(0);
                                    graph.getViewport().setMaxX(response.getJSONObject(i).getJSONArray("data").length());

                                    graph.getViewport().setXAxisBoundsManual(true);

                                }
                            }

                            ListViewParams = (LinearLayout.LayoutParams) MarketPlacesView.getLayoutParams();
                            ListViewParams.height = pixels * response.length();
                            MarketPlacesView.setLayoutParams(ListViewParams);

                            MarketplaceItemAdapter adapter = new MarketplaceItemAdapter(ArticleActivity.this, items, graphColors);

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
        TextView viewTitle = (TextView) findViewById(R.id.articleForecastTitle);

        // fixme: use string builder and i18n
        String text = "";

        if (forecasts.get(5) != null) {
            text += "· d'ici 5 jours : " + forecasts.get(5) + " €\n";
        }
        if (forecasts.get(42) != null) {
            text += "· dans 40 jours : " + forecasts.get(42) + " €\n";
        }
        if (forecasts.get(90) != null) {
            text += "· dans 3 mois : " + forecasts.get(90) + " €\n";
        }


        view.setText(text);
        viewTitle.setText("Prédiction du prix pour les jours à venir:");

        productPrediction = view.getText().toString();

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
        double val=0;
        for (int i=0;i<params.length;i++){
             val+=params[i]*Math.pow(x,(i+1));
        }
        return val;
        /*
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
        */
    }

    public DataPoint[] getDatasFromJSONArray(JSONArray datas) {
        List<DataPoint> values = new ArrayList<DataPoint>();
        DataPoint[] finalValues;
        Double tmpX;

        // k <=> new SimpleDateFormat("yyyy-MM-dd").parse(datas.getJSONObject(k).getString("date").split("T")[0])

            for (int k = 0; k < datas.length(); k++) {
                try {
                        tmpX = datas.getJSONObject(k).getDouble("price");
                    values.add(new DataPoint(
                            k,
                            tmpX));
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }


        finalValues = new DataPoint[values.size()];
        finalValues = values.toArray(finalValues);

        return finalValues;
    }

    public void setArticleContent(JSONObject articleDatas) {
        try {
            setTitle(articleDatas.has("name") ?
                    articleDatas.getString("name") : "");

            Name.setText(articleDatas.has("name") ?
                    articleDatas.getString("name") : "");

            Category.setText(articleDatas.has("category") ?
                    articleDatas.getString("category") : "Article");

            About.setText((articleDatas.has("brand") ?
                    (articleDatas.getString("brand") + " ") : "") +
                    (articleDatas.has("model") ?
                            (articleDatas.getString("model") + " ") : "") + "");

            new DownloadImageFromInternet(ArticleImage)
                    .execute(articleDatas.has("image") ?
                            articleDatas.getString("image") : "");

            MinPrice.setText(((articleDatas.has("history") ?
                    (articleDatas.getJSONObject("history").getJSONObject("min").getDouble("price")) : 0) != 0) ?
                    (
                            ((Integer.parseInt(("" + articleDatas.getJSONObject("history").getJSONObject("min").getDouble("price")).split("\\.")[1]) == 0) ?
                                    ("" + articleDatas.getJSONObject("history").getJSONObject("min").getDouble("price")).split("\\.")[0] :
                                    (("" + articleDatas.getJSONObject("history").getJSONObject("min").getDouble("price")).split("\\.")[0] + ","
                                            + ("" + articleDatas.getJSONObject("history").getJSONObject("min").getDouble("price")).split("\\.")[1])) + "€ (min)"
                    ) : "");

            MaxPrice.setText(((articleDatas.has("history") ?
                    (articleDatas.getJSONObject("history").getJSONObject("max").getDouble("price")) : 0) != 0) ?
                    (
                            ((Integer.parseInt(("" + articleDatas.getJSONObject("history").getJSONObject("max").getDouble("price")).split("\\.")[1]) == 0) ?
                                    ("" + articleDatas.getJSONObject("history").getJSONObject("max").getDouble("price")).split("\\.")[0] :
                                    (("" + articleDatas.getJSONObject("history").getJSONObject("max").getDouble("price")).split("\\.")[0] + ","
                                            + ("" + articleDatas.getJSONObject("history").getJSONObject("max").getDouble("price")).split("\\.")[1])) + "€ (max)"
                    ) : "");

            productName = articleDatas.has("name") ?
                    articleDatas.getString("name") : "";

            productBrand = articleDatas.has("brand") ?
                    articleDatas.getString("brand") : "" ;

            productModel = articleDatas.has("model") ?
                    articleDatas.getString("model") : "" ;

            productAuthor = articleDatas.has("author") ?
                    articleDatas.getString("author") : "" ;

            productPriceMin = MinPrice.getText().toString();

            productPriceMax = MaxPrice.getText().toString();

            mainArticleInfo.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getMarketplaceImageUrl(String marketplace) {
        switch (marketplace) {
            case "Fnac":
            case "Fnac.com":
            case "Fnac - Darty":
                return "http://www.gurret.fr/wp-content/uploads/2016/09/logo-fnac-100x100.png";
            case "eBay":
                return "https://media.glassdoor.com/sql/7853/ebay-squarelogo-1496964024857.png";
            case "EasyLounge":
                return "http://static8.viadeo-static.com/SxUoMWEuUex3z4PBoXM5pmX6tDg=/fit-in/200x200/filters:fill(white)/9460eed72f9c4f718ed0f4d82f0a67a8/1434475018.jpeg";
            case "Cobra":
                return "https://pbs.twimg.com/profile_images/872129592571678720/XB_9tBsy.jpg";
            case "Villatech":
                return "https://www.ecoreuil.fr/m/Store/logo_s/r1q_gCS8.jpg";
            case "Darty":
            case "Darty.com":
                return "https://www.darty.com/static/gdH5/desktop2/common/images/darty_sprite/sprite_darty_logo.png";
            case "Cultura":
            case "Cultura.com":
                return "https://recrutement.cultura.com/wp-content/uploads/2017/10/logocultura.png";
            case "Conforama":
                return "https://yt3.ggpht.com/-dYCfhiGJFP4/AAAAAAAAAAI/AAAAAAAAAAA/mnaiL4O69a8/s100-mo-c-c0xffffffff-rj-k-no/photo.jpg";
            case "Intermarché":
            case "Intermarche":
                return "https://yt3.ggpht.com/-5i2kx1ySioo/AAAAAAAAAAI/AAAAAAAAAAA/HTvwFV9sGes/s100-mo-c-c0xffffffff-rj-k-no/photo.jpg";
            case "Boulanger":
                return "https://media.glassdoor.com/sql/846210/boulanger-squarelogo-1462867429355.png";
            case "CDiscount":
                return "https://yt3.ggpht.com/-glCnQuggty4/AAAAAAAAAAI/AAAAAAAAAAA/Mu_qMlU3NyM/s100-mo-c-c0xffffffff-rj-k-no/photo.jpg";
            case "Ubaldi":
            case "Ubaldi.com":
                return "https://media.custplace.com/users_pictures/1460726645.png";
            case "iacono":
            case "iacono.fr":
                return "https://www.iacono.fr/boutique/images_boutique/logo/logoPetit.jpg";
            case "Amazon":
            case "Amazon.fr":
                return "https://images.sftcdn.net/images/t_optimized,f_auto/p/09acf18a-9a64-11e6-a2ec-00163ec9f5fa/1927264272/amazon-pour-windows-10-logo.png";
            case "Samsung":
            case "Samsung Shop France":
                return "https://arwac.be/files/uploads/2015/03/samsung-logo.jpg";
            case "RueDuCommerce":
            case "RudeDuCommerce":
                return "https://media.glassdoor.com/sql/1010446/rue-du-commerce-squarelogo-1475235072256.png";
            case "PriceMinister":
            case "PriceMinister - Rakuten":
            case "PriceMinister Rakuten":
                return "https://media.licdn.com/mpr/mpr/shrinknp_100_100/AAIA_wDGAAAAAQAAAAAAAA1lAAAAJGY0MGI3MzM1LTkyMmUtNDlhNi1iMmY0LTBjZTBjMzA2MjJlOA.png";
            case "Materiel.net":
                return "data:image/jpeg;base64,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";
            default:
                return "http://www.jlucas.fr/modeles/peel7/images/cart-logo.png";
        }
    }

    public void checkedMarketplaceHandler(View v) {
        CheckBox checkBox = (CheckBox) v;
        Random rnd = new Random();

        for (int i = 0; i < MarketPlacesView.getChildCount(); i++) {
            if (MarketPlacesView.getChildAt(i) == v.getParent()) {
                mySeries.get(i).setColor(Color.parseColor(graphColors[i]));
                mySeries.get(i).setThickness(6);

                if (checkBox.isChecked()) graph.addSeries(mySeries.get(i));
                else graph.removeSeries(mySeries.get(i));
            }
        }
    }

    public void sendMailBook() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{""});
        i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.putExtra(Intent.EXTRA_SUBJECT, "Un utilisateur de MarketWatcher vous partage un article  ");
        i.putExtra(Intent.EXTRA_TEXT   , "Bonjour,\n" +
                "\n" +
                nameUser +" pense que cet article sur Marketwatcher pourrait vous intéresser : \n\n" +
                productName+" - "+productAuthor+"\n" +
                "\n" +
                "Actuellement son prix minimal est de "+productPriceMin+" sur "+MarketplaceMin+" et son prix maximal est de "+productPriceMax+" sur "+MarketplaceMax+".\n" +
                "\n" +
                (productPrediction != null ? productPrediction : "")+"\n\n"+
                "Merci de votre confiance et à bientôt,\n" +
                "\n" +
                "L'équipe Marketwatcher\n\n" +
                "Site : https://www.marketwatcher.fr/\n" +
                "Application : https://play.google.com/store/apps/details?id=fr.marketwatcher.android");
        try {
            startActivity(Intent.createChooser(i, "Envoi de l'email..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ArticleActivity.this, "Il n'y a pas de clients mail installé", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendMailOther() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{""});
        i.putExtra(Intent.EXTRA_SUBJECT, "Un utilisateur de MarketWatcher vous partage un article  ");
        i.putExtra(Intent.EXTRA_TEXT   , "Bonjour,\n" +
                "\n" +
                nameUser+" pense que cet article sur Marketwatcher pourrait vous intéresser : \n\n" +
                productName+" - "+productBrand+" - "+productModel+"\n" +
                "\n" +
                "Actuellement son prix minimal est de "+productPriceMin+" sur "+MarketplaceMin+" et son prix maximal est de "+productPriceMax+" sur "+MarketplaceMax+".\n" +
                "\n" +
                (productPrediction != null ? productPrediction : "")+"\n"+
                "Merci de votre confiance et à bientôt,\n" +
                "\n" +
                "L'équipe Marketwatcher\n\n" +
                "Site : https://www.marketwatcher.fr/\n"+
                "Application : https://play.google.com/store/apps/details?id=fr.marketwatcher.android");
        try {
            startActivity(Intent.createChooser(i, "Envoi de l'email..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ArticleActivity.this, "Il n'y a pas de clients mail installé", Toast.LENGTH_SHORT).show();
        }
    }

    public View.OnClickListener shareListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            try {
                nameUser = nameUser != null ? nameUser : "Un utilisateur";
                if(productAuthor.isEmpty()) {
                    sendMailOther();
                } else {
                    sendMailBook();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void onLinkClick(View v)
    {
        Intent browserIntent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(offerUrl));
        startActivity(browserIntent);
    }
}
