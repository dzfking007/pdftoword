package com.ld.pdftoword;

import java.util.Comparator;

/**
 * 行排序
 * @author Seven
 * @date 2017年9月20日
 */
public class PdfLineComparator implements Comparator{

	@Override
	public int compare(Object o1, Object o2) {
		// TODO Auto-generated method stub
		PdfLine pdfLine1 = (PdfLine)o1;
		PdfLine pdfLine2 = (PdfLine)o2;
		//根据Y坐标排序
		float y1 = pdfLine1.getY();
		float y2 = pdfLine2.getY();
		if(y1>y2) {
			return 1;
		}else if(y1==y2) {
			return 0;
		}else {
			return -1;
		}
	}

}
