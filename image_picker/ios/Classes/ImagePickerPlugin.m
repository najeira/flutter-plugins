#import "ImagePickerPlugin.h"

@interface ImagePickerPlugin ()<UINavigationControllerDelegate, UIImagePickerControllerDelegate>
@end

@implementation ImagePickerPlugin {
    FlutterResult _result;
    UIImagePickerController *_imagePickerController;
    UIViewController *_viewController;
}

+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    FlutterMethodChannel* channel = [FlutterMethodChannel
                                     methodChannelWithName:@"image_picker"
                                     binaryMessenger:[registrar messenger]];
    UIViewController *viewController = [UIApplication sharedApplication].delegate.window.rootViewController;
    ImagePickerPlugin *instance = [[ImagePickerPlugin alloc] initWithViewController:viewController];
    [registrar addMethodCallDelegate:instance channel:channel];
}

- (instancetype)initWithViewController:(UIViewController *)viewController {
    self = [super init];
    if (self) {
        _viewController = viewController;
        _imagePickerController = [[UIImagePickerController alloc] init];
    }
    return self;
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
    if (_result) {
        _result(nil);
        _result = nil;
    }
    
    if ([@"pick" isEqualToString:call.method]) {
        _result = result;
        _imagePickerController.modalPresentationStyle = UIModalPresentationCurrentContext;
        _imagePickerController.delegate = self;
        
        if (call.arguments[@"crop"]) {
            _imagePickerController.allowsEditing = YES;
        }
        
        if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]) {
            UIAlertControllerStyle style = UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPad
            ? UIAlertControllerStyleAlert
            : UIAlertControllerStyleActionSheet;
            
            UIAlertController *alert = [UIAlertController alertControllerWithTitle:nil message:nil preferredStyle:style];
            [alert addAction:[UIAlertAction actionWithTitle:[self localizeString:@"Take Photo"]
                                                      style:UIAlertActionStyleDefault
                                                    handler:^(UIAlertAction *action) {
                                                        [self showCamera];
                                                    }]];
            [alert addAction:[UIAlertAction actionWithTitle:[self localizeString:@"Choose Photo"]
                                                      style:UIAlertActionStyleDefault
                                                    handler:^(UIAlertAction *action) {
                                                        [self showPhotoLibrary];
                                                    }]];
            [alert addAction:[UIAlertAction actionWithTitle:[self localizeString:@"Cancel"]
                                                      style:UIAlertActionStyleCancel
                                                    handler:^(UIAlertAction *action) {
                                                        [self sendResult:nil];
                                                    }]];
            [_viewController presentViewController:alert animated:YES completion:nil];
        } else {
            [self showPhotoLibrary];
        }
    } else {
        result(FlutterMethodNotImplemented);
    }
}

- (void)showCamera {
    _imagePickerController.sourceType = UIImagePickerControllerSourceTypeCamera;
    [_viewController presentViewController:_imagePickerController animated:YES completion:nil];
}

- (void)showPhotoLibrary {
    _imagePickerController.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
    [_viewController presentViewController:_imagePickerController animated:YES completion:nil];
}

- (void)imagePickerController:(UIImagePickerController *)picker
didFinishPickingMediaWithInfo:(NSDictionary<NSString *, id> *)info {
    [_imagePickerController dismissViewControllerAnimated:YES completion:nil];
    UIImage *image = [info objectForKey:UIImagePickerControllerEditedImage];
    if (image == nil) {
        image = [info objectForKey:UIImagePickerControllerOriginalImage];
    }
    if (image == nil) {
        image = [info objectForKey:UIImagePickerControllerCropRect];
    }
    image = [self normalizedImage:image];
    NSData *data = UIImageJPEGRepresentation(image, 1.0);
    NSString *tmpDirectory = NSTemporaryDirectory();
    NSString *guid = [[NSProcessInfo processInfo] globallyUniqueString];
    // TODO(jackson): Using the cache directory might be better than temporary
    // directory.
    NSString *tmpFile = [NSString stringWithFormat:@"image_picker_%@.jpg", guid];
    NSString *tmpPath = [tmpDirectory stringByAppendingPathComponent:tmpFile];
    if ([[NSFileManager defaultManager] createFileAtPath:tmpPath contents:data attributes:nil]) {
        [self sendResult:tmpPath];
    } else {
        [self sendError:@"Temporary file could not be created"];
    }
}

// The way we save images to the tmp dir currently throws away all EXIF data
// (including the orientation of the image). That means, pics taken in portrait
// will not be orientated correctly as is. To avoid that, we rotate the actual
// image data.
// TODO(goderbauer): investigate how to preserve EXIF data.
- (UIImage *)normalizedImage:(UIImage *)image {
    if (image.imageOrientation == UIImageOrientationUp) {
        return image;
    }
    UIGraphicsBeginImageContextWithOptions(image.size, NO, image.scale);
    [image drawInRect:(CGRect){0, 0, image.size}];
    UIImage *normalizedImage = UIGraphicsGetImageFromCurrentImageContext();
    UIGraphicsEndImageContext();
    return normalizedImage;
}

- (NSString *)localizeString:(NSString *)key {
    return NSLocalizedStringFromTableInBundle(key,
                                              @"ImagePickerPlugin",
                                              [NSBundle bundleForClass:ImagePickerPlugin.class],
                                              nil);
}

- (void)sendResult:(id _Nullable)result {
    if (_result) {
        _result(result);
        _result = nil;
    }
}

- (void)sendError:(NSString *)message {
    if (_result) {
        _result([FlutterError errorWithCode:@"image_picker"
                                    message:[self localizeString:message]
                                    details:nil]);
        _result = nil;
    }
}

@end
