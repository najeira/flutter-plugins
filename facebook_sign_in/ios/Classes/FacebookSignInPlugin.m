#import "FacebookSignInPlugin.h"
#import <FBSDKCoreKit/FBSDKCoreKit.h>
#import <FBSDKLoginKit/FBSDKLoginKit.h>

@implementation FacebookSignInPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    FlutterMethodChannel* channel = [FlutterMethodChannel
                                     methodChannelWithName:@"facebook_sign_in"
                                     binaryMessenger:[registrar messenger]];
    FacebookSignInPlugin* instance = [[FacebookSignInPlugin alloc] init];
    [registrar addApplicationDelegate:instance];
    [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    if ([@"signIn" isEqualToString:call.method]) {
        FBSDKLoginManager *login = [[FBSDKLoginManager alloc] init];
        [login logInWithReadPermissions:call.arguments[@"permissions"]
                     fromViewController:nil
                                handler:^(FBSDKLoginManagerLoginResult *fbResult, NSError *error) {
                                    [self handleLoginWithCallback:result result:fbResult error:error];
                                }];
    } else {
        result(FlutterMethodNotImplemented);
    }
}

- (void)handleLoginWithCallback:(FlutterResult)callback result:(FBSDKLoginManagerLoginResult *)result error:(NSError *)error {
    if (error) {
        callback([FlutterError errorWithCode:[NSString stringWithFormat:@"Error %ld", (long)error.code]
                                     message:error.domain
                                     details:error.localizedDescription]);
    } else {
        if (result && result.token) {
            callback(@{@"token": result.token.tokenString,
                       @"appID": result.token.appID,
                       @"userID": result.token.userID,
                       @"expires": @(result.token.expirationDate.timeIntervalSince1970),
                       @"lastRefresh": @(result.token.refreshDate.timeIntervalSince1970)});
        } else {
            callback(nil);
        }
    }
}

- (BOOL)application:(UIApplication *)app openURL:(NSURL *)url options:(NSDictionary *)options {
    return [[FBSDKApplicationDelegate sharedInstance] application:app openURL:url options:options];
}

@end
