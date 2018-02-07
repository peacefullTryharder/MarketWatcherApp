package fr.marketwatcher.android;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {
    EditText mRegNameView = null;
    EditText mRegPasswordView = null;
    AutoCompleteTextView mRegEmailView = null;
    Button mRegSubmitView = null;
    private boolean successReg = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mRegNameView = (EditText) findViewById(R.id.regName);
        mRegPasswordView = (EditText) findViewById(R.id.regPassword);
        mRegEmailView = (AutoCompleteTextView) findViewById(R.id.regEmail);
        mRegSubmitView = (Button) findViewById(R.id.regSubmit);

        mRegNameView.addTextChangedListener(textWatcher);
        mRegEmailView.addTextChangedListener(textWatcher);
        mRegPasswordView.addTextChangedListener(textWatcher);

        mRegSubmitView.setOnClickListener(registerListener);
    }

    public View.OnClickListener registerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isValidName()) {
                Toast.makeText(RegisterActivity.this, "Le nom doit contenir au moins deux caractères.", Toast.LENGTH_LONG).show();
            } else if (!isValidEmail(mRegEmailView.getText().toString())) {
                Toast.makeText(RegisterActivity.this, "L'email n'est pas valide.", Toast.LENGTH_LONG).show();
            } else if (!isValidPw()) {
                Toast.makeText(RegisterActivity.this, "Le mot de passe trop court doit contenir au moins cinq caractères.", Toast.LENGTH_LONG).show();
            } else {
                try {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                URL url = new URL(BaseActivity.API_URL + "/user/register");
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                conn.setRequestMethod("POST");
                                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                                conn.setRequestProperty("Accept", "application/json;charset=UTF-8");
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

                                if (conn.getResponseCode() == 200) {
                                    successReg = true;
                                }

                                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                                Log.i("MSG", conn.getResponseMessage());

                                conn.disconnect();
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(RegisterActivity.this, "Impossible de se connecter, veuillez vérifier votre réseau", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    thread.start();
                    thread.join();

                    if (successReg) {
                        Toast.makeText(getApplicationContext(), "Vous avez bien été inscrit. Connectez-vous maintenant", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Désolé, nous n'avons pas réussi à vous enregistrer", Toast.LENGTH_SHORT).show();
                        mRegNameView.setText(null);
                        mRegEmailView.setText(null);
                        mRegPasswordView.setText(null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(RegisterActivity.this, "Veuillez recommencer s'il vous plaît", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    //Méthode pour agir sur les editText
    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!isValidEmail(mRegEmailView.getText().toString())) {
                mRegEmailView.setTextColor(Color.RED);
            }
            if (!isValidName()) {
                mRegNameView.setTextColor(Color.RED);
            }
            if (!isValidPw()) {
                mRegPasswordView.setTextColor(Color.RED);
            }
            if (isValidEmail(mRegEmailView.getText().toString())) {
                mRegEmailView.setTextColor(Color.WHITE);
            }
            if (isValidName()) {
                mRegNameView.setTextColor(Color.WHITE);
            }
            if (isValidPw()) {
                mRegPasswordView.setTextColor(Color.WHITE);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    public static boolean isValidEmail(String email) {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern) && email.length() > 3;
    }

    private boolean isValidName() {
        return mRegNameView.length() >= 2;
    }

    private boolean isValidPw() {
        return mRegPasswordView.length() >= 5;
    }

}
