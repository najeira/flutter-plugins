# facebook_sign_in

A Flutter plugin for Facebook Sign In.

## Getting Started

### Android

See: https://developers.facebook.com/docs/facebook-login/android

Open your strings.xml file. For example: /app/src/main/res/values/strings.xml.
Add a new string with the name facebook_app_id containing the value of your Facebook App ID, 
and a new string for the protocol for Chrome Custom Tabs:

```
<string name="facebook_app_id">486215874781246</string>
<string name="fb_login_protocol_scheme">fb486215874781246</string>
```

Open AndroidManifest.xml.
Add a uses-permission element to the manifest:

```
<uses-permission android:name="android.permission.INTERNET"/>
```

Add a meta-data element, an activity for Facebook, 
and an activity and intent filter for Chrome Custom Tabs to the application element:

```
<application android:label="@string/app_name" ...>;
    ...
    <meta-data android:name="com.facebook.sdk.ApplicationId" 
        android:value="@string/facebook_app_id"/>
    ...
    <activity android:name="com.facebook.FacebookActivity"
        android:configChanges=
                "keyboard|keyboardHidden|screenLayout|screenSize|orientation"
        android:label="@string/app_name" />
    <activity
        android:name="com.facebook.CustomTabActivity"
        android:exported="true">
        <intent-filter>
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.DEFAULT" />
            <category android:name="android.intent.category.BROWSABLE" />
            <data android:scheme="@string/fb_login_protocol_scheme" />
        </intent-filter>
    </activity>
</application>
```

And more, See: https://developers.facebook.com/docs/facebook-login/android

## iOS
