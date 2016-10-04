package com.bigspawn;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Sort MP3 files script
 *
 */
public class App {
    public static final String TITLE_TAG = "title";
    public static final String TRACK_TAG = "track";
    public static final String ARTIST_TAG = "artist";
    public static final String ALBUM_TAG = "album";
    public static final String YEAR_TAG = "year";
    public static final String REGEX_EXP = "[\\[\\]\\\\\\/\\^\\$\\|\\?\\*\\+\\(\\)\\{\\}`~!@#$%^&\"|,<>]";

    private static String FROM_FOLDER = "C:\\Users\\Grishin\\Downloads\\Sample Music" ;
    private static String TO_FOLDER = "C:\\Users\\Grishin\\Downloads\\Sorted Music";
    private static ArrayList<String> mp3FilesPath = new ArrayList<String>();

    public static void main(String[] args) {
        System.out.println(Arrays.toString(args));
        if (args != null && args.length >= 2) {
            System.out.println("Программа начала работу!");
            FROM_FOLDER = args[0];
            TO_FOLDER = args[1];
            File file = new File(FROM_FOLDER);
            readFilesFromFolder(file);

            for (String mp3FilePath : mp3FilesPath) {
                ID3v1 id3v1Tag = getID3v1Tag(mp3FilePath);
                moveMP3File(mp3FilePath, getToPath(id3v1Tag));
            }
            System.out.println("Файлы рассортированы!");
        }
    }

    public static String getToPath(ID3v1 id3v1Tag) {
        String artist = id3v1Tag.getArtist();
        String year = id3v1Tag.getYear();
        String album = id3v1Tag.getAlbum();
        String track = id3v1Tag.getTrack();
        String title = id3v1Tag.getTitle();
        StringBuilder sb = new StringBuilder();
        sb.append(TO_FOLDER)
                .append("\\").append(correctTag(ARTIST_TAG, artist))
                .append("\\").append(correctTag(YEAR_TAG, year))
                .append(" ").append(correctTag(ALBUM_TAG, album))
                .append("\\").append(correctTag(TRACK_TAG, track))
                .append(" ").append(correctTag(TITLE_TAG, title))
                .append(".mp3");
        return sb.toString();
    }

    public static String correctTag(String type, String tag) {
        if (tag != null && !tag.isEmpty() && type != null && !type.isEmpty()) {
            return tag.replaceAll(REGEX_EXP, "").trim();
        } else {
            throw new NullPointerException("Тег " + type.toUpperCase() + " пустой");
        }
    }

    public static void moveMP3File(String mp3FilePath, String path) {
        Path pathFrom = FileSystems.getDefault().getPath(mp3FilePath);
        Path pathTo = FileSystems.getDefault().getPath(path);
        try {
            Files.createDirectories(Paths.get(path));
            Files.move(pathFrom, pathTo, REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ID3v1 getID3v1Tag(String mp3FilePath) {
        try {
            Mp3File mp3File = new Mp3File(mp3FilePath);
            ID3v1 id3v1Tag = null;
            if (mp3File.hasId3v1Tag()) {
                id3v1Tag = mp3File.getId3v1Tag();
            }
            if (mp3File.hasId3v2Tag()) {
                id3v1Tag = mp3File.getId3v2Tag();
            }
            if (id3v1Tag == null) {
                throw new NullPointerException("Отстствуют теги id3v1 или id3v2 в " + mp3FilePath + " файле");
            }
            return id3v1Tag;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedTagException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void readFilesFromFolder(File folder) {
        if (folder != null) {
            try {
                for (File file : folder.listFiles()) {
                    if (file.isDirectory()) {
                        readFilesFromFolder(file);
                    } else {
                        String filePath = file.getAbsolutePath();
                        if (filePath.endsWith(".mp3") || filePath.endsWith(".MP3")) {
                            mp3FilesPath.add(filePath);
                            System.out.println(filePath);
                        }
                    }
                }
            } catch (NullPointerException e) {
                System.out.println("В указанной папке нет файлов");
            }
        }
    }
}
