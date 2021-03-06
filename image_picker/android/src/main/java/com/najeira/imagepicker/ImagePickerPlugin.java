package com.najeira.imagepicker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Map;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * ImagePickerPlugin
 */
public class ImagePickerPlugin implements MethodCallHandler,
        PluginRegistry.ActivityResultListener, PluginRegistry.RequestPermissionResultListener {
    private Activity activity;
    private ImagePicker imagePicker;

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "image_picker");
        final ImagePickerPlugin plugin = new ImagePickerPlugin(registrar.activity());
        channel.setMethodCallHandler(plugin);
        registrar.addActivityResultListener(plugin);
        registrar.addRequestPermissionResultListener(plugin);
    }

    private ImagePickerPlugin(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onMethodCall(MethodCall call, final Result result) {
        if ("pick".equals(call.method)) {
            boolean isCrop = true;
            if (call.arguments instanceof Map) {
                final Map obj = (Map) call.arguments;
                final Object objCrop = obj.get("crop");
                if (objCrop != null && objCrop instanceof Boolean) {
                    isCrop = (boolean) objCrop;
                }
            }
            try {
                imagePicker = new ImagePicker();
                imagePicker.setTitle("Photo");
                imagePicker.setCropImage(isCrop);
                imagePicker.startChooser(activity, new ImagePickerCallback(result));
            } catch (Exception ex) {
                result.error("pick", ex.getLocalizedMessage(), null);
            }
        } else {
            result.notImplemented();
        }
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (imagePicker != null) {
            imagePicker.onActivityResult(activity, requestCode, resultCode, data);
        }
        return false;
    }

    @Override
    public boolean onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        if (imagePicker != null) {
            imagePicker.onRequestPermissionsResult(activity, requestCode, permissions, grantResults);
        }
        return false;
    }

    private static class ImagePickerCallback extends ImagePicker.Callback {
        private Result flutterResult;

        private ImagePickerCallback(Result result) {
            this.flutterResult = result;
        }

        @Override
        public void onPickImage(Uri imageUri) {
            if (flutterResult != null) {
                flutterResult.success(imageUri.getPath());
                flutterResult = null;
            }
        }

        @Override
        public void onCancel() {
            if (flutterResult != null) {
                flutterResult.success(null);
                flutterResult = null;
            }
        }

        @Override
        public void onCropImage(Uri imageUri) {
            if (flutterResult != null) {
                flutterResult.success(imageUri.getPath());
                flutterResult = null;
            }
        }

        public void cropConfig(CropImage.ActivityBuilder builder) {
            builder.setMultiTouchEnabled(false)
                    .setCropShape(CropImageView.CropShape.RECTANGLE)
                    .setRequestedSize(640, 640)
                    .setAspectRatio(1, 1);
        }

        public void onPermissionDenied(int requestCode, String permissions[], int[] grantResults) {
            if (flutterResult != null) {
                flutterResult.error("pick", "permission", null);
                flutterResult = null;
            }
        }
    }
}
