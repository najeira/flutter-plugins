package com.najeira.deviceinfo;

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
  /**
   * Plugin registration.
   */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "device_info");
    channel.setMethodCallHandler(new DeviceInfoPlugin());
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + Build.VERSION.RELEASE);
    } else if ("systemVersion".equals(call.method)) {
      result.success(Build.VERSION.RELEASE);
    } else if ("model".equals(call.method)) {
      result.success("Linux");
    } else if ("modelName".equals(call.method)) {
      result.success(Build.MODEL);
    } else {
      result.notImplemented();
    }
  }
}
