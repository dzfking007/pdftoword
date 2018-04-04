package com.ld.pdftoword;

/**
 * 回调函数
 * @author Seven
 * @date 2018年1月2日
 */
public abstract class Callback {
	
	/**
	 * 转化回调函数
	 * @param curNum 当前转化到了第几页
	 * @param totalNum 总页数
	 */
	public abstract void convertCallBack(int curNum,int totalNum);

}
