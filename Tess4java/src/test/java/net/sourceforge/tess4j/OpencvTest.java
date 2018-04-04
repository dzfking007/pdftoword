package net.sourceforge.tess4j;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.LoggerFactory;

import kedacom.tuxiangshibie.Tuxiangshibie;
import net.sourceforge.tess4j.util.LoggHelper;
import opencv.OpenCVUtil;

public class OpencvTest {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(new LoggHelper().toString());
	/**
	 * 找到文字轮廓
	 */
	@Test
	public void findWordLunkuo() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat src = Imgcodecs.imread("D:\\3.jpg");
		Mat dst = new Mat();
		
		dst = OpenCVUtil.filterHSV(src);
//		dst = OpenCVUtil.medianblur(dst);
		Imgcodecs.imwrite("D:\\3_1.jpg", dst);
//		Imgproc.cvtColor(dst, dst, Imgproc.COLOR_BGR2GRAY);
		//做一下膨胀，x与y方向都做，但系数不同
        //使用了Erode方法，腐蚀操作，针对白色区域，所以等效于对文字进行了膨胀
        Mat kernal = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(50, 2 ),new Point( 1, 1 ) );
        Imgproc.erode(dst, dst, kernal);
//        Imgproc.dilate(dst, dst, kernal);
        Imgcodecs.imwrite("D:\\3_2.jpg", dst);
        //二值化
        Imgproc.threshold(dst, dst, 0, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
        Imgcodecs.imwrite("D:\\3_3.jpg", dst);
        ////检测连通域，每一个连通域以一系列的点表示，FindContours方法只能得到第一个域
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(dst, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_NONE);
        Imgproc.cvtColor(dst, dst, Imgproc.COLOR_GRAY2BGR);
        for(int i=0;i<contours.size();i++) {
        	//Imgproc.drawContours(src, contours, i, new Scalar(0,0,255));
        	Rect rect = Imgproc.boundingRect(contours.get(i));
        	
//        	if(rect.area()<1000 || rect.height<30 || rect.height>600)continue;
        	Imgproc.rectangle(dst, rect.tl(), rect.br(), new Scalar(0,0,255));
        	System.out.println(i+":"+rect.area()+" H:"+rect.height);
        }
        
        Imgcodecs.imwrite("D:\\3_4.jpg", dst);
        
        
		
	}
	
	
	/**
	 * /连接图像中断裂的边缘  
//以某一点(i,j)为中心,分析它的八邻域  
	 * @return
	 */
	@Test
	public void ConnectEdge()  {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat src = Imgcodecs.imread("D:\\3_3.jpg",Imgcodecs.IMREAD_GRAYSCALE);
	    int width = src.width();  
	    int height = src.height();  
	    double H=0.0,S=0.0,V=0.0;  
	    int widthStep = 2;
	    for(int i=2;i<height-2;i++)
	    {
	        for(int j=2;j<width-2;j++)
	        {
	        	if(src.get(i*widthStep, j)==null)continue;
	        	//如果该中心点为255,则考虑它的八邻域  
	            if(src.get(i*widthStep, j)[0]==255.0)  
	            {  
	            	int num = 0;  
	                for (int k = -1;k < 2;k++)  
	                {  
	                    for (int l = -1;l < 2;l++)  
	                    {  
	                    	if(src.get((i + k)*widthStep, j + l)!=null) {
	                    		//如果八邻域中有灰度值为0的点，则去找该点的十六邻域  
		                        if(k != 0 && l != 0 && src.get((i + k)*widthStep, j + l)[0] == 255.0)  
		                            num++;  
	                    	}
	                        
	                    }  
	                }
//	                System.out.println(num);
	              //如果八邻域中只有一个点是255，说明该中心点为端点，则考虑他的十六邻域  
	                if(num == 1)  
	                {  
	                    for (int k = -4;k < 5;k++)  
	                    {  
	                        for (int l = -4;l < 5;l++)  
	                        {  
	                            //如果该点的十六邻域中有255的点，则该点与中心点之间的点置为255  
	                        	if(src.get((i + k)*widthStep, j + l)!=null) {
	                        		if(!(k < 4 && k > -4 && l < 4 && l > -4) && src.get((i + k)*widthStep, j + l)[0] == 255.0)  
		                            {  
		                                src.put((i + k / 2) * widthStep, j + l / 2, 255);
		                            }  
	                        	}
	                            
	                        }  
	                    }  
	                }  
	            }
	        }
	    }
	    
	    Imgcodecs.imwrite("D:\\3_6.jpg", src);
	}
	
	@Test
	public void testOpen() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat src = Imgcodecs.imread("D:\\ocr\\3.jpg");
		Mat dst = OpenCVUtil.binarization(src,200);
		Imgcodecs.imwrite("D:\\3_1.jpg", dst);
		//中值滤波 去噪
		dst = OpenCVUtil.medianblur(dst);
		
		dst = OpenCVUtil.open(dst,new Size(1,1));
