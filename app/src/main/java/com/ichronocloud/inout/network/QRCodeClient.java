package com.ichronocloud.inout.network;

import android.content.Context;

import java.io.IOException;

import retrofit.client.OkClient;
import retrofit.client.Request;
import retrofit.client.Response;

/**
 * Created by lxl on 14-4-11.
 */
public class QRCodeClient extends OkClient {

    private final Context mContext;

    public QRCodeClient(Context context) {
        super();
        this.mContext = context;
    }

    @Override
    public Response execute(Request request) throws IOException {
        Response response = super.execute(request);
        return response;
    }
}
