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
 * interface MahasiswaService akan memetakan URL-URL web API
 * ke method-method yang dapat digunakan untuk mengakses URL-URL tersebut
 */

public interface MahasiswaService {
    // untuk mengambil semua record data mahasiswa
    @GET("mahasiswa/all")
    Call<List<Mahasiswa>> getAllMhs();

    // mengambil data mahasiswa berdasarkan NIM
    @GET("mahasiswa/{nim}")
    Call<Mahasiswa> getMhsByNim(@Path("nim") String nim);

    // menambahkan data mahasiswa
    @Headers("Content-Type: application/json")
    @POST("mahasiswa/new")
    Call<ResponseBody> createMhs(@Body Mahasiswa mahasiswa);

    // mengupdate data mahasiswa
    @Headers("Content-Type: application/json")
    @PUT("mahasiswa/update/{old_nim}")
    Call<ResponseBody> updateMhs(@Path("old_nim") String oldNim, @Body Mahasiswa mahasiswa);

    // menghapus data mahasiswa
    @DELETE("mahasiswa/{nim}")
    Call<ResponseBody> deleteMhs(@Path("nim") String nim);
}
