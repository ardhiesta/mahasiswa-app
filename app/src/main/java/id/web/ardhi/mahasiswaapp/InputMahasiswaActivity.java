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
    EditText etNim, etNama, etAlamat;
    MahasiswaService kuliahService;
    boolean isEdit = false;
    Mahasiswa oldMahasiswa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_mahasiswa);

        etNim = (EditText) findViewById(R.id.etNim);
        etNama = (EditText) findViewById(R.id.etNama);
        etAlamat = (EditText) findViewById(R.id.etAlamat);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        kuliahService = retrofit.create(MahasiswaService.class);

        loadDataMhs();
    }

    private void loadDataMhs(){
        oldMahasiswa = getIntent().getParcelableExtra("data-mahasiswa");

        if (oldMahasiswa != null){
            etNim.setText(oldMahasiswa.getNim());
            etNama.setText(oldMahasiswa.getNama());
            etAlamat.setText(oldMahasiswa.getAlamat());

            isEdit = true;
        }
    }

    public void saveMhs(View view) {
        String nim = etNim.getText().toString();
        String nama = etNama.getText().toString();
        String alamat = etAlamat.getText().toString();

        Mahasiswa mahasiswa = new Mahasiswa();
        mahasiswa.setNim(nim);
        mahasiswa.setNama(nama);
        mahasiswa.setAlamat(alamat);

        if (!isEdit) {
            simpanMhsBaru(mahasiswa);
        } else {
            updateDataMhs(oldMahasiswa.getNim(), mahasiswa);
        }
    }

    private void updateDataMhs(String oldNim, Mahasiswa mahasiswa){
        Call<ResponseBody> mahasiswaCall = kuliahService.updateMhs(oldNim, mahasiswa);

        mahasiswaCall.enqueue(new Callback<ResponseBody>() {
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
                                Toast.makeText(InputMahasiswaActivity.this,
                                        "berhasil mengupdate data mahasiswa", Toast.LENGTH_LONG).show();
                                finish();
                            } else {
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
                        "gagal mengupdate data mahasiswa " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void simpanMhsBaru(Mahasiswa mahasiswa){
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
                                sendMessage();
                                finish();
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

        });
    }

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
