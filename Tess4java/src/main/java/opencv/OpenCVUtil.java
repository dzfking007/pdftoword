package opencv;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.tess4j.util.LoggHelper;

public class OpenCVUtil {
	private static final Logger logger = LoggerFactory.getLogger(new LoggHelper().toString());
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	//剪切图片
	public static Mat jianqie(Mat image,int cut_x,int cut_y,int cut_width,int cut_height) {
        int height = image.rows();  
        int width  = image.cols(); 
        logger.info("height:"+height+" width:"+width);
        int ceil_height = height - cut_height;  
        int ceil_width  = width - cut_width;
        logger.info("ceil_height:"+ceil_height+" ceil_width:"+ceil_width);
        Rect rect = new Rect(cut_x,cut_y,ceil_width,ceil_height);  
        Mat retMat = new Mat(image,rect); 

        return retMat;
     
	}
	
	/**
	 * 灰度图像
	 */
	public static Mat gray(Mat srcMat) {
		 Mat dstMat = Mat.zeros(srcMat.size(),srcMat.type());
		 Imgproc.cvtColor(srcMat, dstMat, Imgproc.COLOR_RGB2GRAY);
		 return dstMat;
	}
	/**
	 * 设置图像对比度和亮度
	 * double alpha = 1.15; /**< 控制对比度 
	 *	 double beta=0;  /**< 控制亮度 
	 */
	public static Mat dbd(double alpha, double beta,Mat srcMat) {
		 Mat dstMat = Mat.zeros(srcMat.size(),srcMat.type());
		 srcMat.convertTo(dstMat, srcMat.type(), alpha, beta);
		 return dstMat;
	}
	
