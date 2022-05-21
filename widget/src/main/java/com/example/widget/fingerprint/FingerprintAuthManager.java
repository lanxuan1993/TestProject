package com.example.widget.fingerprint;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import android.util.Log;

import com.example.widget.fingerprint.callback.FingerprintCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

/**
 * @author: created by ZhaoBeibei on 2020-04-03 11:05
 * @describe: 指纹管理类
 * 1. isAvailable() 方法，判断指纹是否可用
 * 2. encrypt, 开启指纹
 * 3. decrypt, 验证指纹
 * 4. delete()方法，关闭指纹
 * 5. dismiss() 方法，使dialog消失（可选不用）
 */
@TargetApi(23)
public class FingerprintAuthManager {
    public static final String TAG = "FingerprintAuthManager";
    public static final String ENCRYPT = "encrypt";
    public static final String DECRYPT = "decrypt";
    private static final String DIALOG_FRAGMENT_TAG = "FpAuthDialog";
    private static final String ANDROID_KEY_STORE = "AndroidKeyStore";
    public static final String FINGERPRINT_PREF_IV = "aes_iv";
    private static final String CREDENTIAL_DELIMITER = "|:|";
    public static int mMaxAttempts = 5;
    public static FingerprintManager mFingerPrintManager;
    public FingerprintAuthenticationDialogFragment mFragment;
    public static FingerprintCallback mCallbackContext;
    public static Context mContext;
    public static String packageName;
    public static KeyStore mKeyStore;
    public static KeyGenerator mKeyGenerator;
    public static Cipher mCipher;
    private static String mClientId;
    private static String mUsername = "";
    private static String mClientSecret;
    private static boolean mCipherModeCrypt = true;
    private static boolean mUserAuthRequired = false;
    public static String action;

    public enum PluginError {
        BAD_PADDING_EXCEPTION,
        CERTIFICATE_EXCEPTION,
        FINGERPRINT_CANCELLED,
        FINGERPRINT_DATA_NOT_DELETED,
        FINGERPRINT_ERROR,
        FINGERPRINT_NOT_AVAILABLE,
        FINGERPRINT_PERMISSION_DENIED,
        ILLEGAL_BLOCK_SIZE_EXCEPTION,
        INIT_CIPHER_FAILED,
        INVALID_ALGORITHM_PARAMETER_EXCEPTION,
        IO_EXCEPTION,
        JSON_EXCEPTION,
        MINIMUM_SDK,
        MISSING_ACTION_PARAMETERS,
        MISSING_PARAMETERS,
        NO_SUCH_ALGORITHM_EXCEPTION,
        SECURITY_EXCEPTION,
        FRAGMENT_NOT_EXIST
    }

    public static FingerprintAuthManager getInstance(Context context) {
        return new FingerprintAuthManager(context);
    }

    public FingerprintAuthManager(Context context) {
        mContext = context;
        packageName = context.getPackageName();
        initData();
    }

    private void initData() {
        if (android.os.Build.VERSION.SDK_INT < 23) {
            return;
        }
        mFingerPrintManager = mContext.getSystemService(FingerprintManager.class);

        try {
            mKeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE);
            mKeyStore = KeyStore.getInstance(ANDROID_KEY_STORE);
            mCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to get an instance of KeyGenerator or Cipher", e);
        } catch (NoSuchProviderException e) {
            throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
        } catch (KeyStoreException e) {
            throw new RuntimeException("Failed to get an instance of KeyStore", e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get an instance of Cipher", e);
        }
    }

