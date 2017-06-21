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
  if ([@"getPlatformVersion" isEqualToString:call.method]) {
    result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
  } else if ([@"systemVersion" isEqualToString:call.method]) {
    result([[UIDevice currentDevice] systemVersion]);
  } else if ([@"modelName" isEqualToString:call.method]) {
    result([self getModelName]);
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
