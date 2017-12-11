package id.web.ardhi.mahasiswaapp;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import id.web.ardhi.mahasiswaapp.model.Mahasiswa;
import id.web.ardhi.mahasiswaapp.service.MahasiswaService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InputMahasiswaActivity extends AppCompatActivity {
    // EditText untuk memasukkan data mahasiswa
    EditText etNim, etNama, etAlamat;

    // mahasiswaService adalah instansiasi dari interface MahasiswaService
    MahasiswaService mahasiswaService;

    // variabel isEdit digunakan sebagai flag untuk menandai proses edit data atau input data
    boolean isEdit = false;

    // variabel oldMahasiswa digunakan untuk menampung data mahasiswa yang asli sebelum diedit
    Mahasiswa oldMahasiswa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_mahasiswa);

        etNim = (EditText) findViewById(R.id.etNim);
        etNama = (EditText) findViewById(R.id.etNama);
        etAlamat = (EditText) findViewById(R.id.etAlamat);

        // membuat object Retrofit, memasukkan URL web API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // instansiasi MahasiswaService
        mahasiswaService = retrofit.create(MahasiswaService.class);

        // memuat data mahasiswa yang akan diedit
        loadDataMhs();
    }

    private void loadDataMhs(){
        // mengambil data mahasiswa yang akan diedit
        // data mahasiswa yang akan diedit dikirim menggunakan Intent dari Activity lain
        oldMahasiswa = getIntent().getParcelableExtra("data-mahasiswa");

        // jika Intent berisi data mahasiswa yang akan diedit maka EditText diisi
        if (oldMahasiswa != null){
            etNim.setText(oldMahasiswa.getNim());
            etNama.setText(oldMahasiswa.getNama());
            etAlamat.setText(oldMahasiswa.getAlamat());

            // flag isEdit diset ke true untuk mendai proses edit data
            isEdit = true;
        }
    }

    // method yang dieksekusi ketika tombol simpan diklik
    public void saveMhs(View view) {
        // mengambil data yang diinputkan di EditText
        String nim = etNim.getText().toString();
        String nama = etNama.getText().toString();
        String alamat = etAlamat.getText().toString();

        // membuat object Mahasiswa dari data yang diinputkan di EditText
        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setNim(nim);
        mahasiswa.setNama(nama);
        mahasiswa.setAlamat(alamat);

        if (!isEdit) {
            // jika nilai variabel isEdit false
            // yang dilakukan adalah menyimpan data mahasiswa
            simpanMhsBaru(mahasiswa);
        } else {
            // jika isEdit true, yang dilakukan adalah proses mengedit data
            updateDataMhs(oldMahasiswa.getNim(), mahasiswa);
        }
    }

    // mengupdate data mahasiswa
    private void updateDataMhs(String oldNim, Mahasiswa mahasiswa){
        // memanggil API untuk mengupdate data
        Call<ResponseBody> mahasiswaCall = mahasiswaService.updateMhs(oldNim, mahasiswa);
        mahasiswaCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        String responseStatus = response.body().string();

                        if (!responseStatus.equals("")) {
                            // parsing response yang didapat dari API, mengambil status code
                            JSONObject jsonStatus = new JSONObject(responseStatus);
                            String status = jsonStatus.getString("status");
                            if (status.equals("200")) {
                                // jika didapat code 200, proses update berhasil
                                Toast.makeText(InputMahasiswaActivity.this,
                                        "berhasil mengupdate data mahasiswa",
                                        Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                // jika didapat code selain 200, artinya proses update gagal
                                Toast.makeText(InputMahasiswaActivity.this,
                                        "gagal mengupdate data mahasiswa, error code: " + status,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(InputMahasiswaActivity.this, "gagal menambahkan data mahasiswa",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(InputMahasiswaActivity.this,
                        "gagal mengupdate data mahasiswa " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // menyimpan data mahasiswa
    private void simpanMhsBaru(Mahasiswa mahasiswa){
        // memanggil API untuk menyimpan data
        Call<ResponseBody> saveMahasiswa = mahasiswaService.createMhs(mahasiswa);
        saveMahasiswa.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        String responseStatus = response.body().string();

                        if (!responseStatus.equals("")) {
                            // parsing response yang didapat dari API, mengambil status code
                            JSONObject jsonStatus = new JSONObject(responseStatus);
                            String status = jsonStatus.getString("status");
                            if (status.equals("200")) {
                                // jika didapat code 200, proses update berhasil
                                Toast.makeText(InputMahasiswaActivity.this,
                                        "berhasil menambahkan data mahasiswa",
                                        Toast.LENGTH_LONG).show();
                                sendMessage();
                                finish();
                            } else {
                                // jika didapat code selain 200, artinya proses update gagal
                                Toast.makeText(InputMahasiswaActivity.this,
                                        "gagal menambahkan data mahasiswa, error code: " + status,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        Toast.makeText(InputMahasiswaActivity.this,
                                "gagal menambahkan data mahasiswa",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(InputMahasiswaActivity.this,
                        "gagal menambahkan data mahasiswa " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }

        });
    }

    // menutup Activity ketika tombol batal diklik
    public void batal(View view){
        finish();
    }

    private void sendMessage() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("Data Mhs Event");
        // You can also include some extra data.
        intent.putExtra("Message", "Data Mhs Updated");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
