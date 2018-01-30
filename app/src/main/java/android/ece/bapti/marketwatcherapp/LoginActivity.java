package android.ece.bapti.marketwatcherapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

        // An appropriate listener is assigned to the views that need it
        mSubmitView.setOnClickListener(submitListener);

    }

    public OnClickListener submitListener = new OnclickListener () {
            @Override
            public void onClick(View view){

                // To unable lately
                /* HomeIntent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(HomeIntent); */

                mail = mEmailView.getText().toString();
                pw = mPasswordView.getText().toString();
                Log.i("mail : " +mail+ " ; pw : " +pw);
                try {
                  // Encoding Query parameters
                  String donnees = URLEncoder.encode("login" (ou autre), "ISO-8859-1")+ "="+URLEncoder.encode(mail, "ISO-8859-1");
                  donnees += "&"+URLEncoder.encode("password" (ou autre), "ISO-8859-1")+ "=" + URLEncoder.encode(pw, "ISO-8859-1");
                  
                  // We sent data on a distant adress
                  String api = LoginActivity.getRequestUrl("http://..."+donnees);
                  Log.i("api", "result request : "+api);
                  info = new JSONObject(api);
                  connect = info.getBoolean("connect");
                } catch (Exception e) {
                  e.printStackTrace();
                }
                //If the account exists
                if( connect == true)
                {
                    //Start new activity
                    HomeIntent = new Intent(self, MainActivity.class);
                    startActivity(HomeIntent);

                }
                else if (connect ==false)
                {
                    Toast.makeText(getApplicationContext(),"Identifiants incorrects",Toast.LENGTH_SHORT).show();
                    // mEmailView.setText(null);
                    mPasswordView.setText(null);
                }
                */

                // To Activate lately
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
                }

            }
        };

    public static String getRequestUrl(String url){
        InputStream inputStream = null;
        String result = "";
        try {

              // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}