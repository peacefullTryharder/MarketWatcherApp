package fr.marketwatcher.app;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    // Temporary logins
    private String email = "baptiste.ludot@gmail.com";
    private String password = "1234";

    public Intent HomeIntent;

    AutoCompleteTextView mEmailView = null;
    EditText mPasswordView = null;
    Button mSubmitView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = (AutoCompleteTextView) findViewById(R.id.logEmail);
        mPasswordView = (EditText) findViewById(R.id.logPassword);
        mSubmitView = (Button) findViewById(R.id.logSubmit);

        mSubmitView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                // To unable lately
                HomeIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(HomeIntent);

                // To Activate lately
                /*
                if(mEmailView.getText().toString().equals(email) &&
                        mPasswordView.getText().toString().equals(password))
                {
                    HomeIntent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(HomeIntent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Identifiants incorrects",Toast.LENGTH_SHORT).show();
                    // mEmailView.setText(null);
                    mPasswordView.setText(null);
                } */

            }
        });
    }
}