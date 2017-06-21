import 'dart:async';

import 'package:flutter/services.dart';

class DeviceInfo {
  static const MethodChannel _channel =
      const MethodChannel('device_info');

  static Future<String> get platformVersion =>
      _channel.invokeMethod('getPlatformVersion');
}
