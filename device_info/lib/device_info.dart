import 'dart:async';

import 'package:flutter/services.dart';

class DeviceInfo {
  static const MethodChannel _channel = const MethodChannel('device_info');
  static Future<String> get platformVersion => _channel.invokeMethod('platformVersion');
  static Future<String> get systemVersion => _channel.invokeMethod('systemVersion');
  static Future<String> get model => _channel.invokeMethod('model');
  static Future<String> get modelName => _channel.invokeMethod('modelName');
  static Future<String> get versionName => _channel.invokeMethod('versionName');
}
