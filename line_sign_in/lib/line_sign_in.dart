import 'dart:async';

import 'package:flutter/services.dart';

class LineSignIn {
  static const MethodChannel _channel = const MethodChannel('line_sign_in');

  // Triggers user authentication with LINE.
  static Future<LineSession> signIn() async {
    final Map<String, dynamic> data = await _channel.invokeMethod('signIn');
    var ts = new LineSession._(data);
    return ts;
  }
}

class LineSession {
  final Map<String, dynamic> _data;
  
  LineSession._(this._data);
  
  String get token => this._data['token'];
  
  String get name => this._data['userName'];
  
  String get id => this._data['userID'];
  
  @override
  String toString() {
    return '$runtimeType($_data)';
  }
}
