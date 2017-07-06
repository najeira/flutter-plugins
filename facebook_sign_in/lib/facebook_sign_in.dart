import 'dart:async';

import 'package:flutter/services.dart';

class FacebookSignIn {
  static const MethodChannel _channel = const MethodChannel('facebook_sign_in');
  
  // Logs the user in with the requested read permissions.
  // "public_profile", "email", "user_friends"
  static Future<FacebookSession> signIn({List<String> premissions}) async {
    var data = await _channel.invokeMethod(
      'signIn', <String, dynamic>{"premissions": premissions});
    if (data == null || !(data is Map)) {
      return null;
    }
    var ts = new FacebookSession._(data);
    return ts;
  }
}

class FacebookSession {
  final Map<String, dynamic> _data;
  
  FacebookSession._(this._data);

  String get token => this._data['token'];
  String get userID => this._data['userID'];
  String get applicationID => this._data['applicationID'];
  int get expires => this._data['expires'];
  int get lastRefresh => this._data['lastRefresh'];

  @override
  String toString() {
    return '$runtimeType($_data)';
  }
}
