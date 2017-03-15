package x_systems.x_messenger.activities;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.sasl.SASLErrorException;

import java.io.IOException;
import java.util.Objects;

import x_systems.x_messenger.application.Values;
import x_systems.x_messenger.consistent_connection.ConnectionCheck;
import x_systems.x_messenger.R;
import x_systems.x_messenger.exceptions.InvalidDatabaseOrServerCredentials;
import x_systems.x_messenger.local.Local;
import x_systems.x_messenger.pgp.PgpStarter;
import x_systems.x_messenger.storage.Database;
import x_systems.x_messenger.storage.Property;
import x_systems.x_messenger.xmpp.XMPP;

public class LoginActivity extends ExtendedActivity {

    /**
     *  TODO: Login offline through encrypted comparison of username + password
     *  encrypt with your own public key + SaltedHash
     *  SecureRandom()
     */
    private EditText PasswordView;
    private TextView errorMessage;
    private LinearLayout Container;
    private LinearLayout containerLoader;
    private ImageView loader;
    private Button login;
    private LoginActivity loginActivity;
    private int falseLoginCount = 0;
    private boolean continueLogin = true;
    private String password;

    Database database = new Database(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashActivity.isApplicationRunning = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initializeViews();
        initializeVariables();
        ConnectionCheck.forcedCheck(this);
        if (XMPP.connection != null && XMPP.connection.isConnected() && XMPP.connection.isAuthenticated())
            goToBaseActivity();
    }

    @Override
    protected void onResume() {
        SplashActivity.isApplicationRunning = true;
        super.onResume();
        ConnectionCheck.startRepeatedChecks(this);
    }

    public void onClick_Login(View view) {
        password = PasswordView.getText().toString();
        startLoader();
        decideLogin();
    }

    private void initializeViews()
    {
        PasswordView = (EditText)this.findViewById(R.id.Password);
        Container = (LinearLayout)this.findViewById(R.id.linearLayout2);
        containerLoader = (LinearLayout)this.findViewById(R.id.containerLoader);
        loader = (ImageView)this.findViewById(R.id.loader);
        errorMessage = (TextView)this.findViewById(R.id.error_message);
        login = (Button)this.findViewById(R.id.Login);
    }

    private void initializeVariables()
    {
        loginActivity = this;
    }

    private void startLoader()
    {
        Container.setVisibility(View.GONE);

        containerLoader.setVisibility(View.VISIBLE);
        loader.setBackgroundResource(R.drawable.loader_base);
        ((AnimationDrawable) loader.getBackground()).start();
    }

    private void decideLogin()
    {
        if (Objects.equals(password, new Database(this).readProperty(Property.Type.WHIPE_OUT_PASSWORD))) {
            Local.wipeAllData(this);
            goToBaseActivity();
        }
        System.out.println("trying login base: "+password+" == "+new Database(this).readProperty(Property.Type.ENCRYPTED_PASSWORD));
        if (XMPP.connection != null && XMPP.connection.isConnected())
            tryOnlineLogin();
        else
            tryOfflineLogin();
    }

    private void tryOnlineLogin() {

            new Thread(new Runnable() {
                private boolean AttemptToLogin = true;
                private int AttemptCount = 0;

                @Override
                public void run() {
                    System.out.println("trying to login");
                    while (continueLogin && AttemptToLogin && AttemptCount <= 50) {
                        if (XMPP.connection != null && !XMPP.connection.isAuthenticated() && XMPP.connection.isConnected()) {
                            try {
                                System.out.println("Logging..");
                                XMPP.connection.login(
                                        LoginActivity.this.database.readProperty(Property.Type.USER_JID).split("@")[0],
                                        PasswordView.getText().toString()
                                );
                                System.out.println("Logged in..");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(!new Database(LoginActivity.this).readProperty(Property.Type.ENCRYPTED_PASSWORD).equals(PasswordView.getText().toString()))
                                            new Database(LoginActivity.this).writeProperty(Property.Type.ENCRYPTED_PASSWORD, PasswordView.getText().toString());
                                        Toast.makeText(LoginActivity.this, "Online Login", Toast.LENGTH_LONG).show();
                                        Values.Password = PasswordView.getText().toString();
                                    }
                                });
                                continueLogin = false;
                                goToBaseActivity();
                            } catch (SASLErrorException e) {
                                if (Objects.equals(e.getMessage(), "SASLError using SCRAM-SHA-1: bad-auth")) {
                                    try {
                                        System.out.println("InvalidDatabaseOrServerCredentials");
                                        throw new InvalidDatabaseOrServerCredentials(e);
                                    } catch (InvalidDatabaseOrServerCredentials invalidDatabaseOrServerCredentials) {
                                        Local.wipeAllData(LoginActivity.this);
                                        invalidDatabaseOrServerCredentials.printStackTrace();
                                    }
                                }
                                e.printStackTrace();
                                System.out.println(e.toString());
                            } catch (SmackException | XMPPException | IOException e) {
                                e.printStackTrace();
                                System.out.println(e.toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                                System.out.println(e.toString());
                            }
                        } else {
                            AttemptToLogin = false;
                            System.out.println("Login failed.");
                        }
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (continueLogin)
                                tryOfflineLogin();
                            else {
                                stopLoader();
                                continueLogin = true;
                            }
                        }
                    });
                }
            }).start();

    }

    private void tryOfflineLogin()
    {
        System.out.println("trying offline login");
        if (database.offlineLogin(password))
        {
            goToBaseActivity();
            Toast.makeText(this, "Offline Login", Toast.LENGTH_LONG).show();
            Values.Password = PasswordView.getText().toString();
        }
        else {
            stopLoader();
            addFailedAttempt();
        }
    }

    private void addFailedAttempt() {
        falseLoginCount++;
        if (falseLoginCount >= 5) {
            Local.wipeAllData(this);
        } else if (falseLoginCount >= 3)
        {
            login.setEnabled(false);
            errorMessage.setVisibility(View.VISIBLE);
            new CountDownTimer(30000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    errorMessage.setText("Password incorrect.\n Please wait "+millisUntilFinished/1000+" seconds.");
                }

                @Override
                public void onFinish() {
                    errorMessage.setVisibility(View.GONE);
                    errorMessage.setText("Password incorrect.\n Please wait 30 seconds.");
                    login.setEnabled(true);
                }
            }.start();
        }
    }

    private void goToBaseActivity()
    {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //TODO: Don't add login to back
                PasswordView.setText("");
                database.writeProperty(Property.Type.AUTO_LOCK_TIME, String.valueOf(SystemClock.elapsedRealtime()));
                Intent goToBaseActivity = new Intent(loginActivity, BaseActivity.class);
                PgpStarter.password = password;
                startActivity(goToBaseActivity);
            }
        });
    }

    public void onClick_Cancel(View view) {
        continueLogin = false;
    }

    private void stopLoader()
    {
        containerLoader.setVisibility(View.GONE);
        if (loader.getBackground() instanceof AnimationDrawable)
            ((AnimationDrawable) loader.getBackground()).stop();

        Container.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        SplashActivity.isApplicationRunning = false;
        super.onPause();
    }

    @Override
    protected void onStart() {
        SplashActivity.isApplicationRunning = true;
        super.onStart();
    }
}