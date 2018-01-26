package android.ece.bapti.marketwatcherapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class ArticleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Article");
        setContentView(R.layout.activity_article);

        GraphView graph = (GraphView) findViewById(R.id.graph);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 160),
                new DataPoint(1, 154.4),
                new DataPoint(2, 143.2),
                new DataPoint(3, 172),
                new DataPoint(4, 149)
        });
        series.setColor(getColor(R.color.colorPrimaryDark));

        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 130),
                new DataPoint(1, 157),
                new DataPoint(2, 162),
                new DataPoint(3, 148),
                new DataPoint(4, 147)
        });
        series2.setColor(getColor(android.R.color.holo_red_light));

        graph.getGridLabelRenderer().setHorizontalLabelsVisible(false);

        graph.addSeries(series2);
        graph.addSeries(series);
    }
}
