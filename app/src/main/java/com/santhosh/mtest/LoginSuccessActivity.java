package com.santhosh.mtest;


import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class LoginSuccessActivity  extends AppCompatActivity {


    public static final String USER_NAME ="username";
    public static final String TOKEN ="token";
    public static final String USERID ="userId";

    String username;
    String token;

    TextView userNameView;
    TextView tokenView;
    Button editButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginsuccess);

         username = getIntent().getExtras().getString(USER_NAME);
         token = getIntent().getExtras().getString(TOKEN);
         userNameView = findViewById(R.id.username);
        tokenView = findViewById(R.id.token);
        editButton = findViewById(R.id.edit);
        editButton.setVisibility(View.GONE);
        userNameView.setText("UserName: "+userNameView.getText().toString());
        tokenView.setText("Token: "+tokenView.getText().toString());
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpEditButton();
    }

    private void setUpEditButton() {
        try {
            int userId = ((MTestApplication) getApplication()).getPersistenceService().getUserId(username);
            editButton.setVisibility(userId == -1 ? View.GONE : View.VISIBLE);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LoginSuccessActivity.this,EditUserActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString(TOKEN,token);
                    bundle.putString(USER_NAME,username);

                    bundle.putInt(USERID,userId);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
        } catch (RemoteException e) {
            editButton.setVisibility(View.GONE);
            e.printStackTrace();
        }

    }

}