//		dst = OpenCVUtil.close(dst,new Size(4,4));
		Imgcodecs.imwrite("D:\\3_2.jpg", dst);
	}
	
	//版面批处理
	@Test
	public void bmcl() throws IOException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		File dir = new File("D:\\一审卷宗");
		if(dir.isDirectory()) {
			File subFils[] = dir.listFiles();
			for(int i=0;i<subFils.length;i++) {
				File subFile = subFils[i];
//				System.out.println(subFile.getPath());
				if(subFile.isDirectory()) {
					File subSubFiles[] = subFile.listFiles();
					for(int j=0;j<subSubFiles.length;j++) {
						File subSubFile = subSubFiles[j];
						if(!subSubFile.isDirectory() && subSubFile.getName().endsWith(".jpg")) {
							String outpath =subSubFile.getParentFile().getPath()+File.separator+subSubFile.getName().replaceAll("\\.", "")+".jpg";
							Mat src = OpenCVUtil.fileToMat(subSubFile.getPath());
							OpenCVUtil.bmfx(src, outpath);
						}
					}
				}else {
					if(subFile.getName().endsWith(".jpg")) {
						String outpath = subFile.getParentFile().getPath()+File.separator+subFile.getName().replaceAll("\\.", "")+".jpg";
						System.out.println(subFile.getPath());
						Mat src = OpenCVUtil.fileToMat(subFile.getPath());
						OpenCVUtil.bmfx(src, outpath);
					}
				}
				
			}
		}
	}
	
	//版面分析
	@Test
	public void bmfx() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat src = Imgcodecs.imread("D:\\ocr\\21.jpg");
//		doOcr("D:\\ocr\\3.jpg", "D:\\ocr\\3.txt");
		Mat dst = OpenCVUtil.filterHSV(src);
//		dst = OpenCVUtil.gray(dst);
		//HSV
		Core.bitwise_not(dst, dst);
		int thro = OpenCVUtil.Otsu(dst);
		System.out.println("thro:"+thro);
		//二值化
		dst = OpenCVUtil.binarization(dst, thro);
		Imgcodecs.imwrite("D:\\ocr\\3_1.jpg", dst);
		//矫正图片方向
//		dst = OpenCVUtil.jiaozheng(dst);
//		Imgcodecs.imwrite("D:\\ocr\\3_1_1.jpg", dst);
		//竖直投影
		Mat verMat = new Mat(dst.rows(), dst.cols(), CvType.CV_8U, new Scalar(255));
		Map xmap = OpenCVUtil.getVerProjImage(dst,verMat,20);
		Imgcodecs.imwrite("D:\\ocr\\3_1_2.jpg", verMat);
		int minX = (int)xmap.get("minX");
		int maxX = (int)xmap.get("maxX");
		System.out.println("minX"+minX+":maxX:"+maxX);
		dst = OpenCVUtil.jianqie(dst, minX, 0, (dst.width()-maxX+minX), 0);
		Imgcodecs.imwrite("D:\\ocr\\3_1_3.jpg", dst);
		//对竖直剪切后的图像横向投影
		//获取横向投影
		Mat horMat = new Mat(dst.rows(), dst.cols(), CvType.CV_8U, new Scalar(255));
		Map ymap = OpenCVUtil.getHorProjImage(dst,horMat,20);
		Imgcodecs.imwrite("D:\\ocr\\3_1_4.jpg", horMat);
		int minY = (int)ymap.get("minY");
		int maxY = (int)ymap.get("maxY");
		System.out.println("minY"+minY+":maxY:"+maxY);
		dst = OpenCVUtil.jianqie(dst, 0, minY, 0, (dst.height()-maxY+minY));
		Imgcodecs.imwrite("D:\\ocr\\3_1_5.jpg", dst);
