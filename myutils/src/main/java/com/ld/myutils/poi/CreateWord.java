package com.ld.myutils.poi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ddf.EscherColorRef.SysIndexProcedure;
import org.apache.poi.ddf.EscherColorRef.SysIndexSource;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.BreakType;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPageMargins;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBorder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTFonts;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHeight;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHpsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTInd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTParaRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSignedTwipsMeasure;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSpacing;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGrid;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGridCol;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTextScale;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTVerticalJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHeightRule;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHint;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STMerge;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;

/**
 * 生成word
 * @author Seven
 * @date 2017年9月19日
 */
public class CreateWord {
	 /** user space units per inch */
    private static final float POINTS_PER_INCH = 72;
    
    /** user space units per millimeter */
    private static final float POINTS_PER_MM = 1 / (10 * 2.54f) * POINTS_PER_INCH;
    /**  A rectangle the size of A4 Paper. */
    public static final String A4[] = new String[]{String.valueOf((int)(210 * POINTS_PER_MM)*20), String.valueOf((int)(297 * POINTS_PER_MM)*20)};
	
	private XWPFDocument xdoc = null;
	XWPFParagraph paragraph = null;

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		CreateWord createWord = new CreateWord(800.0f,800.0f);
		createWord.insertZcsj(217.73f);
		createWord.insertDqsj(93.859985f);
		createWord.insertWords("微", 16,true, "微软雅黑");
		createWord.insertWords("信", 16,true, "微软雅黑");
		createWord.insertWords("微", 16,true, "微软雅黑");
		createWord.insertWords("A", 9,false, "Times-Italic");
		createWord.insertZcsj(400.63f);
		createWord.insertWords("-", 10,false, "微软雅黑");
		createWord.insertWords("-", 10,false, "微软雅黑");
		createWord.insertWords("微", 10,false, "微软雅黑");
		
