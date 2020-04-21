package businessregistration.lightspace.com.radioapp.Recording;

import android.os.Environment;

import java.io.File;
import java.util.Stack;

/**
 * Created by user on 10/5/2018.
 */

public class FolderHistory {

    Stack<String> folderHistory;

    public FolderHistory()
    {
        folderHistory = new Stack<>();
    }

    // getHistory() gets the root which is MUSIC folder in the SD card.
    public Stack<String> getHistory() {
        File storageDir = new File(Environment.getExternalStorageDirectory(), "/JesusIsLord");

        File musicFilePath = Environment.getExternalStorageDirectory();

        folderHistory.add(musicFilePath.toString());
        return folderHistory;
    }

}
