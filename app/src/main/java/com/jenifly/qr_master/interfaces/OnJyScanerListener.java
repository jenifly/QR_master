package com.jenifly.qr_master.interfaces;

import com.google.zxing.Result;

/**
 * Created by Vondear on 2017/9/22.
 */

public interface OnJyScanerListener {
    void onSuccess(String type, Result result);

    void onFail(String type, String message);
}
