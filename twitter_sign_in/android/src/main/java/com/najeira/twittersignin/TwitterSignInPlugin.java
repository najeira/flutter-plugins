package com.najeira.twittersignin;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Logger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.util.HashMap;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * TwitterSignInPlugin
 */
public class TwitterSignInPlugin implements MethodCallHandler,
        PluginRegistry.ActivityResultListener {
    private Activity activity;
    private TwitterAuthClient authClient;

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "twitter_sign_in");
        final TwitterSignInPlugin plugin = new TwitterSignInPlugin(registrar.activity());
        channel.setMethodCallHandler(plugin);
        registrar.addActivityResultListener(plugin);
    }

    private TwitterSignInPlugin(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if ("init".equals(call.method)) {
            final String consumer = call.argument("consumer");
            final String secret = call.argument("secret");
            final TwitterAuthConfig authConfig = new TwitterAuthConfig(consumer, secret);
            final Logger logger = new DefaultLogger(Log.DEBUG);
            final Object debug = call.argument("debug");
            final TwitterConfig config = new TwitterConfig.Builder(activity)
                    .logger(logger)
                    .twitterAuthConfig(authConfig)
                    .debug(debug != null ? (Boolean) debug : false)
                    .build();
            Twitter.initialize(config);
            result.success(null);
        } else if ("signIn".equals(call.method)) {
            if (authClient == null) {
                authClient = new TwitterAuthClient();
            }
            authClient.authorize(activity, new TwitterAuthorizeCallback(result));
        } else if ("signIn".equals(call.method)) {
            if (authClient != null) {
                authClient.cancelAuthorize();
            }
            result.success(null);
        } else {
            result.notImplemented();
        }
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (authClient != null) {
            authClient.onActivityResult(requestCode, resultCode, data);
        }
        return false;
    }

    private static class TwitterAuthorizeCallback extends Callback<TwitterSession> {
        private final Result flutterResult;
        private TwitterAuthorizeCallback(Result result) {
            this.flutterResult = result;
        }

        @Override
        public void success(com.twitter.sdk.android.core.Result<TwitterSession> result) {
            final HashMap<String, Object> data = new HashMap<>();
            final TwitterSession session = result.data;
            if (session != null) {
                data.put("userID", session.getUserId());
                data.put("userName", session.getUserName());
                final TwitterAuthToken authToken = session.getAuthToken();
                if (authToken != null) {
                    data.put("authToken", authToken.token);
                    data.put("authTokenSecret", authToken.secret);
                }
            }
            flutterResult.success(data);
        }

        @Override
        public void failure(TwitterException exception) {
            flutterResult.error("signIn", null, exception.getLocalizedMessage());
        }
    }
}
