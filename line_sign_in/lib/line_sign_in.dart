import 'dart:async';

import 'package:flutter/services.dart';

class LineSignIn {
  static const MethodChannel _channel =
      const MethodChannel('line_sign_in');

  static Future<String> get platformVersion =>
      _channel.invokeMethod('getPlatformVersion');
}
