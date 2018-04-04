package com.ld.pdftoword;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.poi.xwpf.usermodel.XWPFTable;

import com.ld.myutils.LoggerUtil;


/**
 * 表格数据
 * @author Seven
 * @date 2017年9月22日
 */
public class Table {
	private static Logger logger = LoggerUtil.getJdkLog();
	private int rownum;//行数
	private int colnum;//列数
	private String matrix[][];//矩阵
	private Map<String,TableCell> tableData = new HashMap<String,TableCell>();
	private List<MergeTableCell> mergeTableCells = new ArrayList<MergeTableCell>();//定义合并的单元格
	private List<TableLine> xBaseLine = new ArrayList<TableLine>();
	private List<TableLine> yBaseLine = new ArrayList<TableLine>();
	private XWPFTable xwpfTable; 
	public Table(int rownum,int colnum) {
		this.rownum = rownum;
		this.colnum = colnum;
		//初始化数组
		matrix = new String[rownum][colnum];
		for(int row=0;row<rownum;row++) {
			for(int col=0;col<colnum;col++) {
				matrix[row][col] = row+","+col;
			}
		}
		
	}

	public int getRownum() {
		return rownum;
	}

	public void setRownum(int rownum) {
		this.rownum = rownum;
	}

	public int getColnum() {
		return colnum;
	}

	public void setColnum(int colnum) {
		this.colnum = colnum;
	}

	
	
	public Map<String, TableCell> getTableData() {
		return tableData;
	}

	public void setTableData(Map<String, TableCell> tableData) {
		this.tableData = tableData;
	}

	public String[][] getMatrix() {
		return matrix;
	}

	public void setMatrix(String[][] matrix) {
		this.matrix = matrix;
	}

	public List<MergeTableCell> getMergeTableCells() {
		return mergeTableCells;
	}

	public void setMergeTableCells(List<MergeTableCell> mergeTableCells) {
		this.mergeTableCells = mergeTableCells;
	}

	public List<TableLine> getxBaseLine() {
		return xBaseLine;
	}

	public void setxBaseLine(List<TableLine> xBaseLine) {
		this.xBaseLine = xBaseLine;
	}

	public List<TableLine> getyBaseLine() {
		return yBaseLine;
	}

	public void setyBaseLine(List<TableLine> yBaseLine) {
		this.yBaseLine = yBaseLine;
	}

	public XWPFTable getXwpfTable() {
		return xwpfTable;
	}

	public void setXwpfTable(XWPFTable xwpfTable) {
		this.xwpfTable = xwpfTable;
	}

	/**
	 * 分析合并单元格信息
	 */
	public void analsysMergeCell() {
		Map<String,List<String>> fenge = new HashMap<String,List<String>>();
		for(int row=0;row<rownum;row++) {
			for(int col=0;col<colnum;col++) {
				String cellContent = matrix[row][col];
				if(fenge.get(cellContent)==null) {
					List<String> list = new ArrayList<String>();
					list.add(row+","+col);
					fenge.put(cellContent, list);
				}else {
					List list = fenge.get(cellContent);
					list.add(row+","+col);
				}
			}
		}
		
		for(Map.Entry<String, List<String>> entry : fenge.entrySet()) {
			List<String> valus = entry.getValue();
			if(valus.size()>1) {//大于1就有合并单元格
				MergeTableCell mergeTableCell = new MergeTableCell();
				int fromRow=0,toRow=0,fromCol=0,toCol=0;
				List<Integer> rowNums = new ArrayList<Integer>();
				List<Integer> colNums = new ArrayList<Integer>();
				for(String rowCol : valus) {
					rowNums.add(Integer.parseInt(rowCol.split(",")[0]));
					colNums.add(Integer.parseInt(rowCol.split(",")[1]));
				}
				//排序
				rowNums.sort(new Comparator<Integer>() {
					@Override
					public int compare(Integer o1, Integer o2) {
						// TODO Auto-generated method stub
						if(o1>o2) {
							return 1;
						}else if(o1==o2) {
							return 0;
						}else {
							return -1;
						}
					}
				});
				mergeTableCell.setFromRow(rowNums.get(0));
				mergeTableCell.setToRow(rowNums.get(rowNums.size()-1));
				colNums.sort(new Comparator<Integer>() {
					@Override
					public int compare(Integer o1, Integer o2) {
						// TODO Auto-generated method stub
						if(o1>o2) {
							return 1;
						}else if(o1==o2) {
							return 0;
						}else {
							return -1;
						}
					}
				});
				mergeTableCell.setFromCol(colNums.get(0));
				mergeTableCell.setToCol(colNums.get(colNums.size()-1));
				mergeTableCells.add(mergeTableCell);
			}
			
			
		}
	}
	
	public void printMatrix() {
		for(int row=0;row<rownum;row++) {
			for(int col=0;col<colnum;col++) {
				logger.info(matrix[row][col] +"  ");
			}
		}
		
	}
	
	public void printTableData() {
		for(int row=0;row<rownum;row++) {
			for(int col=0;col<colnum;col++) {
				TableCell cell = tableData.get(row+","+col);
			}
		}
		
	}
	
	public void printMergeCell() {
		for(MergeTableCell mergeTableCell : mergeTableCells) {
		}
		
		
	}
	
	
	
	

}