		createWord.insertZcsj(91.56f);
//		createWord.insertImage("D:\\Image24.jpg",  "Image24",Document.PICTURE_TYPE_JPEG);
		
		
		int[] colWidthArr = new int[] { 1500, 3000, 1200, 850, 600, 850 };  
	    XWPFTable table = createWord.createTable(8, 6, true, colWidthArr);  
	    createWord.setTableWidthAndHAlign(table, "8000", STJc.CENTER);  
	    
	    
		createWord.saveFile("D:\\1.doc");
	}
	
	public CreateWord(float pageWidth,float pageHeight) {
		xdoc = new XWPFDocument();
		//设置页面属性
		CTSectPr sectPr = xdoc.getDocument().getBody().isSetSectPr() ? xdoc  
			        .getDocument().getBody().getSectPr()  
			        : xdoc.getDocument().getBody().addNewSectPr();  
	   //设置纸张大小
		CTPageSz pgsz = sectPr.isSetPgSz() ? sectPr.getPgSz() : sectPr.addNewPgSz();
		pgsz.setW(new BigInteger(String.valueOf((int)pageWidth*20)));  
		pgsz.setH(new BigInteger(String.valueOf((int)pageHeight*20)));  
		pgsz.setOrient(STPageOrientation.PORTRAIT);  	        
	    CTPageMar ctPageMar = sectPr.addNewPgMar(); 
	    ctPageMar.setLeft(new BigInteger("0"));
	    ctPageMar.setTop(new BigInteger("0"));
	    ctPageMar.setRight(new BigInteger("0"));
	    ctPageMar.setBottom(new BigInteger("0"));
	}
	
	
	
    
	/**
	 * 增加新页面
	 */
	public void addNewPage() {
		 XWPFParagraph xp = xdoc.createParagraph();  
		 xp.setPageBreak(true);
	}
	/**
	 * 设置段前缩进
	 * @param y
	 */
	public void insertDqsj(float y) {
		if(paragraph==null) {
			paragraph = xdoc.createParagraph();
		}
		//设置缩进
		CTPPr ctpPr = null;
		if (paragraph.getCTP() != null) {  
			if (paragraph.getCTP().getPPr() != null) {  
		        ctpPr = paragraph.getCTP().getPPr();  
			} else {  
		        ctpPr = paragraph.getCTP().addNewPPr();  
		    }  
		}
		//设置段前缩进
		CTSpacing pSpacing = ctpPr.getSpacing() != null ? ctpPr.getSpacing(): ctpPr.addNewSpacing();
		pSpacing.setBefore(new BigInteger(String.valueOf((int)y*20)));  
	}
	/**
	 * 设置左侧缩进
	 * @param y
	 */
	public void insertZcsj(float x) {
		paragraph = xdoc.createParagraph();
		//设置缩进
		CTPPr ctpPr = null;
		if (paragraph.getCTP() != null) {  
			if (paragraph.getCTP().getPPr() != null) {  
		        ctpPr = paragraph.getCTP().getPPr();  
			} else {  
		        ctpPr = paragraph.getCTP().addNewPPr();  
		    }  
		}
		//设置左侧缩进
		CTInd pInd = ctpPr.getInd() != null ? ctpPr.getInd() : ctpPr.addNewInd();  
		//一行=100 一磅=20
		float suojin = x*20;
		pInd.setFirstLine(new BigInteger(String.valueOf((int)suojin)));
	}
	
	/**
	 * 插入文字
	 * @param words
	 * @param x
	 * @param y
	 * @param fontSize
	 * @param font
	 * @param gap
	 * @throws IOException 
	 */
	public void insertWords(String words,int fontSize,boolean isbold,String font)  {
		if(paragraph==null) {
			paragraph = xdoc.createParagraph();
		}
		XWPFRun run = paragraph.createRun();
		run.setFontFamily(font);
		run.setFontSize(fontSize);
		run.setText(words);
		run.setBold(isbold);
	}
	
	
	
	/**
	 * 插入图片
	 * @param imagePath
	 * @format 图片格式  png、jpg
	 */
	public void insertImage(String imagePath,String filename,int format,float width,float height) {
		if(paragraph==null) {
			paragraph = xdoc.createParagraph();
		}
		CTPPr ctpPr = paragraph.getCTP().getPPr();
		try {
			 XWPFRun run = paragraph.createRun();
			 File imageFile = new File(imagePath);
			 run.addPicture(new FileInputStream(imageFile),format, filename, Units.toEMU(width), Units.toEMU(height));
			 if(imageFile.exists()) {
				 imageFile.delete();
			 }
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		 
	}
	
	/**
	 * 表格操作-----------------------------------------------------------------------
	 * 
	 */
	
	/** 
	   * @Description:删除指定位置的表格,被删除表格后的索引位置-- 
	   */  
	  public void deleteTableByIndex(XWPFDocument xdoc, int pos) {  
	    Iterator<IBodyElement> bodyElement = xdoc.getBodyElementsIterator();  
	    int eIndex = 0, tableIndex = -1;  
	    while (bodyElement.hasNext()) {  
	      IBodyElement element = bodyElement.next();  
	      BodyElementType elementType = element.getElementType();  
	      if (elementType == BodyElementType.TABLE) {  
	        tableIndex++;  
	        if (tableIndex == pos) {  
	          break;  
	        }  
	      }  
	      eIndex++;  
	    }  
	    xdoc.removeBodyElement(eIndex);  
	  }  
	  
	  public XWPFTable getTableByIndex(XWPFDocument xdoc, int index) {  
	    List<XWPFTable> tablesList = getAllTable(xdoc);  
	    if (tablesList == null || index < 0 || index > tablesList.size()) {  
	      return null;  
	    }  
	    return tablesList.get(index);  
	  }  
	  
	  public List<XWPFTable> getAllTable(XWPFDocument xdoc) {  
	    return xdoc.getTables();  
	  }  
	  
	  /** 
	   * @Description: 得到表格内容(第一次跨行单元格视为一个，第二次跳过跨行合并的单元格) 
	   */  
	  public List<List<String>> getTableRContent(XWPFTable table) {  
	    List<List<String>> tableContentList = new ArrayList<List<String>>();  
	    for (int rowIndex = 0, rowLen = table.getNumberOfRows(); rowIndex < rowLen; rowIndex++) {  
	      XWPFTableRow row = table.getRow(rowIndex);  
	      List<String> cellContentList = new ArrayList<String>();  
	      for (int colIndex = 0, colLen = row.getTableCells().size(); colIndex < colLen; colIndex++) {  
	        XWPFTableCell cell = row.getCell(colIndex);  
	        CTTc ctTc = cell.getCTTc();  
	        if (ctTc.isSetTcPr()) {  
	          CTTcPr tcPr = ctTc.getTcPr();  
	          if (tcPr.isSetHMerge()) {  
	            CTHMerge hMerge = tcPr.getHMerge();  
	            if (STMerge.RESTART.equals(hMerge.getVal())) {  
	              cellContentList.add(getTableCellContent(cell));  
	            }  
	          } else if (tcPr.isSetVMerge()) {  
	            CTVMerge vMerge = tcPr.getVMerge();  
	            if (STMerge.RESTART.equals(vMerge.getVal())) {  
	              cellContentList.add(getTableCellContent(cell));  
	            }  
	          } else {  
	            cellContentList.add(getTableCellContent(cell));  
	          }  
	        }  
	      }  
	      tableContentList.add(cellContentList);  
	    }  
	    return tableContentList;  
	  }  
	  
	  /** 
	   * @Description: 得到表格内容,合并后的单元格视为一个单元格 
	   */  
	  public List<List<String>> getTableContent(XWPFTable table) {  
	    List<List<String>> tableContentList = new ArrayList<List<String>>();  
	    for (int rowIndex = 0, rowLen = table.getNumberOfRows(); rowIndex < rowLen; rowIndex++) {  
	      XWPFTableRow row = table.getRow(rowIndex);  
	      List<String> cellContentList = new ArrayList<String>();  
	      for (int colIndex = 0, colLen = row.getTableCells().size(); colIndex < colLen; colIndex++) {  
	        XWPFTableCell cell = row.getCell(colIndex);  
	        cellContentList.add(getTableCellContent(cell));  
	      }  
	      tableContentList.add(cellContentList);  
	    }  
	    return tableContentList;  
	  }  
	  
	  public String getTableCellContent(XWPFTableCell cell) {  
	    StringBuffer sb = new StringBuffer();  
	    List<XWPFParagraph> cellPList = cell.getParagraphs();  
	    if (cellPList != null && cellPList.size() > 0) {  
	      for (XWPFParagraph xwpfPr : cellPList) {  
	        List<XWPFRun> runs = xwpfPr.getRuns();  
	        if (runs != null && runs.size() > 0) {  
	          for (XWPFRun xwpfRun : runs) {  
	            sb.append(xwpfRun.getText(0));  
	          }  
	        }  
	      }  
	    }  
	    return sb.toString();  
	  }  
	  
	  /** 
	   * @Description: 创建表格,创建后表格至少有1行1列,设置列宽 
	   */  
	  public XWPFTable createTable(int rowSize, int cellSize,  
	      boolean isSetColWidth, int[] colWidths) {  
	    XWPFTable table = xdoc.createTable(rowSize, cellSize); 
	    if (isSetColWidth) {  
	      CTTbl ttbl = table.getCTTbl();  
	      CTTblGrid tblGrid = ttbl.addNewTblGrid();  
	      for (int j = 0, len = Math.min(cellSize, colWidths.length); j < len; j++) {  
	        CTTblGridCol gridCol = tblGrid.addNewGridCol();  
	        gridCol.setW(new BigInteger(String.valueOf(colWidths[j]*20)));  
	      }  
	    }  
	    return table;  
	  }  
	  
	  /** 
	   * @Description: 设置表格总宽度与水平对齐方式 
	   */  
	  public void setTableWidthAndHAlign(XWPFTable table, String width,  
	      STJc.Enum enumValue) {  
	    CTTblPr tblPr = getTableCTTblPr(table);  
	    CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr  
	        .addNewTblW();  
	    if (enumValue != null) {  
	      CTJc cTJc = tblPr.addNewJc();  
	      cTJc.setVal(enumValue);  
	    }  
	    tblWidth.setW(new BigInteger(width));  
	    tblWidth.setType(STTblWidth.DXA);  
	  }  
	  
	  /** 
	   * @Description: 得到Table的CTTblPr,不存在则新建 
	   */  
	  public CTTblPr getTableCTTblPr(XWPFTable table) {  
	    CTTbl ttbl = table.getCTTbl();  
	    CTTblPr tblPr = ttbl.getTblPr() == null ? ttbl.addNewTblPr() : ttbl  
	        .getTblPr();  
	    return tblPr;  
	  }  
	  
	  /** 
	   * @Description: 得到Table的边框,不存在则新建 
	   */  
	  public CTTblBorders getTableBorders(XWPFTable table) {  
	    CTTblPr tblPr = getTableCTTblPr(table);  
	    CTTblBorders tblBorders = tblPr.isSetTblBorders() ? tblPr  
	        .getTblBorders() : tblPr.addNewTblBorders();  
	    return tblBorders;  
	  }  
	  
	  /** 
	   * @Description: 设置表格边框样式 
	   */  
	  public void setTableBorders(XWPFTable table, CTBorder left, CTBorder top,  
	      CTBorder right, CTBorder bottom) {  
	    CTTblBorders tblBorders = getTableBorders(table);  
	    if (left != null) {  
	      tblBorders.setLeft(left);  
	    }  
	    if (top != null) {  
	      tblBorders.setTop(top);  
	    }  
	    if (right != null) {  
	      tblBorders.setRight(right);  
	    }  
	    if (bottom != null) {  
	      tblBorders.setBottom(bottom);  
	    }  
	  }  
	  
	  /** 
	   * @Description: 表格指定位置插入一行, index为新增行所在的行位置(不能大于表行数) 
	   */  
	  public void insertTableRowAtIndex(XWPFTable table, int index) {  
	    XWPFTableRow firstRow = table.getRow(0);  
	    XWPFTableRow row = table.insertNewTableRow(index);  
	    if (row == null) {  
	      return;  
	    }  
	    CTTbl ctTbl = table.getCTTbl();  
	    CTTblGrid tblGrid = ctTbl.getTblGrid();  
	    int cellSize = 0;  
	    boolean isAdd = false;  
	    if (tblGrid != null) {  
	      List<CTTblGridCol> gridColList = tblGrid.getGridColList();  
	      if (gridColList != null && gridColList.size() > 0) {  
	        isAdd = true;  
	        for (CTTblGridCol ctlCol : gridColList) {  
	          XWPFTableCell cell = row.addNewTableCell();  
	          setCellWidthAndVAlign(cell, ctlCol.getW().toString(),  
	              STTblWidth.DXA, null);  
	        }  
	      }  
	    }  
	    // 大部分都不会走到这一步  
	    if (!isAdd) {  
	      cellSize = getCellSizeWithMergeNum(firstRow);  
	      for (int i = 0; i < cellSize; i++) {  
	        row.addNewTableCell();  
	      }  
	    }  
	  }  
	  
	  /** 
	   * @Description: 删除表一行 
	   */  
	  public void deleteTableRow(XWPFTable table, int index) {  
	    table.removeRow(index);  
	  }  
	  
	  /** 
	   * @Description: 统计列数(包括合并的列数) 
	   */  
	  public int getCellSizeWithMergeNum(XWPFTableRow row) {  
	    List<XWPFTableCell> firstRowCellList = row.getTableCells();  
	    int cellSize = firstRowCellList.size();  
	    for (XWPFTableCell xwpfTableCell : firstRowCellList) {  
	      CTTc ctTc = xwpfTableCell.getCTTc();  
	      if (ctTc.isSetTcPr()) {  
	        CTTcPr tcPr = ctTc.getTcPr();  
	        if (tcPr.isSetGridSpan()) {  
	          CTDecimalNumber gridSpan = tcPr.getGridSpan();  
	          cellSize += gridSpan.getVal().intValue() - 1;  
	        }  
	      }  
	    }  
	    return cellSize;  
	  }  
	  
	  /** 
	   * @Description: 得到CTTrPr,不存在则新建 
	   */  
	  public CTTrPr getRowCTTrPr(XWPFTableRow row) {  
	    CTRow ctRow = row.getCtRow();  
	    CTTrPr trPr = ctRow.isSetTrPr() ? ctRow.getTrPr() : ctRow.addNewTrPr();  
	    return trPr;  
	  }  
	  
	  /** 
	   * @Description: 设置行高 
	   */  
	  public void setRowHeight(XWPFTableRow row, String hight,  
	      STHeightRule.Enum heigthEnum) {  
	    CTTrPr trPr = getRowCTTrPr(row);  
	    CTHeight trHeight;  
	    if (trPr.getTrHeightList() != null && trPr.getTrHeightList().size() > 0) {  
	      trHeight = trPr.getTrHeightList().get(0);  
	    } else {  
	      trHeight = trPr.addNewTrHeight();  
	    }  
	    trHeight.setVal(new BigInteger(hight));  
	    if (heigthEnum != null) {  
	      trHeight.setHRule(heigthEnum);  
	    }  
	  }  
	  
	  /** 
	   * @Description: 隐藏行 
	   */  
	  public void setRowHidden(XWPFTableRow row, boolean hidden) {  
	    CTTrPr trPr = getRowCTTrPr(row);  
	    CTOnOff hiddenValue;  
	    if (trPr.getHiddenList() != null && trPr.getHiddenList().size() > 0) {  
	      hiddenValue = trPr.getHiddenList().get(0);  
	    } else {  
	      hiddenValue = trPr.addNewHidden();  
	    }  
	    if (hidden) {  
	      hiddenValue.setVal(STOnOff.TRUE);  
	    } else {  
	      hiddenValue.setVal(STOnOff.FALSE);  
	    }  
	    setRowAllCellHidden(row, hidden);  
	  }  
	  
	  public void setRowAllCellHidden(XWPFTableRow row, boolean isVanish) {  
	    for (int colIndex = 0, colLen = row.getTableCells().size(); colIndex < colLen; colIndex++) {  
	      XWPFTableCell cell = row.getCell(colIndex);  
	      setCellHidden(cell, isVanish);  
	    }  
	  }  
	  
	  
	  
	  /** 
	   * @Description: 设置单元格内容 
	   */  
	  public void setCellNewContent(boolean isLineStart,XWPFTable table, int rowIndex, int col,String content,String font,int fontSize,boolean isbold) {  
	    XWPFTableCell cell = table.getRow(rowIndex).getCell(col);  
	    XWPFParagraph p = null;
	    if(isLineStart) {

	    	if(cell.getText()!=null && cell.getText().trim().length()>0) {//如果不是表格的第一行则创建新的段落
	    		
	    		p = cell.addParagraph();
	    	}else {
	    		p = getCellFirstParagraph(cell); 
	    	}
	    }else {
	    	p = getCellFirstParagraph(cell); 
	    }
//	    p = getCellFirstParagraph(cell); 
	    XWPFRun run = null;
	    /*if (p.getRuns() != null && p.getRuns().size() > 0) {
	    	run = p.getRuns().get(0);
		} else {
			run = p.createRun();
		}*/
	    
	    
	    run = p.createRun();
	    
	    /*List<XWPFRun> cellRunList = p.getRuns();  
	    if (cellRunList == null || cellRunList.size() == 0) {  
	      return;  
	    }  
	    for (int i = cellRunList.size() - 1; i >= 1; i--) {  
	      p.removeRun(i);  
	    }  
	    
	    XWPFRun run = cellRunList.get(0);  */
	    /*if(tableP==null) {
	    	tableP = cell.addParagraph();
	    }
	    if(tableRun == null) {
	    	tableRun = tableP.createRun();
	    }
//	    XWPFParagraph p = cell.addParagraph();
//	    XWPFRun run = p.createRun();*/
	    /*if(isLineStart) {
	    	run.addBreak(BreakType.TEXT_WRAPPING);
	    }*/
	    
		run.setFontFamily(font);
		run.setFontSize(fontSize);
		run.setText(content);
		run.setBold(isbold);
	  }  
	    
	  
	  /** 
	   * @Description: 设置合并单元格内容 
	   */  
	  public void setMergeCellNewContent(XWPFTable table, int rowIndex, int col,String content) {  
	    XWPFTableCell cell = table.getRow(rowIndex).getCell(col);  
	    XWPFParagraph p = getCellFirstParagraph(cell); 
	    XWPFRun run = null;
	    if (p.getRuns() != null && p.getRuns().size() > 0) {
	    	run = p.getRuns().get(0);
		} else {
			run = p.createRun();
		}
	    
	    
	    /*List<XWPFRun> cellRunList = p.getRuns();  
	    if (cellRunList == null || cellRunList.size() == 0) {  
	      return;  
	    }  
	    for (int i = cellRunList.size() - 1; i >= 1; i--) {  
	      p.removeRun(i);  
	    }  
	    
	    XWPFRun run = cellRunList.get(0);  */
	    /*if(tableP==null) {
	    	tableP = cell.addParagraph();
	    }
	    if(tableRun == null) {
	    	tableRun = tableP.createRun();
	    }
//	    XWPFParagraph p = cell.addParagraph();
//	    XWPFRun run = p.createRun();*/

		run.setText(content);
	  }  
	  
	  /** 
	     * @Description: 设置段落文本样式(高亮与底纹显示效果不同)设置字符间距信息(CTSignedTwipsMeasure) 
	     * @param verticalAlign 
	     *            : SUPERSCRIPT上标 SUBSCRIPT下标 
	     * @param position 
	     *            :字符间距位置：>0提升 <0降低=磅值*2 如3磅=6 
	     * @param spacingValue 
	     *            :字符间距间距 >0加宽 <0紧缩 =磅值*20 如2磅=40 
	     * @param indent 
	     *            :字符间距缩进 <100 缩 
	     */  
	  
	    public void setParagraphRunFontInfo(boolean isLineStart,XWPFTable table, int rowIndex, int col,  
	            String content, String cnFontFamily, String enFontFamily,  
	            String fontSize, boolean isBlod, boolean isItalic,  
	            boolean isStrike, boolean isShd, String shdColor,  
	            STShd.Enum shdStyle, int position, int spacingValue, int indent) {
	    	XWPFTableCell cell = table.getRow(rowIndex).getCell(col);  
	    	XWPFParagraph p = null;
	    	if(isLineStart) {
		    	p = cell.addParagraph();
		    	
		    }else {
		    	p = getCellFirstParagraph(cell); 
		    }
//	    	p = getCellFirstParagraph(cell); 
		    
		    XWPFRun pRun = null;
		    if (p.getRuns() != null && p.getRuns().size() > 0) {
		    	pRun = p.getRuns().get(0);
			} else {
				pRun = p.createRun();
			}
	        CTRPr pRpr = getRunCTRPr(p, pRun);  
	        pRun.setText(content);
	        /*if (content!=null && content.length()>0) {  
	            // pRun.setText(content);  
	            if (content.contains("\n")) {// System.properties("line.separator")  
	                String[] lines = content.split("\n");  
	                pRun.setText(lines[0], 0); // set first line into XWPFRun  
	                for (int i = 1; i < lines.length; i++) {  
	                    // add break and insert new text  
	                    pRun.addBreak();  
	                    pRun.setText(lines[i]);  
	                }  
	            } else {  
	                pRun.setText(content, 0);  
	            }  
	        }  */
	        // 设置字体  
	        CTFonts fonts = pRpr.isSetRFonts() ? pRpr.getRFonts() : pRpr  
	                .addNewRFonts();  
	        if (enFontFamily!=null && enFontFamily.length()>0 ) {  
	            fonts.setAscii(enFontFamily);  
	            fonts.setHAnsi(enFontFamily);  
	        }  
	        if (cnFontFamily!=null && cnFontFamily.length()>0) {  
	            fonts.setEastAsia(cnFontFamily);  
	            fonts.setHint(STHint.EAST_ASIA);  
	        }  
	        // 设置字体大小  
	        CTHpsMeasure sz = pRpr.isSetSz() ? pRpr.getSz() : pRpr.addNewSz();  
	        sz.setVal(new BigInteger(fontSize));  
	  
	        CTHpsMeasure szCs = pRpr.isSetSzCs() ? pRpr.getSzCs() : pRpr  
	                .addNewSzCs();  
	        szCs.setVal(new BigInteger(fontSize));  
	  
	        // 设置字体样式  
	        // 加粗  
	        if (isBlod) {  
	            pRun.setBold(isBlod);  
	        }  
	        // 倾斜  
	        if (isItalic) {  
	            pRun.setItalic(isItalic);  
	        }  
	        // 删除线  
	        if (isStrike) {  
	            pRun.setStrikeThrough(isStrike);  
	        }  
	        if (isShd) {  
	            // 设置底纹  
	            CTShd shd = pRpr.isSetShd() ? pRpr.getShd() : pRpr.addNewShd();  
	            if (shdStyle != null) {  
	                shd.setVal(shdStyle);  
	            }  
	            if (shdColor != null) {  
	                shd.setColor(shdColor);  
	                shd.setFill(shdColor);  
	            }  
	        }  
	  
	        // 设置文本位置  
	        if (position != 0) {  
	            pRun.setTextPosition(position);  
	        }  
	        if (spacingValue > 0) {  
	            // 设置字符间距信息  
	            CTSignedTwipsMeasure ctSTwipsMeasure = pRpr.isSetSpacing() ? pRpr  
	                    .getSpacing() : pRpr.addNewSpacing();  
	            ctSTwipsMeasure  
	                    .setVal(new BigInteger(String.valueOf(spacingValue)));  
	        }  
	        if (indent > 0) {  
	            CTTextScale paramCTTextScale = pRpr.isSetW() ? pRpr.getW() : pRpr  
	                    .addNewW();  
	            paramCTTextScale.setVal(indent);  
	        }  
	    }  
	  
	  /** 
	   * @Description: 删除单元格内容 
	   */  
	  public void deleteCellContent(XWPFTable table, int rowIndex, int col) {  
	    XWPFTableCell cell = table.getRow(rowIndex).getCell(col);  
	    XWPFParagraph p = getCellFirstParagraph(cell);  
	    List<XWPFRun> cellRunList = p.getRuns();  
	    if (cellRunList == null || cellRunList.size() == 0) {  
	      return;  
	    }  
	    for (int i = cellRunList.size() - 1; i >= 0; i--) {  
	      p.removeRun(i);  
	    }  
	  }  
	  
	  /** 
	   * @Description: 隐藏单元格内容 
	   */  
	  public void setHiddenCellContent(XWPFTable table, int rowIndex, int col) {  
	    XWPFTableCell cell = table.getRow(rowIndex).getCell(col);  
	    setCellHidden(cell, true);  
	  }  
	  
	  public CTPPr getParagraphCTPPr(XWPFParagraph p) {  
		    CTPPr pPPr = null;  
		    if (p.getCTP() != null) {  
		      if (p.getCTP().getPPr() != null) {  
		        pPPr = p.getCTP().getPPr();  
		      } else {  
		        pPPr = p.getCTP().addNewPPr();  
		      }  
		    }  
		    return pPPr;  
		  }  
	  /** 
	   * @Description: 得到XWPFRun的CTRPr 
	   */  
	  public CTRPr getRunCTRPr(XWPFParagraph p, XWPFRun pRun) {  
	    CTRPr pRpr = null;  
	    if (pRun.getCTR() != null) {  
	      pRpr = pRun.getCTR().getRPr();  
	      if (pRpr == null) {  
	        pRpr = pRun.getCTR().addNewRPr();  
	      }  
	    } else {  
	      pRpr = p.getCTP().addNewR().addNewRPr();  
	    }  
	    return pRpr;  
	  }  
	  
	  public void setCellHidden(XWPFTableCell cell, boolean isVanish) {  
	    XWPFParagraph p = getCellFirstParagraph(cell);  
	    CTPPr pPPr = getParagraphCTPPr(p);  
	    CTParaRPr paRpr = pPPr.isSetRPr() ? pPPr.getRPr() : pPPr.addNewRPr();  
	    CTOnOff vanishCtOnOff = paRpr.isSetVanish() ? paRpr.getVanish() : paRpr  
	        .addNewVanish();  
	    if (isVanish) {  
	      vanishCtOnOff.setVal(STOnOff.TRUE);  
	    } else {  
	      vanishCtOnOff.setVal(STOnOff.FALSE);  
	    }  
	    List<XWPFRun> cellRunList = p.getRuns();  
	    if (cellRunList == null || cellRunList.size() == 0) {  
	      return;  
	    }  
	    for (XWPFRun xwpfRun : cellRunList) {  
	      CTRPr pRpr = getRunCTRPr(p, xwpfRun);  
	      vanishCtOnOff = pRpr.isSetVanish() ? pRpr.getVanish() : pRpr  
	          .addNewVanish();  
	      if (isVanish) {  
	        vanishCtOnOff.setVal(STOnOff.TRUE);  
	      } else {  
	        vanishCtOnOff.setVal(STOnOff.FALSE);  
	      }  
	    }  
	  }  
	  
	  /** 
	   *  
	   * @Description: 得到Cell的CTTcPr,不存在则新建 
	   */  
	  public CTTcPr getCellCTTcPr(XWPFTableCell cell) {  
	    CTTc cttc = cell.getCTTc();  
	    CTTcPr tcPr = cttc.isSetTcPr() ? cttc.getTcPr() : cttc.addNewTcPr();  
	    return tcPr;  
	  }  
	  
	  /** 
	   * @Description: 设置垂直对齐方式 
	   */  
	  public void setCellVAlign(XWPFTableCell cell, STVerticalJc.Enum vAlign) {  
	    setCellWidthAndVAlign(cell, null, null, vAlign);  
	  }  
	  
	  /** 
	   * @Description: 设置列宽和垂直对齐方式 
	   */  
	  public void setCellWidthAndVAlign(XWPFTableCell cell, String width,  
	      STTblWidth.Enum typeEnum, STVerticalJc.Enum vAlign) {  
	    CTTcPr tcPr = getCellCTTcPr(cell);  
	    CTTblWidth tcw = tcPr.isSetTcW() ? tcPr.getTcW() : tcPr.addNewTcW();  
	    if (width != null) {  
	      tcw.setW(new BigInteger(width));  
	    }  
	    if (typeEnum != null) {  
	      tcw.setType(typeEnum);  
	    }  
	    if (vAlign != null) {  
	      CTVerticalJc vJc = tcPr.isSetVAlign() ? tcPr.getVAlign() : tcPr  
	          .addNewVAlign();  
	      vJc.setVal(vAlign);  
	    }  
	  }  
	  
	  /** 
	   * @Description: 得到单元格第一个Paragraph 
	   */  
	  public XWPFParagraph getCellFirstParagraph(XWPFTableCell cell) {  
	    XWPFParagraph p;  
	    if (cell.getParagraphs() != null && cell.getParagraphs().size() > 0) {  
	      p = cell.getParagraphs().get(cell.getParagraphs().size()-1);  
	    } else {  
	      p = cell.addParagraph();  
	    }  
	    return p;  
	  }  
	  
	  /** 
	   * @Description: 跨列合并 
	   */  
	  public void mergeCellsHorizontal(XWPFTable table, int row, int fromCell,  
	      int toCell) {  
	    for (int cellIndex = fromCell; cellIndex <= toCell; cellIndex++) {  
	      XWPFTableCell cell = table.getRow(row).getCell(cellIndex);  
	      if (cellIndex == fromCell) {  
	        // The first merged cell is set with RESTART merge value  
	        getCellCTTcPr(cell).addNewHMerge().setVal(STMerge.RESTART);  
	      } else {  
	        // Cells which join (merge) the first one,are set with CONTINUE  
	        getCellCTTcPr(cell).addNewHMerge().setVal(STMerge.CONTINUE);  
	      }  
	    }  
	  }  
	  
	  
	  /** 
	   * @Description: 跨行合并 
	   * @see http://stackoverflow.com/questions/24907541/row-span-with-xwpftable 
	   */  
	  public void mergeCellsVertically(XWPFTable table, int col, int fromRow,  
	      int toRow) {  
	    for (int rowIndex = fromRow; rowIndex <= toRow; rowIndex++) {  
	      XWPFTableCell cell = table.getRow(rowIndex).getCell(col);  
	      if (rowIndex == fromRow) {  
	        // The first merged cell is set with RESTART merge value  
	        getCellCTTcPr(cell).addNewVMerge().setVal(STMerge.RESTART);  
	      } else {  
	        // Cells which join (merge) the first one,are set with CONTINUE  
	        getCellCTTcPr(cell).addNewVMerge().setVal(STMerge.CONTINUE);  
	      }  
	    }  
	  }  
	  
	/**
	 * ----------------------------------------------------------
	 * @param filepath
	 */
	public void saveFile(String filepath) {
		// 在服务器端生成  
        FileOutputStream fos = null;
		try {
			File file = new File(filepath);
			if(file.exists())file.delete();
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				xdoc.write(fos);
				fos.close();  
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
	        
		}
        
	}

}
