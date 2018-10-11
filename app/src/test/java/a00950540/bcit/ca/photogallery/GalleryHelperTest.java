package a00950540.bcit.ca.photogallery;

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class GalleryHelperTest {
    private GalleryHelper helper;
    private ArrayList<String> gallerySub;
    private String str0 = "/_Caption0_20181000_111100_";
    private String str1 = "/_Caption1_20181001_111101_";
    private String str2 = "/_Caption2_20181002_111102_";
    private String str3 = "/_Caption3_20181003_111103_";
    private String str4 = "/_Caption4_20181004_111104_";
    private String str5 = "/_Caption5_20181005_111105_";
    private String str6 = "/_Caption6_20181006_111106_";
    private String str7 = "/_Caption7_20181007_111107_";
    private String str8 = "/_Caption8_20181008_111108_";
    private String str9 = "/_Caption9_20181009_111109_";

    @Before
    public void initialize() {
        gallerySub = fakePopulate();
        helper = new GalleryHelper();
    }

    private ArrayList<String> fakePopulate() {
        ArrayList<String> sub = new ArrayList<>();
        for(int i = 0; i < 10; i++ ) {

            String str = "/_Caption" + i
                    + "_" + "2018100" + i
                    + "_" + "11110" + i + "_";
            sub.add(str);
        }
        return  sub;
    }


    @Test
    public void fakePopulateValid() {
        assertEquals(gallerySub.get(0), str0);
        assertEquals(gallerySub.get(1), str1);
        assertEquals(gallerySub.get(2), str2);
        assertEquals(gallerySub.get(3), str3);
        assertEquals(gallerySub.get(4), str4);
        assertEquals(gallerySub.get(5), str5);
        assertEquals(gallerySub.get(6), str6);
        assertEquals(gallerySub.get(7), str7);
        assertEquals(gallerySub.get(8), str8);
        assertEquals(gallerySub.get(9), str9);
    }

    @Test
    public void testGetCaption()
    {
        String caption = helper.getCaption(str9);
        assertEquals(caption, "Caption9");
    }

    @Test
    public void testGetDate()
    {
        Date date = helper.getDate("00000000");
        Date actualDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        try {
             actualDate = sdf.parse("00000000");
        } catch (ParseException ex) {
        }
        assertEquals(date, actualDate);
    }

    @Test
    public void testGetDateTime()
    {
        String dateTimeStr = "20181000115959";
        Date date = helper.getDateTime(dateTimeStr);
        Date actualDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        try {
            actualDate = sdf.parse(dateTimeStr);
        } catch (ParseException ex) {
        }
        assertEquals(date, actualDate);
    }

    @Test
    public void testGetDateStringFromPath()
    {
        String dateTime = helper.getDateStringFromPath(str3);
        assertEquals(dateTime, "20181003");
    }

    @Test
    public void testGetDateTimeStringFromPath()
    {
        String dateTimeStr = helper.getDateTimeStringFromPath(str3);
        assertEquals(dateTimeStr, "20181003111103");
    }

    @Test
    public void testFilterGalleryByCaption() {
        ArrayList<String> testGallery = helper.filterGalleryByCaption("Caption1", gallerySub);
        assertEquals(testGallery.size(), 1);
        assertEquals(testGallery.get(0), str1);
    }

    @Test
    public void testFilterGalleryByDate() {
        Date dateFrom = helper.getDate("20181000");
        Date dateTo = helper.getDate("20181001");
        ArrayList<String> testGallery = helper.filterGalleryByDate(dateFrom,dateTo, gallerySub);
        assertEquals(testGallery.size(), 1);
        assertEquals(testGallery.get(0), str0);
    }
}
