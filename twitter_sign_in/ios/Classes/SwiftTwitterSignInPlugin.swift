import Flutter
import UIKit
    
public class SwiftTwitterSignInPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    let channel = FlutterMethodChannel(name: "twitter_sign_in", binaryMessenger: registrar.messenger());
    let instance = SwiftTwitterSignInPlugin();
    registrar.addMethodCallDelegate(instance, channel: channel);
  }

  public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
    result("iOS " + UIDevice.current.systemVersion);
  }
}
