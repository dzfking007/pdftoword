package net.sourceforge.tess4j;

import java.io.File;
import java.util.ArrayList;

import kedacom.wenbenjiaodui.POIUtils;
import opencv.OpenCVUtil;

public class ReadTable {

	public static void main(String[] args) {
		String datapath = "src/main/resources";
		System.out.println(datapath);
		Tesseract1 tesseract1 = new Tesseract1();
		tesseract1.setDatapath(new File(datapath).getPath());
		
		// TODO Auto-generated method stub
		ArrayList<ArrayList<String>> tableImgpath = OpenCVUtil.cvFindContours("D:\\3.jpg");
		
		ArrayList<ArrayList<String>> tableContents = new ArrayList<ArrayList<String>>();
		for(ArrayList<String> rowList : tableImgpath) {
			ArrayList<String> rows = new ArrayList<String>();
			for(String path : rowList) {
				try {
					String celltent = tesseract1.doOCR(new File(path)) ;
					rows.add(celltent);
				} catch (TesseractException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			tableContents.add(rows);
		}
		//生成word
		try {
			POIUtils.getWord("测试测试", tableContents, "测试1.doc");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
