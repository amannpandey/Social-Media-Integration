package com.amanpandey.socialmediaintegration;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.tasks.Task;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


public class MainActivity extends AppCompatActivity {
    SignInButton signInButton;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private CircleImageView circleImageView;
    private TextView txtName, txtEmail, textView;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        LoginButton loginButton = findViewById(R.id.login_button);
        txtName = findViewById(R.id.profile_name);
        txtEmail = findViewById(R.id.profile_email);
        textView=findViewById(R.id.textView);
        circleImageView = findViewById(R.id.profile_pic);

        callbackManager = CallbackManager.Factory.create();
        loginButton.setPermissions(Arrays.asList("public_profile","email"));
        checkLoginStatus();

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                textView.setText("Facebook Profile");
                signInButton.setVisibility(INVISIBLE);
                Toast.makeText(MainActivity.this, "Login Successful...", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this, "Login Cancel...", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(MainActivity.this, "Login Failed...", Toast.LENGTH_LONG).show();
            }
        });





  /*      LoginManager.getInstance().logInWithReadPermissions(
                MainActivity.this,
                Arrays.asList("email"));
*/

    }

    private void signIn() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    AccessTokenTracker tokenTracker=new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken accessToken, AccessToken currentAccessToken) {
            if(currentAccessToken==null){
                txtName.setText("");
                txtEmail.setText("");
                textView.setText("The Sparks Foundation");
               // circleImageView.setImageResource(0);
                Drawable res = getResources().getDrawable(R.drawable.tsf);
                circleImageView.setImageDrawable(res);
                signInButton.setVisibility(VISIBLE);
                Toast.makeText(MainActivity.this, "User Logged out", Toast.LENGTH_LONG).show();
            }
            else
                loaduserProfile(currentAccessToken);
        }
    };





   /* AccessTokenTracker tokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken==null)
            {
                txtName.setText("");
                txtEmail.setText("");
                textView.setText("The Sparks Foundation");
               // circleImageView.setImageResource(0);
                Drawable res = getResources().getDrawable(R.drawable.tsf);
                circleImageView.setImageDrawable(res);
                signInButton.setVisibility(VISIBLE);
                Toast.makeText(MainActivity.this, "User Logged out", Toast.LENGTH_LONG).show();
            }
            else
                loadUserProfile(currentAccessToken);
        }
    };
*/
  /*  private void loadUserProfile(AccessToken newAccessToken){
        GraphRequest request = GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    //String email = object.getString("email");
                    String id = object.getString("id");
                    String image_url = "https://graph.facebook.com/"+id+ "/picture?type=normal";


                    txtName.setText("User Name :"+first_name +" "+ last_name);
                    txtEmail.setText("Id :  "+id);

                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.dontAnimate();
                    Glide.with( MainActivity.this).load(image_url).into(circleImageView);

                  //  Toast.makeText(MainActivity.this, "Failed To patch Profile Details1...", Toast.LENGTH_LONG).show();

                } catch (JSONException e){
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Failed To patch Profile Details...", Toast.LENGTH_LONG).show();

                }

            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name, last_name, email, id");
        request.setParameters(parameters);
        request.executeAsync();
    }
*/
    private void loaduserProfile(AccessToken newAccessToken){
        GraphRequest request=GraphRequest.newMeRequest(newAccessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                try {
                    String first_name= jsonObject.getString("first_name");
                    String last_name= jsonObject.getString("last_name");
                    //String email= jsonObject.getString("email");
                    String id= jsonObject.getString("id");

                    String image_url="https://graph.facebook.com/"+id+"/picture?type=normal";

                    txtEmail.setText("Id : "+id);
                    txtName.setText("User Name : "+first_name+" "+last_name);
                    RequestOptions requestOptions=new RequestOptions();
                    requestOptions.dontAnimate();

                    Glide.with(MainActivity.this).load(image_url).into(circleImageView);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Failed To patch Profile Details...", Toast.LENGTH_LONG).show();
                }

            }
        });
        Bundle parameters=new Bundle();
        parameters.putString("fields","first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();

    }


    private void checkLoginStatus(){
        if(AccessToken.getCurrentAccessToken()!=null){
            loaduserProfile(AccessToken.getCurrentAccessToken());
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            if (acct != null) {
                String personName = acct.getDisplayName();
                String personGivenName = acct.getGivenName();
                String personFamilyName = acct.getFamilyName();
                String personEmail = acct.getEmail();
                String personId = acct.getId();
                Uri personPhoto = acct.getPhotoUrl();
                Toast.makeText(this, "Logged In Successfully !", Toast.LENGTH_SHORT).show();
            }
            startActivity(new Intent(MainActivity.this, Profile.class));
        } catch (ApiException e) {
            Log.d("Message", e.toString());
        }
    }
}