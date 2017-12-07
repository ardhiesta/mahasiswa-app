package id.web.ardhi.mahasiswaapp.service;

import java.util.List;

import id.web.ardhi.mahasiswaapp.model.Mahasiswa;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by linuxluv on 08/11/17.
 */

public interface MahasiswaService {
    @GET("mahasiswa/all")
    Call<List<Mahasiswa>> getAllMhs();

    @GET("mahasiswa/{nim}")
    Call<Mahasiswa> getMhsByNim(@Path("nim") String nim);

    @Headers("Content-Type: application/json")
    @POST("mahasiswa/new")
    Call<ResponseBody> createMhs(@Body Mahasiswa mahasiswa);

    @PUT("mahasiswa/update/{old_nim}")
    Call<ResponseBody> updateMhs(@Path("old_nim") String oldNim, @Body Mahasiswa mahasiswa);

    @DELETE("mahasiswa/{nim}")
    Call<ResponseBody> deleteMhs(@Path("nim") String nim);
}
