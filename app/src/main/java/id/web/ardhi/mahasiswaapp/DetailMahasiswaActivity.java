package id.web.ardhi.mahasiswaapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.CallScreeningService;
import android.view.View;
import android.widget.TextView;
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

public class DetailMahasiswaActivity extends AppCompatActivity {
    TextView tvNim, tvNama, tvAlamat;
    MahasiswaService kuliahService;
    Mahasiswa mahasiswa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_mahasiswa);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvNim = (TextView) findViewById(R.id.tvNim);
        tvNama = (TextView) findViewById(R.id.tvNama);
        tvAlamat = (TextView) findViewById(R.id.tvAlamat);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        kuliahService = retrofit.create(MahasiswaService.class);

        loadDataMhs();
    }

    public void editDataMhs(View view){
        Intent intent = new Intent(DetailMahasiswaActivity.this, InputMahasiswaActivity.class);
        intent.putExtra("data-mahasiswa", mahasiswa);
        startActivity(intent);
    }

    public void hapusDataMhs(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(DetailMahasiswaActivity.this);

        builder.setTitle("Delete");
        builder.setMessage("Anda yakin menghapus data mahasiswa "+tvNim.getText().toString()+" ?");
        builder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nim = tvNim.getText().toString();
                Call<ResponseBody> responseBodyCall = kuliahService.deleteMhs(nim);
                responseBodyCall.enqueue(new Callback<ResponseBody>() {
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
                                        Toast.makeText(DetailMahasiswaActivity.this, "berhasil menghapus data mahasiswa",
                                                Toast.LENGTH_LONG).show();
                                        finish();
                                    } else {
                                        Toast.makeText(DetailMahasiswaActivity.this,
                                                "gagal menghapus data mahasiswa, error code: " + status,
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            } else {
                                Toast.makeText(DetailMahasiswaActivity.this, "gagal menghapus data mahasiswa",
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

                    }
                });

                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Batal", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void loadDataMhs(){
        mahasiswa = getIntent().getParcelableExtra("data-mahasiswa");

        if (mahasiswa != null){
            tvNim.setText(mahasiswa.getNim());
            tvNama.setText(mahasiswa.getNama());
            tvAlamat.setText(mahasiswa.getAlamat());
        }
    }
}
