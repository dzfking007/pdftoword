package com.ld.pdftoword;
/**
 * 合并的单元格
 * @author Seven
 * @date 2017年9月25日
 */
public class MergeTableCell {

	private int fromRow;//行合并
	private int toRow;//行合并
	private int fromCol;//列合并
	private int toCol;
	public int getFromRow() {
		return fromRow;
	}
	public void setFromRow(int fromRow) {
		this.fromRow = fromRow;
	}
	public int getToRow() {
		return toRow;
	}
	public void setToRow(int toRow) {
		this.toRow = toRow;
	}
	public int getFromCol() {
		return fromCol;
	}
	public void setFromCol(int fromCol) {
		this.fromCol = fromCol;
	}
	public int getToCol() {
		return toCol;
	}
	public void setToCol(int toCol) {
		this.toCol = toCol;
	}
	
	
}