	//腐蚀
	//double erosion_size = 1.2;
	public static Mat erode( double erosion_size,Mat srcMat) {
		 Mat dstMat = Mat.zeros(srcMat.size(),srcMat.type());
		 Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                 new Size( 2*erosion_size + 1, 2*erosion_size+1 ),
                 new Point( erosion_size, erosion_size ) );
		 Imgproc.erode(srcMat, dstMat, element);
		 return dstMat;
	}
	
	//膨胀
	//double erosion_size = 1.3;
	public static Mat dilate(double erosion_size ,Mat srcMat) {
		 Mat dstMat = Mat.zeros(srcMat.size(),srcMat.type());
		 Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT,
                 new Size( 2*erosion_size + 1, 2*erosion_size+1 ),
                 new Point( erosion_size, erosion_size) );
		 Imgproc.dilate(srcMat, dstMat, element);
		 return dstMat;
	}
	
	//开运算
	public static Mat open(Mat src, Size size) {
		Mat dst = Mat.zeros(src.size(), src.type());
		Imgproc.morphologyEx(src, dst, Imgproc.MORPH_OPEN, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, size));
		return dst;
	}
	
	//开运算
	public static Mat close(Mat src,Size size) {
		Mat dst = Mat.zeros(src.size(), src.type());
		Imgproc.morphologyEx(src, dst, Imgproc.MORPH_CLOSE, Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, size));
		return dst;
	}
	/**
     * 二值化
     * 
     * @param oriImg
     * @param outputImg
     */
    public static Mat binarization(Mat srcMat,double thresh) {
//        Imgproc.cvtColor(srcMat, srcMat, Imgproc.COLOR_RGB2GRAY);
//        Imgproc.adaptiveThreshold(srcMat, srcMat, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 25, 10);
    	Imgproc.threshold(srcMat, srcMat, thresh, 255, Imgproc.THRESH_BINARY|Imgproc.THRESH_OTSU);
        return srcMat;
    }
	
	/**
	 * 归一化处理
	 */
	public static Mat normalize(Mat srcMat) {
        Mat dst = new Mat();
        Core.normalize(srcMat, dst,0, 255, Core.NORM_MINMAX);
        return dst;
	}
	
	/**
	 * 中值滤波将图像的每个像素用邻域(以当前像素为中心的正方形区域)像素的中值代替。对椒盐噪声最有效的滤波器，去除跳变点非常有效。
	 */
	public static Mat medianblur(Mat srcMat) {
        Mat dst = new Mat();
        Imgproc.medianBlur(srcMat, dst, 1);
        return dst;
	}
	
	/**
	 * 双边滤波器
	 * 为避免滤波器平滑图像去噪的同时使边缘也模糊，这种情况下使用双边滤波器。关于双边滤波器的解释参见
	 */
	public static Mat Bilatrialblur(Mat srcMat) {
        Mat dst = new Mat();
        Imgproc.bilateralFilter(srcMat, dst, 10, 20, 5);
        return dst;
	}
	
	/**
	 * 归一化滤波器（Homogeneous blur）

也是均值滤波器，用输出像素点核窗口内的像素均值代替输出点像素值。
	 */
	public static Mat Homogeneousblur(Mat srcMat) {
        Mat dst = new Mat();
        Imgproc.blur(srcMat, dst, new Size(10,10),new Point(-1, -1));
        return dst;
	}
	/*(*
	 * 高斯滤波器（Guassian blur）

是实际中最常用的滤波器，高斯滤波是将输入数组的每一个像素点与 高斯内核 卷积将卷积和当作输出像素值。高斯核相当于对输出像素的邻域赋予不同的权值，输出像素点所在位置的权值最大（对应高斯函数的均值位置）。二维高斯函数为，
	 */
	public static Mat Guassianblur(Mat srcMat) {
        Mat dst = new Mat();
        Imgproc.GaussianBlur(srcMat, dst, new Size(5, 5), 0, 0, Core.BORDER_CONSTANT);
        return dst;
	}
	
	//要先进性灰度和二值化
	//计算图像的水平投影，并返回一幅图像， 返回 Y 的最大值和最小值  ,水平投影只投水平一半，宽一半
	public static Map getHorProjImage(Mat src,Mat dst,int padding){
		Map<String,Integer> ymap = new HashMap<String,Integer>();//存储Y轴最大最小值
	    int maxLine = 0, maxNum = 0;//重置255最大数目和最大行
	    int minLine = 0, minNum = src.cols();//重置255最小数目和最小行
	    int height = src.rows(), width = src.cols();//图像的高和宽
	    int tmp = 0;//保存当前行的255数目
	    int []projArray = new int[height];//保存每一行255数目的数组
	    //循环访问图像数据，查找每一行的255点的数目
	    for (int i = 0; i < height; ++i)
	    {
	        tmp = 0;
	        for (int j = 0; j < width; ++j)
	        {
	            if (src.get(i, j) != null && src.get(i, j)[0] == 255){
	                ++tmp;
	            }
	        }
	        if(tmp<20)tmp=0;
	        projArray[i] = tmp;
	        if (tmp > maxNum)
	        {
	            maxNum = tmp;
	            maxLine = i;
	        }
	        if (tmp < minNum)
	        {
	            minNum = tmp;
	            minLine = i;
	        }
	    }
	    //创建并绘制水平投影图像
	    for (int i = 0; i < height; ++i){
	    	if(projArray[i]>10) {
	    		Imgproc.line(dst, new Point(projArray[i], i), new Point(0, i), Scalar.all(0));
	    	}
	    }
	    //获得最小Y值
	    int minY = 0,maxY=height;
	    for(int minj=padding;minj<projArray.length;minj++) {
	    	if(projArray[minj]>0) {
	    		minY = minj;
	    		break;
	    	}
	    }
	  //根据间隙获取最小值
	    int first = padding;
	    for(int minj=padding;minj<projArray.length*1/3;minj++) {
	    	if(projArray[minj]>0) {
	    		int gap = minj-first;
	    		if(gap>100) {//第一个间隙大于100的
		    		minY= minj-20;
		    		break;
		    	}else {
		    		first = minj;
		    	}
	    	}
	    }
	    
	    for(int maxj=projArray.length-padding;maxj>projArray.length*2/3;maxj--) {
	    	if(projArray[maxj]>0) {
	    		maxY = maxj;
	    		break;
	    	}
	    }
	    //根据间隙获取最大值
	  //根据间隙获取最大值
	    int end = projArray.length-padding;
	    for(int maxj=projArray.length-padding;maxj>=projArray.length*2/3;maxj--) {
	    	if(projArray[maxj]>0) {
	    		int gap = end-maxj;
	    		if(gap>80) {//第一个间隙大于100的
		    		maxY= maxj+20;
		    		break;
		    	}else {
		    		end = maxj;
		    	}
	    	}
	    }
	    
	    ymap.put("minY", minY);
	    ymap.put("maxY", maxY);
	    return ymap;
	}
	
	
	//计算图像的竖直投影，并返回一幅图像,返回X 的最大值和最小值
	public static Map getVerProjImage(Mat src,Mat dst,int padding){
		Map<String,Integer> xmap = new HashMap<String,Integer>();//存储Y轴最大最小值
	    int maxCol = 0, maxNum = 0;//重置255最大数目和最大行
	    int minCol = 0, minNum = src.rows();//重置255最小数目和最小行
	    int height = src.rows(), width = src.cols();//图像的高和宽
	    int tmp = 0;//保存当前行的255数目
	    int projArray[] = new int[width];//保存每一行255数目的数组
	    //循环访问图像数据，查找每一行的255点的数目
	    for (int col = 0; col < width; ++col)
	    {
	        tmp = 0;
	        for (int row = 0; row < height; ++row)
	        {
	            if (src.get(row, col) != null && src.get(row, col)[0] == 255)//对黑色进行投影
	            {
	                ++tmp;
	            }       
	        }
	        if(tmp<20)tmp=0;
	        projArray[col] = tmp;
	        if (tmp > maxNum)
	        {
	            maxNum = tmp;
	            maxCol = col;
	        }
	        if (tmp < minNum)
	        {
	            minNum = tmp;
	            minCol = col;
	        }
	    }
	  //创建并绘制水平投影图像
	    

	    for (int col = 0; col < width; ++col)
	    {
	        if(projArray[col]>10) {
	    		Imgproc.line(dst, new Point(col, projArray[col]), new Point(col, 0), Scalar.all(0));
	    	}
	    }

	    
	  //获得最小X值
	    int minX = 0,maxX=width;
	    for(int minj=padding;minj<projArray.length-1;minj++) {
	    	if(projArray[minj]>0) {
	    		minX = minj;
	    		break;
	    	}
	    	
	    }
	    
	    //根据间隙获取最小值
	    int first = padding;
	    for(int minj=padding;minj<projArray.length*1/3;minj++) {
	    	if(projArray[minj]>0) {
	    		int gap = minj-first;
	    		if(gap>100) {//第一个间隙大于100的
		    		minX= minj-20;
		    		break;
		    	}else {
		    		first = minj;
		    	}
	    	}
	    }
	    
	    
	    for(int maxj=projArray.length-padding;maxj>padding;maxj--) {
	    	if(projArray[maxj]>0) {
	    		maxX = maxj;
	    		break;
	    	}
	    }
	  //根据间隙获取最大值
	    int end = projArray.length-padding;
	    for(int maxj=projArray.length-padding;maxj>=projArray.length*2/3;maxj--) {
	    	if(projArray[maxj]>0) {
	    		int gap = end-maxj;
	    		if(gap>80) {//第一个间隙大于100的
		    		maxX= maxj+20;
		    		break;
		    	}else {
		    		end = maxj;
		    	}
	    	}
	    }
	    
	    xmap.put("minX", minX);
	    xmap.put("maxX", maxX);
	    return xmap;
	}
	
	/**
	 * 锐化图片
	 * @param bitmap
	 */
	public static Mat filter2D(Mat srcMat) {
		Mat dst = new Mat();
	    	/*
	 	自定义核
	 	0   -1  0
	 	-1  5   -1
	   	0   -1  0
	    */
	    Mat kernel = new Mat(3, 3, CvType.CV_32F);
	    kernel.put(0, 0, 0.0);
	    kernel.put(0, 1, -1.0);
	    kernel.put(0, 2, 0.0);
	    kernel.put(1, 0, -1.0);
	    kernel.put(1, 1, 5.0);
	    kernel.put(1, 2, -1.0);
	    kernel.put(2, 0, 0.0);
	    kernel.put(2, 1, -1.0);
	    kernel.put(2, 2, 0.0);
	    // 对图像和自定义核做卷积
	    Imgproc.filter2D(srcMat, dst, srcMat.depth(), kernel);
	    // Mat转Bitmap
	    return dst;
	}
	
	// Canny(Mat image, Mat edges, double threshold1, double threshold2, int
    // apertureSize, boolean L2gradient)
    // 第一个参数，InputArray类型的image，输入图像，即源图像，填Mat类的对象即可，且需为单通道8位图像。
    // 第二个参数，OutputArray类型的edges，输出的边缘图，需要和源图片有一样的尺寸和类型。
    // 第三个参数，double类型的threshold1，第一个滞后性阈值。
    // 第四个参数，double类型的threshold2，第二个滞后性阈值。
    // 第五个参数，int类型的apertureSize，表示应用Sobel算子的孔径大小，其有默认值3。
    // 第六个参数，bool类型的L2gradient，一个计算图像梯度幅值的标识，有默认值false。
	//边缘检测
    public static Mat canny(Mat srcMat,int threshold) {
    	Mat dst = new Mat();
        Imgproc.Canny(srcMat, dst, threshold, threshold * 3, 3, true);
        return dst;
    }
	
	//先新进行二值化
	//矫正文本图像
	public static Mat jiaozheng(Mat srcMat) {
		Mat dst = new Mat();
		List<Point> list = new ArrayList<Point>();
		int colnum = srcMat.cols();
		int rownum = srcMat.rows();
		int nums = colnum;
		if(colnum>rownum) {//选取长宽最小的作为正方形的便利便捷
			nums = rownum;
		}else {
			nums = colnum;
		}
		for(int row=0 ; row<nums; row++) {//y =x
			for(int col=nums*1/4; col<nums*3/4;col++) {//x = y 正方形
				double value = srcMat.get(row, col)[0];
				if(value==255) {//背景为白色的部分
					Point point = new Point(col, row);
					list.add(point);
				}
			}
		}
		MatOfPoint2f matOfPoint2f = new MatOfPoint2f(list.toArray(new Point[list.size()]));
		RotatedRect rotatedRect = Imgproc.minAreaRect(matOfPoint2f);
		//画出外接最小矩形
		Mat rectMat = new Mat();
		srcMat.copyTo(rectMat);
		Imgproc.cvtColor(rectMat, rectMat, Imgproc.COLOR_GRAY2RGB);
		Point pts[] = {new Point(),new Point(),new Point(),new Point()};
		rotatedRect.points(pts);
		Imgproc.line(rectMat, pts[0], pts[1], new Scalar(0,0,255));
		Imgproc.line(rectMat, pts[1], pts[2], new Scalar(0,0,255));
		Imgproc.line(rectMat, pts[2], pts[3], new Scalar(0,0,255));
		Imgproc.line(rectMat, pts[3], pts[0], new Scalar(0,0,255));
		Imgcodecs.imwrite("D:\\ocr\\3_1_100.jpg", rectMat);
		//
		
		double angel = rotatedRect.angle;
		logger.info("angel:{}",angel);
		logger.info("width:"+rotatedRect.size.width +",height:"+rotatedRect.size.height);
		
		if(Double.compare(rotatedRect.size.width,rotatedRect.size.height)<0){
			angel = 90 -rotatedRect.angle;
			angel = 90-angel;
        }else {
        	angel = -rotatedRect.angle;
        }
		
		/*if(angel < -45) {
			angel = 90 + angel;
		}else if(angel>=-45 && angel<0) {
			angel = -angel;
		}else {
			angel = angel;
		}*/
		logger.info("angel:{}",angel);
		
		if(angel == -90.0 || angel == -0.0 || angel == 90.0 || angel == 0.0) angel = 0.0;
//		if(angel>-1 && angel<1) angel = 0;
		Point center = new Point(srcMat.cols()/2, srcMat.rows()/2);
		Mat M = Imgproc.getRotationMatrix2D(center, angel, 1.0);
		Imgproc.warpAffine(srcMat, dst, M, dst.size(), Imgproc.INTER_CUBIC,Core.BORDER_CONSTANT,Scalar.all(0));
		return dst;
	}
	
	/**
	 * 过滤图片颜色
	 */
	public static Mat filterHSV(Mat srcMat) {
		Mat hsv = new Mat();
		Imgproc.cvtColor(srcMat, hsv, Imgproc.COLOR_RGB2HSV);
		Mat resultGray = new Mat(hsv.rows(), hsv.cols(),CvType.CV_8U,new Scalar(255));  
	    double H=0.0,S=0.0,V=0.0;   
	    for(int i=0;i<hsv.rows();i++)
	    {
	        for(int j=0;j<hsv.cols();j++)
	        {
	            H=hsv.get(i, j)[0];
	            S=hsv.get(i, j)[1];
	            V=hsv.get(i, j)[2];

	            if(S<=250)//黑色
	            {       
	                if((  H>=0 && H <= 180) && V <=150)
	                {
	                    resultGray.put(i,j,0);
	                }

	            }
	        }
	    }
	    return resultGray;
	}
	
	
	//查找矩形封闭区域
	/**
	 * 返回临时切片文件路径
	 * @param srcImgpath
	 * @return
	 */
		public static ArrayList<ArrayList<String>> cvFindContours(String srcImgpath) {
			Mat src = Imgcodecs.imread(srcImgpath);
			Imgcodecs.imwrite("D:\\1_1.jpg", src);
			//HSV转化
			Mat hsv = new Mat();
			Imgproc.cvtColor(src, hsv, Imgproc.COLOR_BGR2HSV);
			Mat resultGray = new Mat(hsv.rows(), hsv.cols(),CvType.CV_8U,new Scalar(255));  
		    Mat resultColor = new Mat(hsv.rows(), hsv.cols(),CvType.CV_8UC3,new Scalar(255, 255, 255));
		    double H=0.0,S=0.0,V=0.0;   
		    List value = new ArrayList();
		    for(int i=0;i<hsv.rows();i++)
		    {
		        for(int j=0;j<hsv.cols();j++)
		        {
		            H=hsv.get(i, j)[0];
		            S=hsv.get(i, j)[1];
		            V=hsv.get(i, j)[2];
		            if(S<=250)//黑色
		            {       
		                if((  H>=0 && H <= 180) && V <=150)
		                {
		                    resultGray.put(i,j,0);
		                    if(!value.contains(H))value.add(H);
		                }

		            }
		        }
		    }
//		    System.out.println(value);
		    Imgcodecs.imwrite("D:\\1_2.jpg", resultGray);
			//灰度化
		    Imgproc.cvtColor(resultGray, resultGray, Imgproc.COLOR_GRAY2RGB);
		    Imgproc.cvtColor(resultGray, src, Imgproc.COLOR_RGB2GRAY);
		    Imgcodecs.imwrite("D:\\1_3.jpg", src);
			//二值化
			Imgproc.adaptiveThreshold(src, src, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 25, 17);
			Imgcodecs.imwrite("D:\\1_4.jpg", src);
			//去噪
			Mat kernelDilate = Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(1, 1));
			Imgproc.dilate(src, src, kernelDilate);//膨胀
			Imgcodecs.imwrite("D:\\1_5.jpg", src);
			Mat kernelErode = Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(2, 2));
			Imgproc.erode(src, src, kernelErode);//腐蚀
			Imgcodecs.imwrite("D:\\1_6.jpg", src);
			//黑白转换
