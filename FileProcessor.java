import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileProcessor {

    public static String[][] getFiles(String folderPath) {
        // Get all files in folder
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        String[] fileNames = new String[listOfFiles.length];
        for (int i = 0; i < listOfFiles.length; i++) {
            fileNames[i] = listOfFiles[i].getName().substring(0, listOfFiles[i].getName().length() - 4);
        }
        String[] filePaths = new String[listOfFiles.length];
        for (int i = 0; i < listOfFiles.length; i++) {
            filePaths[i] = listOfFiles[i].getPath();
        }
        String[][] result = new String[listOfFiles.length][2];
        for (int i = 0; i < listOfFiles.length; i++) {
            result[i][0] = fileNames[i];
            result[i][1] = filePaths[i];
        }
        return result;
    }

    public static List<Object[]> getFiles2(String folderPath) {
        File rootFolder = new File(folderPath);
        // Get all folders from rootFolder and put into list
        File[] listOfFoldersInRoot = rootFolder.listFiles();
        List<File> listOfGenreFolders = new ArrayList<>();
        for (File folder : listOfFoldersInRoot) {
            if (folder.isDirectory()) {
                listOfGenreFolders.add(folder);
            }
        }

        // For each folder, get all files and put into list of type Object[][] for path and genres
        List<Object[]> fileInfoList = new ArrayList<>();
        for (File genreFolder : listOfGenreFolders) {
            String genres = genreFolder.getName();
            File[] listOfAlbumsInGenre = genreFolder.listFiles();
            for (File albumFolder : listOfAlbumsInGenre) {
                if (albumFolder.isDirectory()) {
                    collectTracks(albumFolder, genres, fileInfoList);
                }
            }
        }

        return fileInfoList;
    }

    public static String[] getSnippetFileNames(String folderPath) {
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        String[] fileNames = new String[listOfFiles.length];
        for (int i = 0; i < listOfFiles.length; i++) {
            fileNames[i] = listOfFiles[i].getName();
        }
        return fileNames;
    }

    private static void collectTracks(File albumFolder, String genres, List<Object[]> fileInfoList) {
        for (File file : albumFolder.listFiles()) {
            if (file.isFile()) {
                String name = file.getName();
                String path = file.getAbsolutePath();
                fileInfoList.add(new Object[]{ name, path, genres });
            }
        }
    }
}
