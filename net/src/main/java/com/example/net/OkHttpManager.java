package com.example.net;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.tools.utils.FileUtils;
import com.example.tools.utils.NumberFormatUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author: created by ZhaoBeiBei on 2020-06-03 10:32
 * @describe: OkHttp管理类
 */
public class OkHttpManager {
    private static final String TAG = OkHttpManager.class.getName();
    private static OkHttpManager sInstance = null;
    private static OkHttpClient mOkHttpClient = null;
    private static long CONNECT_TIMEOUT = 30;
    public static Map<String, Long> requestIdMap = new HashMap<>();

    public static OkHttpManager getInstance() {
        if (sInstance == null) {
            synchronized (OkHttpManager.class) {
                if (sInstance == null) {
                    sInstance = new OkHttpManager();
                }
            }
        }
        return sInstance;
    }

    private OkHttpManager() {
        if (mOkHttpClient == null) {
            /**
             * 如果不喜欢系统的Http 的打印方式，可以自己去实现Interceptor 接口
             * 但是统一拦截的header 是无法打印的，因为是在请求发出后统一拦截打印的。
             */
//            MyHttpInterceptor myHttpInterceptor = new MyHttpInterceptor();
//            myHttpInterceptor.setLevel(MyHttpInterceptor.Level.BODY);
            mOkHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)//设置连接超时时间
//                    .addInterceptor(myHttpInterceptor)
//                    .addNetworkInterceptor(mRequestInterceptor)
//                    .addInterceptor(new DynamicTimeoutInterceptor())
                    .build();
        }
    }



    /**
     * 请求的拦截处理
     */
    Interceptor mRequestInterceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            String requestKey = getRequestKey(request);

            if (null == requestIdMap.get(requestKey)) {
                requestIdMap.put(requestKey, System.currentTimeMillis());
            } else {
                //如果是重复的请求，抛出一个自定义的错误，这个错误大家根据自己的业务定义吧
                Log.i("REPEAT-REQUEST", "重复请求" + requestKey);
                return new Response.Builder()
                        .protocol(Protocol.get("CUSTOM_REPEAT_REQ_PROTOCOL"))
                        .request(request)
                        .build();
            }
            Response originalResponse = chain.proceed(request);
            logRequestHeaders(request);//把统一拦截的header 打印出来
            return originalResponse.newBuilder().build();
        }
    };

    public static String getRequestKey(Request request) {
        String requestKey = "Request{"
                + "method="
                + request.method()
                + ", url="
                + request.url().toString()
                + ", request_body="
                + getRequestBody(request)
                + '}';
        return requestKey;
    }

    public static String getRequestBody(Request request) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpUrl.Builder urlBuilder = request.url().newBuilder();
        if ("GET".equals(request.method())) { // GET方法
            HttpUrl httpUrl = urlBuilder.build();
            // 打印所有get参数
            Set<String> paramKeys = httpUrl.queryParameterNames();
            for (String key : paramKeys) {
                String value = httpUrl.queryParameter(key);
                stringBuilder.append(" " + key + ":" + value);
            }
        } else if ("POST".equals(request.method())) { // POST方法
            // FormBody和url不太一样，若需添加公共参数，需要新建一个构造器
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            // 把已有的post参数添加到新的构造器
            if (request.body() instanceof FormBody) {
                FormBody formBody = (FormBody) request.body();
                for (int i = 0; i < formBody.size(); i++) {
                    bodyBuilder.addEncoded(formBody.encodedName(i), formBody.encodedValue(i));
                }
            }

            FormBody newBody = bodyBuilder.build();
            // 打印所有post参数
            for (int i = 0; i < newBody.size(); i++) {
                stringBuilder.append(" " + newBody.name(i) + ":" + newBody.value(i));
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 打印全局统一拦截添加的Http Headers
     * 全局拦截的http 没法在配置中直接打印处理，因为先http 请求然后打印然后拦截添加的
     *
     * @param request
     */
    private static void logRequestHeaders(Request request) {
        Log.w("OkHttp", "开始打印HTTP请求  Headers \n");
        Headers headers = request.headers();
        for (int i = 0, count = headers.size(); i < count; i++) {
            String name = headers.name(i);
            // Skip headers from the request body as they are explicitly logged above.
            if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                Log.i("OkHttp: " + i + " ", name + ": " + headers.value(i));
            }
        }
        Log.w("OkHttp", "打印HTTP请求完成  Headers \n");
    }

//    /**
//     * 动态设置接口请求超时时间
//     */
//    private class DynamicTimeoutInterceptor implements Interceptor {
//        @Override
//        public okhttp3.Response intercept(Chain chain) throws IOException {
//            Request request = chain.request();
//            String questUrl = request.url().toString();
//            if (questUrl.contains(NetConstant.fetchToken)) {
//                return chain.withConnectTimeout(5, TimeUnit.SECONDS).proceed(request);
//            }
//            return chain.proceed(request);
//        }
//    }

    /**
     * 获取Headers
     *
     * @param headersMap
     * @return
     */
    private Headers getHeaders(Map<String, Object> headersMap) {
        Headers.Builder headerBuilder = new Headers.Builder();
        if (headersMap != null && headersMap.size() > 0) {
            for (String key : headersMap.keySet()) {
                headerBuilder.add(key, (String) headersMap.get(key));
            }
        }
        Headers headers = headerBuilder.build();
        return headers;
    }

    /**
     * 拼接Get的Url请求地址
     *
     * @param url
     * @param requestParams
     * @return
     */
    public String appendUrl(String url, Map<String, Object> requestParams) {
        HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder();
        if (requestParams != null && requestParams.size() > 0) {
            for (String key : requestParams.keySet()) {
                builder.addQueryParameter(key, (String) requestParams.get(key));
            }
        }
        String newUrl = builder.build().toString();
        return newUrl;
    }


    /**
     * get请求，异步方式，获取网络数据，是在子线程中执行的，需要切换到主线程才能更新UI
     *
     * @param url
     * @param commonCallBack
     * @return
     */
    public void getDataAsyn(String url, Map<String, Object> headers, Map<String, Object> params,
                            final CommonCallBack commonCallBack) {
        url = appendUrl(url, params);
        Request request = new Request.Builder()
                .get()
                .url(url)
                .headers(getHeaders(headers))
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                commonCallBack.failed(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    commonCallBack.success(response);
                } else {
                    commonCallBack.failed(response.message());
                }
            }
        });
    }


    /**
     * post请求，异步方式，提交数据，是在子线程中执行的，需要切换到主线程才能更新UI
     *
     * @param url
     * @param params
     * @param commonCallBack
     */
    public void postDataAsyn(String url, Map<String, Object> headers, Map<String, Object> params,
                             final CommonCallBack commonCallBack) {
        //1 构造RequestBody
        FormBody.Builder formBuilder = new FormBody.Builder();
        if (params != null && params.size() > 0) {
            for (HashMap.Entry<String, Object> entry : params.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    formBuilder.add(entry.getKey(), (String) entry.getValue());
                }
            }
        }
        RequestBody body = formBuilder.build();
        //2 构造Request
        Request request = new Request.Builder()
                .post(body)
                .url(url)
                .headers(getHeaders(headers))
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                commonCallBack.failed(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 200) {
                    commonCallBack.success(response);
                } else {
                    commonCallBack.failed(response.message());
                }
            }
        });
    }


    /**
     * 上传文件
     *
     * @param url
     * @param files
     * @param fileCallBack
     */
    public void uploadFile(Context mContext, String url, Map<String, Object> headers, Map<String, Object> params,
                           String uploadKey, JSONArray files, final FileCallBack fileCallBack) {
        MultipartBody.Builder multiBuilder = new MultipartBody.Builder();
        multiBuilder.setType(MultipartBody.FORM);
        for (int i = 0; i < files.length(); i++) {
            JSONObject jsonObject = files.optJSONObject(i);
            String localId = jsonObject.optString("localId");
            String fileName = jsonObject.optString("fileName");
            String mimeType = jsonObject.optString("mimeType");

            String img = localId.replace("bhfile://", "");
            String path = FileUtils.getFilePath(mContext, Environment.DIRECTORY_PICTURES) + "/" + img;
            File file = new File(path);
            if (file.exists()) {
                multiBuilder.addFormDataPart(uploadKey,
                        fileName,
                        RequestBody.create(MediaType.parse(mimeType), file));
            }
        }

        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                multiBuilder.addFormDataPart(entry.getKey(), (String) entry.getValue());
            }
        }

        MultipartBody multipartBody = multiBuilder.build();
        ProgressRequestBody progressRequestBody = new ProgressRequestBody(multipartBody,
                new ProgressRequestBody.ProgressListener() {
                    int lastProgress = 1;

                    @Override
                    public void onProgress(long byteWrite, long contentLength) {
                        double currProgress = byteWrite * 100 / (double) contentLength;
                        if (currProgress > lastProgress) {
                            lastProgress = (int) (currProgress / 10) * 10 + 10;
                            fileCallBack.progress(NumberFormatUtils.formatDecimal(currProgress));
                        }
                    }
                });

        Request request = new Request.Builder()
                .post(progressRequestBody)
                .url(url)
                .headers(getHeaders(headers))
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                fileCallBack.failed( e.getMessage());
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                Log.i(TAG, "success " + response);
                if (response.code() == 200) {
                    fileCallBack.success(response);
                } else {
                    fileCallBack.failed(response.message());
                }
            }
        });
    }

    /**
     * 下载文件
     *
     * @param url
     * @param file         储存下载的文件
     * @param fileCallBack
     */
    public void downloadFile(String url, Map<String, Object> headers, Map<String, Object> params,
                             final File file, final FileCallBack fileCallBack) {
        Request request = new Request.Builder()
                .get()
                .url(appendUrl(url, params))
                .headers(getHeaders(headers))
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            int lastProgress = 1;

            @Override
            public void onFailure(Call call, IOException e) {
                fileCallBack.failed(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) {
                long contentLength = response.body().contentLength();
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    long byteWrite = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        byteWrite += len;
                        if (contentLength > 0) {
//                            double currProgress = byteWrite * 100 / (double) contentLength;
//                            int currProgress = (int) (byteWrite * 1.0f / contentLength * 100);
                            int currProgress = (int) (((float)byteWrite / contentLength) * 100);

//                            if (currProgress > lastProgress) {
//                                lastProgress = (int) (currProgress / 10) * 10 + 10;
//                                fileCallBack.progress(NumberFormatUtils.formatDecimal(currProgress));
//                            }
                            fileCallBack.progress(String.valueOf(currProgress));

                        }
                    }
                    fos.flush();
                    fileCallBack.success(response);
                } catch (Exception e) {
                    e.printStackTrace();
                    fileCallBack.failed(e.getMessage());
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    /**
     * 自定义普通网络请求回调接口
     */
    public interface CommonCallBack {
        void success(Response response) throws IOException;

        void failed(String error);
    }


    /**
     * 自定义上传下载文件回调接口
     */
    public interface FileCallBack {
        void success(Response response) throws IOException;

        void failed(String error);

        void progress(String progress);
    }
}