//		doOcr("D:\\ocr\\3_1_5.jpg", "D:\\ocr\\3_1_5.txt");
		//矫正图片方向
		dst = OpenCVUtil.jiaozheng(dst);
		Imgcodecs.imwrite("D:\\ocr\\3_1_6.jpg", dst);
//		doOcr("D:\\ocr\\3_1_6.jpg", "D:\\ocr\\3_1_6.txt");
		//缩放图像，固定宽高
		/*dst = OpenCVUtil.erode(0.5, dst);
		Imgcodecs.imwrite("D:\\ocr\\3_2.jpg", dst);*/
//		doOcr("D:\\ocr\\3_2.jpg", "D:\\ocr\\3_2.txt");
		//中值滤波 去噪
		dst = OpenCVUtil.medianblur(dst);
		Imgcodecs.imwrite("D:\\ocr\\3_1_7.jpg", dst);
		doOcr("D:\\ocr\\3_1_7.jpg", "D:\\ocr\\3_1_7.txt");
//		dst = OpenCVUtil.open(dst,new Size(1,1));
//		dst = OpenCVUtil.close(dst,new Size(4,4));
		Imgcodecs.imwrite("D:\\ocr\\3_3.jpg", dst);
//		doOcr("D:\\ocr\\3_3.jpg", "D:\\ocr\\3_3.txt");
	}
	
	
	
	private void doOcr(String imgpath,String outpath) {
		String datapath = "src/main/resources";
		System.out.println(datapath);
		Tesseract1 tesseract1 = new Tesseract1();
		tesseract1.setDatapath(new File(datapath).getPath());
		String result = null;
		FileOutputStream fos = null;
		try {
			result = tesseract1.doOCR(new File(imgpath));
			Rectangle rectangle = new Rectangle();
			StringReader sr = new StringReader(result);
			BufferedReader br = new BufferedReader(new StringReader(result));
			fos = new FileOutputStream(new File(outpath));
			String line = null;
			while((line = br.readLine())!=null) {
				line = line.replaceAll(" ", "").trim();//去除空格
				if(line.length()<=0)continue;
				line = line + System.getProperty("line.separator");
				fos.write(line.getBytes());
			}
			
			
		} catch (TesseractException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				fos.flush();
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	}
	
	
	@Test
	public void jiaozheng() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat src = Imgcodecs.imread("D:\\ocr\\12.jpg");
		Mat dst = OpenCVUtil.gray(src);
		dst = OpenCVUtil.open(dst, new Size(200,30));
		dst = OpenCVUtil.close(dst, new Size(200,30));
		int thro = OpenCVUtil.Otsu(dst);
		dst = OpenCVUtil.binarization(dst, thro);
		Imgcodecs.imwrite("D:\\ocr\\3_1.jpg", dst);
		Mat edges = new Mat();
		Imgproc.Canny(dst, edges, 20, 160);
		Imgcodecs.imwrite("D:\\ocr\\3_2.jpg", edges);
		Mat lines = new Mat();//检测到的直线集合
		Imgproc.HoughLinesP(edges, lines, 1, Math.PI/180, 45, 400, 20);
		Mat lineMat = new Mat();
		src.copyTo(lineMat);
		List<Double> angelList = new ArrayList<Double>();
		for(int i=0;i<lines.rows();i++) {
			double[] points = lines.get(i, 0);
	        double x1,x2,y1, y2;
	        //just need the horizontal lines
	        x1 = points[0];
	        x2 = points[2];
	        y1 = points[1];
	        y2 = points[3];
	        // if it slopes, get the average of them, store the y-coordinate
	        //偏差
	       /* if (Math.abs(y1 - y2) < 30) {
	            lineYs.add((y1 + y2) / 2);
	        }
	        if(Math.abs(x1 - x2) < 30) {
	        	 lineXs.add((x1 + x2) / 2);
	        }*/
	        
	        double atan = Math.atan2(y1 - y2, x1 - x2);
	        angelList.add(atan);
	        System.out.println(atan);
	        Imgproc.line(lineMat, new Point(x1, y1), new Point(x2,y2), new Scalar(0, 0, 255), 1);
		}
		Imgcodecs.imwrite("D:\\ocr\\3_3.jpg", lineMat);
		Mat angelMat = new Mat();
		src.copyTo(angelMat);
		Point center = new Point(src.cols()/2, src.rows()/2);
		Mat M = Imgproc.getRotationMatrix2D(center, -angelList.get(0), 1.0);
		Imgproc.warpAffine(angelMat, angelMat, M, dst.size(), Imgproc.INTER_CUBIC,Core.BORDER_DEFAULT,Scalar.all(255));
	    Imgcodecs.imwrite("D:\\ocr\\3_4.jpg", angelMat);

	}
	
	@Test
	public void jiaozheng2() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat src = Imgcodecs.imread("D:\\ocr\\4_1.jpg",Imgcodecs.IMREAD_GRAYSCALE);
		//翻转颜色
