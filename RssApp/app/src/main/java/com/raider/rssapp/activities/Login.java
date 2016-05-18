package com.raider.rssapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.raider.rssapp.R;
import com.raider.rssapp.utils.ServerConnection;
import com.raider.rssapp.utils.Util;
import com.raider.rssapp.utils.Var;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;

public class Login extends AppCompatActivity {

    private EditText txtNickname;
    private EditText txtPassword;
    private Button btSignin;
    private Button btLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtNickname = (EditText) findViewById(R.id.txtNickname);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        btSignin = (Button) findViewById(R.id.btSignin);
        btLogin = (Button) findViewById(R.id.btLogin);

        btSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
            }
        });

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckUser checkUser = new CheckUser(txtNickname.getText().toString(), txtPassword.getText().toString());
                checkUser.execute();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private class CheckUser extends AsyncTask<Void,Void,Void> {

        private String nick;
        private String password;
        private Boolean exists = false;

        public CheckUser(String nick, String password) {
            this.nick = nick;
            this.password = new String(Hex.encodeHex(DigestUtils.sha1(password)));
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ServerConnection serverConnection = new ServerConnection();
            try {

                exists = serverConnection.checkUser(nick, password);

                serverConnection.getUserId(nick);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (exists == true) {
                Intent intent = new Intent(Login.this, MainMenu.class);
                startActivity(intent);
            } else {
                Util.toast(getApplicationContext(), getResources().getString(R.string.toastLoginFail));
            }
        }
    }
}
