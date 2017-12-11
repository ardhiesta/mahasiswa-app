package id.web.ardhi.mahasiswaapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;
import id.web.ardhi.mahasiswaapp.model.Mahasiswa;
import id.web.ardhi.mahasiswaapp.service.MahasiswaService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;

    // BASE_URL_API memuat alamat aplikasi web API yang dibuat menggunakan SLim Framework
    // ganti ip address sesuai dengan konfigurasi di laptop anda
    public static final String BASE_API_URL = "http://192.168.57.1/web/api_kuliah/index.php/";

    // variabel mListView digunakan untuk menghubungkan komponen ListViewdi layout dengan activity
    private ListView mListView;

    // mahasiswas memuat data seluruh mahasiswa dalam bentuk Array object Mahasiswa
    List<Mahasiswa> mahasiswas;

    // mahasiswaService adalah instansiasi dari interface MahasiswaService
    MahasiswaService mahasiswaService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocalBroadcastManager.getInstance(MainActivity.this).registerReceiver(mMessageReceiver,
                new IntentFilter("Data Mhs Event"));

        fab = (FloatingActionButton) findViewById(R.id.fabAddMhs);

        mListView = (ListView) findViewById(R.id.lvMhs);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, InputMahasiswaActivity.class);
                startActivity(intent);
            }
        });

        // membuat object Retrofit, memasukkan URL web API
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // instansiasi MahasiswaService
        mahasiswaService = retrofit.create(MahasiswaService.class);

        // memuat data mahasiswa dari web API
        loadDataMhs(mahasiswaService);
    }

    private void loadDataMhs(MahasiswaService kuliahService) {
        // array untuk menampung data mahasiswa yang didapat dari web API
        mahasiswas = new ArrayList<>();

        // memanggil web API untuk mengambil semua record data mahasiswa
        Call<List<Mahasiswa>> listMhs = kuliahService.getAllMhs();

        // arrayListNamaMhs adalah array berisi String yang menampung nama mahasiswa
        // nama-nama mahasiswa akan ditampilkan pada ListView
        final List<String> arrayListNamaMhs = new ArrayList<>();
        listMhs.enqueue(new Callback<List<Mahasiswa>>() {
            @Override
            public void onResponse(Call<List<Mahasiswa>> call, Response<List<Mahasiswa>> response) {
                // parsing response
                for (int i = 0; i < response.body().size(); i++) {
                    // mengambil nama-nama mahasiswa, dimasukkan ke arrayListNamaMhs
                    arrayListNamaMhs.add(response.body().get(i).getNama());

                    // membuat object mahasiswa untuk setiap record data mahasiswa yang didapat
                    Mahasiswa mahasiswa = response.body().get(i);

                    // mengisi array mahasiswas dengan object mahasiswa yang mewakili 1 record data mahasiswa
                    mahasiswas.add(mahasiswa);
                }

                // membuat ArrayAdapter untuk memuat data nama-nama mahasiswa ke ListView
                ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,
                        android.R.layout.simple_list_item_1, arrayListNamaMhs);
                // mengatur adapter ListView
                mListView.setAdapter(adapter);

                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        Intent intent = new Intent(MainActivity.this, DetailMahasiswaActivity.class);
                        intent.putExtra("data-mahasiswa", mahasiswas.get(i));
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onFailure(Call<List<Mahasiswa>> call, Throwable t) {
                // menampilkan error ke log
                Log.w("ERROR", t.getMessage());
            }
        });
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("Message");
            Log.d("receiver", "Got message: " + message);
            loadDataMhs(mahasiswaService);
        }
    };
}
