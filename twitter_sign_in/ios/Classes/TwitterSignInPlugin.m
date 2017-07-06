#import "TwitterSignInPlugin.h"
#import <TwitterKit/TwitterKit.h>

@interface NSError (FlutterError)
@property(readonly, nonatomic) FlutterError *flutterError;
@end

@implementation NSError (FlutterError)
- (FlutterError *)flutterError {
  return [FlutterError errorWithCode:[NSString stringWithFormat:@"Error %ld", (long)self.code]
                             message:self.domain
                             details:self.localizedDescription];
}
@end

@implementation TwitterSignInPlugin

+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"twitter_sign_in"
            binaryMessenger:[registrar messenger]];
  TwitterSignInPlugin* instance = [[TwitterSignInPlugin alloc] init];
  [registrar addApplicationDelegate:instance];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([call.method isEqualToString:@"init"]) {
    [[Twitter sharedInstance] startWithConsumerKey:call.arguments[@"consumer"] consumerSecret:call.arguments[@"secret"]];
    result(nil);
  } else if ([call.method isEqualToString:@"signIn"]) {
    [[Twitter sharedInstance] logInWithCompletion:^(TWTRSession *session, NSError *error) {
        if (error) {
          result(error.flutterError);
        } else {
          if (session) {
            result(@{@"authToken": session.authToken,
              @"authTokenSecret": session.authTokenSecret,
              @"userName": session.userName,
              @"userID": session.userID});
          }
        }
    }];
  } else {
    result(FlutterMethodNotImplemented);
  }
}

- (BOOL)application:(UIApplication *)app openURL:(NSURL *)url options:(NSDictionary *)options {
  return [[Twitter sharedInstance] application:app openURL:url options:options];
}

@end
