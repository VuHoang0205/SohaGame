package com.s.sdk.base;

public final class Constants {

    private Constants() {
    }

    /**
     * url
     */
    public static final String BASE_URL = "https://maoristudio.net";
    //    public static final String BASE_URL = "https://beta.soap.soha.vn";
    public static final String URL_LOGIN = BASE_URL + "/webapp_client/login/?";
    public static final String URL_PAYMENT = BASE_URL + "/webapp_client/payment/?";

    public static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCU+1bLfPmcY7qrF/dTbAtuJlv4R/FVc1WEH9HK"
            + "U0jQjX/n/db9vz/x0i3te/bKLNEcwUhBu+PWPnOt/qVURG9BUT6RsCRFUn0CyGiUKoy45o9K/mJA"
            + "HmbrNtrUB6ckrYLF75Y50nUNsBVHUDw8yQymmiOBT1gc/KM5s1xTz44LMwIDAQAB";

    public static final String PUBLIC_KEY_MQTT = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC6oRGJPRMfBMf6xJAU6qbAqQfz\n"
            + "oxxfE1pFw4zPeyaVXKZ1JDhnfGrVLOs2tCKwX3h7YLTYkii2NnNmjcDdvqwkIzTu\n"
            + "PCiK1tgiIjZDZe8YiamrTL9mBLvLvrnC6xukY8MB/lzr/htkB9RtSMiqkXkwUlCs\n" + "DPKiz9QXXiGh6T2FQQIDAQAB";
    //pref
    public final static String PREF_INIT_MODEL = "PREF_INIT_MODEL";
    public static final String PREF_LOGIN_RESULT = "PREF_LOGIN_RESULT";
    public static final String PREF_ADS_ID_GG = "PREF_ADS_ID_GG";
    public static final String PREF_IS_SEND_PUSH_NOTIFY_SUCCESS = "PREF_IS_SEND_PUSH_NOTIFY_SUCCESS";
    public static final String PREF_USER_GAME_INFO = "PREF_USER_GAME_INFO";
    public static final String PREF_USER_ID_OLD = "PREF_USER_ID_OLD";
    public static final String PREF_VERSION_CODE_APP = "PREF_VERSION_CODE_APP";
    public static final String PREF_RESPONSE_INIT_DATA = "PREF_RESPONSE_INIT_DATA";
    public static final String PREF_CLIENTID_MQTT = "PREF_CLIENTID_MQTT";
    public static final String PREF_LOCATION_DB = "PREF_LOCATION_DB";
    public static final String PREF_LIST_DB_CONFIG = "PREF_LIST_DB_CONFIG";

    public static final String BUNDLE_EXTRA_DATA = "BUNDLE_EXTRA_DATA";
    public static final String BUNDLE_EXTRA_DATA_2 = "BUNDLE_EXTRA_DATA_2";

    public static final String KEY_STYLE_LOGIN = "KEY_STYLE_LOGIN";
    public static final int LOGIN_POPUP = 9;

}
