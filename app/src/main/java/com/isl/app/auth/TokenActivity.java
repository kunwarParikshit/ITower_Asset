/*
 * Copyright 2015 The AppAuth for Android Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.isl.app.auth;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.isl.api.IApiRequest;
import com.isl.api.RetrofitApiClient;
import com.isl.dao.cache.AppPreferences;
import com.isl.constant.AppConstants;
import com.isl.constant.WebMethods;
import com.isl.dao.DataBaseHelper;
import com.isl.itower.ValidateUDetails;
import com.isl.itower.HomeActivity;
import com.isl.itower.Version;
import com.isl.modal.ResponceLoginList;
import com.isl.util.HttpUtils;
import com.isl.util.NetworkManager;
import com.isl.util.Utils;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceDiscovery;
import net.openid.appauth.ClientAuthentication;
import net.openid.appauth.TokenRequest;
import net.openid.appauth.TokenResponse;

import org.apache.http.NameValuePair;
import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import infozech.itower.R;
import okio.Okio;

/**
 * Displays the authorized state of the user. This activity is provided with the outcome of the
 * authorization flow, which it uses to negotiate the final authorized state,
 * by performing an authorization code exchange if necessary. After this, the activity provides
 * additional post-authorization operations if available, such as fetching user info and refreshing
 * access tokens.
 */
public class TokenActivity extends AppCompatActivity {
    AppPreferences mAppPreferences;
    ResponceLoginList response = null;
    PackageInfo pInfo = null;
    String mobileVersion = "",iemi="";
    String[] dataTypeID, timeStamp;
    String[] tmpDataTS = new String[2];

    private static final String TAG = "TokenActivity";

    private static final String KEY_USER_INFO = "userInfo";

    private AuthorizationService mAuthService;
    private AuthStateManager mStateManager;
    private final AtomicReference<JSONObject> mUserInfoJson = new AtomicReference<>();
    private ExecutorService mExecutor;
    private Configuration mConfiguration;
    String userPrincipalName = "",displayName = "",TokenID = "",ssoLoginId;
    NetworkManager networkManager; //108

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAppPreferences = new AppPreferences(TokenActivity.this);
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        networkManager = new NetworkManager(); //108
        mobileVersion = Build.VERSION.RELEASE;



        mStateManager = AuthStateManager.getInstance(this);
        mExecutor = Executors.newSingleThreadExecutor();
        mConfiguration = Configuration.getInstance(this);

        Configuration config = Configuration.getInstance(this);
        if (config.hasConfigurationChanged()) {
            Toast.makeText(
                    this,
                    "Configuration change detected",
                    Toast.LENGTH_SHORT)
                    .show();
            signOut();
            return;
        }

        mAuthService = new AuthorizationService(
                this,
                new AppAuthConfiguration.Builder()
                        .setConnectionBuilder(config.getConnectionBuilder())
                        .build());

        setContentView(R.layout.activity_token);



        displayLoading("Restoring state...");

