package com.najeira.imagepicker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.List;

// based on https://github.com/linchaolong/ImagePicker
class ImagePicker {
    private static final String TAG = "ImagePicker";

    private Callback callback;
    private boolean isCropImage = true;
    private CharSequence title;

    private Uri cropImageUri;

    void setCropImage(boolean cropImage) {
        isCropImage = cropImage;
    }

    void setTitle(CharSequence title) {
        this.title = title;
    }

    private boolean requestCameraPermission(Activity activity) {
        if (!CropImage.isExplicitCameraPermissionRequired(activity)) {
            return false;
        }
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.CAMERA},
                CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        return true;
    }

    private boolean requestCameraPermission(Fragment fragment) {
        if (!CropImage.isExplicitCameraPermissionRequired(fragment.getActivity())) {
            return false;
        }
        fragment.requestPermissions(
                new String[]{Manifest.permission.CAMERA},
                CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE);
        return true;
    }

    void startChooser(Activity activity, @NonNull Callback callback) {
        this.callback = callback;
        if (!requestCameraPermission(activity)) {
            activity.startActivityForResult(
                    CropImage.getPickImageChooserIntent(activity, getTitle(activity), false, true),
                    CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE);
        }
    }

    public void startChooser(Fragment fragment, @NonNull Callback callback) {
        this.callback = callback;
        if (!requestCameraPermission(fragment)) {
            fragment.startActivityForResult(
                    CropImage.getPickImageChooserIntent(
                            fragment.getContext(), getTitle(fragment.getActivity()), false, true),
                    CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE);
        }
    }

    public void startCamera(Activity activity, @NonNull Callback callback) {
        this.callback = callback;
        if (!requestCameraPermission(activity)) {
            activity.startActivityForResult(
                    CropImage.getCameraIntent(activity, null),
                    CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE);
        }
    }

    public void startCamera(Fragment fragment, @NonNull Callback callback) {
        this.callback = callback;
        if (!requestCameraPermission(fragment)) {
            fragment.startActivityForResult(
                    CropImage.getCameraIntent(fragment.getActivity(), null),
                    CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE);
        }
    }

    public void startGallery(Activity activity, @NonNull Callback callback) {
        this.callback = callback;
        activity.startActivityForResult(
                getGalleryIntent(activity, false),
                CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE);
    }

    public void startGallery(Fragment fragment, @NonNull Callback callback) {
        this.callback = callback;
        fragment.startActivityForResult(
                getGalleryIntent(fragment.getActivity(), false),
                CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE);
    }

    private CharSequence getTitle(Context context) {
        if (TextUtils.isEmpty(title)) {
            return "Photo";
        }
        return title;
    }

    private Intent getGalleryIntent(Context context, boolean includeDocuments) {
        PackageManager packageManager = context.getPackageManager();

        List<Intent> galleryIntents = CropImage.getGalleryIntents(
                packageManager, Intent.ACTION_GET_CONTENT, includeDocuments);
        if (galleryIntents.size() == 0) {
            // if no intents found for get-content try pick intent action (Huawei P9).
            galleryIntents = CropImage.getGalleryIntents(packageManager, Intent.ACTION_PICK, includeDocuments);
        }

        Intent target;
        if (galleryIntents.isEmpty()) {
            target = new Intent();
        } else {
            target = galleryIntents.get(galleryIntents.size() - 1);
            galleryIntents.remove(galleryIntents.size() - 1);
        }

        // Create a chooser from the main  intent
        Intent chooserIntent = Intent.createChooser(target, getTitle(context));

        // Add all other intents
        chooserIntent.putExtra(
                Intent.EXTRA_INITIAL_INTENTS,
                galleryIntents.toArray(new Parcelable[galleryIntents.size()]));

        return chooserIntent;
    }

    boolean onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        return onActivityResultInner(activity, null, requestCode, resultCode, data);
    }

    boolean onActivityResult(Fragment fragment, int requestCode, int resultCode, Intent data) {
        return onActivityResultInner(null, fragment, requestCode, resultCode, data);
    }

    private boolean onActivityResultInner(Activity activity, Fragment fragment, int requestCode, int resultCode, Intent data) {
        Context context = (activity != null) ? activity : fragment.getContext();
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri pickImageUri = CropImage.getPickImageResultUri(context, data);
                if (CropImage.isReadExternalStoragePermissionsRequired(context, pickImageUri)) {
                    if (activity != null) {
                        ActivityCompat.requestPermissions(
                                activity,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                    } else {
                        fragment.requestPermissions(
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE);
                    }
                } else {
                    if (activity != null) {
                        handlePickImage(activity, pickImageUri);
                    } else {
                        handlePickImage(fragment, pickImageUri);
                    }
                }
            } else {
                if (callback != null) {
                    callback.onCancel();
                }
            }
            return true;
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                handleCropResult(context, CropImage.getActivityResult(data));
            } else {
                if (callback != null) {
                    callback.onCancel();
                }
            }
            return true;
        }
        return false;
    }

    private static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    boolean onRequestPermissionsResult(Activity activity, int requestCode, String permissions[],
                                    int[] grantResults) {
        return onRequestPermissionsResultInner(activity, null, requestCode, permissions, grantResults);
    }

    boolean onRequestPermissionsResult(Fragment fragment, int requestCode, String permissions[],
                                    int[] grantResults) {
        return onRequestPermissionsResultInner(null, fragment, requestCode, permissions, grantResults);
    }

    private boolean onRequestPermissionsResultInner(
            Activity activity, Fragment fragment, int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == CropImage.CAMERA_CAPTURE_PERMISSIONS_REQUEST_CODE) {
            if (grantResults != null
                    && grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (activity != null) {
                    CropImage.startPickImageActivity(activity);
                } else {
                    CropImage.startPickImageActivity(fragment.getActivity());
                }
            } else {
                if (callback != null) {
                    callback.onPermissionDenied(requestCode, permissions, grantResults);
                }
            }
            return true;
        } else if (requestCode == CropImage.PICK_IMAGE_PERMISSIONS_REQUEST_CODE) {
            if (cropImageUri != null
                    && grantResults != null
                    && grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (activity != null) {
                    handlePickImage(activity, cropImageUri);
                } else {
                    handlePickImage(fragment, cropImageUri);
                }
            } else {
                if (callback != null) {
                    callback.onPermissionDenied(requestCode, permissions, grantResults);
                }
            }
            return true;
        }
        return false;
    }

    private void handleCropResult(Context context, CropImage.ActivityResult result) {
        if (result.getError() == null) {
            cropImageUri = result.getUri();
            if (callback != null) {
                callback.onCropImage(handleUri(context, cropImageUri));
            }
        } else {
            Log.e(TAG, "handleCropResult error", result.getError());
        }
    }

    private void handlePickImage(Activity activity, Uri imageUri) {
        handlePickImageInner(activity, null, imageUri);
    }

    private void handlePickImage(Fragment fragment, Uri imageUri) {
        handlePickImageInner(null, fragment, imageUri);
    }

    private void handlePickImageInner(Activity activity, Fragment fragment, Uri imageUri) {
        if (callback != null) {
            Context context = (activity != null) ? activity : fragment.getContext();
            callback.onPickImage(handleUri(context, imageUri));
        }
        if (!isCropImage) {
            return;
        }
        CropImage.ActivityBuilder builder = CropImage.activity(imageUri);
        callback.cropConfig(builder);
        if (activity != null) {
            builder.start(activity);
        } else {
            builder.start(fragment.getActivity(), fragment);
        }
    }

    private Uri handleUri(Context context, Uri imageUri) {
        if ("content".equals(imageUri.getScheme())) {
            String realPathFromUri = getRealPathFromUri(context, imageUri);
            if (!TextUtils.isEmpty(realPathFromUri)) {
                return Uri.fromFile(new File(realPathFromUri));
            }
        }
        return imageUri;
    }

    static abstract class Callback {
        public abstract void onPickImage(Uri imageUri);

        public void onCropImage(Uri imageUri) {

        }

        public void onCancel() {

        }

        public void cropConfig(CropImage.ActivityBuilder builder) {
            builder.setMultiTouchEnabled(false)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setRequestedSize(640, 640)
                    .setAspectRatio(5, 5);
        }

        public void onPermissionDenied(int requestCode, String permissions[], int[] grantResults) {

        }
    }
}
