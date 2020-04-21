package businessregistration.lightspace.com.radioapp.Recording;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by user on 10/5/2018.
 */

public class FileList {

    String state = Environment.getExternalStorageState(); // get the state of External SD card.

    //data[][] will store all the details of file. It will be N x 3 matrix, where N is the
    //no. of files and 3 is for file name, image, and details. Image property will have the name of
    //the extension as it is same as the image name.
    //data[i][0] = fileName; data[i][1] = details; data[i][2] = extension name.

    private String[][] data;
    String fileName;

    public FileList(String fileName) {
        this.fileName = fileName;
    }

    public String[][] getFileList()  {

        if (Environment.MEDIA_MOUNTED.equals(state)) {

            //musicFilePath provides the path of Music Directory as given in my phone. emulated/0/music/
            //So in my example I have used MUSIC folder as my root folder and all the files under this will be displayed.

             File musicFilePath;


            if (fileName.compareTo("root") == 0)


                musicFilePath = new File(Environment.getExternalStorageDirectory(),"/JesusIsLord");


            else {
                musicFilePath = new File(fileName);
            }

            File[] fileList = musicFilePath.listFiles();

            try {

                data = new String[fileList.length][3];


            for (int i = 0; i < fileList.length; i++) {
                //extracting base file name from absolute file name.
                String baseName[] = fileList[i].toString().split("/");
                data[i][0] = baseName[baseName.length - 1];

                if (fileList[i].isDirectory()) {
                    data[i][1] = Integer.toString(fileList[i].listFiles().length) + " files";
                    data[i][2] = "folder";
                } else {
                    data[i][1] = Integer.toString((int) fileList[i].length() / 1024) + " Kb";
                    int length = fileList[i].toString().length();
                    data[i][2] = fileList[i].toString().substring(length - 3, length);
                }
            }

            } catch (NullPointerException ex ) {

                ex.printStackTrace();

            }


        } else {
            Log.i("CustomError", "SD card not inserted");
        }
        return data;
    }
}
