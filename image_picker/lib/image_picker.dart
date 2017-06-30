import 'dart:async';

import 'package:flutter/services.dart';

class ImagePicker {
  static const MethodChannel _channel = const MethodChannel('image_picker');
  static Future<String> pick() async {
    return _channel.invokeMethod('pick');
  }
}
