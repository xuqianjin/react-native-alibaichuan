package com.alibcdemo;

import android.app.Activity;
import android.app.Application;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.ali.auth.third.core.model.Session;
import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.callback.AlibcTradeCallback;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcAddCartPage;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcDetailPage;
import com.alibaba.baichuan.android.trade.page.AlibcMyCartsPage;
import com.alibaba.baichuan.android.trade.page.AlibcMyOrdersPage;
import com.alibaba.baichuan.android.trade.page.AlibcShopPage;
import com.alibaba.baichuan.trade.biz.AlibcConstants;
import com.alibaba.baichuan.trade.biz.applink.adapter.AlibcFailModeType;
import com.alibaba.baichuan.trade.biz.context.AlibcResultType;
import com.alibaba.baichuan.trade.biz.context.AlibcTradeResult;
import com.alibaba.baichuan.trade.biz.core.taoke.AlibcTaokeParams;
import com.alibaba.baichuan.trade.biz.login.AlibcLogin;
import com.alibaba.baichuan.trade.biz.login.AlibcLoginCallback;
import com.alibaba.baichuan.trade.common.utils.AlibcLogger;
import com.facebook.react.bridge.Callback;

import com.alibaba.baichuan.android.trade.AlibcTradeSDK;
import com.alibaba.baichuan.android.trade.callback.AlibcTradeInitCallback;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

public class RNAlibcSdkModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private static final String TAG = "RNAlibcSdkModule----->";
    private final static String NOT_LOGIN = "not login";
    private final static String INVALID_TRADE_RESULT = "invalid trade result";
    private final static String INVALID_PARAM = "invalid";

    static private RNAlibcSdkModule mRNAlibcSdkModule = null;

    static public RNAlibcSdkModule sharedInstance(ReactApplicationContext context) {
        if (mRNAlibcSdkModule == null) {
            return new RNAlibcSdkModule(context);
        } else {
            return mRNAlibcSdkModule;
        }
    }

    public RNAlibcSdkModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
