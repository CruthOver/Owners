package com.wiradipa.fieldOwners.ApiHelper;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CustomAddVenue {

    private String auth_token;
    private String mDescription;
    private String mAddress;
    private double mLatitude;
    private double mLongitude;
    private String mStartHour;
    private String mEndHour;
    private String mPicture;
    private byte[] mOwnerPhotos;
    byte[] mFacility;

    public CustomAddVenue(){}

    public CustomAddVenue(String auth_token, String mDescription, String mAddress, double mLatitude, double mLongitude, String mStartHour, String mEndHour, String mPicture, String mPath, String mFacility) {
        this.auth_token = auth_token;
        this.mDescription = mDescription;
        this.mAddress = mAddress;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mStartHour = mStartHour;
        this.mEndHour = mEndHour;
        this.mPicture = mPicture;


        File imageFie = new File(mPath.substring(7));
        int size = (int) imageFie.length();
        mOwnerPhotos = new byte[size];

        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(imageFie));
            bufferedInputStream.read(mOwnerPhotos, 0, mOwnerPhotos.length);
            bufferedInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public void setmDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public void setmAddress(String mAddress) {
        this.mAddress = mAddress;
    }

    public void setmLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public void setmLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public void setmStartHour(String mStartHour) {
        this.mStartHour = mStartHour;
    }

    public void setmEndHour(String mEndHour) {
        this.mEndHour = mEndHour;
    }

    public void setmPicture(String mPicture) {
        this.mPicture = mPicture;
    }

    public void setmOwnerPhotos(byte[] mOwnerPhotos) {
        this.mOwnerPhotos = mOwnerPhotos;
    }

    public void setmFacility(byte[] mFacility) {
        this.mFacility = mFacility;
    }

    public String getAuth_token() {
        return auth_token;
    }

    public String getmDescription() {
        return mDescription;
    }

    public String getmAddress() {
        return mAddress;
    }

    public double getmLatitude() {
        return mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public String getmStartHour() {
        return mStartHour;
    }

    public String getmEndHour() {
        return mEndHour;
    }

    public String getmPicture() {
        return mPicture;
    }

    public byte[] getmOwnerPhotos() {
        return mOwnerPhotos;
    }

    public byte[] getmFacility() {
        return mFacility;
    }
}