//		Core.bitwise_not(src, src);
		Point center = new Point(src.cols()/2, src.rows()/2);
		Mat padded = new Mat();
		int opWidth = Core.getOptimalDFTSize(src.rows());
		int opHeight = Core.getOptimalDFTSize(src.cols());
		Core.copyMakeBorder(src, padded, 0, opWidth-src.rows(), 0, opHeight-src.cols(), Core.BORDER_CONSTANT, Scalar.all(0));
		
		List<Mat> planes = new ArrayList<Mat>(2);
		Mat real = new Mat();
		padded.convertTo(real, CvType.CV_32F);
		
		planes.add(real);
		planes.add(Mat.zeros(padded.size(),CvType.CV_32F));
		
		Mat comImg = new Mat();
		//Merge into a double-channel image
		Core.merge(planes, comImg);
		//Use the same image as input and output,
		//so that the results can fit in Mat well
		Core.dft(comImg, comImg);
		
		//Compute the magnitude
		//planes[0]=Re(DFT(I)), planes[1]=Im(DFT(I))
		//magnitude=sqrt(Re^2+Im^2)
		Core.split(comImg, planes);
		Core.magnitude(planes.get(0), planes.get(1), planes.get(0));
		//Switch to logarithmic scale, for better visual results
		//M2=log(1+M1)
		Mat magMat = planes.get(0);
		
		
		Core.add(magMat, Scalar.all(1), magMat);
		Core.log(magMat, magMat);

		//Crop the spectrum
		//Width and height of magMat should be even, so that they can be divided by 2
		//-2 is 11111110 in binary system, operator & make sure width and height are always even
		magMat = new Mat(magMat, new Rect(0, 0, magMat.cols() & -2, magMat.rows() & -2));
		//Rearrange the quadrants of Fourier image,
		//so that the origin is at the center of image,
		//and move the high frequency to the corners
		int cx = magMat.cols()/2;
		int cy = magMat.rows()/2;
		Mat q0 = new Mat(magMat, new Rect(0, 0, cx, cy));// Top-Left - Create a ROI per quadrant
		Mat q1 = new Mat(magMat, new Rect(cx, 0, cx, cy));// Top-Right
		Mat q2 = new Mat(magMat, new Rect(0, cy, cx, cy));// Bottom-Left
		Mat q3 = new Mat(magMat, new Rect(cx, cy, cx, cy));// Bottom-Right
		
		Mat tmp = new Mat();
		q0.copyTo(tmp);
		q3.copyTo(q0);
		tmp.copyTo(q3);
	 
		q1.copyTo(tmp);
		q2.copyTo(q1);
		tmp.copyTo(q2);
		//替换值
		for(int x=0;x<cx;x++) {
			for(int y=0;y<cy;y++) {
				magMat.put(y, x, q0.get(y, x));
			}
		}
		for(int x=cx;x<magMat.cols();x++) {
			for(int y=0;y<cy;y++) {
				magMat.put(y, x, q1.get(y, x-cx));
			}
		}
		for(int x=0;x<cx;x++) {
			for(int y=cy;y<magMat.rows();y++) {
				magMat.put(y, x, q2.get(y-cy, x));
			}
		}
		for(int x=cx;x<magMat.cols();x++) {
			for(int y=cy;y<magMat.rows();y++) {
				magMat.put(y, x, q3.get(y-cy, x-cx));
			}
		}
		//
		Core.normalize(magMat, magMat, 0, 255, Core.NORM_MINMAX);
		Mat magImg = new Mat(magMat.size(), CvType.CV_8UC1);
		magMat.convertTo(magImg,CvType.CV_8UC1);
		Imgcodecs.imwrite("D:\\ocr\\3_1.jpg", magImg);
		//二值化
		int thod = OpenCVUtil.Otsu(magImg);
		Imgproc.threshold(magImg, magImg, 130, 255, Imgproc.THRESH_BINARY);
		Imgcodecs.imwrite("D:\\ocr\\3_2.jpg", magImg);
		//寻找直线
		MatOfDouble lines = new  MatOfDouble();//检测到的直线集合
		Imgproc.HoughLinesP(magImg, lines, 1, Math.PI/180, 200, 200, 20);
		Mat lineMat = new Mat(magImg.size(),CvType.CV_8UC3);
		List<Double> angelList = new ArrayList<Double>();
		System.out.println(lines.rows());
		
		for(int i=0;i<lines.rows();i++) {
			/*double rho = lines.get(i, 0)[0], theta = lines.get(i, 0)[1];
			Point pt1 = new Point();
			Point pt2 = new Point();
			double a = Math.cos(theta), b = Math.sin(theta);
			double x0 = a*rho, y0 = b*rho;
			pt1.x = Math.round(x0 + 1000*(-b));
			pt1.y = Math.round(y0 + 1000*(a));
			pt2.x = Math.round(x0 - 1000*(-b));
			pt2.y = Math.round(y0 - 1000*(a));
			Imgproc.line(lineMat,pt1,pt2,new Scalar(255,0,0),3,8,0);*/

			double[] points = lines.get(i, 0);
	        double x1,x2,y1, y2;
	        //just need the horizontal lines
	        x1 = points[0];
	        y1 = points[1];
	        x2 = points[2];
	        y2 = points[3];
	        if((x1>magImg.cols()/2 && x2<magImg.cols()/2 && y1>magImg.rows()/2 && y2<magImg.rows()/2)
	         || (x2>magImg.cols()/2 && x1<magImg.cols()/2 && y2>magImg.rows()/2 && y1<magImg.rows()/2)
	         || (x1 == magImg.cols()/2 && x2 == magImg.cols()/2)
	         || (y1 == magImg.rows()/2 && y2 == magImg.rows()/2)) {
	        	//计算交叉点
	        	Point p1 = new Point(x1, y1);  
	        	Point p2 = new Point(x2, y2);  
	        	      
	        	Point p3 = new Point(0, magImg.rows()/2);  
	        	Point p4 = new Point(magImg.cols(), magImg.rows()/2);  
	        	if(p1.y ==  magImg.rows()/2 && p2.y ==  magImg.rows()/2) {//中心水平线
	        		Imgproc.line(lineMat, new Point(x1, y1), new Point(x2,y2), new Scalar(0, 0, 255), 1);
	        		System.out.println("shuiping");
	        	}else if(p1.x ==  magImg.cols()/2 && p2.x ==  magImg.cols()/2){//中心竖直线
	        		Imgproc.line(lineMat, new Point(x1, y1), new Point(x2,y2), new Scalar(0, 0, 255), 1);
	        		System.out.println("shuzhi");
	        	}else {
	        		List l1 = CalParam(p1, p2);  
		        	 List l2 = CalParam(p3, p4);  
		        	 Point rp = getIntersectPoint(l1, l2);  
		        	 if(rp!=null) {
		        		 System.out.println("他们的交点为: (" + rp.x + "," + rp.y + ")" + ":("+Math.abs(rp.x-magImg.cols()/2)+","+Math.abs(rp.y-magImg.rows()/2)+")");  
		        		 if(Math.abs(rp.x-magImg.cols()/2)<10 && Math.abs(rp.y-magImg.rows()/2)<10) {
		        			 Imgproc.line(lineMat, new Point(x1, y1), new Point(x2,y2), new Scalar(0, 0, 255), 1);
		        			 System.out.println("他们的交点为: (" + rp.x + "," + rp.y + ")" + ":("+Math.abs(rp.x-magImg.cols()/2)+","+Math.abs(rp.y-magImg.rows()/2)+")");  
		        			 System.out.println("line");
		        			 //计算角度
		        			 double theta = Math.atan((x1-x2)/(y1-y2));
		        			 double angel = Math.atan(magImg.rows()*Math.tan(theta)/magImg.cols())*180/Math.PI;
		        			 System.out.println("theta:"+angel);
		        			 /*double angel=0;
		        			 double piThresh = Math.PI/90;
		        			 double pi2 = Math.PI/2;
		        			 double theta = Math.atan((y1-y2)/(x1-x2));
		        			 System.out.println("theta:"+theta);
		        			 if(Math.abs(theta) < piThresh || Math.abs(theta-pi2) < piThresh) {
		        				 
		        			 }else{
		        			 		angel = theta;
		        			 }
		        			 
		        			  
		        			 angel = angel<pi2 ? angel : angel-Math.PI;
		        			 if(angel != pi2){
		        				double angelT = magImg.rows()*Math.tan(angel)/magImg.cols();
		        			 	angel = Math.atan(angelT);
		        			 }
		        			
		        			 double angelD = angel*180/Math.PI;
		        			 
		        			 System.out.println("angel:"+angel+"，angelD："+angelD);*/
		        		 }
		        	 }
	        	}
	        	 
	        }
	        
	        
	      
	       
		}
		Imgcodecs.imwrite("D:\\ocr\\3_3.jpg", lineMat);
		
	 
	}
	
	@Test
	public void tets() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat src = Imgcodecs.imread("D:\\ocr\\6.jpg",Imgcodecs.IMREAD_GRAYSCALE);
		Point center = new Point(src.cols()/2, src.rows()/2);
		Mat rotMatS = Imgproc.getRotationMatrix2D(center, 60, 1.0);
		Imgproc.warpAffine(src, src, rotMatS, src.size(), 1, 0, new Scalar(255,255,255));
		Imgcodecs.imwrite("D:\\ocr\\4_1.jpg", src);
	}
	
	/** 
	 * 计算两点的直线方程的参数a,b,c 
	 * @param p1 
	 * @param p2 
	 * @return 
	 */  
	public static List CalParam(Point p1, Point p2){  
	    double a,b,c;  
	    double x1 = p1.x, y1 = p1.y, x2 = p2.x, y2 = p2.y;  
	    a = y2 - y1;  
	    b = x1 - x2;  
	    c = (x2 - x1) * y1 - (y2 - y1) * x1;  
	    if (b < 0) {  
	        a *= -1; b *= -1; c *= -1;  
	    }else if (b == 0 && a < 0) {  
	        a *= -1; c *= -1;  
	    } 
	    List ret = new ArrayList();
	    ret.add(a);
	    ret.add(b);
	    ret.add(c);
	    return ret;  
	}  
	  
	/** 
	 * 计算两条直线的交点 
	 * @param pm1 
	 * @param pm2 
	 * @return 
	 */  
	public static Point getIntersectPoint(List l1, List l2){  
	    return getIntersectPoint((double)l1.get(0), (double)l1.get(1), (double)l1.get(2), (double)l2.get(0), (double)l2.get(1), (double)l2.get(2));  
	}  
	  
	public static Point getIntersectPoint(double a1, double b1, double c1, double a2, double b2, double c2){  
	    Point p = null;  
	    double m = a1 * b2 - a2 * b1;  
	    if (m == 0) {  
	        return null;  
	    }  
	    double x = (c2 * b1 - c1 * b2) / m;  
	    double y = (c1 * a2 - c2 * a1) / m;  
	    p = new Point(x, y);  
	    return p;  
	}  
	
	@Test
	public void testByteToMat() throws IOException {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		File file = new File("D:\\ysjz\\jsjz\\1民事一审案件审理流程管理情况、民事案件流程管理表\\案件流程表1.jpg");
		BufferedImage bufferedImage = ImageIO.read(file);
		byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
		Mat mat = Mat.eye(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC3);
		mat.put(0, 0, pixels);
		Imgcodecs.imwrite("D:\\1111.jpg", mat);
	}
	
	@Test
	public void testGetSSqq() throws IOException, TesseractException {
//		logger.info("aaaaa");
		List<String> list = Tuxiangshibie.getSsqq("D:\\ocr\\20.jpg","src/main/resources");
		for(String s : list) {
			System.out.println("[qqqqq]"+s);
		}
	}
	
	

}
