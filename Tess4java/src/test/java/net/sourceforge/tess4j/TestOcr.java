package net.sourceforge.tess4j;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;
import opencv.OpenCVUtil;

public class TestOcr {

	public static void main(String[] args) {
		// TODO 锟皆讹拷锟斤拷锟缴的凤拷锟斤拷锟斤拷锟�
		//锟斤拷锟斤拷图片锟斤拷址  
		try {
			tess4j();
		} catch (TesseractException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		cmd();
	} 
	
	public static void cmd() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		long start = System.currentTimeMillis();
		String srcImgpath = "D:\\101.jpg";
		String outImgpath = "D:\\1_out.jpg";
		//HSV
		Mat srcMat = Imgcodecs.imread(srcImgpath);
		Mat dstMat = OpenCVUtil.filterHSV(srcMat);
		Imgcodecs.imwrite(outImgpath, dstMat);
		long mid = System.currentTimeMillis();
		long midtime = (mid - start)/1000;
		System.out.println("OpenCv锟斤拷时-----"+midtime);
		try {     
			String valCode = new OCR().recognizeText(new File(outImgpath), "jpg");//jpg锟斤拷图片锟斤拷式     
			System.out.println(valCode);     
		} catch (IOException e) {     
			e.printStackTrace();     
		} catch (Exception e) {  
			e.printStackTrace();  
		}  
		long end = System.currentTimeMillis();
		long time = (end - start)/1000;
		System.out.println("锟斤拷锟斤拷锟绞�-----"+time);
	}
	
	public static void tess4j() throws TesseractException, IOException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		long start = System.currentTimeMillis();
		String srcImgpath = "D:\\3_2.jpg";
		String outImgpath = "D:\\1_5_0.jpg";
		Mat srcMat = Imgcodecs.imread(srcImgpath);
		//剪切
		Mat jqMat = OpenCVUtil.jianqie(srcMat, 200, 0, 400, 0);
		//HSV
		Mat hsvMat = OpenCVUtil.filterHSV(jqMat);
		//去椒盐噪点 , 中值滤波器
		Mat medianblurMat = OpenCVUtil.medianblur(hsvMat);
		//腐蚀
		Mat erodeMat = OpenCVUtil.erode(1.2, medianblurMat);
		//膨化
		Mat dilateMat = OpenCVUtil.dilate(1.4, erodeMat);
		
		Imgcodecs.imwrite(outImgpath, hsvMat);
		long mid = System.currentTimeMillis();
		long midtime = (mid - start)/1000;
		System.out.println("OpenCv耗时-----"+midtime);
		String datapath = "src/main/resources";
		System.out.println(datapath);
		Tesseract1 tesseract1 = new Tesseract1();
		tesseract1.setDatapath(new File(datapath).getPath());
		String result = tesseract1.doOCR(new File(outImgpath));
		Rectangle rectangle = new Rectangle();
		StringReader sr = new StringReader(result);
		BufferedReader br = new BufferedReader(new StringReader(result));
		FileOutputStream fos = new FileOutputStream(new File("D:\\102.txt"));
		String line = null;
		while((line = br.readLine())!=null) {
			line = line.replaceAll(" ", "").trim();//去除空格
			if(line.length()<=0)continue;
			line = line + System.getProperty("line.separator");
			fos.write(line.getBytes());
		}
		
		fos.flush();
		fos.close();
		
		System.out.println(result);
		long end = System.currentTimeMillis();
		long time = (end - start)/1000;
		System.out.println("总耗时-----"+time);
		
	}

}


