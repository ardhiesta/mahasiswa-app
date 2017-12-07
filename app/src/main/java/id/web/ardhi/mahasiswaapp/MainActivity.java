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
    public static final String BASE_API_URL = "http://192.168.57.1/web/api_kuliah/index.php/";
    private ListView mListView;
    List<Mahasiswa> mahasiswas;
    MahasiswaService kuliahService;

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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        kuliahService = retrofit.create(MahasiswaService.class);

        loadDataMhs(kuliahService);
    }

    private void loadDataMhs(MahasiswaService kuliahService){
        mahasiswas = new ArrayList<>();

        Call<List<Mahasiswa>> listMhs = kuliahService.getAllMhs();

        final List<String> arrayListNamaMhs = new ArrayList<>();
        listMhs.enqueue(new Callback<List<Mahasiswa>>() {
            @Override
            public void onResponse(Call<List<Mahasiswa>> call, Response<List<Mahasiswa>> response) {
                for (int i=0; i<response.body().size(); i++){
                    arrayListNamaMhs.add(response.body().get(i).getNama());

                    Mahasiswa mahasiswa = response.body().get(i);

                    mahasiswas.add(mahasiswa);
                }

                ArrayAdapter adapter = new ArrayAdapter(MainActivity.this,
                        android.R.layout.simple_list_item_1, arrayListNamaMhs);
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
                System.out.println("");
            }
        });
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("Message");
            Log.d("receiver", "Got message: " + message);
            loadDataMhs(kuliahService);
        }
    };
}
