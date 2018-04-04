package com.ld.pdftoword;
/**
 * pdf 行
 * @author Seven
 * @date 2017年9月20日
 */

import java.util.LinkedList;
import java.util.List;


public class PdfLine {

	private float x;
	private float y;
	private int type = -1;//对应PDFElem type
	private List<PdfElem> elemList = new LinkedList<PdfElem> ();
	
	
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public List<PdfElem> getElemList() {
		return elemList;
	}
	public void setElemList(List<PdfElem> elemList) {
		this.elemList = elemList;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	
}
