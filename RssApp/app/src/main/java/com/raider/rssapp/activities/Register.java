package com.raider.rssapp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.raider.rssapp.R;
import com.raider.rssapp.classes.User;
import com.raider.rssapp.utils.ServerConnection;
import com.raider.rssapp.utils.Util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;

import java.io.IOException;
import java.security.DigestException;

public class Register extends AppCompatActivity {

    private EditText txtNombre;
    private EditText txtSurname;
    private EditText txtNick;
    private EditText txtPass;
    private EditText txtMail;
    private Button btFinish;
    private int defNickColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtNombre = (EditText) findViewById(R.id.txtNombre);
        txtSurname = (EditText) findViewById(R.id.txtPassword);
        txtNick = (EditText) findViewById(R.id.txtNick);
        txtPass = (EditText) findViewById(R.id.txtPass);
        txtMail = (EditText) findViewById(R.id.txtMail);
        btFinish = (Button) findViewById(R.id.btFinish);

        defNickColor = txtNick.getHighlightColor();

        txtNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtNick.setHighlightColor(defNickColor);
            }
        });

        btFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (txtSurname.getText() != null && txtPass.getText() != null
                        && txtNick.getText() != null && txtNombre.getText() != null) {

                    User user = new User();
                    user.setName(txtNombre.getText().toString());
                    user.setSurname(txtSurname.getText().toString());
                    user.setNickname(txtNick.getText().toString());
                    user.setPass(txtPass.getText().toString());
                    user.setMail(txtMail.getText().toString());

                    CreateUser createUser = new CreateUser(user);
                    createUser.execute();
                } else {

                    Util.toast(getApplicationContext(), getResources().getString(R.string.toastEmptyFields));
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private class CreateUser extends AsyncTask<Void,Void,Void> {

        private User user;
        private Boolean isCreated;

        private CreateUser (User user) {
            this.user = user;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            ServerConnection sc = new ServerConnection();
            try {

                isCreated = sc.createUser(user.getNickname(),new String(Hex.encodeHex(DigestUtils.sha1(user.getPass()))) , user.getName(), user.getSurname(), user.getMail()) == true;

            }catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (isCreated == true) {
                Util.toast(getApplicationContext(), getResources().getString(R.string.toastCreatedUser));
                Intent intent = new Intent(Register.this, Login.class);
                Bundle b = new Bundle();
                b.putString("nick", user.getNickname());
                b.putString("pass", user.getPass());
                startActivity(intent);

            } else {

                if (isCreated == false) {
                    Util.toast(getApplicationContext(), getResources().getString(R.string.toastCreatedUserExists));
                    txtNick.setHighlightColor(Color.RED);
                }
            }
        }
    }
}
