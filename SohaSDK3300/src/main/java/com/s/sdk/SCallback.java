package com.s.sdk;

public interface SCallback<RESULT> {
    void onSuccess(RESULT result);

    void onError();

    void onCancel();

}
