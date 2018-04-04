/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ld.pdftoword;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.util.Charsets;

import com.ld.myutils.LoggerUtil;
import com.ld.myutils.poi.CreateWord;

/**
 * This is an example on how to get some x/y coordinates of text.
 *
 * @author Ben Litchfield
 */
public class PrintTextLocations extends PDFTextStripper
{
	private static Logger logger = LoggerUtil.getJdkLog();
	private boolean isPageStart = false;//是否是页面开始
	private boolean isParagraphStart = false;//是否是段落开始
	private boolean isLineStart = false;//是否是行开始
	private List<PdfLine> pdfContentList;
	private PdfLine pdfLine = new PdfLine();
    /**
     * Instantiate a new PDFTextStripper object.
     *
     * @throws IOException If there is an error loading the properties.
     */
    public PrintTextLocations(List<PdfLine> pdfContentList) throws IOException
    {
    	this.pdfContentList = pdfContentList;
    }

    /**
     * This will print the documents data.
     *
     * @param args The command line arguments.
     *
     * @throws IOException If there is an error parsing the document.
     */
    public static void main( String[] args ) throws IOException
    {
            PDDocument document = null;
            try
            {
                document = PDDocument.load( new File("D:\\基于Word2vec的文档分类.pdf") );
                List<PdfLine> pdfContentList = new ArrayList<PdfLine>();
                PDFTextStripper stripper = new PrintTextLocations(pdfContentList);
                stripper.setSortByPosition( true );
                stripper.setStartPage( 0 );
                stripper.setEndPage( document.getNumberOfPages() );

                Writer dummy = new OutputStreamWriter(new ByteArrayOutputStream());
                stripper.writeText(document, dummy);
                
            }
            finally
            {
                if( document != null )
                {
                    document.close();
                }
            }

    }
    

    /**
     * Override the default functionality of PDFTextStripper.
     */
    @Override
    protected void writeString(String string, List<TextPosition> textPositions) throws IOException
    {
        for (TextPosition text : textPositions)
        {
        	 PDColor strokingColor = getGraphicsState().getStrokingColor();
             PDColor nonStrokingColor = getGraphicsState().getNonStrokingColor();
             RenderingMode renderingMode = getGraphicsState().getTextState().getRenderingMode();             
            logger.info( "String[" + text.getXDirAdj() + "," +
                    text.getYDirAdj() + " fs=" + text.getFontSize()+ " Font="+text.getFont().getName()  + " xscale=" +
                    text.getXScale() + " yscale=" + text.getYScale() + " height=" + text.getHeightDir() + " space=" +
                    text.getWidthOfSpace() + " width=" +
                    text.getWidthDirAdj() + "Rendering mode:" + renderingMode +"Stroking color:" + strokingColor+
                    "Non-Stroking color: " + nonStrokingColor+"]" + text.getUnicode() +" -- " + (int)text.getUnicode().toCharArray()[0]);
           
            String fontname = text.getFont().getName();
            if(fontname!=null) {
            	if(fontname.contains("+")) {
            		fontname = fontname.split("\\+")[1];
            	}
            }
            
            boolean isbold = false;
            if(text.getFont().getName().contains("Bold")) {
            	isbold = true;
            }
            if(isParagraphStart) {//段落开始则插入左侧缩进
            	isParagraphStart = false;
            	pdfLine.setX(text.getXDirAdj());
                pdfLine.setY(text.getYDirAdj());
                pdfLine.setType(PdfElem.TYPE_TEXT);
            }
            if(isLineStart) {
            	isLineStart = false;
            	pdfLine.setX(text.getXDirAdj());
                pdfLine.setY(text.getYDirAdj());
                pdfLine.setType(PdfElem.TYPE_TEXT);
            }
            if(isPageStart) {//页面刚开始，则插入段前间距
            	isPageStart = false;
            }
            PdfElem pdfElem = new PdfElem();
            pdfElem.setX(text.getXDirAdj());
            pdfElem.setY(text.getYDirAdj());
            pdfElem.setTextContent(text.getUnicode());
            pdfElem.setType(PdfElem.TYPE_TEXT);
            pdfElem.setFontsize((int)text.getXScale());
            pdfElem.setFontname(fontname);
            pdfElem.setFontBold(isbold);
            pdfLine.getElemList().add(pdfElem);
        }
    }
    
    @Override
	protected void writeLineSeparator() throws IOException {
		// TODO Auto-generated method stub
    	logger.info("writeLineSeparator");
    	//换行
    	logger.info(pdfLine.getX()+":"+pdfLine.getY()+":"+pdfLine.getType()+"SIZE:"+pdfLine.getElemList().size());
    	if(pdfLine.getType()>0) {
//    		pdfContentList.add(pdfLine);
    		PdfLine line = new PdfLine();
        	line.setX(pdfLine.getX());
        	line.setY(pdfLine.getY());
        	line.setType(pdfLine.getType());
        	line.getElemList().addAll(pdfLine.getElemList());
        	
        	pdfContentList.add(line);
    	}
    	
    	pdfLine = new PdfLine();
    	isLineStart = true;
		super.writeLineSeparator();
	}
    
	@Override
	protected void writeWordSeparator() throws IOException {
		// TODO Auto-generated method stub
//		logger.info("writeWordSeparator");
		super.writeWordSeparator();
	}

	@Override
	protected void writePage() throws IOException {
		// TODO Auto-generated method stub
		isPageStart = true;
		super.writePage();
	}

	@Override
	protected void writePageStart() throws IOException {
		// TODO Auto-generated method stub
		super.writePageStart();
	}

	@Override
	protected void writePageEnd() throws IOException {
		// TODO Auto-generated method stub
		super.writePageEnd();
	}
	
	@Override
	protected void writeParagraphSeparator() throws IOException {
		// TODO Auto-generated method stub
		super.writeParagraphSeparator();
	}

	@Override
	protected void writeParagraphStart() throws IOException {
		// TODO Auto-generated method stub
		logger.info("writeParagraphStart");
		//段落开始创建行
		isParagraphStart = true;
//		pdfLine = new PdfLine();
		super.writeParagraphStart();
	}

	@Override
	protected void writeParagraphEnd() throws IOException {
		// TODO Auto-generated method stub
		logger.info("writeParagraphEnd");
		if(pdfLine.getType()>0) {
			if(!pdfContentList.contains(pdfLine)) {
//	    		pdfContentList.add(pdfLine);
	    		PdfLine line = new PdfLine();
	        	line.setX(pdfLine.getX());
	        	line.setY(pdfLine.getY());
	        	line.setType(pdfLine.getType());
	        	line.getElemList().addAll(pdfLine.getElemList());
	        	pdfContentList.add(line);
	    		pdfLine = new PdfLine();
			}

        	
        	
    	}

		super.writeParagraphEnd();
	}

	/**
     * This will print the usage for this document.
     */
    private static void usage()
    {
        System.err.println( "Usage: java " + PrintTextLocations.class.getName() + " <input-pdf>" );
    }
}