        if (savedInstanceState != null) {
            try {
                mUserInfoJson.set(new JSONObject(savedInstanceState.getString(KEY_USER_INFO)));
            } catch (JSONException ex) {
                Log.e(TAG, "Failed to parse saved user info JSON, discarding", ex);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mExecutor.isShutdown()) {
            mExecutor = Executors.newSingleThreadExecutor();
        }

        if (mStateManager.getCurrent().isAuthorized()) {
            displayAuthorized();
            fetchUserInfo();
            return;
        }

        // the stored AuthState is incomplete, so check if we are currently receiving the result of
        // the authorization flow from the browser.
        AuthorizationResponse response = AuthorizationResponse.fromIntent(getIntent());
        AuthorizationException ex = AuthorizationException.fromIntent(getIntent());

        if (response != null || ex != null) {
            mStateManager.updateAfterAuthorization(response, ex);
        }

        if (response != null && response.authorizationCode != null) {
            // authorization code exchange is required
            mStateManager.updateAfterAuthorization(response, ex);
            exchangeAuthorizationCode(response);
        } else if (ex != null) {
            displayNotAuthorized("Authorization flow failed: " + ex.getMessage());
        } else {
            displayNotAuthorized("No authorization state retained - reauthorization required");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        // user info is retained to survive activity restarts, such as when rotating the
        // device or switching apps. This isn't essential, but it helps provide a less
        // jarring UX when these events occur - data does not just disappear from the view.
        if (mUserInfoJson.get() != null) {
            state.putString(KEY_USER_INFO, mUserInfoJson.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuthService.dispose();
        mExecutor.shutdownNow();
    }

    @MainThread
    private void displayNotAuthorized(String explanation) {
        findViewById(R.id.not_authorized).setVisibility( View.VISIBLE);
        findViewById(R.id.authorized).setVisibility( View.GONE);
        findViewById(R.id.loading_container).setVisibility( View.GONE);

        ((TextView)findViewById(R.id.explanation)).setText(explanation);
        findViewById(R.id.reauth).setOnClickListener((View view) -> signOut());
    }

    @MainThread
    private void displayLoading(String message) {
        findViewById(R.id.loading_container).setVisibility( View.VISIBLE);
        findViewById(R.id.authorized).setVisibility( View.GONE);
        findViewById(R.id.not_authorized).setVisibility( View.GONE);

        ((TextView)findViewById(R.id.loading_description)).setText(message);
    }

    @MainThread
    private void displayAuthorized() {
        findViewById(R.id.authorized).setVisibility( View.VISIBLE);
        findViewById(R.id.not_authorized).setVisibility( View.GONE);
        findViewById(R.id.loading_container).setVisibility( View.GONE);

        AuthState state = mStateManager.getCurrent();

        TextView refreshTokenInfoView = (TextView) findViewById(R.id.refresh_token_info);
        refreshTokenInfoView.setText((state.getRefreshToken() == null)
                ? R.string.no_refresh_token_returned
                : R.string.refresh_token_returned);

        TextView idTokenInfoView = (TextView) findViewById(R.id.id_token_info);
        idTokenInfoView.setText((state.getIdToken()) == null
                ? R.string.no_id_token_returned
                : R.string.id_token_returned);
        //Code by Avdhesh
      /*  if (state.getIdToken()!=null){
            mAppPreferences.setTokenID(state.getIdToken());
        }
*/
        TextView accessTokenInfoView = (TextView) findViewById(R.id.access_token_info);
        if (state.getAccessToken() == null) {
            accessTokenInfoView.setText(R.string.no_access_token_returned);
        } else {
            Long expiresAt = state.getAccessTokenExpirationTime();
            if (expiresAt == null) {
                accessTokenInfoView.setText(R.string.no_access_token_expiry);
            } else if (expiresAt < System.currentTimeMillis()) {
                accessTokenInfoView.setText(R.string.access_token_expired);
            } else {
                String template = getResources().getString(R.string.access_token_expires_at);
                accessTokenInfoView.setText( String.format(template,
                        DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss ZZ").print(expiresAt)));
            }
        }

        TextView refreshTokenButton = (TextView) findViewById(R.id.refresh_token);
        refreshTokenButton.setVisibility(state.getRefreshToken() != null
                ? View.VISIBLE
                : View.GONE);
        refreshTokenButton.setOnClickListener((View view) -> refreshAccessToken());

        TextView viewProfileButton = (TextView) findViewById(R.id.view_profile);

        AuthorizationServiceDiscovery discoveryDoc =
                state.getAuthorizationServiceConfiguration().discoveryDoc;
        if ((discoveryDoc == null || discoveryDoc.getUserinfoEndpoint() == null)
                && mConfiguration.getUserInfoEndpointUri() == null) {
            viewProfileButton.setVisibility( View.GONE);
        } else {
            viewProfileButton.setVisibility( View.VISIBLE);
            viewProfileButton.setOnClickListener((View view) -> fetchUserInfo());
            //viewProfileButton.setOnClickListener((View view) -> signOut());


        }
        TextView sign_out = (TextView) findViewById(R.id.sign_out);
        sign_out.setOnClickListener((View view) -> signOut());
        sign_out.setVisibility(View.GONE);

        View userInfoCard = findViewById(R.id.userinfo_card);
        JSONObject userInfo = mUserInfoJson.get();
        if (userInfo == null) {
            userInfoCard.setVisibility( View.GONE);
        } else {
            try {


                if(getApplicationContext().getPackageName().equalsIgnoreCase("infozech.tawal")){
                    if (userInfo.has("preferred_username")) {
                        userPrincipalName = userInfo.getString("preferred_username");
                    }
                    Log.d("preferred_username",""+userPrincipalName);
                    if (userInfo.has("id_token")) {
                        TokenID = userInfo.getString("id_token");
                       // mAppPreferences.setTokenID(TokenID);
                        Log.d("TokenIDAvdhesh",""+TokenID);
                    }

                    if (userInfo.has("displayName")) {
                        displayName = userInfo.getString("displayName");
                    }else{
                        displayName = userInfo.getString("preferred_username");
                    }
                }else{
                    if (userInfo.has("userPrincipalName")) {
                        userPrincipalName = userInfo.getString("userPrincipalName");
                    }

                    if (userInfo.has("displayName")) {
                        displayName = userInfo.getString("displayName");
                    }
                }



                ((TextView) findViewById(R.id.userinfo_name)).setText(userPrincipalName);
                ((TextView) findViewById(R.id.userinfo_json)).setText(mUserInfoJson.toString());
                userInfoCard.setVisibility( View.GONE);

                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        String deviceToken = task.getResult();
                        mAppPreferences.setGCMRegistationId(deviceToken);
                    }
                });
                //mAppPreferences.setGCMRegistationId( FirebaseMessaging.getInstance().getToken().toString());
             //   int permission = PermissionChecker.checkSelfPermission(TokenActivity.this, Manifest.permission.READ_PHONE_STATE);
              /*  if (permission == PermissionChecker.PERMISSION_GRANTED) {
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    if(Build.VERSION.SDK_INT<28) {
                        iemi = telephonyManager.getDeviceId();
                    }else{

                    }
                }*/
                iemi = "";
                if (Utils.isNetworkAvailable(TokenActivity.this)) {
                    RetrofitApiClient.init(IApiRequest.class,getApplicationContext());
                    //108
                    networkManager.getToken(new NetworkManager.TokenCallback() {
                        @Override
                        public void onTokenReceived(String token) {
                            new LoginTask(TokenActivity.this,0,userPrincipalName,displayName,token).execute();
                        }

                        @Override
                        public void onTokenError(String error) {
                            Toast.makeText(TokenActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    });
                    //108
                   // getToken();
                  //  new LoginTask(TokenActivity.this,0,userPrincipalName,displayName).execute();
                }


            } catch (JSONException ex) {
                Log.e(TAG, "Failed to read userinfo JSON", ex);
            }
        }

        return;
    }

    @MainThread
    private void refreshAccessToken() {
        displayLoading("Refreshing access token");
        performTokenRequest(
                mStateManager.getCurrent().createTokenRefreshRequest(),
                this::handleAccessTokenResponse);
    }

    @MainThread
    private void exchangeAuthorizationCode(AuthorizationResponse authorizationResponse) {
        displayLoading("Exchanging authorization code");
        performTokenRequest(
                authorizationResponse.createTokenExchangeRequest(),
                this::handleCodeExchangeResponse);
    }

    @MainThread
    private void performTokenRequest(
            TokenRequest request,
            AuthorizationService.TokenResponseCallback callback) {
        ClientAuthentication clientAuthentication;
        try {
            clientAuthentication = mStateManager.getCurrent().getClientAuthentication();
        } catch (ClientAuthentication.UnsupportedAuthenticationMethod ex) {
            Log.d(TAG, "Token request cannot be made, client authentication for the token "
                    + "endpoint could not be constructed (%s)", ex);
            displayNotAuthorized("Client authentication method is unsupported");
            return;
        }

        mAuthService.performTokenRequest(
                request,
                clientAuthentication,
                callback);
    }

    @WorkerThread
    private void handleAccessTokenResponse(
            @Nullable TokenResponse tokenResponse,
            @Nullable AuthorizationException authException) {
        mStateManager.updateAfterTokenResponse(tokenResponse, authException);
        runOnUiThread(this::displayAuthorized);
    }

    @WorkerThread
    private void handleCodeExchangeResponse(
            @Nullable TokenResponse tokenResponse,
            @Nullable AuthorizationException authException) {

        mStateManager.updateAfterTokenResponse(tokenResponse, authException);
        if (!mStateManager.getCurrent().isAuthorized()) {
            final String message = "Authorization Code exchange failed"
                    + ((authException != null) ? authException.error : "");

            // WrongThread inference is incorrect for lambdas
            //noinspection WrongThread
            runOnUiThread(() -> displayNotAuthorized(message));
        } else {
            runOnUiThread(this::displayAuthorized);
        }
    }

    /**
     * Demonstrates the use of  to retrieve
     * user info from the IDP's user info endpoint. This callback will negotiate a new access
     * token / id token for use in a follow-up action, or provide an error if this fails.
     */
    @MainThread
    private void fetchUserInfo() {
        displayLoading("Fetching user info");
        mStateManager.getCurrent().performActionWithFreshTokens(mAuthService, this::fetchUserInfo);
    }

    @MainThread
    private void fetchUserInfo(String accessToken, String idToken, AuthorizationException ex) {
        if (ex != null) {
            Log.e(TAG, "Token refresh failed when fetching user info");
            mUserInfoJson.set(null);
            runOnUiThread(this::displayAuthorized);
            return;
        }

        AuthorizationServiceDiscovery discovery =
                mStateManager.getCurrent()
                        .getAuthorizationServiceConfiguration()
                        .discoveryDoc;

        URL userInfoEndpoint;
        try {
            userInfoEndpoint =
                    mConfiguration.getUserInfoEndpointUri() != null
                            ? new URL(mConfiguration.getUserInfoEndpointUri().toString())
                            : new URL(discovery.getUserinfoEndpoint().toString());
        } catch (MalformedURLException urlEx) {
            Log.e(TAG, "Failed to construct user info endpoint URL", urlEx);
            mUserInfoJson.set(null);
            runOnUiThread(this::displayAuthorized);
            return;
        }

        mExecutor.submit(() -> {
            try {
                HttpURLConnection conn =
                        (HttpURLConnection) userInfoEndpoint.openConnection();
                conn.setRequestProperty("Authorization", "Bearer " + accessToken);
                conn.setInstanceFollowRedirects(false);

                /*BufferedSource configSource =
                        Okio.buffer( Okio.source(TokenActivity.this.getResources()
                                .openRawResource(R.raw.user_details)));
                Buffer configData = new Buffer();
                configSource.readAll(configData);
                JSONObject mConfigJson = new JSONObject(configData.readString( Charset.forName("UTF-8")));
                mUserInfoJson.set(mConfigJson);*/
                String response = Okio.buffer( Okio.source(conn.getInputStream()))
                        .readString( Charset.forName("UTF-8"));
                mUserInfoJson.set(new JSONObject(response));
            } catch (IOException ioEx) {
                Log.e(TAG, "Network error when querying userinfo endpoint", ioEx);
                showSnackbar("Fetching user info failed");
            } catch (JSONException jsonEx) {
                Log.e(TAG, "Failed to parse userinfo response");
                showSnackbar("Failed to parse user info");
            }

            runOnUiThread(this::displayAuthorized);
        });
    }

    @MainThread
    private void showSnackbar(String message) {
        Snackbar.make(findViewById(R.id.coordinator),
                message,
                Snackbar.LENGTH_SHORT)
                .show();
    }

    @MainThread
    public void signOut() {
        // discard the authorization and token state, but retain the configuration and
        // dynamic client registration (if applicable), to save from retrieving them again.
        AuthState currentState = mStateManager.getCurrent();
        AuthState clearedState =
                new AuthState(currentState.getAuthorizationServiceConfiguration());
        if (currentState.getLastRegistrationResponse() != null) {
            clearedState.update(currentState.getLastRegistrationResponse());
        }
        mStateManager.replace(clearedState);

        Intent mainIntent = new Intent(this, ValidateUDetails.class);
        mainIntent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finish();
    }

    public class LoginTask extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Context> contextRef;
        ProgressDialog pd;
        Context con;
        AppPreferences mAppPref;
        int login=0;
        String res;
        String displayName,token; //108
        public LoginTask(Context con,int login,String LoginId,String displayName,String token) {
            this.contextRef = new WeakReference<>(TokenActivity.this);
            this.con = con;
            mAppPref = new AppPreferences(TokenActivity.this);
            this.login = login;
            this.displayName = displayName;
            ssoLoginId = LoginId;
            this.token = token; //108
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(7);
                nameValuePairs.add(new BasicNameValuePair("loginId",ssoLoginId));
                nameValuePairs.add(new BasicNameValuePair("pwd", "11@"));
                nameValuePairs.add(new BasicNameValuePair("mId", "40"));
                nameValuePairs.add(new BasicNameValuePair("version",pInfo.versionName));
                String addParams = "deviceId=" + mAppPref.getGCMRegistationId()
                        + "~" + "emiNo=" + iemi + "~login="+login + "~ip="+getIPAddress()
                        +"~language="+mAppPreferences.getLanCode()
                        +"~mobileVersion="+mobileVersion
                        +"~authStatus=SSO"
                        +"~appId="+getApplicationContext().getPackageName();;
                //addParams="deviceId="+mAppPref.getGCMRegistationId()+"~"+"emiNo="+"355370060893490";//0.3
                nameValuePairs.add(new BasicNameValuePair("addParams",addParams));
                nameValuePairs.add(new BasicNameValuePair("retryCnt", "0"));
                nameValuePairs.add(new BasicNameValuePair("languageCode", "en"));
                res = Utils.httpPostRequest1(con,mAppPreferences.getConfigIP()+ WebMethods.url_Authenticate, nameValuePairs,token); //108
                response = new Gson().fromJson(res, ResponceLoginList.class);
            } catch (Exception e) {
                e.printStackTrace();
                response = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Context context = contextRef.get();
            if (context != null && !((Activity) context).isFinishing()) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                    pd = null;
                }
            }
            if (response != null
                    && response.getDetails().get(0).getSuccess()
                    .equalsIgnoreCase("S")
                    && response.getDetails().get(0).getMessage().length() == 0) {
                setLoginResponce(response,ssoLoginId,displayName);
            } else if (response != null
                    && response.getDetails().get(0).getSuccess()
                    .equalsIgnoreCase("S")
                    && response.getDetails().get(0).getMessage().length() != 0) {
                Utils.toastMsg(TokenActivity.this, response.getDetails()
                        .get(0).getMessage());
            } else if (response != null
                    && response.getDetails().get(0).getSuccess()
                    .equalsIgnoreCase("V")) {
                setLoginResponce(response,ssoLoginId,displayName);
                version();
            }else if (response != null	&& response.getDetails().get(0).getSuccess().equalsIgnoreCase("M")) {
                //WorkFlowUtils.toastMsg(ValidateUDetails.this, response.getDetails().get(0).getMessage());
                alreadyLogin(response.getDetails().get(0).getMessage(),"M",ssoLoginId,displayName);
            }else if (response != null	&& response.getDetails().get(0).getSuccess().equalsIgnoreCase("N")) {
                //WorkFlowUtils.toastMsg(ValidateUDetails.this, response.getDetails().get(0).getMessage());
                alreadyLogin1(response.getDetails().get(0).getMessage(),"N",(response.getDetails().get(0).getuId()));
             //   alreadyLogin(response.getDetails().get(0).getMessage(),"N",ssoLoginId,displayName);
            }
            else if (response != null	&& response.getDetails().get(0).getMessage().length()>0) {
                Utils.toastMsg(TokenActivity.this, response.getDetails()
                        .get(0).getMessage());
                signOut();
            }else {
                Utils.toastMsg(TokenActivity.this, "Server Not Available");
                signOut();
            }
            super.onPostExecute(result);
        }
    }


    public void setLoginResponce(ResponceLoginList response,String loginId,String displayName) {
        DataBaseHelper dbHelper = new DataBaseHelper(TokenActivity.this);
        dbHelper.open();
        mAppPreferences.setLoginId(loginId);
        mAppPreferences.setPassword("11@");
        mAppPreferences.setPopUp("show popup"); // 0.2
        mAppPreferences.setNotificationTone("default");// 0.2
        mAppPreferences.setNotificationToneName("Default Tone");// 0.2
        mAppPreferences.setTTAssignRb("off");
        mAppPreferences.setTTUpdateRb("off");
        mAppPreferences.setTTEscalateRb("off");
        mAppPreferences.setPMScheduleRb("off");
        mAppPreferences.setPMEscalateRb("off");
        mAppPreferences.setRegionId(response.getDetails().get(0).getrId());
        mAppPreferences.setRoleId(response.getDetails().get(0).getRoleId());

        //mAppPreferences.setName(displayName);
        if(response.getDetails().get(0).getName().contains("~")){
            String [] arr = response.getDetails().get(0).getName().split("~");
            mAppPreferences.setName(arr[0]);
            mAppPreferences.setUserMailid(arr[1]);
            mAppPreferences.setUserNumber(arr[2]);
            mAppPreferences.setUserGroup(arr[3]);
            mAppPreferences.setUserGroupName(arr[4]);
        }

        mAppPreferences.setLastLogin(response.getDetails().get(0).getLastLogin());
        mAppPreferences.saveUserId(String.valueOf(response.getDetails().get(0).getuId()));
        mAppPreferences.setCounrtyID(response.getDetails().get(0).getCnId());
        mAppPreferences.setHubID(response.getDetails().get(0).gethId());
        mAppPreferences.setCircleID(response.getDetails().get(0).getCrId());
        mAppPreferences.setZoneID(response.getDetails().get(0).getzId());
        mAppPreferences.setClusterID(response.getDetails().get(0).getClId());
        mAppPreferences.setPIOMEID(response.getDetails().get(0).getOmId());
        mAppPreferences.setDataTS(response.getDetails().get(0).getDataTS());

        mAppPreferences.setUserCategory(response.getDetails().get(0).getUserCategory());  //1.0
        mAppPreferences.setUserSubCategory(response.getDetails().get(0).getUserSubCategory());
        // mAppPreferences.setOperatorWiseGroup (response.getDetails().get(0).getDistanceRange());

        // PM images Configuration
        mAppPreferences.setPmConfiguration(response.getDetails().get(0).getPMConfiguration());
        String pmConfig = mAppPreferences.getPmConfiguration();
        if (pmConfig.length() != 0) {
            String baseArray[] = pmConfig.split("@");
            int activityID = 0, paramID = 0;
            String paramName = "";
            dbHelper.clearpmconfig();
            if (baseArray.length > 0) {
                for (int i = 0; i < baseArray.length; i++) {
                    String tmpArray[] = baseArray[i].split("\\$");
                    activityID = Integer.parseInt(tmpArray[0].toString());
                    tmpArray = tmpArray[1].split("\\#");
                    paramID = Integer.parseInt(tmpArray[0].toString());
                    paramName = tmpArray[1].toString();
                    dbHelper.insertPmConfiguration(activityID, paramID,paramName);
                }
            }
        }

        // TT Configuration
        mAppPreferences.setTTConfiguration(response.getDetails().get(0).getTtconfiguration());
        String ttConfig = mAppPreferences.getTTConfiguration();
        if (ttConfig.length() != 0) {
            String baseArray[] = ttConfig.split("@");
            if (baseArray.length > 2) {
                mAppPreferences.setTTminimage(Integer.parseInt(baseArray[0]));
                mAppPreferences.setTTmaximage(Integer.parseInt(baseArray[1]));
                mAppPreferences.setTTimageMessage(baseArray[2]);
                mAppPreferences.setTTMediaFileType(baseArray[3]);
            }
        }

        // FF Configuration
        mAppPreferences.setFFConfiguration(response.getDetails().get(0).getFfconfiguration());
        String ffConfig = mAppPreferences.getFFConfiguration();
        /*if (ffConfig.length() != 0) {
            String baseArray[] = ffConfig.split("@");
            if (baseArray.length > 0) {
                mAppPreferences.setFFminimage(Integer.parseInt(baseArray[0]));
                mAppPreferences.setFFmaximage(Integer.parseInt(baseArray[1]));
                mAppPreferences.setFFimageMessage(baseArray[2]);
            }
        }*/
        if (ffConfig.length() != 0) {
            String baseArray[] = ffConfig.split("@");
            if (baseArray.length > 6) {
                mAppPreferences.setApprovalRemarks(baseArray[0]);
                mAppPreferences.setIsRating(baseArray[1]);
                mAppPreferences.setPMphotoInfoRating(baseArray[2]);
                mAppPreferences.setQualityPMtaskRating(baseArray[3]);
                mAppPreferences.setVisualCheck1Rating(baseArray[4]);
                mAppPreferences.setVisualCheck2Rating(baseArray[5]);
                mAppPreferences.setVisualCheck3Rating(baseArray[6]);
            }
        }

        if (response.getDetails().get(0).getUserTracking().length() != 0) {
            mAppPreferences.setUserTracking(response.getDetails().get(0).getUserTracking());
            String[] dataUserTrack = response.getDetails().get(0).getUserTracking().split("\\~");
            mAppPreferences.setTrackingOnOff(dataUserTrack[0]);
            mAppPreferences.setUserTrackUploadTime(Utils.dateNotification());
        }

        if (response.getDetails().get(0).getDistanceRange().length() != 0) {
            String[] dataTS = response.getDetails().get(0).getDistanceRange().split("\\~");
            if (dataTS.length > 11) {
                mAppPreferences.setEnableFillingField(dataTS[0]);
                mAppPreferences.setSiteMotorable(Integer.parseInt(dataTS[1]));
                mAppPreferences.setSavePMBackgroundEnable( Integer.parseInt(dataTS[2]) );
                mAppPreferences.setSiteNameEnable(Integer.parseInt(dataTS[3])  );
                mAppPreferences.setAutoDateTime(dataTS[4]);
                mAppPreferences.setSearchTTDateRange(Integer.parseInt(dataTS[5]));
                mAppPreferences.setEnablePrePopulateSitesTT(Integer.parseInt(dataTS[6]));
                mAppPreferences.setPMImageUploadType(Integer.parseInt(dataTS[7]));
                mAppPreferences.setPMRejectMadatoryFields(Integer.parseInt(dataTS[8]));
                mAppPreferences.setPMReviewPlanDate(Integer.parseInt(dataTS[9]));
                mAppPreferences.setVideoUploadMaxSize( Integer.parseInt(dataTS[10]));
                mAppPreferences.setIsVideoCompress(Integer.parseInt(dataTS[11]));
                if (dataTS.length > 13) {
                    mAppPreferences.setOperatorWiseUserField(dataTS[12]);
                    mAppPreferences.setHyperLinkPM(dataTS[13]);
                }
                if (dataTS.length > 14) {
                    mAppPreferences.setCalendarMonth(Integer.parseInt(dataTS[14]));
                }
                if (dataTS.length > 15) {
                    mAppPreferences.setDocumentUploadMaxSize(Integer.parseInt(dataTS[15]));
                }
                if(dataTS.length>16)
                {
                    mAppPreferences.setCheckIn(String.valueOf(dataTS[16]));
                }

            }
        }
        mAppPreferences.setSrvcTime(response.getDetails().get(0).getSrvcTime());
        // mAppPreferences.setSrvcTime(120000);
        mAppPreferences.setToggleButton("yes");
        if (response.getDetails().get(0).getDataTS().length() != 0) {
            String[] dataTS = mAppPreferences.getDataTS().split(",");
            dataTypeID = new String[dataTS.length];
            timeStamp = new String[dataTS.length];
            for (int i = 0; i < dataTS.length; i++) {
                tmpDataTS = dataTS[i].split("\\~");
                dataTypeID[i] = tmpDataTS[0];
                timeStamp[i] = tmpDataTS[1];
            }
            if (!mAppPreferences.getFirstTimeRunApp().equalsIgnoreCase("A")) {
                dbHelper.clearDataTS();
                dbHelper.dataTS(dataTypeID, timeStamp, "", "", 0,"0");
                dbHelper.updateWorkFlowTimeStamp(mAppPreferences.getDataTS(),0);
                mAppPreferences.setFirstTimeRunApp("A");
            } else {
                dbHelper.dataTS(dataTypeID, timeStamp, "", "", 1,"0");
                dbHelper.updateWorkFlowTimeStamp(mAppPreferences.getDataTS(),1);
            }
        }
        if (response.getForm()!=null && response.getForm().size() > 0) {
            //scheduleAlarm(); // call service for check app version
            AppConstants.moduleList.clear();
            dbHelper.clearFormRights();
            dbHelper.insertFormRight(response.getForm());
            mAppPreferences.setLoginState(1);
            Utils.toastMsg(TokenActivity.this, "Login Successfully");
            Intent i = new Intent(TokenActivity.this, HomeActivity.class);
            startActivity(i);
            finish();
        } else {
            Utils.toastMsg(TokenActivity.this, "You are not authorized to access modules.");
            new LogoutTask(TokenActivity.this).execute();
        }
        dbHelper.close();

    }

    public class LogoutTask extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String res;
        ResponceLoginList response = null;

        public LogoutTask(Context con) {
            this.con = con;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Logging Out...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
                        1);
                nameValuePairs.add(new BasicNameValuePair("userId",
                        mAppPreferences.getUserId()));
                nameValuePairs.add(new BasicNameValuePair("languageCode", ""+ mAppPreferences.getLanCode()));
                res = Utils.httpPostRequest(con,mAppPreferences.getConfigIP()+ WebMethods.url_logout,nameValuePairs);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd !=null && pd.isShowing()) {
                pd.dismiss();
            }
            DataBaseHelper dbHelper = new DataBaseHelper(TokenActivity.this);
            dbHelper.open();
            dbHelper.clearFormRights();
            dbHelper.close();
            mAppPreferences.setLoginState(0);
            mAppPreferences.saveSyncState(0);
            signOut();

        }
    }

    public void alreadyLogin(String msg,String flag,String loginId,String displayName) {
        // open pop up for passward expired
        final Dialog actvity_dialog = new Dialog(TokenActivity.this, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.back_confirmation_alert);
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();
        Button positive = (Button) actvity_dialog.findViewById( R.id.bt_ok );
        Button negative = (Button) actvity_dialog.findViewById( R.id.bt_cancel );
        TextView title = (TextView) actvity_dialog.findViewById( R.id.tv_title );
        TextView tv_header = (TextView) actvity_dialog.findViewById( R.id.tv_header );
        tv_header.setTypeface( Utils.typeFace( TokenActivity.this ) );
        positive.setTypeface( Utils.typeFace( TokenActivity.this ) );
        negative.setTypeface( Utils.typeFace( TokenActivity.this ) );
        title.setTypeface( Utils.typeFace( TokenActivity.this ) );
        title.setText(msg);
        positive.setText("YES");
        negative.setText("NO");

        if(flag.equalsIgnoreCase("N")){
            positive.setVisibility(View.GONE);
            negative.setText("OK");
        }

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                //108
                RetrofitApiClient.init(IApiRequest.class,getApplicationContext());
                networkManager.getToken(new NetworkManager.TokenCallback() {
                    @Override
                    public void onTokenReceived(String token) {

                        new LoginTask(TokenActivity.this,0,userPrincipalName,displayName,token).execute();
                    }

                    @Override
                    public void onTokenError(String error) {
                        Toast.makeText(TokenActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
                //108
             //   new LoginTask(TokenActivity.this,1,loginId,displayName).execute();

            }
        });

        negative.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                new LogoutTask(TokenActivity.this).execute();
            }
        });
    }

    public void alreadyLogin1(String msg,String flag,String userId) { // open pop up for passward expired
        final Dialog actvity_dialog = new Dialog(TokenActivity.this, R.style.FullHeightDialog);
        actvity_dialog.requestWindowFeature( Window.FEATURE_NO_TITLE);
        actvity_dialog.getWindow().setBackgroundDrawableResource(R.color.nevermind_bg_color);
        actvity_dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        actvity_dialog.setContentView(R.layout.back_confirmation_alert);
        final Window window_SignIn = actvity_dialog.getWindow();
        window_SignIn.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.MATCH_PARENT);
        window_SignIn.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        actvity_dialog.show();
        Button positive = (Button) actvity_dialog.findViewById( R.id.bt_ok );
        Button negative = (Button) actvity_dialog.findViewById( R.id.bt_cancel );
        TextView title = (TextView) actvity_dialog.findViewById( R.id.tv_title );
        TextView tv_header = (TextView) actvity_dialog.findViewById( R.id.tv_header );
        tv_header.setTypeface( Utils.typeFace( TokenActivity.this ) );
        positive.setTypeface( Utils.typeFace( TokenActivity.this ) );
        negative.setTypeface( Utils.typeFace( TokenActivity.this ) );
        title.setTypeface( Utils.typeFace( TokenActivity.this ) );
        title.setText(msg);
        positive.setText("YES");
        negative.setText("NO");

        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();
                new LogoutAuth2(TokenActivity.this,userId).execute();
                //new LoginTask(ValidateUDetails.this, 0,token).execute();
                //new LoginTask(ValidateUDetails.this,1).execute();

            }
        });

        negative.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                actvity_dialog.cancel();

            }
        });
    }

    public class LogoutAuth2 extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String userId;
        public LogoutAuth2(Context con,String userId) {
            this.con = con;
            this.userId = userId;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show(con, null, "Loading...");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                String response = HttpUtils.httpGetRequest(WebMethods.url_logout_ad);
            } catch (Exception e) {
                e.printStackTrace();
                //auditTrailList = null;
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            //Toast.mak+eText(getActivity(),"Successfully logout", Toast.LENGTH_SHORT).show();
            //  Utils.toast( HomeActivity.this, "36" );
//            stopLocationService();
//            stopPunchInnotificatioService();
//            Intent i = new Intent( HomeActivity.this, ValidateUDetails.class );
//            i.setFlags( Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
//            startActivity( i );
//            DataBaseHelper dbHelper = new DataBaseHelper( HomeActivity.this );
//            dbHelper.open();
//            dbHelper.clearFormRights();
//            dbHelper.close();
//            mAppPreferences.setLoginState( 0 );
//            mAppPreferences.saveSyncState( 0 );
//            //mAppPreferences.setGCMRegistationId("");
//            finish();
            new LogoutTask1(TokenActivity.this,userId).execute();
        }
    }

    public class LogoutTask1 extends AsyncTask<Void, Void, Void> {
        ProgressDialog pd;
        Context con;
        String res,userId;
        ResponceLoginList response = null;


        public LogoutTask1(Context con,String userId) {
            this.con = con;
            this.userId = userId;
        }

        @Override
        protected void onPreExecute() {
            pd = ProgressDialog.show( con, null, "Logging Out..." );

            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>( 1 );
                nameValuePairs.add( new BasicNameValuePair( "userId",userId ) );
                res = Utils.httpPostRequest( con, mAppPreferences.getConfigIP() + WebMethods.url_logout, nameValuePairs );
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
            //Toast.makeText(getActivity(),"Successfully logout", Toast.LENGTH_SHORT).show();
//			stopLocationService();
//			stopPunchInnotificatioService();
            Utils.toast( TokenActivity.this, "36" );
//			Intent i = new Intent( ValidateUDetails.this, ValidateUDetails.class );
//			i.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
//			startActivity( i );
//			DataBaseHelper dbHelper = new DataBaseHelper( HomeActivity.this );
//			dbHelper.open();
//			dbHelper.clearFormRights();
//			dbHelper.close();
//			mAppPreferences.setLoginState( 0 );
//			mAppPreferences.saveSyncState( 0 );
            //mAppPreferences.setGCMRegistationId("");
            //	finish();
        }
    }

    public void version() { // check app version
        mAppPreferences.setLoginState(2);
        Intent i = new Intent(TokenActivity.this, Version.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
        finish();
    }

    public String getIPAddress()
    {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress()) ) {
                        return  inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {

        }
        return null;
    }


}
