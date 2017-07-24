#import "LineSignInPlugin.h"

@implementation LineSignInPlugin {
    FlutterResult _result;
}

+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    FlutterMethodChannel* channel = [FlutterMethodChannel
                                     methodChannelWithName:@"line_sign_in"
                                     binaryMessenger:[registrar messenger]];
    LineSignInPlugin* instance = [[LineSignInPlugin alloc] init];
    [registrar addApplicationDelegate:instance];
    [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    if (_result) {
        _result(nil);
        _result = nil;
    }
    if ([@"signIn" isEqualToString:call.method]) {
        _result = result;
        [LineSDKLogin sharedInstance].delegate = self;
        [[LineSDKLogin sharedInstance] startLogin];
    } else if ([@"hasApp" isEqualToString:call.method]) {
        BOOL canLogin = [[LineSDKLogin sharedInstance] canLoginWithLineApp];
        result(@(canLogin));
    } else {
        result(FlutterMethodNotImplemented);
    }
}

- (void)didLogin:(LineSDKLogin *)login
      credential:(LineSDKCredential *)credential
         profile:(LineSDKProfile *)profile
           error:(NSError *)error {
    if (!_result) {
        return;
    }

    if (error) {
        // Login failed with an error. Use the error parameter to identify the problem.
        _result([FlutterError errorWithCode:@"line_sign_in"
                                     message:error.localizedDescription
                                     details:nil]);
    }
    else {
        //NSString *pictureUrlString;
        //if (pictureURL) {
        //    pictureUrlString = profile.pictureURL.absoluteString;
        //}
        _result(@{@"token": credential.accessToken.accessToken,
                   @"userID": profile.userID,
                   @"userName": profile.displayName,
                   @"expires": @(credential.accessToken.expiresIn)});
    }
}

- (BOOL)application:(UIApplication *)app openURL:(NSURL *)url options:(NSDictionary *)options {
    return [[LineSDKLogin sharedInstance] handleOpenURL:url];
}

@end
