package fr.marketwatcher.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.content.res.AppCompatResources;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.text.TextWatcher;
import android.text.Editable;

import fr.marketwatcher.android.MainActivity;


public class LoginActivity extends AppCompatActivity {
    public Intent HomeIntent;
    private String resultLog = null;

    AutoCompleteTextView mLogEmailView = null;
    EditText mLogPasswordView = null;
    Button mLogSubmitView = null;

    TextView mLogRegisterLink = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        if (preferences.contains("token")) {
            HomeIntent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(HomeIntent);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLogEmailView = (AutoCompleteTextView) findViewById(R.id.logEmail);
        mLogPasswordView = (EditText) findViewById(R.id.logPassword);
        mLogSubmitView = (Button) findViewById(R.id.logSubmit);
        mLogRegisterLink = (TextView) findViewById(R.id.logRegisterLink);

        // Enable Android O autocomplete
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLogEmailView.setAutofillHints("email");
            mLogPasswordView.setAutofillHints("password");
        }

        mLogRegisterLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

//        // An appropriate listener is assigned to the views that need it
//        mLogEmailView.addTextChangedListener(textWatcher);
//        mLogPasswordView.addTextChangedListener(textWatcher);

        mLogSubmitView.setOnClickListener(loginListener);
    }

    public OnClickListener loginListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(BaseActivity.API_URL + "/user/token");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                            conn.setRequestProperty("Accept", "application/json;charset=UTF-8");
                            conn.setDoOutput(true);
                            conn.setDoInput(true);

                            JSONObject jsonParam = new JSONObject();
                            jsonParam.put("email", mLogEmailView.getText().toString());
                            jsonParam.put("password", mLogPasswordView.getText().toString());

                            Log.i("JSON", jsonParam.toString());
                            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                            //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                            os.writeBytes(jsonParam.toString());

                            os.flush();
                            os.close();


                            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                            Log.i("MSG", conn.getResponseMessage());

                            if (conn.getResponseCode() == 200) {
                                InputStream inputStream = null;
                                try {
                                    // receive response as inputStream
                                    inputStream = conn.getInputStream();

                                    // convert inputstream to string
                                    if (inputStream != null)
                                        resultLog = convertInputStreamToString(inputStream);
                                    else {
                                        Toast.makeText(getApplicationContext(), "Erreur, nous n'avons pas réussi à vous connecter", Toast.LENGTH_SHORT).show();
                                        // mEmailView.setText(null);
                                        mLogPasswordView.setText(null);
                                    }

                                } catch (Exception e) {
                                    Log.d("InputStream", e.getLocalizedMessage());
                                }
                            }

                            conn.disconnect();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                thread.start();
                thread.join();

                if (resultLog != null) {
                    JSONObject info = new JSONObject(resultLog);
                    saveToken(info.getString("token"));

                    HomeIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(HomeIntent);
                } else {
                    Toast.makeText(LoginActivity.this, "Identifiants incorrects", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(LoginActivity.this, "Veuillez recommencer s'il vous plaît", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private void saveToken(String token) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.apply();
    }
}