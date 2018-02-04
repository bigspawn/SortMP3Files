package com.bigspawn;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

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
import javax.swing.JTextArea;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Sort MP3 files script
 */
public class App {

  public static final Logger logger = LogManager.getLogger("MyApp");
  public static final String TITLE_TAG = "title";
  public static final String TRACK_TAG = "track";
  public static final String ARTIST_TAG = "artist";
  public static final String ALBUM_TAG = "album";
  public static final String YEAR_TAG = "year";
  public static final String REGEX_EXP = "[\\[\\]\\\\/^$|?*+(){}`~!@#&\",<>:]";
  public static String FROM_FOLDER;
  public static String TO_FOLDER;
  private static JTextArea textArea;
  private static ArrayList<String> mp3FilesPath = new ArrayList<String>();

  public static void main(String[] args) {
    AppGUI appGUI = new AppGUI();
    textArea = appGUI.getTextArea();
  }

  public static String getToPath(ID3v1 id3v1Tag) {
    String artist = id3v1Tag.getArtist();
    String year = id3v1Tag.getYear();
    String album = id3v1Tag.getAlbum();
    String track = id3v1Tag.getTrack();
    String title = id3v1Tag.getTitle();
    logger.info(
        "Info from mp3 file:\nartist='" + artist + "'; year='" + year + "'; album='" + album + "'; track='"
            + track + "'; title='" + title + "'");
    StringBuilder sb = new StringBuilder();
    sb.append(TO_FOLDER)
        .append("\\").append(correctTag(ARTIST_TAG, artist))
        .append("\\").append(correctTag(YEAR_TAG, year))
        .append(" ").append(correctTag(ALBUM_TAG, album))
        .append("\\").append(correctTag(TRACK_TAG, track))
        .append(" ").append(correctTag(TITLE_TAG, title))
        .append(".mp3");
    logger.info("Path with replaced symbols:\n" + sb);
    return sb.toString();
  }

  public static String correctTag(String type, String tag) {
    if (tag != null && !tag.isEmpty() && type != null && !type.isEmpty()) {
      return tag.replaceAll(REGEX_EXP, "").trim();
    } else {
      throw new NullPointerException("Тег пустой");
    }
  }

  public static void moveMP3File(String mp3FilePath, String path) {
    Path pathFrom = FileSystems.getDefault().getPath(mp3FilePath);
    Path pathTo = FileSystems.getDefault().getPath(path);
    try {
      Files.createDirectories(Paths.get(path));
      Files.move(pathFrom, pathTo, REPLACE_EXISTING);
    } catch (IOException e) {
      logger.error(e, e);
      e.printStackTrace();
      textArea.append(e.toString());
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
        throw new NullPointerException(
            "Отстствуют теги id3v1 или id3v2 в " + mp3FilePath + " файле");
      }
      return id3v1Tag;
    } catch (IOException e) {
      logger.error(e, e);
      e.printStackTrace();
    } catch (UnsupportedTagException e) {
      logger.error(e, e);
      e.printStackTrace();
    } catch (InvalidDataException e) {
      logger.error(e, e);
      e.printStackTrace();
    } catch (NullPointerException e) {
      logger.error(e, e);
      e.printStackTrace();
    }
    return null;
  }

  public static void readFilesFromFolder(File folder) {
    if (folder != null) {
      try {
        File[] files = folder.listFiles();
        if (files != null && files.length > 0) {
          for (File file : files) {
            if (file.isDirectory()) {
              readFilesFromFolder(file);
            } else {
              String filePath = file.getAbsolutePath();
              if (filePath.endsWith(".mp3") || filePath.endsWith(".MP3")) {
                mp3FilesPath.add(filePath);
                logger.info(filePath);
                textArea.append(filePath + System.lineSeparator());
              }
            }
          }
        } else {
          throw new NullPointerException("Папка пуста!");
        }
      } catch (NullPointerException e) {
        textArea.append("В указанной папке нет mp3 файлов\n");
        textArea.append(e.toString());
      }
    }
  }

  public static void start() {
    try {
      if (FROM_FOLDER != null && TO_FOLDER != null && !FROM_FOLDER.isEmpty() && !TO_FOLDER
          .isEmpty()) {
        textArea.append("Программа начала работу!\n");
        logger.info("Программа начала работу!");
        File file = new File(FROM_FOLDER);
        textArea.append("Перенос файлов из:" + System.lineSeparator());
        logger.info("Перенос файлов из:");
        readFilesFromFolder(file);
        textArea.append("Перенос файлов в:" + System.lineSeparator());
        logger.info("Перенос файлов в:");
        for (String mp3FilePath : mp3FilesPath) {
          ID3v1 id3v1Tag = getID3v1Tag(mp3FilePath);
          if (id3v1Tag != null) {
            String toPath = getToPath(id3v1Tag);
            textArea.append(toPath + System.lineSeparator());
            logger.info(toPath);
            moveMP3File(mp3FilePath, toPath);
          } else {
            throw new Exception("Тег для файла '" + mp3FilePath + "' не найден");
          }
        }
        textArea.append("Файлы рассортированы!\n");
      } else {
        textArea.append("Не указан пути до папок!\n");
      }
    } catch (Exception e) {
      logger.error(e, e);
      textArea.append(e.toString());
    }

  }
}
