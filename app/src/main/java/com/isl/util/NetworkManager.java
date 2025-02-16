package com.isl.util;

import com.google.gson.JsonObject;
import com.isl.api.IApiRequest;
import com.isl.api.RetrofitApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import infozech.itower.BuildConfig;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

public class NetworkManager {

    public void getToken(final TokenCallback callback) {
        // Make network request to retrieve token
        JsonObject jsonObject = new JsonObject();

       /*jsonObject.addProperty("username","SOE_RSVC");
       jsonObject.addProperty("password","Soe@202030");*/

      jsonObject.addProperty("username","SOE_RSVC");
        jsonObject.addProperty("password","Soe@202030");

    //   jsonObject.addProperty("username","amistya");
//       jsonObject.addProperty("username","Infozech");
//        jsonObject.addProperty("password","Infozech@123");

        /*<add key="TokenUserName" value="superadmin" />
        <add key="TokenPassowrd" value="tms@1234" />*/

        IApiRequest request = RetrofitApiClient.getRequest();
        if (request == null) {
          //  callback.onTokenError("IApiRequest is not initialized.");
            return;
        }
        Call<ResponseBody> call = request.getToken(jsonObject);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    if(response.isSuccessful()) {
                        String response1 = response.body().string();
                        JSONObject object = new JSONObject(response1);
                        String token = object.getString("AccessToken");
                        String message = object.getString("Duration");
                        callback.onTokenReceived(token);
                      //  new ValidateUDetails.LoginTask(ValidateUDetails.this, 0).execute();
                    }
                    else {
                        callback.onTokenError("Failed to get token: " + response.message());
                    }

                }

                catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                callback.onTokenError("Network error: " + t.getMessage());
            }
        });
    }

    // Define other utility methods for API calls

    public interface TokenCallback {
        void onTokenReceived(String token);
        void onTokenError(String error);
    }
}
