package com.ld.pdftoword;

import org.apache.poi.xwpf.usermodel.Document;

/**
 * pdf 元素 
 * @author Seven
 * @date 2017年9月20日
 */
public class PdfElem {
	
	public static int TYPE_TEXT = 1;//文字
	public static int TYPE_IMAGE = 2;//图片
	public static int TYPE_TABLE = 3;//表格
	
	private float x;
	private float y;
	private int type;//元素类型
	private String textContent;//文本内容
	private int fontsize;//字体大小
	private String fontname;//字体
	private boolean fontBold;//是否是粗体
	private String imagePath;//图片路径
	private String imageName;//图片名称
	private int imageType;//图片格式Document.PICTURE_TYPE_JPEG
	private float imageWidth;//图片宽度
	private float imageHeight;//图片高度
	private Table table;//表格
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getTextContent() {
		return textContent;
	}
	public void setTextContent(String textContent) {
		this.textContent = textContent;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public int getFontsize() {
		return fontsize;
	}
	public void setFontsize(int fontsize) {
		this.fontsize = fontsize;
	}
	public String getFontname() {
		return fontname;
	}
	public void setFontname(String fontname) {
		this.fontname = fontname;
	}
	public int getImageType() {
		return imageType;
	}
	public void setImageType(int imageType) {
		this.imageType = imageType;
	}
	public boolean isFontBold() {
		return fontBold;
	}
	public void setFontBold(boolean fontBold) {
		this.fontBold = fontBold;
	}
	public float getImageWidth() {
		return imageWidth;
	}
	public void setImageWidth(float imageWidth) {
		this.imageWidth = imageWidth;
	}
	public float getImageHeight() {
		return imageHeight;
	}
	public void setImageHeight(float imageHeight) {
		this.imageHeight = imageHeight;
	}
	public Table getTable() {
		return table;
	}
	public void setTable(Table table) {
		this.table = table;
	}
	
	

}
