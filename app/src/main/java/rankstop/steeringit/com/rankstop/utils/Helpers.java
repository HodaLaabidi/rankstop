package rankstop.steeringit.com.rankstop.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class Helpers {

    public static MultipartBody.Part prepareFilePart(String partName, Uri fileUri, Context context) {
        File file = FileUtils.getFile(context, fileUri);

        RequestBody requestFile = RequestBody.create(MediaType.parse(context.getContentResolver().getType(fileUri)), file);

        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }

    public static RequestBody createPartFormString(String value) {
        return RequestBody.create(MultipartBody.FORM, value);
    }

    public static Uri getImageUri(byte[] chartData, Context context) {
        Bitmap bm = BitmapFactory.decodeByteArray(chartData, 0, chartData.length);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bm, "Image Description", null);
        return Uri.parse(path);
    }
}
