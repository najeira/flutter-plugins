package com.najeira.deviceinfo;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * DeviceInfoPlugin
 */
public class DeviceInfoPlugin implements MethodCallHandler {
  private Activity activity;

  /**
   * Plugin registration.
   */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "device_info");
    channel.setMethodCallHandler(new DeviceInfoPlugin(registrar));
  }

  private DeviceInfoPlugin(Registrar registrar) {
    this.activity = registrar.activity();
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if ("systemVersion".equals(call.method)) {
      result.success(Build.VERSION.RELEASE);
    } else if ("model".equals(call.method)) {
      result.success("Linux");
    } else if ("modelName".equals(call.method)) {
      result.success(Build.MODEL);
    } else if ("versionName".equals(call.method)) {
      try{
        PackageManager pm = activity.getPackageManager();
        String pn = activity.getPackageName();
        PackageInfo packageInfo = pm.getPackageInfo(pn, 0);
        result.success(packageInfo.versionName);
      } catch(Exception ex) {
        result.error("device_info", ex.toString(), null);
      }
    } else {
      result.notImplemented();
    }
  }
}