//			double H=0.0,S=0.0,V=0.0;   
			value.clear();
			for(int i=0;i<src.rows();i++)
		    {
		        for(int j=0;j<src.cols();j++)
		        {
		            H=src.get(i, j)[0];
		            if(!value.contains(H))value.add(H);
		            src.put(i, j, 255.0 - H);
		        }
		    }
			System.out.println(value);
			Imgcodecs.imwrite("D:\\1_7.jpg", src);
			//直线检测
			Mat lines = new Mat();
			int threshold = 10;
			double minLineLength = 300;
			double maxLineGap = 10;
			Imgproc.HoughLinesP(src, lines, 1, Math.PI / 180,threshold, minLineLength, maxLineGap);
			Imgcodecs.imwrite("D:\\1_8.jpg", lines);
			//get the lines information from lines and store in lineYs
			List<Double> lineXs = new ArrayList<Double>();
			List<Double> lineYs = new ArrayList<Double>();
			List<Double> uniqueLineXs = new ArrayList<Double>();
			List<Double> uniqueLineYs = new ArrayList<Double>();
			Mat showbeforLines = new Mat();
			Imgproc.cvtColor(src, showbeforLines, Imgproc.COLOR_GRAY2RGB);
		    for (int i = 0; i < lines.rows(); i++) {
		        double[] points = lines.get(i, 0);
		        double x1,x2,y1, y2;
		        //just need the horizontal lines
		        x1 = points[0];
		        x2 = points[2];
		        y1 = points[1];
		        y2 = points[3];
		        // if it slopes, get the average of them, store the y-coordinate
		        //偏差
		        if (Math.abs(y1 - y2) < 30) {
		            lineYs.add((y1 + y2) / 2);
		        }
		        if(Math.abs(x1 - x2) < 30) {
		        	 lineXs.add((x1 + x2) / 2);
		        }
		        Imgproc.line(showbeforLines, new Point(x1, y1), new Point(x2,y2), new Scalar(0, 0, 0), 5);
		    }
		    Imgcodecs.imwrite("D:\\1_9.jpg", showbeforLines);
		    //生成切割图像
		    Mat cutMat = new Mat();
		    showbeforLines.copyTo(cutMat);
		    //
		    getUniqueLines(lineXs, uniqueLineXs, 80);
		    getUniqueLines(lineYs, uniqueLineYs, 80);
		    System.out.println(uniqueLineYs);
			System.out.println(uniqueLineXs);
		    //去除边界太近的线条
		    List<Double> lastLineXs = new ArrayList<Double>();
		    List<Double> lastLineYs = new ArrayList<Double>();
		    removeSideLines(uniqueLineXs, lastLineXs, 50,src.width(),0);
		    removeSideLines(uniqueLineYs, lastLineYs, 50,0,src.height());
		    System.out.println(lastLineYs);
			System.out.println(lastLineXs);
		  //划线y
