package  com.isl.assetManagement.api
import FetchDeviceIDRequest
import com.isl.assetManagement.responses.*
import com.isl.assetManagement.room.entity.*
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface IApiRequest {

    @POST("realms/MAST/protocol/openid-connect/token")
    @FormUrlEncoded
    fun getAuthToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("grant_type") grantType: String,
        @Field("username") username: String,
        @Field("password") password: String
    ): Call<TokenResponse>

    @POST("rest-api/v1/movement/1/users/deviceId")
    fun fetchDeviceID(
        @Header("Authorization") authHeader: String,
        @Body body: FetchDeviceIDRequest
    ): Observable<FetchUserIdResponse>

    @GET("1/configdata/LOV")
    fun paramData(
        @Query("paramType") paramType: String?,
    ): Call<List<ParamTypeResponse>>


    @GET("rest-api/v1/movement/{userId}/tasks/summary")
    fun getTaskSummary(
        @Header("Authorization") authHeader: String,
        @Path("userId") userId: String,
        @Query("requestId") requestId: String?,
        @Query("requestStatus") requestStatus: String?,
        @Query("fromLocation") fromLocation: String?,
        @Query("toLocation") toLocation: String?,
        @Query("fromDate") fromDate: String?,
        @Query("toDate") toDate: String?
    ): Call<List<TaskSummaryResponse>>



    @GET("rest-api/v1/movement/{userId}/tasks")
    fun fetchAssetRequest(
        @Header("Authorization") authHeader: String,
        @Path("userId") userId: String,
        @Query("requestId") requestId: String?,
        @Query("requestFlag") requestFlag: String?,
        @Query("requestStatus") requestStatus: String?,
        @Query("fromLocation") fromLocation: String?,
        @Query("toLocation") toLocation: String?,
        @Query("fromDate") fromDate: String?,
        @Query("toDate") toDate: String?
    ): Call<List<AssetRequestResponse>>


    @GET("api/service/get/configuration")
    fun fetchConfiguration(@Query("moduleName") moduleName: String,
                           @Query("configName") configName: String
    ): Call<List<LevelResponse>>


    @GET("rest-api/v1/movement/{userId}/tasks/{requestId}/detail")
    fun fetchTaskDetail(
        @Header("Authorization") authHeader: String,
        @Path("userId") userId: String,
        @Path("requestId") requestId: String
    ): Call<TaskDetailResponce>


    @GET("rest-api/v1/site/{userId}/assets/list")
    suspend fun fetchAssetDetails(
        @Header("Authorization") authHeader: String,
        @Path("userId") userId: String,
        @Query("siteId") siteId: String,
        @Query("assetId") assetId: String?,
        @Query("qrCode") qrCode: String?
    ): Response<List<AssetDetailsResponse>> // Must use Response<> for suspend
}


