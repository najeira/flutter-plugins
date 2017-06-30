import 'dart:async';

import 'package:flutter/services.dart';

class FacebookSignIn {
  static const MethodChannel _channel =
      const MethodChannel('facebook_sign_in');

  static Future<String> get platformVersion =>
      _channel.invokeMethod('getPlatformVersion');
}
