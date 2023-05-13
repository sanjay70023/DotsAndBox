package com.tiptop.dotsandboxes.imagepicker;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import com.tiptop.dotsandboxes.R;
import com.tiptop.dotsandboxes.utils.FileUtils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import timber.log.Timber;

import static com.tiptop.dotsandboxes.imagepicker.Cropper.CROP_TYPE_OVAL;


/**
 * Created by agile-01 on 6/10/2016.
 * <p>
 * get instance using {@link ImagePickerFragment#newInstance} <br>
 * call {@link ImagePickerFragment#captureImageFromCamera} for camera <br>
 * call {@link ImagePickerFragment#selectImageFromGallery} for gallery <br>
 * <p>
 * Call {@link ImagePickerFragment#processActivityResult}  <br>
 * inside activity/fragment's {@link Activity#onActivityResult} to get result bitmap from camera/gallery<br>
 * <p>
 * Call {@link ImagePickerFragment#processPermissionResults}  <br>
 * inside activity/fragment's {@link Activity#onRequestPermissionsResult}  for marshmallow permissions<br>
 * <p>
 * Call  {@link #removeCallbacks()} on destroy of activity/fragment<br/>
 * <p>
 * Never call {@link #getActivity()} / {@link #getResources()} directly inside this fragment<br/>
 * since this fragment will never be attached to activity but to be accessed from another fragment.<br>
 * doing this will throw an {@linkplain IllegalStateException}
 */
public class ImagePickerFragment extends Fragment {

    //region vars
    public static final int REQUEST_CAPTURE_FROM_CAMERA = 1001;
    public static final int REQUEST_SELECT_FROM_GALLERY = 10002;

    public static final int REQUEST_PERMISSION_CAMERA = 501;
    public static final int REQEST_PERMISSION_FOR_GALLRY = 502;


