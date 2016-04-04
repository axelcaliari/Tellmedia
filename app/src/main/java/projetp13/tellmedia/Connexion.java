package projetp13.tellmedia;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Axel on 04/04/2016.
 */
public class Connexion extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connexion);

        final EditText username = (EditText) findViewById(R.id.username);

        final EditText firstName = (EditText) findViewById(R.id.firstName);

        final EditText lastName = (EditText) findViewById(R.id.lastName);

        final EditText password = (EditText) findViewById(R.id.password);

        Button valider = (Button) findViewById(R.id.valider);
        valider.setText("Valider");
        valider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message;
                try {
                    String data = username.getText() + ":" + firstName.getText() + ":" + lastName.getText() + ":" + password.getText();
                    String encoding = Base64.encodeToString(data.getBytes(), Base64.NO_WRAP);

                    HttpUriRequest request = new HttpPost("http://tellmedia.herokuapp.com/api/signup");
                    request.addHeader("Authorization", "Basic " + encoding);
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpResponse reponse = httpclient.execute(request);
                    HttpEntity messageEntity = reponse.getEntity();
                    InputStream is = messageEntity.getContent();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));

                } catch (IOException e) {
                    e.printStackTrace();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
