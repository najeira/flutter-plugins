package com.najeira.facebooksignin;

import android.app.Activity;
import android.content.Intent;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * FacebookSignInPlugin
 */
public class FacebookSignInPlugin implements MethodCallHandler, PluginRegistry.ActivityResultListener {
    private Activity activity;
    private CallbackManager callbackManager;

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "facebook_sign_in");
        final FacebookSignInPlugin plugin = new FacebookSignInPlugin(registrar.activity());
        channel.setMethodCallHandler(plugin);
        registrar.addActivityResultListener(plugin);
    }

    private FacebookSignInPlugin(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if ("signIn".equals(call.method)) {
            Collection<String> permissions = null;
            Object permissionsObject = call.argument("permissions");
            if (permissionsObject != null && permissionsObject instanceof Collection) {
                permissions = (Collection<String>) permissionsObject;
            }

            final LoginManager loginManager = LoginManager.getInstance();
            if (callbackManager == null) {
                callbackManager = CallbackManager.Factory.create();
            }
            loginManager.registerCallback(callbackManager, new FacebookLoginCallback(result));
            loginManager.logInWithReadPermissions(activity, permissions);
        } else if ("signOut".equals(call.method)) {
            final LoginManager loginManager = LoginManager.getInstance();
            loginManager.logOut();
            result.success(null);
        } else {
            result.notImplemented();
        }
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        return false;
    }

    private static class FacebookLoginCallback implements com.facebook.FacebookCallback<LoginResult> {
        private Result flutterResult;

        private FacebookLoginCallback(Result result) {
            this.flutterResult = result;
        }

        @Override
        public void onSuccess(LoginResult result) {
            final HashMap<String, Object> data = new HashMap<>();
            final AccessToken accessToken = result.getAccessToken();
            if (accessToken != null) {
                data.put("userID", accessToken.getUserId());
                data.put("token", accessToken.getToken());
                data.put("appID", accessToken.getApplicationId());
                data.put("expires", dateToUnixtime(accessToken.getExpires()));
                data.put("lastRefresh", dateToUnixtime(accessToken.getLastRefresh()));
            }
            flutterResult.success(data);
        }

        static private Long dateToUnixtime(Date date) {
            if (date == null) {
                return null;
            }
            return date.getTime() / 1000;
        }

        @Override
        public void onCancel() {
            flutterResult.error("signIn", "cancel", null);
        }

        @Override
        public void onError(FacebookException error) {
            flutterResult.error("signIn", error.getLocalizedMessage(), null);
        }
    }
}
