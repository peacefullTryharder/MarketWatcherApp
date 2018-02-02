package android.ece.bapti.marketwatcherapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import android.widget.EditText;
import android.widget.Toast;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;
import android.text.TextWatcher;
import android.text.Editable;


public class LoginActivity extends AppCompatActivity {

    // Temporary logins
    private String email = "baptiste.ludot@gmail.com";
    private String password = "1234";
    public Intent HomeIntent;
    private int status = 0;

    AutoCompleteTextView mLogEmailView = null;
    EditText mLogPasswordView = null;
    EditText mRegNameView = null;
    EditText mRegPasswordView = null;
    AutoCompleteTextView mRegEmailView = null;
    Button mLogSubmitView = null;
    Button mRegSubmitView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

            sendPostLogin();

            if(mLogEmailView.getText().toString().equals(email) &&
                    mLogPasswordView.getText().toString().equals(password))
            {
                HomeIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(HomeIntent);
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Identifiants incorrects",Toast.LENGTH_SHORT).show();
                // mEmailView.setText(null);
                mLogPasswordView.setText(null);
            }

        }
    };

    public OnClickListener registerListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            if(sendPostRegister() == 200) {
                Toast.makeText(getApplicationContext(),"Vous avez bien été inscrit. Connectez-vous maintenant",Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Désolé, nous n'avons pas réussi à vous enregistrer",Toast.LENGTH_SHORT).show();
                mRegNameView.setText(null);
                mRegEmailView.setText(null);
                mRegPasswordView.setText(null);
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


    public int sendPostRegister() {
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

                    status = conn.getResponseCode();

                    Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                    Log.i("MSG" , conn.getResponseMessage());

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        return  status;

    }

    public void sendPostLogin() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://api.marketwatcher.fr/user/token");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept", "application/json;charset=UTF-8");
                    //conn.setRequestProperty("Authorization","Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiI1YTcxZGFlOGY2ZWZhZTEzYzVmY2Q1NWMiLCJpYXQiOjE1MTc0MTEzMDgsImV4cCI6MTUxNzg0MzMwOH0.wcxs9twlGeWN8To-C2FGTzd82TrxzNnGgRgTCKDq7RQ");
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

                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

}