//        reactContext.addActivityEventListener(mActivityEventListener);
    }

    @Override
    public String getName() {
        return "RNAlibcSdk";
    }

    @ReactMethod
    public void setChannel(String typeName, String channelName) {
        AlibcTradeSDK.setChannel(typeName, channelName);
    }

    @ReactMethod
    public void setISVVersion(String isvVersion) {
        AlibcTradeSDK.setISVVersion(isvVersion);
    }

    @ReactMethod
    public void setSyncForTaoke(boolean isSyncForTaoke, Callback callback) {
        boolean result = AlibcTradeSDK.setSyncForTaoke(isSyncForTaoke);
        callback.invoke(result);
    }

    /**
     * 是否登录
     */
    @ReactMethod
    public void isLogin(final Callback callback) {
        callback.invoke(AlibcLogin.getInstance().isLogin());
    }

    /**
     * 初始化SDK---无参数传入
     */
    @ReactMethod
    public void initTae(Callback successCallback, Callback errorCallback) {
        AlibcTradeSDK.asyncInit((Application) reactContext.getApplicationContext(), new AlibcTradeInitCallback() {
            @Override
            public void onSuccess() {
                successCallback.invoke("init success");
            }

            @Override
            public void onFailure(int code, String msg) {
                WritableMap map = Arguments.createMap();
                map.putInt("code", code);
                map.putString("message", msg);
                errorCallback.invoke(map);
            }
        });
    }

    //登录
    @ReactMethod
    public void showLogin(Callback successCallback, Callback errorCallback) {
        AlibcLogin alibcLogin = AlibcLogin.getInstance();
        alibcLogin.showLogin(new AlibcLoginCallback() {
            @Override
            public void onSuccess(int loginResult, String openId, String userNick) {
                WritableMap map = Arguments.createMap();
                map.putString("message", "login success");
                successCallback.invoke(map);
            }

            @Override
            public void onFailure(int code, String msg) {
                // code：错误码  msg： 错误信息
                WritableMap map = Arguments.createMap();
                map.putInt("code", code);
                map.putString("message", msg);
                errorCallback.invoke(map);
            }
        });
    }

    /**
     * 获取已登录的用户信息---无参数传入
     */
    @ReactMethod
    public void getUserInfo(Callback successCallback, Callback errorCallback) {
        if (AlibcLogin.getInstance().isLogin()) {
            Session session = AlibcLogin.getInstance().getSession();
            WritableMap map = Arguments.createMap();
            map.putString("nick", session.nick);
            map.putString("avatarUrl", session.avatarUrl);
            map.putString("openId", session.openId);
            map.putString("topAuthCode", session.topAuthCode);
            map.putString("topExpireTime", session.topExpireTime);
            map.putString("userid", session.userid);
            successCallback.invoke(map);
        } else {
            WritableMap map = Arguments.createMap();
            map.putString("message", NOT_LOGIN);
            errorCallback.invoke(map);
        }
    }

    //登出
    @ReactMethod
    public void showLogout(Callback successCallback, Callback errorCallback) {
        AlibcLogin alibcLogin = AlibcLogin.getInstance();
        alibcLogin.logout(new AlibcLoginCallback() {
            @Override
            public void onSuccess(int code, String openId, String userNick) {
                WritableMap map = Arguments.createMap();
                map.putInt("code", code);
                map.putString("message", "logout success");
                successCallback.invoke(map);
            }

            @Override
            public void onFailure(int code, String msg) {
                // code：错误码  msg： 错误信息
                WritableMap map = Arguments.createMap();
                map.putInt("code", code);
                map.putString("message", msg);
                errorCallback.invoke(map);
            }
        });
    }

    @ReactMethod
    public void show(final ReadableMap param, Callback successCallback, Callback errorCallback) {

        String type = this.getValue(param, "type");
        ReadableMap payload = param.getMap("payload");
        // 处理参数
        AlibcShowParams showParams = this.dealShowParams(payload);
        AlibcTaokeParams taokeParams = this.dealTaokeParams(payload);

        Map<String, String> trackParams = new HashMap<>();

        Activity nowActivity = getCurrentActivity();

        Log.v(TAG, type);
        switch (type) {
            case "url":
                this.openByUrl(payload.getString("url"), payload, successCallback, errorCallback);
                break;
            case "detail":
                this.openByBizCode(new AlibcDetailPage(payload.getString("itemid")), "detail", payload, successCallback, errorCallback);
                break;
            case "shop":
                this.openByBizCode(new AlibcShopPage(payload.getString("shopId")), "shop", payload, successCallback, errorCallback);
                break;
            case "orders":
                this.openByBizCode(new AlibcMyOrdersPage(payload.getInt("orderStatus"), payload.getBoolean("allOrder")), "order", payload, successCallback, errorCallback);
                break;
            case "addCart":
                this.openByBizCode(new AlibcAddCartPage(payload.getString("itemid")), "detail", payload, successCallback, errorCallback);
                break;
            case "mycart":
                this.openByBizCode(new AlibcMyCartsPage(), "cart", payload, successCallback, errorCallback);
                break;
            default:
                WritableMap map = Arguments.createMap();
                map.putString("message", INVALID_PARAM);
                errorCallback.invoke(map);
                break;
        }
    }

    private void openByUrl(String url, ReadableMap payload, Callback successCallback, Callback errorCallback) {
        // 处理参数
        AlibcShowParams showParams = this.dealShowParams(payload);
        AlibcTaokeParams taokeParams = this.dealTaokeParams(payload);
        Map<String, String> trackParams = new HashMap<>();

        AlibcTrade.openByUrl(getCurrentActivity(),
                "",
                url,
                null,
                new WebViewClient(),
                new WebChromeClient(),
                showParams,
                taokeParams,
                trackParams,
                this.dealCallback(successCallback, errorCallback));
    }

    private void openByBizCode(AlibcBasePage page, String bizcode, ReadableMap payload, Callback successCallback, Callback errorCallback) {
        // 处理参数
        AlibcShowParams showParams = this.dealShowParams(payload);
        AlibcTaokeParams taokeParams = this.dealTaokeParams(payload);
        Map<String, String> trackParams = new HashMap<>();

        AlibcTrade.openByBizCode(getCurrentActivity(),
                page,
                null,
                new WebViewClient(),
                new WebChromeClient(),
                bizcode,
                showParams,
                taokeParams,
                trackParams,
                this.dealCallback(successCallback, errorCallback));
    }


    /**
     * 处理showParams公用参数
     */
    private AlibcShowParams dealShowParams(final ReadableMap payload) {

        AlibcShowParams showParams = new AlibcShowParams();
        // 初始化参数
        String openType = "auto";
        String clientType = "taobao";
        String BACK_URL = "alisdk://";

        if (!this.isEmpty(this.getValue(payload, "openType"))) {
            openType = this.getValue(payload, "openType");
        }
        if (!this.isEmpty(this.getValue(payload, "clientType"))) {
            clientType = this.getValue(payload, "clientType");
        }
        if (!this.isEmpty(this.getValue(payload, "BACK_URL"))) {
            BACK_URL = this.getValue(payload, "BACK_URL");
        }

        if (openType.equals("native")) {
            showParams.setOpenType(OpenType.Native);
        } else {
            showParams.setOpenType(OpenType.Auto);
        }
        showParams.setClientType(clientType);
        showParams.setBackUrl(BACK_URL);
        showParams.setNativeOpenFailedMode(AlibcFailModeType.AlibcNativeFailModeJumpH5);

        return showParams;
    }

    /**
     * 处理taokeParams公用参数
     */
    private AlibcTaokeParams dealTaokeParams(final ReadableMap payload) {
        // 初始化参数
        String adzoneId = this.getValue(payload, "adzoneId");
        String pid = this.getValue(payload, "pid");
        String subPid = this.getValue(payload, "subPid");
        String unionId = this.getValue(payload, "unionId");

        String taokeAppkey = this.getValue(payload, "taokeAppkey");
        String sellerId = this.getValue(payload, "sellerId");

        AlibcTaokeParams taokeParams = new AlibcTaokeParams("", "", "");
        taokeParams.setAdzoneid(adzoneId);
        taokeParams.setPid(pid);
        taokeParams.setSubPid(subPid);
        taokeParams.setUnionId(unionId);

        if (!this.isEmpty(taokeAppkey)) {
            taokeParams.extraParams.put("taokeAppkey", taokeAppkey);
        }
        if (!this.isEmpty(sellerId)) {
            taokeParams.extraParams.put("sellerId", sellerId);
        }

        return taokeParams;
    }

    private boolean isEmpty(String string) {
        return string == null || string.equals("");
    }

    private String getValue(final ReadableMap payload, String key) {
        return payload.hasKey(key) ? payload.getString(key) : "";
    }

    private int getInt(final ReadableMap payload, String key) {
        return payload.hasKey(key) ? payload.getInt(key) : -1;
    }

    private AlibcTradeCallback dealCallback(Callback successCallback, Callback errorCallback) {
        return new AlibcTradeCallback() {
            @Override
            public void onTradeSuccess(AlibcTradeResult tradeResult) {

                Log.v("ReactNative", TAG + ":onTradeSuccess");
                //打开电商组件，用户操作中成功信息回调。tradeResult：成功信息（结果类型：加购，支付；支付结果）
                if (tradeResult.resultType.equals(AlibcResultType.TYPECART)) {
                    //加购成功
                    WritableMap map = Arguments.createMap();
                    map.putString("type", "card");
                    successCallback.invoke(map);
                } else if (tradeResult.resultType.equals(AlibcResultType.TYPEPAY)) {
                    //支付成功
                    WritableMap map = Arguments.createMap();
                    map.putString("type", "pay");
                    map.putString("orders", "" + tradeResult.payResult.paySuccessOrders);
                    successCallback.invoke(map);
                } else {
                    WritableMap map = Arguments.createMap();
                    map.putString("message", INVALID_TRADE_RESULT);
                    successCallback.invoke(map);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
                WritableMap map = Arguments.createMap();
                map.putInt("code", code);
                map.putString("message", msg);
                errorCallback.invoke(map);
            }
        };
    }

}
