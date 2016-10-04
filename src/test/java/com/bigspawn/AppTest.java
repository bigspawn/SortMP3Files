package com.bigspawn;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v1Tag;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Unit test.
 */
public class AppTest extends Assert {
    private final ID3v1 id3v1 = new ID3v1Tag();

    @Before
    public void intID3v1() {
        id3v1.setTrack("01");
        id3v1.setTitle("It's All Over");
        id3v1.setYear("2006");
        id3v1.setAlbum("One-X");
        id3v1.setArtist("Three Days Grace");
    }

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testCorrectTag() {
        String correctTag = "Asking Alexandria";

        // Check whitespaces
        String tag = App.correctTag(App.TITLE_TAG, " Asking Alexandria ");
        assertEquals(correctTag, tag);

        // Check dismiss charset for files/folders names
        String testTag = "Asking Alexandria [ ] \\ \\\\\\ /////// ^ $ | ? * + ( ) { } ";
        tag = App.correctTag(App.TRACK_TAG, testTag);
        assertEquals(correctTag, tag);

        // Check null tag
        exception.expect(NullPointerException.class);
        App.correctTag(null, null);
    }

    @Test
    public void testGetToPath() {
        String correctPath = "C:\\Users\\Grishin\\Downloads\\Sorted Music\\" +
                "Three Days Grace\\2006 One-X\\01 It's All Over.mp3";
        String path = App.getToPath(id3v1);
        assertEquals(correctPath, path);
    }
}
