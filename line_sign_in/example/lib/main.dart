import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:line_sign_in/line_sign_in.dart';

void main() {
  runApp(new MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => new _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _message;

  @override
  initState() {
    super.initState();
  }
  
  Future<Null> _signInWithLine() async {
    try {
      LineSession session = await LineSignIn.signIn();
      setState((){
        _message = '${session.id} ${session.name} ${session.token.substring(0, 4)}...';
      });
    } on PlatformException catch (ex) {
      setState((){
        _message = ex.toString();
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      home: new Scaffold(
        appBar: new AppBar(
          title: new Text('Plugin example app'),
        ),
        body: new Center(
          child: new RaisedButton(
            child: new Text(_message ?? 'Sign in with LINE'),
            onPressed: () {
              _signInWithLine();
            },
          ),
        ),
      ),
    );
  }
}
