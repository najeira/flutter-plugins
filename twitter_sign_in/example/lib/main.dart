import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:twitter_sign_in/twitter_sign_in.dart';

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
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  initPlatformState() async {
    String message;
    try {
      await TwitterSignIn.init("dummyToken", "dummySecret");
      message = 'init';
    } on PlatformException catch (ex) {
      message = ex.toString();
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted)
      return;

    setState(() {
      _message = message;
    });
  }

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
      home: new Scaffold(
        appBar: new AppBar(
          title: new Text('Twitter plugin app'),
        ),
        body: new Center(
          child: new Text(_message),
        ),
      ),
    );
  }
}
