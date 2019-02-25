package rankstop.steeringit.com.rankstop.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;

public class FileCompressor {

    //max width and height values of the compressed image is taken as 612x816
    private int maxWidth = 612;
    private int maxHeight = 816;
    private Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
    private int quality = 75;
    private String destinationDirectoryPath;

    public FileCompressor(Context context) {
        destinationDirectoryPath = context.getCacheDir().getPath() + File.separator + "images";
        Log.i("TAG_DESTINATION", destinationDirectoryPath);
    }

    public FileCompressor setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    public FileCompressor setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        return this;
    }

    public FileCompressor setCompressFormat(Bitmap.CompressFormat compressFormat) {
        this.compressFormat = compressFormat;
        return this;
    }

    public FileCompressor setQuality(int quality) {
        this.quality = quality;
        return this;
    }

    public FileCompressor setDestinationDirectoryPath(String destinationDirectoryPath) {
        this.destinationDirectoryPath = destinationDirectoryPath;
        return this;
    }

    public File compressToFile(File imageFile) throws IOException {
        return compressToFile(imageFile, imageFile.getName());
    }

    private File compressToFile(File imageFile, String compressedFileName) throws IOException {
        return ImageUtil.compressImage(imageFile, maxWidth, maxHeight, compressFormat, quality,
                destinationDirectoryPath + File.separator + compressedFileName);
    }

    private Bitmap compressToBitmap(File imageFile) throws IOException {
        return ImageUtil.decodeSampledBitmapFromFile(imageFile, maxWidth, maxHeight);
    }

    public Flowable<File> compressToFileAsFlowable(final File imageFile) {
        return compressToFileAsFlowable(imageFile, imageFile.getName());
    }

    private Flowable<File> compressToFileAsFlowable(final File imageFile, final String compressedFileName) {
        return Flowable.defer((Callable<Flowable<File>>) () -> {
            try {
                return Flowable.just(compressToFile(imageFile, compressedFileName));
            } catch (IOException e) {
                return Flowable.error(e);
            }
        });
    }

    public Flowable<Bitmap> compressToBitmapAsFlowable(final File imageFile) {
        return Flowable.defer((Callable<Flowable<Bitmap>>) () -> {
            try {
                return Flowable.just(compressToBitmap(imageFile));
            } catch (IOException e) {
                return Flowable.error(e);
            }
        });
    }

}
