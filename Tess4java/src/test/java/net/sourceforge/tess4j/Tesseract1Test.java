/**
 * Copyright @ 2010 Quan Nguyen
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package net.sourceforge.tess4j;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;

import net.sourceforge.tess4j.util.LoggHelper;
import net.sourceforge.tess4j.util.Utils;
import net.sourceforge.tess4j.util.ImageHelper;
import net.sourceforge.tess4j.util.ImageIOHelper;
import net.sourceforge.tess4j.ITesseract.RenderedFormat;
import net.sourceforge.tess4j.ITessAPI.TessPageIteratorLevel;

import com.recognition.software.jdeskew.ImageDeskew;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Ignore;

public class Tesseract1Test {

    private static final Logger logger = LoggerFactory.getLogger(new LoggHelper().toString());
    static final double MINIMUM_DESKEW_THRESHOLD = 0.05d;
    ITesseract instance;

    private final String datapath = "src/main/resources";
    private final String testResourcesDataPath = "src/test/resources/test-data";

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        instance = new Tesseract1();
        instance.setDatapath(new File(datapath).getPath());
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of doOCR method, of class Tesseract1.
     *
     * @throws Exception while processing image.
     */
    @Test
    public void testDoOCR_File() throws Exception {
        logger.info("doOCR on a PNG image");
        File imageFile = new File(this.testResourcesDataPath, "101.jpg");
        String expResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
        String result = instance.doOCR(imageFile);
        logger.info(result);
        assertEquals(expResult, result.substring(0, expResult.length()));
    }

    /**
     * Test of doOCR method, of class Tesseract.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testDoOCR_UNLV_Zone_File() throws Exception {
        logger.info("doOCR on a PNG image with UNLV zone file .uzn");
        //UNLV zone format: left top width height label
        String filename = String.format("%s/%s", this.testResourcesDataPath, "eurotext_unlv.png");
        File imageFile = new File(filename);
        String expResult = "& duck/goose, as 12.5% of E-mail\n\n"
                + "from aspammer@website.com is spam.\n\n"
                + "The (quick) [brown] {fox} jumps!\n"
                + "Over the $43,456.78 <lazy> #90 dog";
        String result = instance.doOCR(imageFile);
        logger.info(result);
        assertEquals(expResult, result.trim().replace('—', '-'));
    }

    /**
     * Test of doOCR method, of class Tesseract.
     *
     * @throws java.lang.Exception
     */
    @Ignore
    @Test
    public void testDoOCR_File_With_Configs() throws Exception {
        logger.info("doOCR with configs");
        File imageFile = new File(this.testResourcesDataPath, "101.jpg");
        String expResult = "[-0123456789.\n ]+";
        List<String> configs = Arrays.asList("digits");
        instance.setConfigs(configs);
        String result = instance.doOCR(imageFile);
        logger.info(result);
        assertTrue(result.matches(expResult));
    }

    /**
     * Test of doOCR method, of class Tesseract1.
     *
     * @throws Exception while processing image.
     */
    @Test
    public void testDoOCR_File_Rectangle() throws Exception {
        logger.info("doOCR on a BMP image with bounding rectangle");
        File imageFile = new File(this.testResourcesDataPath, "eurotext.bmp");
        Rectangle rect = new Rectangle(0, 0, 1024, 800); // define an equal or smaller region of interest on the image
        String expResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
        String result = instance.doOCR(imageFile, rect);
        logger.info(result);
        assertEquals(expResult, result.substring(0, expResult.length()));
    }

    /**
     * Test of doOCR method, of class Tesseract1.
     *
     * @throws Exception while processing image.
     */
    @Test
    public void testDoOCR_List_Rectangle() throws Exception {
        logger.info("doOCR on a PDF document");
        File imageFile = new File(this.testResourcesDataPath, "101.jpg");
        List<IIOImage> imageList = ImageIOHelper.getIIOImageList(imageFile);
        String expResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
        String result = instance.doOCR(imageList, null);
        logger.info(result);
        assertEquals(expResult, result.substring(0, expResult.length()));
    }

    /**
     * Test of doOCR method, of class Tesseract1.
     *
     * @throws Exception while processing image.
     */
    @Test
    public void testDoOCR_BufferedImage() throws Exception {
        logger.info("doOCR on a buffered image of a PNG");
        File imageFile = new File(this.testResourcesDataPath, "eurotext.png");
        BufferedImage bi = ImageIO.read(imageFile);
        String expResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
        String result = instance.doOCR(bi);
        logger.info(result);
        assertEquals(expResult, result.substring(0, expResult.length()));
    }

    /**
     * Test of deskew algorithm. 倾斜图片 矫正
     *
     * @throws Exception while processing image.
     */
    @Test
    public void testDoOCR_SkewedImage() throws Exception {
        logger.info("doOCR on a skewed PNG image");
        File imageFile = new File(this.testResourcesDataPath, "101.jpg");
        BufferedImage bi = ImageIO.read(imageFile);
        ImageDeskew id = new ImageDeskew(bi);
        double imageSkewAngle = id.getSkewAngle(); // determine skew angle
        logger.info(imageSkewAngle+"");
        if ((imageSkewAngle > MINIMUM_DESKEW_THRESHOLD || imageSkewAngle < -(MINIMUM_DESKEW_THRESHOLD))) {
            bi = ImageHelper.rotateImage(bi, -imageSkewAngle); // deskew image
            ImageIO.write(bi, "jpg", new File(this.testResourcesDataPath, "out.jpg"));
        }

        String expResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
        String result = instance.doOCR(bi);
        logger.info(result);
        assertEquals(expResult, result.substring(0, expResult.length()));
    }

    /**
     * Test of createDocuments method, of class Tesseract1.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testCreateDocuments() throws Exception {
        logger.info("createDocuments for an image");
        File imageFile1 = new File(this.testResourcesDataPath, "eurotext.pdf");
        File imageFile2 = new File(this.testResourcesDataPath, "eurotext.png");
        String outputbase1 = "target/test-classes/test-results/docrenderer1-1";
        String outputbase2 = "target/test-classes/test-results/docrenderer1-2";
        List<RenderedFormat> formats = new ArrayList<RenderedFormat>(Arrays.asList(RenderedFormat.HOCR, RenderedFormat.PDF, RenderedFormat.TEXT));
        instance.createDocuments(new String[]{imageFile1.getPath(), imageFile2.getPath()}, new String[]{outputbase1, outputbase2}, formats);
        assertTrue(new File(outputbase1 + ".pdf").exists());
    }
    
    @Test
    public void testCreateDocuments2() throws Exception {
        logger.info("createDocuments for an image");
        File imageFile1 = new File(this.testResourcesDataPath, "101.jpg");
        String outputbase1 = "target/test-classes/test-results/docrenderer1-1";
        List<RenderedFormat> formats = new ArrayList<RenderedFormat>(Arrays.asList(RenderedFormat.HOCR, RenderedFormat.PDF, RenderedFormat.TEXT,RenderedFormat.BOX));
        instance.createDocuments(new String[]{imageFile1.getPath()}, new String[]{outputbase1}, formats);
        assertTrue(new File(outputbase1 + ".pdf").exists());
    }

    /**
     * Test of getWords method, of class Tesseract1.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testGetWords() throws Exception {
        logger.info("getWords");
        File imageFile = new File(this.testResourcesDataPath, "101.jpg");

        String expResult = "The (quick) [brown] {fox} jumps!\nOver the $43,456.78 <lazy> #90 dog";
        String[] expResults = expResult.split("\\s");

        int pageIteratorLevel = TessPageIteratorLevel.RIL_WORD;
        logger.info("PageIteratorLevel: " + Utils.getConstantName(pageIteratorLevel, TessPageIteratorLevel.class));
        BufferedImage bi = ImageIO.read(imageFile);
        List<Word> result = instance.getWords(bi, pageIteratorLevel);

        // print the complete result
        for (Word word : result) {
            logger.info(word.toString());
        }

        List<String> text = new ArrayList<String>();
        for (Word word : result.subList(0, expResults.length)) {
            text.add(word.getText());
        }

        assertArrayEquals(expResults, text.toArray());
    }

    /**
     * Test of getSegmentedRegions method, of class Tesseract1.
     * 
     * @throws java.lang.Exception
     */
    @Test
    public void testGetSegmentedRegions() throws Exception {
        logger.info("getSegmentedRegions at given TessPageIteratorLevel");
        File imageFile = new File(testResourcesDataPath, "101.jpg");
        BufferedImage bi = ImageIO.read(imageFile);
        int level = TessPageIteratorLevel.RIL_SYMBOL;
        logger.info("PageIteratorLevel: " + Utils.getConstantName(level, TessPageIteratorLevel.class));
        List<Rectangle> result = instance.getSegmentedRegions(bi, level);
        for (int i = 0; i < result.size(); i++) {
            Rectangle rect = result.get(i);
            logger.info(String.format("Box[%d]: x=%d, y=%d, w=%d, h=%d", i, rect.x, rect.y, rect.width, rect.height));
        }

        assertTrue(result.size() > 0);
    }
}
