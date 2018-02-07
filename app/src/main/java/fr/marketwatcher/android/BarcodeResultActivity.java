package fr.marketwatcher.android;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

/**
 * Created by bapti on 07/02/2018.
 */

public class BarcodeResultActivity extends BaseActivity {
    TextView barcodeResult = null;
    EditText gtinView = null;
    Button addProductView = null;
    private boolean successAddProduct = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_activity);

        gtinView = (EditText) findViewById(R.id.gtinField);
        addProductView = (Button) findViewById(R.id.addProduct);
        barcodeResult = (TextView) findViewById((R.id.barcode_result));

        gtinView.addTextChangedListener(textWatcher);
        addProductView.setOnClickListener(addProductListener);

    }

    public View.OnClickListener addProductListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Pattern sPattern = Pattern.compile("^([0-9]{8})([0-9]{4}[0-9]{0,2})?$");

            try
            {
                if(sPattern.matcher(gtinView.getText()).matches())
                {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                URL url = new URL("http://api.marketwatcher.fr/product");
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                conn.setRequestMethod("POST");
                                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                                conn.setRequestProperty("Accept", "application/json;charset=UTF-8");
                                conn.setRequestProperty("Authorization","Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1YTc4ODExZjgyNmVmYzdlYzE2M2VlOGUiLCJpYXQiOjE1MTc4NDY5NzUsImV4cCI6MTUxODI3ODk3NX0.o8D3henhAztT-JTuX8ihePR0sWqVL6Sw4OoQENc4jR0");
                                conn.setDoOutput(true);
                                conn.setDoInput(true);

                                JSONObject jsonParam = new JSONObject();
                                jsonParam.put("GTIN", gtinView.getText().toString());

                                Log.i("JSON", jsonParam.toString());
                                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                                os.writeBytes(jsonParam.toString());

                                os.flush();
                                os.close();

                                if (conn.getResponseCode() ==200)
                                {
                                    successAddProduct=true;
                                }

                                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                                Log.i("MSG" , conn.getResponseMessage());

                                conn.disconnect();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(BarcodeResultActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    thread.start();
                    thread.join();

                    if(successAddProduct) {
                        Toast.makeText(getApplicationContext(),"Votre produit a bien été enregistré.",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"Désolé, nous ne pouvaons accéder à votre requête.",Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Désolé, ce code n'est pas valide",Toast.LENGTH_SHORT).show();
                    gtinView.setText(null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(BarcodeResultActivity.this, "Veuillez recommencer s'il vous plaît", Toast.LENGTH_SHORT).show();
            }

        }
    };

    //Méthode pour agir sur les editText
    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void scanBarcode (View V) {
        Intent intent = new Intent(this, ScanBarcodeActivity.class);
        startActivityForResult(intent, 0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==0) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if(data!=null) {
                    String value = data.getStringExtra("value");
                    barcodeResult.setText("Barcode value : " +value);
                    gtinView.setText(value);
                } else {
                    barcodeResult.setText("No barcode detected");
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}