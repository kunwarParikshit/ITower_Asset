package com.isl.api;

import com.google.gson.JsonObject;
import com.isl.audit.model.AssetListResult;
import com.isl.audit.model.AuditAssetResponse;
import com.isl.audit.model.AuditListResponse;
import com.isl.audit.model.AuditListResult;
import com.isl.audit.model.PerformAuditRequest;
import com.isl.audit.model.PerformAuditResponse;
import com.isl.audit.model.RescheduleResultModel;
import com.isl.audit.model.UploadImageResponse;
import com.isl.incident.GetRttsFoultAreaList;
import com.isl.modal.PmFieldData;
import com.isl.modal.SiteLockResponce;
import com.isl.preventive.GetTxnListResponce;
import com.isl.userTracking.userttracking.RequestModel.ParentModel;
import com.isl.userTracking.userttracking.RequestModel.ResponceUserTracking;
import com.isl.workflow.modal.request.SavePersonRequest;
import com.isl.workflow.modal.request.SearchPersonRequest;
import com.isl.workflow.modal.responce.AccessTokenResponce;
import com.isl.workflow.modal.responce.GetSerachKeyResponse;
import com.isl.workflow.modal.responce.KeyDetailsResponce;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface IApiRequest {
/*@POST("user/v4_register")
    Call<SignUpResponseModel> signUp(@Body SignUpRequest signUpRequest);*/

    //@GET("api/audit/list")
    @GET("audit/list")
    Call<List<AuditListResult>> getAuditList(@Query("bucket") String bucket, @Query("status") String status, @Query("assigneeCheck") String assigneeCheck);

    @GET("auditReport/getAuditReschData")
    Call<List<RescheduleResultModel>> getRescheuleData(@Query("txnid") int txnid, @Query("status") String status);


    @GET("audit/list")
    Call<AuditListResponse> getAuditList();

    @GET("assetTypes/detail")
    Call<List<AssetListResult>> getAssetTypesList();

    @GET("siteaudit/mapping")
    Call<List<AuditAssetResponse>> getAuditAssetList();

   /* @POST("api/audit/perform")
    Call<PerformAuditResponse> performAudit(@Body PerformAuditRequest performAuditRequest);*/

    @POST()
    Call<PerformAuditResponse> performAudit(@Url String url, @Body PerformAuditRequest performAuditRequest);

    @Multipart
    @POST("media/upload/v2")
    Call<UploadImageResponse> uploadImage(@Part MultipartBody.Part file, @Part("module") RequestBody module);

   /* @FormUrlEncoded
    @Headers("Content-Type:application/json")
    @POST("http://49.205.178.202:5300/changeRequestList")
    Call<MyRequestResponse> getMyRequestList(@Field("userGrpLvl") String userGrpLvl,@Field("loginId") String loginId,

                                            @Field("rptType") String rptType,@Field("src") String src);*/

    //Api call for manual field enable disable by Avdhesh
    @GET("api/PM/GetManualPMFieldconfigData")
    Call<List<PmFieldData>> GetManualPMFieldconfigData(@Query("userrole") int roleId,
                                                       @Query("opr") String opr,
                                                       @Query("formId") int formId);

    //Api call for Save User Tracking by Avdhesh
    @FormUrlEncoded
    @POST("api/Service/SaveTechDetails")
    Call<String> getApiSaveMobileInfo(@Field("formData") String saveUserTrackingRequest);


    @POST("data-adaptor/device/pushdata")
    Call<ResponceUserTracking> getApiSaveMobileInfo1(@Body ParentModel saveUserTrackingRequest);


    @GET("/api/iMaintain/GetRttsFaultArea")
    Call<List<GetRttsFoultAreaList>> GetRttsFaultAreaData(@Query("flag") String flag,
                                                          @Query("requestType") String requestType,
                                                          @Query("parentId") String parentId);

    @GET("/api/Service/GetDataIproject")
    Call<GetTxnListResponce> GetPMFildsData(@Query("txnId")  int getTxnList);

    @GET("/api/Service/CheckDiffBtwPM")
    Call<String> getMessgae(@Query("siteId") String site_id,
                                        @Query("activityTypeId") String txn_id);

    @GET("/getSmartLocks")
    Call<SiteLockResponce> getSitelockAbloyID(@Query("siteId") String siteId,
                                              @Query("ownerName") String ownerName,
                                              @Query("userType") String userType,
                                              @Header("Authorization") String token);

    @GET("/sites/v2/getSmartLocks")
    Call<ResponseBody> getSmartLocks(@Query("siteId") String siteId,
                                     @Query("ownerName") String ownerName,
                                     @Query("userType") String userType,
                                     @Header("Authorization") String token);

    @GET("/api/Service/getOwnerName")
    Call<SiteLockResponce> getOwnerName(@Query("impuserId") String impuserId);

    @Headers({
            "Authorization: Basic a0s3eWp4Nzk2YnA1Q0cyRmRuSktPZXNkcEpyazJPalc6VDRBNUhHZU5mUmNCQVBiZA==",
            "Content-Type: application/x-www-form-urlencoded",
            "Cookie: BIGipServerPool-APIGEE-9502=206640138.7717.0000; BIGipServerPool-APIGEE-9502=2552108554.7717.0000"
    })
    @FormUrlEncoded
    @POST("/generate-access-token")
    Call<AccessTokenResponce> genrateAccessToken(@Field("grant_type") String grantType);

    @Headers({
            "accept: application/json",
            "Content-Type: application/json",
            "Cookie: BIGipServerPool-APIGEE-9502=2568885770.7717.0000"
    })
    @POST("/smartaccesslock/query/v2/searchKeys")
    Call<GetSerachKeyResponse> getSearchkeysMaking(@Body RequestBody body,
                                                   @Header("Authorization") String token);

    @Headers({
            "accept: */*",
            "Cookie: BIGipServerPool-APIGEE-9502=2568885770.7717.0000"
    })
    @GET("smartaccesslock/query/v2/keyDetails")
    Call<KeyDetailsResponce> getKeyDetails(@Query("keyIdentity") String keyIdentity,
                                           @Header("Authorization") String token);
    @Headers({
            "accept: */*",
            "Content-Type: application/json",
            "Cookie: BIGipServerPool-APIGEE-9502=2568885770.7717.0000"
    })
    @POST("smartaccesslock/work/v2/handOutKey")
    Call<KeyDetailsResponce> getHangoutKey(@Body RequestBody handOutKeyRequest,
                                           @Header("Authorization") String token);



    @Headers({
            "Content-Type: application/json",
            "Cookie: BIGipServerPool-APIGEE-9502=2568885770.7717.0000"
    })
    @POST("smartaccesslock/query/v2/searchCylinders")
    Call<ResponseBody> searchCylinders(@Header("Authorization")String token, @Body RequestBody body);


    @Headers({
            "accept: */*",
            "Content-Type: application/json",
            "Cookie: BIGipServerPool-APIGEE-9502=2568885770.7717.0000"
    })
    @POST("smartaccesslock/work/v2/updateKeyCylinderAuth")
    Call<KeyDetailsResponce> getUpdateKeyCylinder(@Body RequestBody keyIdentty,
                                                  @Header("Authorization")String s);
    @Headers({
            "Content-Type: application/json",
            "Cookie: BIGipServerPool-APIGEE-9502=2568885770.7717.0000"
    })
    @POST("smartaccesslock/work/v2/updateKeyValidity")
    Call<ResponseBody > getUpdateKeyValidate(@Body RequestBody body,
                                                  @Header("Authorization")String s);

    @GET("smartaccesslock/work/v2/handInKey")
    @Headers({
            "accept: */*",
            "Cookie: BIGipServerPool-APIGEE-9502=2552108554.7717.0000"
    })
    Call<KeyDetailsResponce> getHandileKey(@Query("keyIdentity") String keyIdentity,
                                           @Header("Authorization") String token);

    @POST("smartaccesslock/import/v2/savePersons")
    Call<KeyDetailsResponce> getSavePerson(@Body SavePersonRequest savePersonRequest,
                                           @Header("Authorization") String token);

    @POST("smartaccesslock/query/v2/searchPersons")
    Call<KeyDetailsResponce> getSearchPerson(@Body SearchPersonRequest keyIdentty,
                                             @Header("Authorization")String s);

    @GET("api/iMaintain/GetPriority?")
    Call<ResponseBody> getPriorityData(@Query("siteId") String siteId,@Query("severity") String severity,@Query("serviceImpact") String serviceImpact);

    @POST("api/iMaintain/TTAckRejDGAssign")
    Call<ResponseBody> TTAckRejDGAssign(@Body JsonObject object);

    @POST("api/Service/GetToken")
    Call<ResponseBody> getToken(@Body JsonObject object);

    @POST("api/Service/saveAccessChecklist")
    Call<ResponseBody> saveAccessChecklist(@Body JsonObject object);


}