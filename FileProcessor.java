import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileProcessor {
    public static final String BLACKLIST = "INFO.txt";
    public static final String BLACKLIST2 = ".gitkeep";


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
        
        // If file is not empty
        if (listOfFoldersInRoot == null || listOfFoldersInRoot.length == 0) {
            System.out.println("No folders found in the specified directory.");
            return new ArrayList<>(); // Return empty list if no folders found
        }
        
        for (File folder : listOfFoldersInRoot) {
            if (folder.isDirectory()) {
                listOfGenreFolders.add(folder);
            }
        }

        // For each folder, get all files and put into list of type Object[] for path and genres
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

    public static List<Object[]> getSnippetFileNames(String folderPath) {
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        List<Object[]> fileNames = new ArrayList<>();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isDirectory() || listOfFiles[i].getName().equals(BLACKLIST) || listOfFiles[i].getName().equals(BLACKLIST2)) {
                continue;
            }
            Object[] fileName = new Object[listOfFiles.length];
            fileName[0] = listOfFiles[i].getName();
            fileName[1] = listOfFiles[i].getPath();
            fileName[2] = "snippet";
            fileNames.add(fileName);
        }
        return fileNames;
    }

    private static void collectTracks(File albumFolder, String genres, List<Object[]> fileInfoList) {
        for (File file : albumFolder.listFiles()) {
            if (file.isFile() && !file.getName().equals(BLACKLIST) && !file.getName().equals(BLACKLIST2)) {
                String name = file.getName();
                String path = file.getAbsolutePath();
                fileInfoList.add(new Object[]{ name, path, genres });
            }
        }
    }

    public static String[] getFolders(String folderPath) {
        File folder = new File(folderPath);
        File[] listOfFolders = folder.listFiles();
        String[] folderNames = new String[listOfFolders.length];
        for (int i = 0; i < listOfFolders.length; i++) {
            folderNames[i] = listOfFolders[i].getName();
        }
        return folderNames;
    }
    public static String[] getFilesInFolder(String folderPath) {
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        String[] fileNames = new String[listOfFiles.length];
        for (int i = 0; i < listOfFiles.length; i++) {
            if ( !listOfFiles[i].getName().equals(BLACKLIST) && !listOfFiles[i].getName().equals(BLACKLIST2)) {
                fileNames[i] = listOfFiles[i].getName();
            }
        }
        return fileNames;
    }

    public static String[] getFilesDefault(String folderPath) {
        File folder = new File(folderPath);
        File[] listOfFiles = folder.listFiles();
        String[] fileNames = new String[listOfFiles.length];
        for (int i = 0; i < listOfFiles.length; i++) {
            fileNames[i] = listOfFiles[i].getName();
        }
        return fileNames;
    }
}
