package com.wiradipa.fieldOwners.ApiHelper;

public class UtilsApi {
    public static final String BASE_URL_API = "http://app.lapangbola.com/api/";

    public static BaseApiService getApiService(){
        return RetrofitClient.getClient(BASE_URL_API).create(BaseApiService.class);
    }
}
