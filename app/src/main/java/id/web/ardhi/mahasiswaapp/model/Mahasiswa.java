package id.web.ardhi.mahasiswaapp.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by linuxluv on 08/11/17.
 */

public class Mahasiswa implements Parcelable {
    String nim, nama, alamat;

    public Mahasiswa(){}

    protected Mahasiswa(Parcel in) {
        nim = in.readString();
        nama = in.readString();
        alamat = in.readString();
    }

    public static final Creator<Mahasiswa> CREATOR = new Creator<Mahasiswa>() {
        @Override
        public Mahasiswa createFromParcel(Parcel in) {
            return new Mahasiswa(in);
        }

        @Override
        public Mahasiswa[] newArray(int size) {
            return new Mahasiswa[size];
        }
    };

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nim);
        parcel.writeString(nama);
        parcel.writeString(alamat);
    }
}
