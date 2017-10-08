package info.trongdat.mp3cloud.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;

/**
 * Created with Android Studio.
 * User: ryan.hoo.j@gmail.com
 * Date: 9/14/16
 * Time: 8:42 PM
 * Desc: BitmapUtils
 * TODO To be optimized
 */
public class AlbumUtils {
    Context context;
    private static final String TAG = "AlbumUtils";

    public AlbumUtils(Context context) {
        this.context = context;
    }

//    public Bitmap getCover(int id) {
//        Uri sArtworkUri = Uri
//                .parse("content://media/external/audio/albumart");
//        Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, id);
//
//        Bitmap bitmap = null;
//        try {
//            bitmap = MediaStore.Images.Media.getBitmap(
//                    context.getContentResolver(), albumArtUri);
////            bitmap = Bitmap.createScaledBitmap(bitmap, 72, 72, true);
//
//        } catch (FileNotFoundException exception) {
//            exception.printStackTrace();
//            bitmap = BitmapFactory.decodeResource(context.getResources(),
//                    R.drawable.album_thumbnail2);
//        } catch (IOException e) {
//
//            e.printStackTrace();
//        }
//        return bitmap;
//    }
//
//    public static Bitmap parseAlbum(Album album) {
//        return parseAlbum(new File(album.getImage()));
//    }

//    public static Bitmap parseAlbum(File file) {
//        MediaMetadataRetriever metadataRetriever = new MediaMetadataRetriever();
//        try {
//            metadataRetriever.setDataSource(file.getAbsolutePath());
//        } catch (IllegalArgumentException e) {
//            Log.e(TAG, "parseAlbum: ", e);
//        }
//        byte[] albumData = metadataRetriever.getEmbeddedPicture();
//        if (albumData != null) {
//            return BitmapFactory.decodeByteArray(albumData, 0, albumData.length);
//        }
//        return null;
//    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap) {
        if (bitmap == null) return null;

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }
}
