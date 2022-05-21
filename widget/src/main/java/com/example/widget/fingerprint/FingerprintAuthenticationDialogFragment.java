package com.example.widget.fingerprint;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.tools.utils.AnimUtils;
import com.example.tools.utils.CommonUtils;
import com.example.widget.R;

@TargetApi(23)
public class FingerprintAuthenticationDialogFragment extends DialogFragment {
    public static final String TAG = "FingerprintDialog";
    private TextView messageTextView;
    private TextView mCancelTv;
    private Context mContext;
    private FingerprintManager.CryptoObject mCryptoObject;
    private CancellationSignal mCancellationSignal;
    private static final long DELAY_MILLIS = 1500;
    private int mAttempts = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CommonFragmentDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fingerprint_dialog_container, container, false);
        messageTextView = (TextView) view.findViewById(R.id.fingerprint_description);
        mCancelTv = (TextView) view.findViewById(R.id.tv_cancel);
        mCancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissAllowingStateLoss();
            }
        });
        mContext = getContext();
        return view;
    }

    public void setCryptoObject(FingerprintManager.CryptoObject cryptoObject) {
        mCryptoObject = cryptoObject;
    }

    @Override
    public void onResume() {
        super.onResume();
        startListening(mCryptoObject);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopListening();
    }


    /****************************************指纹识别回调处理*****************************************************/

    public void startListening(FingerprintManager.CryptoObject cryptoObject) {
        if (!FingerprintAuthManager.isFingerprintAuthAvailable()) {
            return;
        }
        mCancellationSignal = new CancellationSignal();
        FingerprintAuthManager.mFingerPrintManager.authenticate(cryptoObject, mCancellationSignal,
                0, mAuthenticationCallback, null);
    }

    public void stopListening() {
        if (mCancellationSignal != null) {
            mCancellationSignal.cancel();
            mCancellationSignal = null;
        }
    }

    /**
     * 指纹监听回调方法
     */
    FingerprintManager.AuthenticationCallback mAuthenticationCallback = new FingerprintManager.AuthenticationCallback() {
        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            // 验证出错回调 指纹传感器会关闭一段时间,在下次调用authenticate时,会出现禁用期(时间依厂商不同30,1分都有)
            if (!CommonUtils.isFastClick()) {
                Log.d(TAG, "onAuthenticationError:" + errorCode + "," + errString);
                showTextViewError(errString);
                messageTextView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onError(errString.toString());
                    }
                }, DELAY_MILLIS);
            }
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            // 验证帮助回调
            Log.d(TAG, "onAuthenticationHelp:" + helpCode + "," + helpString);
            showTextViewError(helpString);
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            //验证成功
            Log.d(TAG, "onAuthenticationSucceeded:");
            messageTextView.removeCallbacks(mResetErrorTextRunnable);
            messageTextView.setTextColor(getContext().getResources().getColor(R.color.success_color));
            messageTextView.setText(messageTextView.getResources().getString(R.string.fingerprint_success));
            messageTextView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (FingerprintAuthManager.action.equals(FingerprintAuthManager.ENCRYPT)) {
                        FingerprintAuthManager.onAuthenticated(false, null);
                    } else {
                        FingerprintAuthManager.onAuthenticated(true, result);
                    }
                    dismissAllowingStateLoss();
                }
            }, DELAY_MILLIS);
        }

        @Override
        public void onAuthenticationFailed() {
            // 验证失败  指纹验证失败后,指纹传感器不会立即关闭指纹验证,系统会提供5次重试的机会,
            // 即调用5次onAuthenticationFailed后,才会调用onAuthenticationError
            Log.d(TAG, "onAuthenticationFailed:" + mAttempts);
            mAttempts++;
            String fingerprint_not_recognized_str = mContext.getResources().getString(R.string.fingerprint_not_recognized);
            String too_many_attempts_str = mContext.getResources().getString(R.string.fingerprint_too_many_attempts);
            if (mAttempts > FingerprintAuthManager.mMaxAttempts) {
                showTextViewError(too_many_attempts_str);
                messageTextView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onError(too_many_attempts_str);
                    }
                }, DELAY_MILLIS);
            } else {
                showTextViewError(fingerprint_not_recognized_str);
            }
        }
    };

    private void showTextViewError(CharSequence error) {
        messageTextView.setText(error);
        messageTextView.setTextColor(messageTextView.getResources().getColor(R.color.error_red));
        AnimUtils.shakeView(getContext(), messageTextView);

        messageTextView.removeCallbacks(mResetErrorTextRunnable);
        messageTextView.postDelayed(mResetErrorTextRunnable, DELAY_MILLIS);
    }


    Runnable mResetErrorTextRunnable = new Runnable() {
        @Override
        public void run() {
            messageTextView.setTextColor(messageTextView.getResources().getColor(R.color.black33));
            messageTextView.setText(messageTextView.getResources().getString(R.string.fingerprint_description));
        }
    };


    public void onError(String err) {
        FingerprintAuthManager.onError(err);
        dismissAllowingStateLoss();
    }
}
