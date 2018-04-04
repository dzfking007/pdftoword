package kedacom.tuxiangshibie;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;
import net.sourceforge.tess4j.util.LoggHelper;
import opencv.OpenCVUtil;

/**
 * 图像识别
 * @author Paytham
 *
 */
public class Tuxiangshibie {
	private static final Logger logger = LoggerFactory.getLogger(new LoggHelper().toString());
	
	/**
	 * 获取诉讼请求
	 * @param filepath 识别图片路径 绝对路径
	 * @param tessdataPath tessdata 路径 一般放在 src/main/resources
	 * @return List<String>,按照顺序排列诉讼请求
	 * @throws IOException 
	 * @throws TesseractException 
	 */
	public static List<String> getSsqq(String filepath,String tessdataPath) throws IOException, TesseractException{
		long start = System.currentTimeMillis();
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat src = OpenCVUtil.fileToMat(filepath);
		String tmpfilepath = filepath.split("\\.")[0]+"tmp."+filepath.split("\\.")[1];
		OpenCVUtil.bmfx(src, tmpfilepath);
		String result = doOcr(tmpfilepath,tessdataPath);
		//删除临时文件
		File tmpFile = new File(tmpfilepath);
		tmpFile.delete();
		String ssqq = "";
		//分析诉讼请求
		BufferedReader br = new BufferedReader(new StringReader(result));
		String line = null;
		boolean jilu = false;
		while((line = br.readLine())!=null) {
			line = line.replaceAll(" ", "").trim();//去除空格
			if(line.length()<=0)continue;
			if(line.contains("事实与理由")) {
				jilu = false;
				break;
			}
			if(jilu){
				ssqq = ssqq + line;
			}
			if(line.contains("诉讼请求") || line.contains("上诉请求") ) {
				jilu = true;
			}
			
			
		}
		logger.info("ssqq[{}] before!",ssqq);
		List<String> ssqqList = new ArrayList<String>();
		if(ssqq.contains("1、")){
			for(int i=1 ; i<10;i++) {
				if(ssqq.contains(i+"、") && ssqq!=null && ssqq.length()>0) {
					ssqq = ssqq.split(i+"、")[1];
					if(ssqq.contains((i+1)+"、")) {
						ssqqList.add(ssqq.split((i+1)+"、")[0]);
						ssqq = ssqq.split((i+1)+"、")[1];
					}
				}else {
					ssqqList.add(ssqq);
					break;
				}
			}
		}else {
			ssqqList.add(ssqq);
		}
		
		logger.info("SSQQ[{}]",ssqqList);
		long end = System.currentTimeMillis();
		logger.info("花费时间[{}s]!",(end-start)/1000);
		return ssqqList;
		
	}
	
	private static String doOcr(String imgpath,String tessdataPath) throws TesseractException {
		String datapath = tessdataPath;
//		String datapath = Tuxiangshibie.class.getClass().getResource("/").getPath();
//		String datapath = ".";
		logger.info("datapath:{}",datapath);
		Tesseract1 tesseract1 = new Tesseract1();
		tesseract1.setDatapath(new File(datapath).getPath());
		String result = null;
		result = tesseract1.doOCR(new File(imgpath));
		return result;
	}

}