    private static final String[] PERMISSIONS_FOR_CAMERA = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };
    private static final String[] PERMISSIONS_FOR_GALLERY = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    public MarshMallowHelper marshMallowHelper;
    public Uri mCameraImageUri;
    private ImagePickerCallback imagePickerCallback;
    private boolean isCropImageEnabled;
    private Cropper cropper;
    //  public String imageFileName;


    public ImagePickerFragment() {

    }
    //endregion

    public static ImagePickerFragment newInstance(@NonNull ImagePickerCallback imagePickerCallback, @NonNull MarshMallowHelper marshMallowHelper) {
        ImagePickerFragment imagePickerFragment = new ImagePickerFragment();
        imagePickerFragment.setImagePickerCallback(imagePickerCallback);
        imagePickerFragment.setCropEnabled(true);
        imagePickerFragment.marshMallowHelper = marshMallowHelper;
        return imagePickerFragment;
    }

    //region public main methods
    public void processActivityResult(int requestCode, int resultCode, Intent data, AppCompatActivity activity) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case Activity.RESULT_OK:
                switch (requestCode) {
                    case REQUEST_CAPTURE_FROM_CAMERA:
                        onCameraActivityResult(mCameraImageUri, activity);
                        break;

                    case REQUEST_SELECT_FROM_GALLERY:
                        if (isImageValid(activity, data.getData())) {
                            onGalleryActivityResult(data.getData(), activity);
                        }
                        break;

                    case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                        onCropActivityResult(data, activity);
                        break;
                }
                break;

            case CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE:
                CropImage.ActivityResult error = CropImage.getActivityResult(data);
                imagePickerCallback.onErrorTakingImage(activity.getResources().getString(R.string.something_went_wrong), error.getError());

            case Activity.RESULT_CANCELED:
                if (imagePickerCallback != null) imagePickerCallback.onCancelTakingImage();
                break;
        }
    }

    public void processActivityResult(int requestCode, int resultCode, Intent data, Fragment fragment) {
        switch (resultCode) {
            case Activity.RESULT_OK:
                switch (requestCode) {
                    case REQUEST_CAPTURE_FROM_CAMERA:
                        onCameraActivityResult(mCameraImageUri, fragment);
                        break;

                    case REQUEST_SELECT_FROM_GALLERY:
                        if (isImageValid(fragment.getActivity(), data.getData())) {
                            onGalleryActivityResult(data.getData(), fragment);
                        }
                        break;

                    case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                        onCropActivityResult(data, fragment.getActivity());
                        break;
                }
                break;

            case CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE:
                CropImage.ActivityResult error = CropImage.getActivityResult(data);
                imagePickerCallback.onErrorTakingImage(fragment.getResources().getString(R.string.something_went_wrong), error.getError());
                break;

            case Activity.RESULT_CANCELED:
                if (imagePickerCallback != null) imagePickerCallback.onCancelTakingImage();
                break;
        }
    }

    public void processPermissionResults(int requestCode, @NonNull int[] grantResults, AppCompatActivity activity) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CAMERA:
                onCameraPermissionResult(activity, grantResults);
                break;


            case REQEST_PERMISSION_FOR_GALLRY:
                onGalleryPermissionResult(activity, grantResults);
                break;
        }
    }

    public void processPermissionResults(int requestCode, @NonNull int[] grantResults, Fragment fragment) {
        switch (requestCode) {
            case REQUEST_PERMISSION_CAMERA:
                onCameraPermissionResult(fragment, grantResults);
                break;


            case REQEST_PERMISSION_FOR_GALLRY:
                onGalleryPermissionResult(fragment, grantResults);
                break;
        }
    }

    public void captureImageFromCamera(AppCompatActivity activity) {
        if (!marshMallowHelper.isPermissionGranted(activity, REQUEST_PERMISSION_CAMERA, PERMISSIONS_FOR_CAMERA)) {
            return;
        }
        Intent intent = getCameraIntent(activity);
        if (intent == null && imagePickerCallback != null) {
            imagePickerCallback.onErrorTakingImage(activity.getResources().getString(R.string.something_went_wrong), new Throwable("Image Name is null"));
            return;
        }
        activity.startActivityForResult(intent, REQUEST_CAPTURE_FROM_CAMERA);
    }

    public void captureImageFromCamera(Fragment fragment) {
        if (!marshMallowHelper.isPermissionGranted(fragment, REQUEST_PERMISSION_CAMERA, PERMISSIONS_FOR_CAMERA)) {
            return;
        } else {
            marshMallowHelper.isPermissionGranted(fragment, REQUEST_PERMISSION_CAMERA, PERMISSIONS_FOR_CAMERA);
        }

        Intent intent = getCameraIntent(fragment.getActivity());
        if (intent == null && imagePickerCallback != null) {
            imagePickerCallback.onErrorTakingImage(fragment.getResources().getString(R.string.something_went_wrong), new Throwable("Image Name is null"));
            return;
        }
        fragment.startActivityForResult(intent, REQUEST_CAPTURE_FROM_CAMERA);
    }

    public void selectImageFromGallery(AppCompatActivity activity) {
        if (!marshMallowHelper.isPermissionGranted(activity, REQEST_PERMISSION_FOR_GALLRY, PERMISSIONS_FOR_GALLERY)) {
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, activity.getResources().getString(R.string.select_picture)), REQUEST_SELECT_FROM_GALLERY);
    }

    public void selectImageFromGallery(Fragment fragment) {
        if (!marshMallowHelper.isPermissionGranted(fragment, REQEST_PERMISSION_FOR_GALLRY, PERMISSIONS_FOR_GALLERY)) {
            return;
        }
       /* if (Build.VERSION.SDK_INT < 19) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            fragment.startActivityForResult(Intent.createChooser(intent, fragment.getResources().getString(R.string.select_picture)), REQUEST_SELECT_FROM_GALLERY);
        } else {*/
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        fragment.startActivityForResult(intent, REQUEST_SELECT_FROM_GALLERY);
        /*}*/
    }

    public void removeCallbacks() {
        if (imagePickerCallback != null)
            imagePickerCallback = null;
    }
    //endregion

    //region public optional methods
    public void setImagePickerCallback(ImagePickerCallback imagePickerCallback) {
        this.imagePickerCallback = imagePickerCallback;
    }

    public void setCropEnabled(boolean cropEnabled) {
        this.isCropImageEnabled = cropEnabled;
        cropper = new Cropper();
    }

    public void setCropAspectRatio(int cropperWidth, int cropperHeight) {
        if (!isCropImageEnabled) {
            throw new IllegalStateException("you need to set crop enabled before setting crop ratio.");
        }
        cropper.cropperWidth = cropperWidth;
        cropper.cropperHeight = cropperHeight;
    }

    public void setCropType(@Cropper.CropperType int cropperType) {
        if (!isCropImageEnabled) {
            throw new IllegalStateException("you need to set crop enabled before setting crop type.");
        }
        cropper.cropperType = cropperType;
    }
    //endregion

    //region private methods
    private void onCameraActivityResult(Uri uri, Fragment fragment) {
        if (isCropImageEnabled) {
            startCropping(uri, fragment);
        } else {
            handleResultFromCamera(uri, fragment.getActivity());
        }
    }

    private void onCameraActivityResult(Uri uri, AppCompatActivity activity) {
        if (isCropImageEnabled) {
            startCropping(uri, activity);
        } else {
            handleResultFromCamera(uri, activity);
        }
    }

    private void onGalleryActivityResult(Uri uri, AppCompatActivity activity) {
        if (isCropImageEnabled) {
            startCropping(uri, activity);
        } else {
            handleResultFromGallery(uri, activity);
        }
    }

    private void onGalleryActivityResult(Uri uri, Fragment fragment) {
        if (isCropImageEnabled) {
            startCropping(uri, fragment);
        } else {
            handleResultFromGallery(uri, fragment.getActivity());
        }
    }

    private void onCropActivityResult(Intent data, Activity activity) {
        CropImage.ActivityResult result = CropImage.getActivityResult(data);
        imagePickerCallback.onCompleteTakingImage(result.getUri(), true);
    }

    private void onCameraPermissionResult(Fragment fragment, int[] grantResults) {
        if (marshMallowHelper.checkGrantResults(grantResults)) {
            captureImageFromCamera(fragment);
        } else {
            Toast.makeText(fragment.getActivity(), fragment.getResources().getString(R.string.app_has_no_permission_for_gallery), Toast.LENGTH_SHORT).show();
        }
    }

    private void onCameraPermissionResult(AppCompatActivity activity, int[] grantResults) {
        if (marshMallowHelper.checkGrantResults(grantResults)) {
            captureImageFromCamera(activity);
        } else {
            Toast.makeText(activity, activity.getResources().getString(R.string.app_has_no_permission_to_capture_photo), Toast.LENGTH_SHORT).show();
        }
    }

    private void onGalleryPermissionResult(Fragment fragment, int[] grantResults) {
        if (marshMallowHelper.checkGrantResults(grantResults)) {
            selectImageFromGallery(fragment);
        } else {
            Toast.makeText(fragment.getActivity(), fragment.getResources().getString(R.string.app_has_no_permission_for_gallery), Toast.LENGTH_SHORT).show();
        }
    }

    private void onGalleryPermissionResult(AppCompatActivity activity, int[] grantResults) {
        if (marshMallowHelper.checkGrantResults(grantResults)) {
            selectImageFromGallery(activity);
        } else {
            Toast.makeText(activity, activity.getResources().getString(R.string.app_has_no_permission_for_gallery), Toast.LENGTH_SHORT).show();
        }
    }

    @Nullable
    private Intent getCameraIntent(Context context) {
       /* Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String imageFileName = getImageFileName(context);
        if (imageFileName == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mCameraImageUri = FileProvider.getUriForFile(context, "com.udgamschool.fileprovider", new File(imageFileName));
        } else {
            mCameraImageUri = Uri.fromFile(new File(imageFileName));
        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraImageUri);

        return intent;*/

        ContentValues values = new ContentValues();
        String imageName = Calendar.getInstance().getTimeInMillis() + ".jpg";
        values.put(MediaStore.Images.Media.TITLE, imageName);
        mCameraImageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, mCameraImageUri);
        return intentCamera;
    }


    @Nullable
    private String getImageFileName(Context context) {
        File externalFilesDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
        if (externalFilesDir == null) return null;
        String path = externalFilesDir.getPath();

        return path + "/" + Calendar.getInstance().getTimeInMillis() + ".jpg";
    }

    private void startCropping(@Nullable Uri uri, Fragment fragment) {
        if (uri == null) {
            imagePickerCallback.onErrorTakingImage(fragment.getResources().getString(R.string.something_went_wrong), new Throwable("startCropping : data returned null on processActivityResult"));
            return;
        }
        getCropBuilder(uri).start(fragment.getActivity(), fragment);
    }

    private void startCropping(@Nullable Uri uri, AppCompatActivity activity) {
        if (uri == null) {
            imagePickerCallback.onErrorTakingImage(activity.getResources().getString(R.string.something_went_wrong), new Throwable("startCropping : data returned null on processActivityResult"));
            return;
        }
        getCropBuilder(uri).start(activity);
    }

    private CropImage.ActivityBuilder getCropBuilder(@NonNull Uri uri) {
        CropImage.ActivityBuilder mBuilder = CropImage.activity(uri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setFixAspectRatio(true)
                .setAutoZoomEnabled(true);
        if (cropper.cropperType == CROP_TYPE_OVAL) {
            mBuilder.setCropShape(CropImageView.CropShape.OVAL);
        } else {
            mBuilder.setCropShape(CropImageView.CropShape.RECTANGLE);
        }
        mBuilder.setAspectRatio(cropper.cropperWidth, cropper.cropperHeight);
        return mBuilder;
    }

    private void handleResultFromCamera(Uri uri, final Context context) {
        if (imagePickerCallback == null) {
            Timber.e("" + "handleResultFromCamera : Image picker callback is null");
            return;
        }

        if (uri == null) {
            imagePickerCallback.onErrorTakingImage(context.getResources().getString(R.string.something_went_wrong), new Throwable("handleResultFromGallery : data returned null on processActivityResult"));
            return;
        }

        imagePickerCallback.onCompleteTakingImage(uri, false);
    }

    private void handleResultFromGallery(@Nullable Uri uri, final Context context) {
        if (imagePickerCallback == null) {
            Timber.e("" + "handleResultFromGallery : Image picker callback is null");
            return;
        }
        if (uri == null) {
            imagePickerCallback.onErrorTakingImage(context.getResources().getString(R.string.something_went_wrong), new Throwable("handleResultFromGallery : data returned null on processActivityResult"));
            return;
        }
        imagePickerCallback.onCompleteTakingImage(uri, false);

    }
    //endregion

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        removeCallbacks();
    }
    //endregion

    /**
     * Check image is valid or not
     *
     * @param context - context
     * @param image   - image uri
     * @return - true if image is valid else false
     */
    public boolean isImageValid(Context context, Uri image) {
        String selectedImagePath = FileUtils.getPath(context, image);
        if (!TextUtils.isEmpty(selectedImagePath)) {
            File destination = new File(selectedImagePath);

            File file = FileUtils.getFile(context, image);

            //Check for corrupt image
            if (file.length() <= 0) {
                Toast.makeText(context, "You have selected invalid image. Please select any other and try again.", Toast.LENGTH_SHORT).show();
                return false;
            }

            //Check image is jpg/jpeg/png only
            String filePath = destination.getAbsolutePath();
            if (filePath.endsWith(".jpg")) {
                Timber.e("" + "JPG Image");
                return true;
            } else if (filePath.endsWith(".jpeg")) {
                Timber.e("" + "JPEG Image");
                return true;
            } else if (filePath.endsWith(".png")) {
                Timber.e("" + "PNG Image");
                return true;
            } else {
                Toast.makeText(context, "Please select valid image.", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            Toast.makeText(context, "Please select valid image.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}