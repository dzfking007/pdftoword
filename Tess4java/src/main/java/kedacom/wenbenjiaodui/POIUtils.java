package kedacom.wenbenjiaodui;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TextAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTcPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

public class POIUtils {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			ArrayList<ArrayList<String>> tableList = new ArrayList<ArrayList<String>>();
			
			for(int i = 0;i<5;i++) {
				ArrayList<String> rowList = new ArrayList<String>();
				for(int j=0;j<5;j++) {
					rowList.add("测试"+i);
				}
				tableList.add(rowList);
			}
			getWord("被告提交证据清单",tableList,"案件.doc");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public static void getWord(String title,ArrayList<ArrayList<String>> tableList,String outPath) throws Exception {  
        String[] deleteStr = null;  
        XWPFDocument xdoc = new XWPFDocument();  
        //标题  
        XWPFParagraph titleMes = xdoc.createParagraph();  
        titleMes.setAlignment(ParagraphAlignment.CENTER);  
        XWPFRun r1 = titleMes.createRun();  
        r1.setBold(true);  
        r1.setFontFamily("微软雅黑");  
        r1.setText(title);//活动名称  
        r1.setFontSize(20);  
        r1.setColor("000000");  
        r1.setBold(true);  
        
        //表格
        XWPFTable dTable = xdoc.createTable(tableList.size(), tableList.get(0).size());  
        createTable(dTable, xdoc,tableList);  
        setEmptyRow(xdoc,r1);  

        // 在服务器端生成  
        FileOutputStream fos = new FileOutputStream(outPath);  
        xdoc.write(fos);  
        fos.close();  
    }  
      
    public static void createTable(XWPFTable xTable,XWPFDocument xdoc,ArrayList<ArrayList<String>> tableList){  
        String bgColor="000000";  
        CTTbl ttbl = xTable.getCTTbl();  
        CTTblPr tblPr = ttbl.getTblPr() == null ? ttbl.addNewTblPr() : ttbl.getTblPr();  
        CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();  
        tblWidth.setW(new BigInteger("8600"));
        tblWidth.setType(STTblWidth.DXA);  
        for(int rowIdx = 0;rowIdx<tableList.size();rowIdx++) {
        	ArrayList<String> rowList = tableList.get(rowIdx);
        	for(int cellIdx = 0;cellIdx<rowList.size();cellIdx++) {
        		setCellText(xdoc, getCellHight(xTable, rowIdx, cellIdx), rowList.get(cellIdx),bgColor,1000);  
        	}
        }
    }  
      
    //设置表格高度  
    private static XWPFTableCell getCellHight(XWPFTable xTable,int rowNomber,int cellNumber){  
        XWPFTableRow row = null;  
        row = xTable.getRow(rowNomber);  
        row.setHeight(100);  
        XWPFTableCell cell = null;  
        cell = row.getCell(cellNumber);  
        return cell;  
    }  
      
    /** 
     *  
     * @param xDocument 
     * @param cell 
     * @param text 
     * @param bgcolor 
     * @param width 
     */  
    private static void setCellText(XWPFDocument xDocument, XWPFTableCell cell,  
            String text, String bgcolor, int width) {  
        CTTc cttc = cell.getCTTc();  
        CTTcPr cellPr = cttc.addNewTcPr();  
        cellPr.addNewTcW().setW(BigInteger.valueOf(width));  
        XWPFParagraph pIO =cell.addParagraph();  
        cell.removeParagraph(0);  
        XWPFRun rIO = pIO.createRun();  
        rIO.setFontFamily("微软雅黑");  
        rIO.setColor("000000");  
        rIO.setFontSize(12);  
        rIO.setText(text);  
    }  
      
    //设置表格间的空行  
    public static void setEmptyRow(XWPFDocument xdoc,XWPFRun r1){  
        XWPFParagraph p1 = xdoc.createParagraph();  
        p1.setAlignment(ParagraphAlignment.CENTER);  
        p1.setVerticalAlignment(TextAlignment.CENTER);  
        r1 = p1.createRun();  
    }  
      
    /** 
     * 创建计划明细表 
     * @param task 
     * @param xTable 
     * @param xdoc 
     * @throws IOException 
     */  
    public static void createSimpleTable(XWPFTable xTable, XWPFDocument xdoc)  
            throws IOException {  
        String bgColor="FFFFFF";  
        CTTbl ttbl = xTable.getCTTbl();  
        CTTblPr tblPr = ttbl.getTblPr() == null ? ttbl.addNewTblPr() : ttbl.getTblPr();  
        CTTblWidth tblWidth = tblPr.isSetTblW() ? tblPr.getTblW() : tblPr.addNewTblW();  
        tblWidth.setW(new BigInteger("8600"));  
        tblWidth.setType(STTblWidth.DXA);  
        for (int i = 0; i < 7; i++) {  
            setCellText(xdoc, getCellHight(xTable, i, 0), "阶段",bgColor,3300);  
            setCellText(xdoc,getCellHight(xTable, i, 1), "階段名稱",bgColor,3300);  
        }  
    }  
}
