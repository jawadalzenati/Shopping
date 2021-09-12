package com.josalla.store;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.josalla.store.model.UserBalance;
import com.josalla.store.model.Users;

import java.util.Arrays;

import es.dmoral.toasty.Toasty;

public class SingInActivity extends AppCompatActivity {

    Button btnMobile, btnUseFaceBook  , btnUseGmail,btnTwitter;
    Button btnFaceBook;
    FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    TextView tvCompleteWithOutSign;
    FirebaseFirestore db;
    String newToken = "";
    private static final String TAG = "SingInActivity";
    GoogleSignInClient mGoogleSignInClient;
    private static int RC_SIGN_IN = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        // full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        setContentView(R.layout.activity_sing_in);
        initView();
        getToken();

        tvCompleteWithOutSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                mAuth.signInAnonymously()
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {

                                FirebaseUser userFir = mAuth.getCurrentUser();
                                Users users = new Users(userFir.getUid() + "", newToken +
                                        "", "", "", "", "", false, "user", "yes",0);
                                db.collection("users").document(userFir.getUid())
                                        .set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        UserBalance userBalance = new UserBalance("0.0");
                                        db.collection("balance")
                                                .document(userFir.getUid() + "")
                                                .set(userBalance).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });


                                    }
                                });

                            }
                        });


            }
        });

//        btnMobile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = getIntent();
//
//                startActivity(new Intent(getApplicationContext(), UsingMobileActivity.class));
//
//
//            }
//        });


    }

    private void initView() {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
     //   FacebookSdk.sdkInitialize(getApplicationContext());
     //   AppEventsLogger.activateApp(getApplicationContext());

        btnFaceBook = findViewById(R.id.btnFaceBook);
//        btnMobile = findViewById(R.id.btnMobile);
        btnUseGmail = findViewById(R.id.btnUseGmail);
        btnTwitter = findViewById(R.id.btn_twitter);
        tvCompleteWithOutSign = findViewById(R.id.tvCompleteWithOutSign);
        initFacebook();
        initGoogle();
        initTwitter();
        initListeners();
        
    }

    private void initTwitter() {

    }

    private void initListeners() {
        initGoogleListeners();
        initFacebookListeners();
        initTwitterListeners();
    }

    private void initTwitterListeners() {
        btnTwitter.setOnClickListener(v-> {
            Intent intent=new Intent(getApplicationContext(),TwitterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });
    }

    private void initFacebookListeners() {
        btnFaceBook.setOnClickListener(v->{
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
            LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    Log.e(TAG, "facebook:onSuccess:" + loginResult);
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Log.e(TAG, "facebook:onCancel");
                }

                @Override
                public void onError(FacebookException error) {
                    Log.e(TAG, "facebook:onError", error);
                }
            });
        });


    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.e(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.e(TAG, "signInWithCredential:failure", task.getException());
//                            Toast.makeText(FacebookLoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }
                    }
                });
    }
    private void initGoogleListeners() {
        btnUseGmail.setOnClickListener(v->{
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    private void initFacebook() {
        mCallbackManager = CallbackManager.Factory.create();
    }

    private void initGoogle() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("267642336727-76l1b9b505k74iddugoh4magj9k1ii90.apps.googleusercontent.com"

                )
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void handleFacebookAccessToken(AccessToken token, String AccessToken) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.e("MAS", AccessToken + " " + user.getUid());
                            // public Users( user_accessToken,  user_name,  user_location,  user_address,  user_phone)
                            FirebaseUser userFir = mAuth.getCurrentUser();

                            Users users = new Users(userFir.getUid() + "", newToken +
                                    "", "", "", "", "", false, "user", "yes",3);
                            db.collection("users").document(userFir.getUid())
                                    .set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {


                                    db.collection("balance")
                                            .document(userFir.getUid())
                                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot doc) {
                                            if (doc.exists()) {
                                                Log.e("MAS", "user exist ");
                                            } else {

                                                UserBalance userBalance = new UserBalance("0.0");
                                                db.collection("balance")
                                                        .document(userFir.getUid() + "")
                                                        .set(userBalance).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                    }
                                                });
                                            }
                                        }
                                    });


                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                    finish();


                                }
                            });


                        } else {
                            Toast.makeText(SingInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private String getToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SingInActivity.this, instanceIdResult -> {
            newToken = instanceIdResult.getToken();
            Log.e("newToken", newToken);
        });
        return newToken;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
Log.d("tttt", data.toString());
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.e(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Google sign in failed", e);
            }
        } else{
            Log.d("ttt", "onActivityResult: error");
        }

    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                           startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }
                    }
                });
    }
      }