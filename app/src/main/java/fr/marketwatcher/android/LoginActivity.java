package fr.marketwatcher.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import android.widget.EditText;
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


public class LoginActivity extends AppCompatActivity {

    public Intent HomeIntent;
    private boolean successReg = false;
    private String resultLog = "";
    private JSONObject info;


    AutoCompleteTextView mLogEmailView = null;
    EditText mLogPasswordView = null;
    EditText mRegNameView = null;
    EditText mRegPasswordView = null;
    AutoCompleteTextView mRegEmailView = null;
    Button mLogSubmitView = null;
    Button mRegSubmitView = null;

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
        mRegNameView = (EditText) findViewById(R.id.regName);
        mRegPasswordView = (EditText) findViewById(R.id.regPassword);
        mRegEmailView = (AutoCompleteTextView) findViewById(R.id.regEmail);
        mLogSubmitView = (Button) findViewById(R.id.logSubmit);
        mRegSubmitView = (Button) findViewById(R.id.regSubmit);

        // An appropriate listener is assigned to the views that need it
        mLogEmailView.addTextChangedListener(textWatcher);
        mLogPasswordView.addTextChangedListener(textWatcher);
        mRegNameView.addTextChangedListener(textWatcher);
        mRegEmailView.addTextChangedListener(textWatcher);
        mRegPasswordView.addTextChangedListener(textWatcher);

        mLogSubmitView.setOnClickListener(loginListener);
        mRegSubmitView.setOnClickListener(registerListener);

    }

    public OnClickListener loginListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            try {

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL("http://api.marketwatcher.fr/user/token");
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
                            Log.i("MSG" , conn.getResponseMessage());

                            if (conn.getResponseCode() == 200)
                            {
                                InputStream inputStream = null;
                                try
                                {
                                    // receive response as inputStream
                                    inputStream = conn.getInputStream();

                                    // convert inputstream to string
                                    if(inputStream != null)
                                        resultLog = convertInputStreamToString(inputStream);
                                    else {
                                        Toast.makeText(getApplicationContext(), "Identifiants incorrects", Toast.LENGTH_SHORT).show();
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
                        }
                    }
                });

                thread.start();
                thread.join();

                info = new JSONObject(resultLog);

                saveToken(info.getString("token"));

                HomeIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(HomeIntent);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(LoginActivity.this, "Veuillez recommencer s'il vous plaît", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public OnClickListener registerListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            try {

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL("http://api.marketwatcher.fr/user/register");
                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                            conn.setRequestProperty("Accept", "application/json;charset=UTF-8");
                            conn.setRequestProperty("Authorization","Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1YTcxZGFlOGY2ZWZhZTEzYzVmY2Q1NWMiLCJpYXQiOjE1MTc0MTEzMDgsImV4cCI6MTUxNzg0MzMwOH0.wcxs9twlGeWN8To-C2FGTzd82TrxzNnGgRgTCKDq7RQ");
                            conn.setDoOutput(true);
                            conn.setDoInput(true);

                            JSONObject jsonParam = new JSONObject();
                            jsonParam.put("email", mRegEmailView.getText().toString());
                            jsonParam.put("name", mRegNameView.getText().toString());
                            jsonParam.put("password", mRegPasswordView.getText().toString());

                            Log.i("JSON", jsonParam.toString());
                            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                            //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                            os.writeBytes(jsonParam.toString());

                            os.flush();
                            os.close();

                            if (conn.getResponseCode() ==200)
                            {
                                successReg=true;
                            }

                            Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                            Log.i("MSG" , conn.getResponseMessage());

                            conn.disconnect();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                thread.start();
                thread.join();

                if (!isValidEmail(mLogEmailView.getText()) || !isValidNamePw()) {
                    Toast.makeText(LoginActivity.this, "Champs incorrects !", Toast.LENGTH_LONG).show();

                } else if (successReg) {
                    Toast.makeText(getApplicationContext(), "Vous avez bien été inscrit. Connectez-vous maintenant", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Désolé, nous n'avons pas réussi à vous enregistrer", Toast.LENGTH_SHORT).show();
                    mRegNameView.setText(null);
                    mRegEmailView.setText(null);
                    mRegPasswordView.setText(null);
                }
            }catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(LoginActivity.this, "Veuillez recommencer s'il vous plaît", Toast.LENGTH_SHORT).show();
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


    public boolean sendPostRegister() {


        return  successReg;

    }

    public void sendPostLogin() {

    }

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private void saveToken (String token) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    private boolean isValidNamePw ()
    {
        if(mRegNameView.length()>=2 && mRegPasswordView.length()>=5)
            return true;
        else return false;
    }

}