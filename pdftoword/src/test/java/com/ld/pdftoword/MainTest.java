package com.ld.pdftoword;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.tools.PDFText2HTML;
import org.junit.Test;

public class MainTest {
	
	@Test
	public void readPdf() {
		 File pdfFile = new File("D:\\中文校对系统中纠错知识库的构造及纠错建议的产生算法.pdf");
	        PDDocument document = null;
	        try
	        {
	        	
	            // 方式一：
	        	/**
	            InputStream input = null;
	            input = new FileInputStream( pdfFile );
	            //加载 pdf 文档
	            PDFParser parser = new PDFParser(new RandomAccessBuffer(input));
	            parser.parse();
	            document = parser.getPDDocument();
	            **/
	            // 方式二：
	            document=PDDocument.load(pdfFile);

	            // 获取页码
	            int pages = document.getNumberOfPages();

	            // 读文本内容
	            PDFTextStripper stripper=new PDFTextStripper();
	            // 设置按顺序输出
	            stripper.setSortByPosition(true);
	            stripper.setStartPage(1);
	            stripper.setEndPage(pages);
	            String content = stripper.getText(document);
	 
	     
	            System.out.println(content);     
	        }
	        catch(Exception e)
	        {
	            System.out.println(e);
	        }

	}
	
	@Test
	public void TestPdfToHtml() throws IOException {
		
		File pdfFile = new File("D:\\微信截屏表情制作指南.pdf");
        PDDocument document = null;
        try
        {
            // 方式一：
        	/**
            InputStream input = null;
            input = new FileInputStream( pdfFile );
            //加载 pdf 文档
            PDFParser parser = new PDFParser(new RandomAccessBuffer(input));
            parser.parse();
            document = parser.getPDDocument();
            **/
            // 方式二：
            document=PDDocument.load(pdfFile);

            // 获取页码
            int pages = document.getNumberOfPages();

            // 读文本内容
            PDFText2HTML stripper=new PDFText2HTML();
            // 设置按顺序输出
            stripper.setSortByPosition(true);
            stripper.setStartPage(1);
            stripper.setEndPage(pages);
            String content = stripper.getText(document);
 
     
            System.out.println(content);     
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

	}
	
	@Test
	public void testTable() {
		Table table = new Table(5, 5);
		table.printMatrix();
	}
	@Test
	public void asciiToString()  
	{  
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		list.add(5);
		list.add(6);
		list.add(7);
		
		for(int x = 0;x<list.size();x++) {
			if(x==(list.size()-1)) {
				System.out.println("最后一个"+list.get(x));
			}
		}
		System.out.println(list.subList(0, 3));
		System.out.println(list.subList(3, 7));
	}  
	
	@Test
	public void testFile() {
		String path = "src/main/resources/";
        File pdffile = new File(path,"复习测试二.pdf");
        try {
			PDDocument doc = PDDocument.load(pdffile);
		} catch (InvalidPasswordException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

}
