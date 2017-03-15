package x_systems.x_messenger.storage;

import android.support.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import x_systems.x_messenger.R;

import static x_systems.x_messenger.storage.FilesList.Type.CHAT;
import static x_systems.x_messenger.storage.FilesList.Type.NOTE;
import static x_systems.x_messenger.storage.FilesList.Type.PHOTO;
import static x_systems.x_messenger.storage.FilesList.Type.VIDEO;

/**
 * Created by Manasseh on 10/14/2016.
 */

public class FilesList {
    public List<Type> fileTypes = new ArrayList<>();
    public List<String> fileTitles = new ArrayList<>();
    public List<String> filePaths = new ArrayList<>();
    public List<Integer> fileIds = new ArrayList<>();

    public enum Type {
        PHOTO(R.drawable.photo),
        VIDEO(R.drawable.video),
        NOTE(R.drawable.note),
        CHAT(R.drawable.chat);
        private int resourceId;

        Type(int resourceId) {
            this.resourceId = resourceId;
        }

        public int getResourceId() {
          return this.resourceId;
        }
    }

    public static Type resourceIdToType(int resourceId) {
        if (PHOTO.getResourceId() == resourceId)
            return PHOTO;
        else if (VIDEO.getResourceId() == resourceId)
            return VIDEO;
        else if (NOTE.getResourceId() == resourceId)
            return NOTE;
        else if (CHAT.getResourceId() == resourceId)
            return CHAT;
        else
            return null;
    }

    public void add(Type fileType, String fileTitle, @Nullable String filePath) {
        fileTypes.add(fileType);
        fileTitles.add(fileTitle);
        filePaths.add(filePath);
    }
}
