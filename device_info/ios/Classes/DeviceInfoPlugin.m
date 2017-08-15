#import "DeviceInfoPlugin.h"
#import <sys/utsname.h>

@implementation DeviceInfoPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"device_info"
            binaryMessenger:[registrar messenger]];
  DeviceInfoPlugin* instance = [[DeviceInfoPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"systemVersion" isEqualToString:call.method]) {
    result([[UIDevice currentDevice] systemVersion]);
  } else if ([@"model" isEqualToString:call.method]) {
    result([[UIDevice currentDevice] model]);
  } else if ([@"modelName" isEqualToString:call.method]) {
    result([self getModelName]);
  } else if ([@"versionName" isEqualToString:call.method]) {
    result([[NSBundle mainBundle] objectForInfoDictionaryKey: @"CFBundleShortVersionString"]);
  } else {
    result(FlutterMethodNotImplemented);
  }
}

- (NSString*)getModelName {
  struct utsname systemInfo;
  uname(&systemInfo);
  return [NSString stringWithCString:systemInfo.machine encoding:NSUTF8StringEncoding];
}

@end