    /*****************判断指纹是否可用(指纹权限&手机设备是否支持&手机设备是否已录入指纹)************************/
    /**
     * 判断指纹是否可用
     *
     * @param callback
     */
    public void isAvailable(FingerprintCallback callback) {
        mCallbackContext = callback;
        if (android.os.Build.VERSION.SDK_INT < 23) {
            mCallbackContext.onError(PluginError.MINIMUM_SDK.name());
            return;
        }
        if (mContext.checkSelfPermission(Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            mCallbackContext.onError(PluginError.FINGERPRINT_PERMISSION_DENIED.name());
            return;
        }
        sendAvailabilityResult();
    }

    /**
     * 返回指纹是否可用
     */
    private void sendAvailabilityResult() {
        String errorMessage = null;
        JSONObject resultJson = new JSONObject();
        try {
            resultJson.put("isAvailable", isFingerprintAuthAvailable());
            resultJson.put("isHardwareDetected", mFingerPrintManager.isHardwareDetected());
            resultJson.put("hasEnrolledFingerprints", mFingerPrintManager.hasEnrolledFingerprints());
            mCallbackContext.onSuccess(resultJson);
            return;
        } catch (JSONException e) {
            errorMessage = PluginError.JSON_EXCEPTION.name();
        } catch (SecurityException e) {
            errorMessage = PluginError.SECURITY_EXCEPTION.name();
        }

        if (null != errorMessage) {
            Log.e(TAG, errorMessage);
            mCallbackContext.onError(errorMessage);
            return;
        }
    }

    /**
     * 手机设备硬件是否支持指纹检测和Android系统环境已录入指纹
     *
     * @return
     */
    public static boolean isFingerprintAuthAvailable() {
        return mFingerPrintManager.isHardwareDetected() && mFingerPrintManager.hasEnrolledFingerprints();
    }

    /******************************************关闭指纹**************************************************/
    /**
     * 关闭指纹
     *
     * @param args
     * @param callback
     */
    public void delete(HashMap<String, Object> args, FingerprintCallback callback) {
        mCallbackContext = callback;
        if (android.os.Build.VERSION.SDK_INT < 23) {
            mCallbackContext.onError(PluginError.MINIMUM_SDK.name());
            return;
        }
        if (args.get("clientId") == null) {
            mCallbackContext.onError(PluginError.MISSING_PARAMETERS.name());
            return;
        }
        mClientId = (String) args.get("clientId");
        if (args.get("username") != null) {
            mUsername = (String) args.get("username");
        }
        initDelete();
    }

    /**
     * 删除
     */
    private void initDelete() {
        boolean ivDeleted = false;
        boolean secretKeyDeleted = false;
        try {
            mKeyStore.load(null);
            mKeyStore.deleteEntry(mClientId);
            secretKeyDeleted = true;
            ivDeleted = deleteIV();
        } catch (KeyStoreException e) {
            Log.e(TAG, "Error while deleting SecretKey.", e);
        } catch (CertificateException e) {
            Log.e(TAG, "Error while deleting SecretKey.", e);
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Error while deleting SecretKey.", e);
        } catch (IOException e) {
            Log.e(TAG, "Error while deleting SecretKey.", e);
        }

        JSONObject resultJson = new JSONObject();
        if (ivDeleted && secretKeyDeleted) {
            mCallbackContext.onSuccess(resultJson);
        } else {
            mCallbackContext.onError(PluginError.FINGERPRINT_DATA_NOT_DELETED.name());
        }
    }

    private boolean deleteIV() {
        return deleteStringPreference(mClientId + mUsername, FINGERPRINT_PREF_IV);
    }

    /**
     * Delete a String preference
     *
     * @param name Preference name
     * @param key  Preference key
     * @return Returns true if deleted otherwise false
     */
    private boolean deleteStringPreference(String name, String key) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        return editor.remove(key).commit();
    }