//		   Mat showLines = new Mat();
//		   Imgproc.cvtColor(showbeforLines, showLines, Imgproc.COLOR_GRAY2RGB);
		  
		   for (double y : lastLineYs) {
			   Point pt1 = new Point(0, y);
			   Point pt2 = new Point(src.width(), y);
			   Imgproc.line(showbeforLines, pt1, pt2, new Scalar(0, 0, 255), 1);
		   }
		   //划x
		 //划线y
		   for (double x : lastLineXs) {
			   Point pt1 = new Point(x, 0);
			   Point pt2 = new Point(x, src.height());
			   Imgproc.line(showbeforLines, pt1, pt2, new Scalar(0, 0, 255), 1);
		   }
		   Imgcodecs.imwrite("D:\\1_10.jpg", showbeforLines);
		   int padding = 5;
		   
		 //黑白转换
		   Imgproc.cvtColor(cutMat, cutMat, Imgproc.COLOR_BGR2GRAY);
			value.clear();
			for(int i=0;i<cutMat.rows();i++)
		    {
		        for(int j=0;j<cutMat.cols();j++)
		        {
		            H=cutMat.get(i, j)[0];
		            if(!value.contains(H))value.add(H);
		            cutMat.put(i, j, 255.0 - H);
		        }
		    }
			System.out.println(value);
		   ArrayList<ArrayList<String>> outImgpaths = new ArrayList<ArrayList<String>>();
		   for(int y=0;y<lastLineYs.size()-1;y++) {//行
			   ArrayList<String> rows = new ArrayList<String>();
			   for(int x=0;x<lastLineXs.size()-1;x++) {//列
				   Rect rect = new Rect(new Point(lastLineXs.get(x)+padding, lastLineYs.get(y)+padding),new Point(lastLineXs.get(x+1)-padding, lastLineYs.get(y+1)-padding));
				   String imgpath = "D:\\1_"+y+"_"+x+".jpg";
				   Imgcodecs.imwrite(imgpath, new Mat(cutMat, rect));
				   rows.add(imgpath);
				   Imgproc.rectangle(cutMat, new Point(lastLineXs.get(x)+padding, lastLineYs.get(y)+padding),new Point(lastLineXs.get(x+1)-padding, lastLineYs.get(y+1)-padding), new Scalar(0, 0, 255), 3);
			   }
			   outImgpaths.add(rows);
		   }
		   
		   Imgcodecs.imwrite("D:\\1_11.jpg", cutMat);
		   
		   return outImgpaths;
		   
		   
		}
		
		/**
		 * 移除距离边界太近的线条边界线条
		 * @param width
		 */
		public static void removeSideLines(List<Double> src,List<Double> dst,int gap,int imgWidth,int imgHeigth) {
			for(Double line : src) {
				if(imgWidth!=0 && imgHeigth==0) { //X
					if(line<gap || (imgWidth-line)<gap)continue;
					dst.add(line);
				}else if(imgWidth==0 && imgHeigth!=0) {//Y
					if(line<gap || (imgHeigth - line)<gap)continue;
					dst.add(line);
				}
				
				
			}
			
		}
		
		/**
		 * filter the source coordinates, if some values are too close ,get the average of them
		 * 最小间距
		 * @param src    source coordinates list
		 * @param dst    destination coordinate list
		 * @param minGap the minimum gap between coordinates
		 */
		public static void getUniqueLines(List<Double> src, List<Double> dst, int minGap) {
		    Collections.sort(src); //sort the source coordinates list
		    for (int i = 0; i < src.size(); i++) {
		        double sum = src.get(i);
		        double num = 1;
		        //when the distance between lines less than minGap, get the average of thema
		        while (i != src.size() - 1 && src.get(i + 1) - src.get(i) < minGap) {
		            num++;
		            sum = sum + src.get(i + 1);
		            i++;
		        }
		        if (num == 1) {
		            dst.add(src.get(i));
		        } else {
		            dst.add(((sum / num)));
		        }
		    }
		}
		
		//大津算法 获取阈值
		public static int Otsu(Mat src){
		    int width = src.cols() ;
		    int height = src.rows() ;
		    int x = 0,y = 0;
		    int pixelCount[] = new int[256];
		    float pixelPro[] = new float[256];
		    int i, j, pixelSum = width * height, threshold = 0;
		    //count every pixel number in whole image
		    for(i = y; i < height; i++)
		    {
		        for(j = x;j <width;j++)
		        {
		            pixelCount[(int)src.get(i, j)[0]]++;
		        }
		    }

		    //count every pixel's radio in whole image pixel
		    for(i = 0; i < 256; i++)
		    {
		        pixelPro[i] = (float)(pixelCount[i]) / (float)(pixelSum);
		    }

		    // segmentation of the foreground and background
		    // To traversal grayscale [0,255],and calculates the variance maximum grayscale values ​​for the best threshold value
		    float w0, w1, u0tmp, u1tmp, u0, u1, u,deltaTmp, deltaMax = 0;
		    for(i = 0; i < 256; i++)
		    {
		        w0 = w1 = u0tmp = u1tmp = u0 = u1 = u = deltaTmp = 0;

		        for(j = 0; j < 256; j++)
		        {
		            if(j <= i)   //background
		            {
		                w0 += pixelPro[j];
		                u0tmp += j * pixelPro[j];
		            }
		            else        //foreground
		            {
		                w1 += pixelPro[j];
		                u1tmp += j * pixelPro[j];
		            }
		        }

		        u0 = u0tmp / w0;
		        u1 = u1tmp / w1;
		        u = u0tmp + u1tmp;
		        //Calculating the variance
		        deltaTmp = w0 * (u0 - u)*(u0 - u) + w1 * (u1 - u)*(u1 - u);
		        if(deltaTmp > deltaMax)
		        {
		            deltaMax = deltaTmp;
		            threshold = i;
		        }
		    }
		    //return the best threshold;
		    return threshold;
		}
		
		/**
		 * 检测圆形
		 * @param src
		 * @return
		 */
		public static Mat detectCircle(Mat src) {
			System.out.println("sssss");
			Mat dst = new Mat();
			src.copyTo(dst);
			Imgproc.HoughCircles(src, dst, Imgproc.CV_HOUGH_GRADIENT, 1.5, 10);
			for (int i = 0; i < dst.rows(); i++) {
				double circle[] = dst.get(i, 0);
				System.out.println(circle);
			}
			return dst;
		}
		
		//版面分析
		public static void bmfx(Mat src,String outPath) {
			
//			Mat src = Imgcodecs.imread(srcPath);
			Mat dst = OpenCVUtil.filterHSV(src);
//			dst = OpenCVUtil.gray(dst);
			//HSV
			Core.bitwise_not(dst, dst);
			int thro = OpenCVUtil.Otsu(dst);
			logger.info("thro:{}",thro);
			//二值化
			dst = OpenCVUtil.binarization(dst, thro);
			//矫正图片方向
//			dst = OpenCVUtil.jiaozheng(dst);
			//竖直投影
			Mat verMat = new Mat(dst.rows(), dst.cols(), CvType.CV_8U, new Scalar(255));
			Map xmap = OpenCVUtil.getVerProjImage(dst,verMat,20);
			int minX = (int)xmap.get("minX");
			int maxX = (int)xmap.get("maxX");
			logger.info("minX:{} maxX:{}",minX,maxX);
			dst = OpenCVUtil.jianqie(dst, minX, 0, (dst.width()-maxX+minX), 0);
			//对竖直剪切后的图像横向投影
			//获取横向投影
			Mat horMat = new Mat(dst.rows(), dst.cols(), CvType.CV_8U, new Scalar(255));
			Map ymap = OpenCVUtil.getHorProjImage(dst,horMat,20);
			int minY = (int)ymap.get("minY");
			int maxY = (int)ymap.get("maxY");
			logger.info("minY"+minY+":maxY:"+maxY);
			dst = OpenCVUtil.jianqie(dst, 0, minY, 0, (dst.height()-maxY+minY));
//			doOcr("D:\\ocr\\3_1_5.jpg", "D:\\ocr\\3_1_5.txt");
			//矫正图片方向
			dst = OpenCVUtil.jiaozheng(dst);
//			doOcr("D:\\ocr\\3_1_6.jpg", "D:\\ocr\\3_1_6.txt");
			//缩放图像，固定宽高
			/*dst = OpenCVUtil.erode(0.5, dst);
			Imgcodecs.imwrite("D:\\ocr\\3_2.jpg", dst);*/
//			doOcr("D:\\ocr\\3_2.jpg", "D:\\ocr\\3_2.txt");
			//中值滤波 去噪
			dst = OpenCVUtil.medianblur(dst);
//			Imgcodecs.imwrite(outPath, dst);
			matToFile(dst, outPath);
//			doOcr("D:\\ocr\\3_1_7.jpg", "D:\\ocr\\3_1_7.txt");
//			dst = OpenCVUtil.open(dst,new Size(1,1));
//			dst = OpenCVUtil.close(dst,new Size(4,4));
//			doOcr("D:\\ocr\\3_3.jpg", "D:\\ocr\\3_3.txt");
		}
		
		/**
		 * 直接从文件转化为MAT ，解决opencv不能读取中文路径文件的问题
		 * @param filepath
		 * @return
		 * @throws IOException
		 */
		public static Mat fileToMat(String filepath) throws IOException {
			File file = new File(filepath);
			BufferedImage bufferedImage = ImageIO.read(file);
			byte[] pixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
			Mat mat = Mat.eye(bufferedImage.getHeight(), bufferedImage.getWidth(), CvType.CV_8UC3);
			mat.put(0, 0, pixels);
			return mat;
		}
		
		/**
		 * 直接从MAT转化为文件 ，解决opencv不能读取中文路径文件的问题
		 * @param filepath
		 * @return
		 * @throws IOException
		 */
		public static void matToFile(Mat mat,String outfilepath)  {
			MatOfByte mob = new MatOfByte();;
	        String fileExten = "."+outfilepath.split("\\.")[1];
			Imgcodecs.imencode(fileExten, mat, mob);
			byte[] byteArray = mob.toArray();
			BufferedOutputStream fos = null;
            try {
                fos = new BufferedOutputStream(new FileOutputStream(new File(outfilepath)));
                fos.write(byteArray);
            } catch (Exception e) {
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

}

