import 'dart:async';

import 'package:flutter/services.dart';

class TwitterSignIn {
  static const MethodChannel _channel =
      const MethodChannel('twitter_sign_in');

  static Future<String> get platformVersion =>
      _channel.invokeMethod('getPlatformVersion');
}
