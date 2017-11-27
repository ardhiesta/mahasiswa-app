package id.web.ardhi.mahasiswaapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
    EditText etNim, etName, etAlamat;
    MahasiswaService kuliahService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_mahasiswa);

        etNim = (EditText) findViewById(R.id.etNim);
        etName = (EditText) findViewById(R.id.etNama);
        etAlamat = (EditText) findViewById(R.id.etAlamat);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        kuliahService = retrofit.create(MahasiswaService.class);
    }

    public void saveMhs(View view) {
        String nim = etNim.getText().toString();
        String nama = etName.getText().toString();
        String alamat = etAlamat.getText().toString();

        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setNim(nim);
        mahasiswa.setNama(nama);
        mahasiswa.setAlamat(alamat);

        Call<ResponseBody> saveMahasiswa = kuliahService.createMhs(mahasiswa);

        saveMahasiswa.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.body() != null) {
                        String responseStatus = response.body().string();
                        System.out.println(responseStatus);

                        if (!responseStatus.equals("")) {
                            JSONObject jsonStatus = new JSONObject(responseStatus);
                            String status = jsonStatus.getString("status");
                            if (status.equals("200")) {
                                Toast.makeText(InputMahasiswaActivity.this, "berhasil menambahkan data mahasiswa", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(InputMahasiswaActivity.this, "gagal menambahkan data mahasiswa, error code: " + status,
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
//                System.out.println(response.message());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(InputMahasiswaActivity.this, "gagal menambahkan data mahasiswa " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

//            @Override
//            public void onResponse(Call<Mahasiswa> call, Response<Mahasiswa> response) {
//                Toast.makeText(InputMahasiswaActivity.this, "berhasil menambahkan data mahasiswa", Toast.LENGTH_LONG).show();
//            }
//
//            @Override
//            public void onFailure(Call<Mahasiswa> call, Throwable t) {
//                Toast.makeText(InputMahasiswaActivity.this, "gagal menambahkan data mahasiswa "+t.getMessage(), Toast.LENGTH_LONG).show();
//            }
        });
    }
}
