package com.wiradipa.fieldOwners.Model;

public class DataTransaksi {
    public String mNamaLapang, mTanggalLapang, mNamaPenyewa, mNamaLapangPenyewa,
    mNamaLapangMain, urlBuktiDp;
    public int mWaktuMain, id, mTotalTagihan;
    public int mStartHour, mEndHour, mStatus;

    public DataTransaksi() { }

    public DataTransaksi(String mNamaLapang, String mTanggalLapang, String mNamaPenyewa, String mNamaLapangPenyewa, int mWaktuMain, String mNamaLapangMain, int mTotalTagihan) {
        this.mNamaLapang = mNamaLapang;
        this.mTanggalLapang = mTanggalLapang;
        this.mNamaPenyewa = mNamaPenyewa;
        this.mNamaLapangPenyewa = mNamaLapangPenyewa;
        this.mWaktuMain = mWaktuMain;
        this.mNamaLapangMain = mNamaLapangMain;
        this.mTotalTagihan = mTotalTagihan;
    }

    public String getmNamaLapang() {
        return mNamaLapang;
    }

    public void setmNamaLapang(String mNamaLapang) {
        this.mNamaLapang = mNamaLapang;
    }

    public String getmTanggalLapang() {
        return mTanggalLapang;
    }

    public void setmTanggalLapang(String mTanggalLapang) {
        this.mTanggalLapang = mTanggalLapang;
    }

    public String getmNamaPenyewa() {
        return mNamaPenyewa;
    }

    public void setmNamaPenyewa(String mNamaPenyewa) {
        this.mNamaPenyewa = mNamaPenyewa;
    }

    public String getmNamaLapangPenyewa() {
        return mNamaLapangPenyewa;
    }

    public void setmNamaLapangPenyewa(String mNamaLapangPenyewa) {
        this.mNamaLapangPenyewa = mNamaLapangPenyewa;
    }

    public int getmWaktuMain() {
        return mWaktuMain;
    }

    public void setmWaktuMain(int mWaktuMain) {
        this.mWaktuMain = mWaktuMain;
    }

    public String getmNamaLapangMain() {
        return mNamaLapangMain;
    }

    public void setmNamaLapangMain(String mNamaLapangMain) {
        this.mNamaLapangMain = mNamaLapangMain;
    }

    public int getmTotalTagihan() {
        return mTotalTagihan;
    }

    public void setmTotalTagihan(int mTotalTagihan) {
        this.mTotalTagihan = mTotalTagihan;
    }
}
