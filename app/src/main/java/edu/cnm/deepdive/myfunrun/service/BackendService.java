package edu.cnm.deepdive.myfunrun.service;

import com.facebook.stetho.inspector.network.SimpleBinaryInspectorWebSocketFrame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.cnm.deepdive.myfunrun.BuildConfig;
import edu.cnm.deepdive.myfunrun.model.entity.History;
import edu.cnm.deepdive.myfunrun.model.entity.Race;
import edu.cnm.deepdive.myfunrun.model.pojo.HistoryWithDetails;
import io.reactivex.Completable;
import io.reactivex.Single;
import java.util.List;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BackendService {

  @GET("events")
  Single<List<Race>> getRaces(@Header("Authorization") String authHeader);

  @GET("events/{id}")
  Single<Race> getRace(@Header("Authorization") String authHeader, @Path("id") long id );

  @POST("events")
  Single<Race> postRace(@Header("Authorization") String authHeader, @Body Race race);

  @PUT("events/{id}")
  Single<Race> updateRace(@Header("Authorization") String authHeader, @Path("id") long id, @Body Race race );

  @DELETE("events/{id}")
  Completable deleteRace(@Header("Authorization") String authHeader, @Path("id") long id );

  @GET("histories")
  Single<List<HistoryWithDetails>> getAllHistories(@Header("Authorization") String authHeader);

  @GET("histories/{id}")
  Single<HistoryWithDetails> getHistory(@Header("Authorization") String authHeader,  @Path("id") long id );

  @POST("histories")
  Single<HistoryWithDetails> postHistory(@Header("Authorization") String authHeader, @Body HistoryWithDetails history);

  @PUT("histories/{id}")
  Single<HistoryWithDetails> putHistory(@Header("Authorization") String authHeader, @Body HistoryWithDetails history, @Path("id") long id );



  static BackendService getInstance() {
    return InstanceHolder.INSTANCE;
  }

  class InstanceHolder {

    private static final BackendService INSTANCE;

    static {
      Gson gson = new GsonBuilder()
          .excludeFieldsWithoutExposeAnnotation()
          .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
          .create();
      HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
      interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
      OkHttpClient client = new OkHttpClient.Builder()
          .addInterceptor(interceptor)
          .build();
      Retrofit retrofit = new Retrofit.Builder()
          .addConverterFactory(GsonConverterFactory.create(gson))
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .client(client)
          .baseUrl(BuildConfig.BASE_URL)
          .build();
      INSTANCE = retrofit.create(BackendService.class);

    }
  }
}
