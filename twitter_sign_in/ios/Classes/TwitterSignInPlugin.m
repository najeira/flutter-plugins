#import "TwitterSignInPlugin.h"
#import <twitter_sign_in/twitter_sign_in-Swift.h>

@implementation TwitterSignInPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftTwitterSignInPlugin registerWithRegistrar:registrar];
}
@end
