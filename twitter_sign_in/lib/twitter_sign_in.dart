import 'dart:async';

import 'package:flutter/services.dart';

class TwitterSignIn {
  static const MethodChannel _channel = const MethodChannel('twitter_sign_in');
  
  // Start Twitter with your consumer key and secret.
  // These will override any credentials present in your applications Info.plist or AndroidManifest.xml.
  static Future<Null> init(String consumer, String secret) async {
    return _channel.invokeMethod('init', <String, String>{"consumer": consumer, "secret": secret});
  }
  
  // Triggers user authentication with Twitter.
  // This method will present UI to allow the user to log in if there are no saved Twitter login credentials.
  static Future<TwitterSession> signIn() async {
    final Map<String, dynamic> data = await _channel.invokeMethod('signIn');
    var ts = new TwitterSession._(data);
    return ts;
  }
}

class TwitterSession {
  final Map<String, dynamic> _data;
  
  TwitterSession._(this._data);
  
  String get authToken => this._data['authToken'];
  
  String get authTokenSecret => this._data['authTokenSecret'];
  
  String get userName => this._data['userName'];
  
  int get userID => _getInt(this._data['userID']);
  
  @override
  String toString() {
    return '$runtimeType($_data)';
  }
}

int _getInt(dynamic value) {
  if (value == null) {
    return 0;
  } else if (value is int) {
    return value;
  } else if (value is num) {
    return value.toInt();
  } else if (value is String) {
    return int.parse(value);
  }
  return 0;
}
