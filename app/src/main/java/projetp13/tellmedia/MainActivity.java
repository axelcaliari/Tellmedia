package projetp13.tellmedia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {
    private TextView afficher1;
    private ListView liste;
    private Bitmap bitmap;
    private static String username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        AsyncTaskRun run = new AsyncTaskRun();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            run.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            run.execute();
    }

    private class AsyncTaskRun extends AsyncTask<String, Void, String> {
        private String affichage;
        private int debut,fin;

        @Override
        protected String doInBackground(String... params) {
            String line;
            String message = "";

            try {
                URL siteURL = new URL("http://tellmedia.herokuapp.com/api/user");
                URLConnection urlConnection = siteURL.openConnection();
                HttpURLConnection httpURLConnection = (HttpURLConnection) urlConnection;

                if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream stream = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                    while ((line = reader.readLine()) != null) {
                        message += line;
                    }
                    reader.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            affichage = "";

            debut = message.indexOf("\"image\":\"")+9;
            fin = message.indexOf("\"", debut);
            URL newurl = null;

            try {
                newurl = new URL(message.substring(debut,fin));
                bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            debut = message.indexOf("\"username\":\"")+12;
            fin = message.indexOf("\"", debut);
            username = message.substring(debut, fin);
            affichage += "Username : " + username + "\n";

            debut = message.indexOf("\"password\":\"")+12;
            fin = message.indexOf("\"", debut);
            password = message.substring(debut,fin);
            affichage += "Password : " + password + "\n";

            return message;

        }

        protected void onPostExecute(String message){
            /*Intent getmessage = getIntent();
            message = getmessage.getIntExtra(MainActivity.message_intent, 0);*/

            afficher1 = (TextView)findViewById(R.id.resultat);
            afficher1.setText(affichage);

            ImageView image = (ImageView) findViewById(R.id.image);
            image.setImageBitmap(bitmap);

            final Button connexion = (Button) findViewById(R.id.connexion);
            connexion.setVisibility(View.INVISIBLE);

            final Button bouton = (Button) findViewById(R.id.bouton);
            bouton.setText("Suivant");
            bouton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bouton.setVisibility(View.GONE);
                    String message = "";
                    try {
                        String data = username + ":" + password;
                        String encoding = Base64.encodeToString(data.getBytes(), Base64.NO_WRAP);

                        HttpUriRequest request = new HttpGet("http://tellmedia.herokuapp.com/api/list");
                        request.addHeader("Authorization", "Basic " + encoding);
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpResponse reponse = httpclient.execute(request);
                        HttpEntity messageEntity = reponse.getEntity();
                        InputStream is = messageEntity.getContent();
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        String line;
                        while ((line = br.readLine()) != null) {
                            message += line;
                        }

                    } catch (IOException e) {
                            e.printStackTrace();
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                    String[] affichage2 = new String[4];

                    int debut = message.indexOf("\"steps\":[\"")+10;
                    int fin = message.indexOf("\"2", debut)-2;
                    String consigne = message.substring(debut, fin);
                    affichage2[0] = consigne;

                    debut = message.indexOf(consigne)+consigne.length()+3;
                    fin = message.indexOf("\"3", debut)-2;
                    consigne = message.substring(debut, fin);
                    affichage2[1] = consigne;

                    debut = message.indexOf(consigne)+consigne.length()+3;
                    fin = message.indexOf("\"4", debut)-2;
                    consigne = message.substring(debut, fin);
                    affichage2[2] = consigne;

                    debut = message.indexOf(consigne)+consigne.length()+3;
                    fin = message.indexOf("\"]", debut);
                    consigne = message.substring(debut, fin);
                    affichage2[3] = consigne;

                    liste = (ListView) findViewById(R.id.liste);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1, affichage2);
                    liste.setAdapter(adapter);

                    connexion.setVisibility(View.VISIBLE);
                    connexion.setText("Se connecter");
                    connexion.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(MainActivity.this, Connexion.class);
                            startActivity(intent);
                        }
                    });
                }

            });

        }

    }
}