    /******************************************指纹Dialog消失**************************************************/
    /**
     * dialog消失
     */
    public void dismissDialog() {
        if (null != mFragment) {
            Activity activity = (Activity) mContext;
            activity.getFragmentManager().beginTransaction().remove(mFragment).commit();
            JSONObject resultJson = new JSONObject();
            try {
                resultJson.put("msg", "Fragment dismissed");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mCallbackContext.onSuccess(resultJson);
        } else {
            mCallbackContext.onError(PluginError.FRAGMENT_NOT_EXIST.name());
        }
    }

    /*************************************指纹开启/验证******************************************/

    public void initEncryptAndDecrypt(String action, HashMap<String, Object> args, FingerprintCallback callback) {
        this.action = action;
        mCallbackContext = callback;
        if (android.os.Build.VERSION.SDK_INT < 23) {
            mCallbackContext.onError(PluginError.MINIMUM_SDK.name());
            return;
        }
        if (args.get("clientId") == null) {
            mCallbackContext.onError(PluginError.MISSING_PARAMETERS.name());
            return;
        }
        mClientId = (String) args.get("clientId");

        if (args.get("username") != null) {
            mUsername = (String) args.get("username");
        }

        if (args.containsKey("userAuthRequired")) {
            mUserAuthRequired = (boolean) args.get("userAuthRequired");
        }

        if (ENCRYPT.equals(action)) {
            mCipherModeCrypt = true;
            String password = "";
            if (args.get("password") != null) {
                password = (String) args.get("password");
            }
            mClientSecret = mClientId + mUsername + CREDENTIAL_DELIMITER + password;
        } else if (DECRYPT.equals(action)) {
            mCipherModeCrypt = false;
            if (args.get("token") == null) {
                mCallbackContext.onError(PluginError.MISSING_ACTION_PARAMETERS.name());
                return;
            }
            mClientSecret = (String) args.get("token");
        }

        initSecretKey();
    }

    /**
     * 初始化加密解密
     */
    private void initSecretKey() {
        SecretKey key = getSecretKey();
        if (key == null) {
            if (createKey()) {
                key = getSecretKey();
            }
        }
        if (key == null) {
            mCallbackContext.onError(PluginError.MISSING_ACTION_PARAMETERS.name());
            return;
        }

        if (isFingerprintAuthAvailable()) {
            Activity activity = (Activity) mContext;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mFragment = new FingerprintAuthenticationDialogFragment();
                    if (initCipher()) {
                        mFragment.setCancelable(false);
                        mFragment.setCryptoObject(new FingerprintManager.CryptoObject(mCipher));
                        FragmentTransaction transaction = activity.getFragmentManager().beginTransaction();
                        transaction.add(mFragment, DIALOG_FRAGMENT_TAG);
                        transaction.commitAllowingStateLoss();
                    } else {
                        mCallbackContext.onError(PluginError.INIT_CIPHER_FAILED.name());
                    }
                }
            });
        } else {
            mCallbackContext.onError(PluginError.FINGERPRINT_NOT_AVAILABLE.name());
        }
    }


    private static SecretKey getSecretKey() {
        String errorMessage = "";
        String getSecretKeyExceptionErrorPrefix = "Failed to get SecretKey from KeyStore: ";
        SecretKey key = null;
        try {
            mKeyStore.load(null);
            key = (SecretKey) mKeyStore.getKey(mClientId, null);
        } catch (KeyStoreException e) {
            errorMessage = getSecretKeyExceptionErrorPrefix
                    + "KeyStoreException: " + e.toString();
        } catch (CertificateException e) {
            errorMessage = getSecretKeyExceptionErrorPrefix
                    + "CertificateException: " + e.toString();
        } catch (UnrecoverableKeyException e) {
            errorMessage = getSecretKeyExceptionErrorPrefix
                    + "UnrecoverableKeyException: " + e.toString();
        } catch (IOException e) {
            errorMessage = getSecretKeyExceptionErrorPrefix
                    + "IOException: " + e.toString();
        } catch (NoSuchAlgorithmException e) {
            errorMessage = getSecretKeyExceptionErrorPrefix
                    + "NoSuchAlgorithmException: " + e.toString();
        }
        if (key == null) {
            Log.e(TAG, errorMessage);
        }
        return key;
    }


    public static void onAuthenticated(boolean withFingerprint, FingerprintManager.AuthenticationResult result) {
        JSONObject resultJson = new JSONObject();
        String errorMessage = "";
        boolean createdResultJson = false;
        try {
            byte[] bytes;
            FingerprintManager.CryptoObject cryptoObject = null;
            if (withFingerprint) {
                resultJson.put("withFingerprint", true);
                cryptoObject = result.getCryptoObject();
            } else {
                resultJson.put("withBackup", true);
                // If failed to init cipher because of InvalidKeyException, create new key
                if (!initCipher()) {
                    createKey();
                }
                if (initCipher()) {
                    cryptoObject = new FingerprintManager.CryptoObject(mCipher);
                }
            }

            if (cryptoObject == null) {
                errorMessage = PluginError.INIT_CIPHER_FAILED.name();
            } else {
                if (mCipherModeCrypt) {
                    bytes = cryptoObject.getCipher().doFinal(mClientSecret.getBytes("UTF-8"));
                    String encodedBytes = Base64.encodeToString(bytes, Base64.NO_WRAP);
                    resultJson.put("token", encodedBytes);
                } else {
                    bytes = cryptoObject.getCipher()
                            .doFinal(Base64.decode(mClientSecret, Base64.NO_WRAP));
                    String credentialString = new String(bytes, "UTF-8");
                    Pattern pattern = Pattern.compile(Pattern.quote(CREDENTIAL_DELIMITER));
                    String[] credentialArray = pattern.split(credentialString);
                    if (credentialArray.length == 2) {
                        String username = credentialArray[0];
                        String password = credentialArray[1];
                        if (username.equalsIgnoreCase(mClientId + mUsername)) {
                            resultJson.put("password", credentialArray[1]);
                        }
                    } else {
                        credentialArray = credentialString.split(":");
                        if (credentialArray.length == 2) {
                            String username = credentialArray[0];
                            String password = credentialArray[1];
                            if (username.equalsIgnoreCase(mClientId + mUsername)) {
                                resultJson.put("password", credentialArray[1]);
                            }
                        }
                    }
                }
                createdResultJson = true;
            }
        } catch (BadPaddingException e) {
            Log.e(TAG, "Failed to encrypt the data with the generated key:"
                    + " BadPaddingException:  " + e.toString());
            errorMessage = PluginError.BAD_PADDING_EXCEPTION.name();
        } catch (IllegalBlockSizeException e) {
            Log.e(TAG, "Failed to encrypt the data with the generated key: "
                    + "IllegalBlockSizeException: " + e.toString());
            errorMessage = PluginError.ILLEGAL_BLOCK_SIZE_EXCEPTION.name();
        } catch (JSONException e) {
            Log.e(TAG, "Failed to set resultJson key value pair: " + e.toString());
            errorMessage = PluginError.JSON_EXCEPTION.name();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (createdResultJson) {
            mCallbackContext.onSuccess(resultJson);
        } else {
            mCallbackContext.onError(errorMessage);
        }
    }


    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with fingerprint.
     */
    public static boolean createKey() {
        String errorMessage = "";
        String createKeyExceptionErrorPrefix = "Failed to create key: ";
        boolean isKeyCreated = false;
        // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
        // for your flow. Use of keys is necessary if you need to know if the set of
        // enrolled fingerprints has changed.
        try {
            mKeyStore.load(null);
            // Set the alias of the entry in Android KeyStore where the key will appear
            // and the constrains (purposes) in the constructor of the Builder
            mKeyGenerator.init(new KeyGenParameterSpec.Builder(mClientId, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(mUserAuthRequired)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            mKeyGenerator.generateKey();
            isKeyCreated = true;
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, createKeyExceptionErrorPrefix
                    + "NoSuchAlgorithmException: " + e.toString());
            errorMessage = PluginError.NO_SUCH_ALGORITHM_EXCEPTION.name();
        } catch (InvalidAlgorithmParameterException e) {
            Log.e(TAG, createKeyExceptionErrorPrefix
                    + "InvalidAlgorithmParameterException: " + e.toString());
            errorMessage = PluginError.INVALID_ALGORITHM_PARAMETER_EXCEPTION.name();
        } catch (CertificateException e) {
            Log.e(TAG, createKeyExceptionErrorPrefix
                    + "CertificateException: " + e.toString());
            errorMessage = PluginError.CERTIFICATE_EXCEPTION.name();
        } catch (IOException e) {
            Log.e(TAG, createKeyExceptionErrorPrefix
                    + "IOException: " + e.toString());
            errorMessage = PluginError.IO_EXCEPTION.name();
        }
        if (!isKeyCreated) {
            Log.e(TAG, errorMessage);
            mCallbackContext.onError(errorMessage);
        }
        return isKeyCreated;
    }

    /**
     * Initialize the {@link Cipher} instance with the created key in the {@link #createKey()}
     * method.
     *
     * @return {@code true} if initialization is successful, {@code false} if the lock screen has
     * been disabled or reset after the key was generated, or if a fingerprint got enrolled after
     * the key was generated.
     */
    private static boolean initCipher() {
        boolean initCipher = false;
        String errorMessage = "";
        String initCipherExceptionErrorPrefix = "Failed to init Cipher: ";
        byte[] mCipherIV;

        try {
            SecretKey key = getSecretKey();
            if (mCipherModeCrypt) {
                mCipher.init(Cipher.ENCRYPT_MODE, key);
                mCipherIV = mCipher.getIV();
                setStringPreference(mClientId + mUsername, FINGERPRINT_PREF_IV, new String(Base64.encode(mCipherIV, Base64.NO_WRAP)));
            } else {
                mCipherIV = Base64.decode(getStringPreference(mClientId + mUsername, FINGERPRINT_PREF_IV), Base64.NO_WRAP);
                IvParameterSpec ivspec = new IvParameterSpec(mCipherIV);
                mCipher.init(Cipher.DECRYPT_MODE, key, ivspec);
            }
            initCipher = true;
        } catch (Exception e) {
            errorMessage = initCipherExceptionErrorPrefix + "Exception: " + e.toString();
        }
        if (!initCipher) {
            Log.e(TAG, errorMessage);
        }
        return initCipher;
    }


    public static String getStringPreference(String name, String key) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }


    public static void setStringPreference(String name, String key, String value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }


    public static void onError(String err) {
        mCallbackContext.onError(err);
    }

}
