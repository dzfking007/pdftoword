/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ld.pdftoword;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import org.apache.pdfbox.contentstream.PDFGraphicsStreamEngine;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.rendering.PageDrawer;
import org.apache.pdfbox.rendering.PageDrawerParameters;
import org.apache.pdfbox.util.Matrix;
import org.apache.pdfbox.util.Vector;

import com.ld.myutils.LoggerUtil;


/**
 * Example showing custom rendering by subclassing PageDrawer.
 * 
 * <p>If you want to do custom graphics processing rather than Graphics2D rendering, then you should
 * subclass {@link PDFGraphicsStreamEngine} instead. Subclassing PageDrawer is only suitable for
 * cases where the goal is to render onto a Graphics2D surface.
 *
 * @author John Hewson
 */
public class CustomPageDrawer{
	private static Logger logger = LoggerUtil.getJdkLog();
	private static double tableBorderWidth = 2.0;//表格宽度
	
	
	
    public CustomPageDrawer() {
		
	}


	public static void main(String[] args) throws IOException
    {
        File file = new File("D:\\pdf.pdf");
        
        PDDocument doc = PDDocument.load(file);
        double pageHeight = doc.getPage(0).getBBox().getHeight();
        logger.info(doc.getPage(0).getBBox().getHeight()+"");
        MyPDFRenderer renderer = new MyPDFRenderer(doc,pageHeight);
        BufferedImage image = renderer.renderImage(4);
        ImageIO.write(image, "PNG", new File("D:\\pdf.png"));
        doc.close();
        
        List<TableLine> lines = renderer.getLineList();
        
        //对y进行从小到大排序
        Collections.sort(lines,new Comparator<TableLine>() {
			@Override
			public int compare(TableLine o1, TableLine o2) {
				// TODO Auto-generated method stub
				if(o1.getY()>o2.getY()) {
					return 1;
				}else if(o1.getY() == o2.getY()) {
					return 0;
				}else {
					return -1;
				}
			}
		});
        
        ArrayList<ArrayList<TableLine>> rows = new ArrayList<ArrayList<TableLine>>();//行线条 ,每行都有多条线条
        TableLine preTableLine = lines.get(0);//前一条线条
        ArrayList<TableLine> rowLines = new ArrayList<TableLine>();
        rows.add(rowLines);
        rowLines.add(preTableLine);
        int rowNum = 1;//第一行
        for(int lineNum = 1;lineNum<lines.size();lineNum++){
        	TableLine tableLine = lines.get(lineNum);
        	if(Math.abs(tableLine.getY()-preTableLine.getY())<tableBorderWidth) {//当前行
        		rows.get(rowNum-1).add(tableLine);//当前行加入线条
        	}else {//
        		if(rows.size()<=rowNum) {//新建一行
        			rowNum++ ;
        			ArrayList<TableLine> newRowLines = new ArrayList<TableLine>();
        			rows.add(newRowLines);
        			rows.get(rowNum-1).add(tableLine);//当前行加入线条
        		}else {//否则行已经建立，直接加入
        			rows.get(rowNum-1).add(tableLine);//当前行加入线条
        		}
        	}
        	preTableLine = tableLine;
        }
        //分析每行 有 多少列
        ArrayList<ArrayList<ArrayList<TableLine>>> cols = new ArrayList<ArrayList<ArrayList<TableLine>>>(rows.size());//每行 所属列的线条
        logger.info("行数："+rows.size());
        for(int i = 0;i<rows.size();i++) {
        	List<TableLine> ls  = rows.get(i); 
        	//对行中间的线条根据X进行排序 从小到大
        	Collections.sort(ls, new Comparator<TableLine>() {

				@Override
				public int compare(TableLine o1, TableLine o2) {
					// TODO Auto-generated method stub
					if(o1.getX()>o2.getX()) {
						return 1;
					}else if(o1.getX() == o2.getX()) {
						return 0;
					}else {
						return -1;
					}
				}
			});
        	ArrayList<ArrayList<TableLine>> colsOfLines = new ArrayList<ArrayList<TableLine>>();//每行所属列的线条
        	ArrayList<TableLine> colLines = new ArrayList<TableLine>();
        	TableLine preLine = ls.get(0);
        	colLines.add(preLine);
        	colsOfLines.add(colLines);
        	
        	int colNum = 1;
        	for(int lineNum = 1;lineNum<ls.size();lineNum++) {
        		TableLine line = ls.get(lineNum);
        		if(Math.abs(line.getX()-preLine.getX())<tableBorderWidth) {
        			colsOfLines.get(colNum-1).add(line);
        		}else {
        			if(colsOfLines.size()<=colNum) {//新建一列
        				colNum++ ;
            			ArrayList<TableLine> newColsLines = new ArrayList<TableLine>();
            			colsOfLines.add(newColsLines);
            			colsOfLines.get(colNum-1).add(line);//当前列加入线条
            		}else {//否则列已经建立，直接加入
            			colsOfLines.get(colNum-1).add(line);//当前行加入线条
            		}
        		}
        		preLine = line;
        	}
        	cols.add(colsOfLines);
        }
        //解析表格行列数，最大
        int tableRowNum = cols.size()-1;//表格行数 
        int tableColNum = 0;//表格列数
        int baseRowNum = 0;//基准行号
        for(int irow=0;irow<cols.size();irow++) {
        	ArrayList<ArrayList<TableLine>> icols = cols.get(irow);
        	if(tableColNum <=icols.size()) {//取每行最大的列数
        		tableColNum = icols.size();
        		baseRowNum = irow;
        	}
    	}
        tableColNum -=1;
        logger.info("tableRowNum:" + tableRowNum + " tableColNum:"+tableColNum+" baseRowNum:"+baseRowNum);
        Table table = new  Table(tableRowNum, tableColNum);
        //分析表格是否有合并的单元格
        //根据标准行来分析其他行 
        //分析X line 列的基准表格
        List<TableLine> xLines = analysXLines(lines);
        table.setxBaseLine(xLines);
        for(int irow=0;irow<tableRowNum;irow++) {//屏蔽掉最后一行
        	ArrayList<ArrayList<TableLine>> icols = cols.get(irow);
        	int realCol = 0;
        	for(int jcol=0;jcol<tableColNum;jcol++) {
        		try {
        			ArrayList<TableLine> colLines = icols.get(realCol);
        			if(Math.abs((xLines.get(jcol)).getX()-colLines.get(0).getX())<tableBorderWidth) {//X轴匹配
        				realCol++;
        				TableCell tableCell = new TableCell();//单元格
                		double x=0,y=0,width = 0,height=0;//单元格宽高
//                		for(int cel=0;cel<colLines.size();cel++) {
                		if(colLines!=null && colLines.size()>0) {
                			/*if(colLines.size()==1) {//只有一条，要合并
                				//判断是横线 还是 竖线
                				if(colLines.get(0).getHeight()>tableBorderWidth) {//竖线
                					
                				}else {//横线
                					
                				}
                			}*/
                        	for(TableLine tableLine : colLines) {
                        		x += tableLine.getX();
                        		y += tableLine.getY();
                        		if(width<=tableLine.getWidth()) {
                        			width = tableLine.getWidth();
                        		}
                        		if(height <= tableLine.getHeight()) {
                        			height = tableLine.getHeight();
                        		}
                        	}
                        	x = x/colLines.size();
                        	y = y/colLines.size();
                        	if(width<tableBorderWidth)width=0;
                        	if(height<tableBorderWidth)height=0;
                        	tableCell.setX(x);
                        	tableCell.setY(y);
                        	tableCell.setHeight(height);
                        	tableCell.setWidth(width);
                        	table.getTableData().put(irow+","+jcol, tableCell);
                		}
        			}else {
        				// TODO: 插入空白单元格
    					TableCell tableCell = new TableCell();//单元格
    					table.getTableData().put(irow+","+jcol, tableCell);
        			}
            		
				} catch (IndexOutOfBoundsException e) {//超出界限 异常
					// TODO: 插入空白单元格
					TableCell tableCell = new TableCell();//单元格
					table.getTableData().put(irow+","+jcol, tableCell);
				}
        		
        	}
    	}
        table.printMatrix();
        table.printTableData();
        
        for(int row=0;row<table.getRownum();row++) {
        	for(int col=0;col<table.getColnum();col++) {
        		TableCell cell = table.getTableData().get(row+","+col);
        		if(cell.getHeight()==0 && cell.getWidth()==0) {//宽高都为0 则向左合并
        			table.getMatrix()[row][col] = table.getMatrix()[row][col-1];
        		}else if(cell.getHeight()==0) {//高为0 则向左合并
        			table.getMatrix()[row][col] = table.getMatrix()[row][col-1];
        		}else if(cell.getWidth()==0) {//宽为0  则向上合并
        			table.getMatrix()[row][col] = table.getMatrix()[row-1][col];
        		}
        	}
        }
        table.printMatrix();
        
       
        //分析Y line 行的基准表格
        List<TableLine> yLines = analysYLines(lines);
        table.setyBaseLine(yLines);
       /* logger.info("-----------------------------------------");
        for(TableLine tableLine : yLines) {
			logger.info("X:"+tableLine.getX()+" Y:"+tableLine.getY()+
             		" WIDTH:"+tableLine.getWidth()+" HEIGHT:"+tableLine.getHeight());
		}*/
        
        table.analsysMergeCell();
        table.printMergeCell();
        //分析表格数量，表格上下排列，根据最后行中的线条不包含向下的纵向线条来判断
       /* for(int irow=0;irow<cols.size();irow++) {
        	System.out.println("第"+(irow+1) +"行--------");
        	ArrayList<ArrayList<TableLine>> icols = cols.get(irow);
        	for(int jcol=0;jcol<icols.size();jcol++) {
        		System.out.println("第"+(jcol+1)+"列---");
        		for(TableLine tableLine : icols.get(jcol)) {
        			System.out.println("X:"+tableLine.getX()+" Y:"+tableLine.getY()+
                     		" WIDTH:"+tableLine.getWidth()+" HEIGHT:"+tableLine.getHeight());
        		}
        	}
    		
    	}*/
    }
    
    
    public List<Table> analysTable(String pdfFilepath,int pdfPageNum) throws InvalidPasswordException, IOException{
    	File file = new File(pdfFilepath);
        
        PDDocument doc = PDDocument.load(file);
        double pageHeight = doc.getPage(0).getBBox().getHeight();
        System.out.println(doc.getPage(0).getBBox().getHeight());
        MyPDFRenderer renderer = new MyPDFRenderer(doc,pageHeight);
        BufferedImage image = renderer.renderImage(pdfPageNum);
        /*String pngpath = pdfFilepath.split("\\.")[0]+".png";
		ImageIO.write(image, "PNG", new File(pngpath));*/
        doc.close();
        ArrayList<TableLine> lines = renderer.getLineList();
        List<Table> tables = analysTableNums(lines);
        System.out.println("表格数量"+tables.size()); 
        //
       
       
        
        
        return tables;
    }
    
    
    /**
     * 分析线条 有几个表格
     * @param lines
     * @return
     */
    public static List<Table> analysTableNums(ArrayList<TableLine> lines){
    	List<Table> tables = new ArrayList<Table>();
    	
    	//对线条进行X轴 横向 排序
    	//对y进行从小到大排序
        Collections.sort(lines,new Comparator<TableLine>() {
			@Override
			public int compare(TableLine o1, TableLine o2) {
				// TODO Auto-generated method stub
				if(o1.getY()>o2.getY()) {
					return 1;
				}else if(o1.getY() == o2.getY()) {
					return 0;
				}else {
					return -1;
				}
			}
		});
    	//分析X 横向线条是否有 间隙
        List<Integer> splitY = new ArrayList<Integer>();//横向分割 线条坐标
    	TableLine preYTableLine = null;
        for(int y=0;y<lines.size();y++) {
    		TableLine line = lines.get(y);
    		System.out.println("X:"+line.getX()+" Y:"+line.getY()+" WIDTH:"+line.getWidth()+" HEIGHT:"+line.getHeight());
    		if(line.getHeight()>tableBorderWidth) {//寻找有高度的
    			
    			if(preYTableLine == null) {//第一条比对线
    				preYTableLine = line;
    				//判断和第一条线是否在同一个表格
    				TableLine firstLine = lines.get(0);
    				if(Math.abs(firstLine.getY()-preYTableLine.getY())>tableBorderWidth) {//则数据不同表格
    					splitY.add(1);
    				}
    				continue;
    			}
    		}
    		if(preYTableLine!=null) {
				if(Math.abs(preYTableLine.getY() - line.getY())>tableBorderWidth) {//不是同一列
					if(line.getY()-preYTableLine.getHeight()-preYTableLine.getY()>tableBorderWidth) {//间隙
						boolean isExist = false;
						if(splitY.size()>0) {
							for(int ypoint : splitY) {
								TableLine yPointLine = lines.get(ypoint);
								if(Math.abs(line.getY() - yPointLine.getY())<tableBorderWidth) {
									isExist = true;
									break;
								}
							}
						}
						if(!isExist) {
							splitY.add(y);
						}
    				}
					if(line.getHeight()>tableBorderWidth) {
						preYTableLine = line;
					}
				}else {//一列的线条挑选 宽度最小的线条
					if(line.getHeight()>tableBorderWidth) {
						if(preYTableLine.getHeight()>line.getHeight()) {
							preYTableLine = line;
						}
					}
					
				} 
			}
    	}
    	//根据X轴分析 将线条分成几块
        ArrayList<ArrayList<TableLine>> yTableLines = new ArrayList<ArrayList<TableLine>>();
        if(splitY.size()<=0) {
        	yTableLines.add(lines);
        }else {
        	int splityStart = 0,splityEnd = 0;
            for(int splity = 0;splity<splitY.size()+1;splity++) {
            	if(splity==splitY.size()) {//最后一个
            		splityEnd = lines.size();
            	}else {
            		splityEnd = splitY.get(splity);
            	}
            	ArrayList<TableLine> subList = new ArrayList<TableLine>(lines.subList(splityStart, splityEnd));
            	yTableLines.add(subList);
            	splityStart = splityEnd;
            }
        }
        
        System.out.println("[Y分割]"+splitY);
       /* for(ArrayList<TableLine>  yTableLine : yTableLines) {
        	System.out.println("------------");
        	for(TableLine tableLine : yTableLine) {
        		System.out.println("X:"+tableLine.getX()+" Y:"+tableLine.getY()+" WIDTH:"+tableLine.getWidth()+" HEIGHT:"+tableLine.getHeight());
        	}
        }*/
        
        
        ArrayList<ArrayList<TableLine>> tableLines = new ArrayList<ArrayList<TableLine>>();//所有表格的线条
        //根据纵向线条分析 横向线条
        for(ArrayList<TableLine>  yTableLine : yTableLines) {
        	if(yTableLine.size()<=0)continue;
        	
        	//对线条进行X轴 横向 排序
        	//对x进行从小到大排序
            Collections.sort(yTableLine,new Comparator<TableLine>() {
    			@Override
    			public int compare(TableLine o1, TableLine o2) {
    				// TODO Auto-generated method stub
    				if(o1.getX()>o2.getX()) {
    					return 1;
    				}else if(o1.getX() == o2.getX()) {
    					return 0;
    				}else {
    					return -1;
    				}
    			}
    		});
        	//分析X 横向线条是否有 间隙
            List<Integer> splitX = new ArrayList<Integer>();//横向分割 线条坐标
        	TableLine preXTableLine = null;
            for(int x=0;x<yTableLine.size();x++) {
        		TableLine line = yTableLine.get(x);
//        		System.out.println("X:"+line.getX()+" Y:"+line.getY()+" WIDTH:"+line.getWidth()+" HEIGHT:"+line.getHeight());
        		if(line.getWidth()>tableBorderWidth) {//寻找有宽度的
        			
        			if(preXTableLine == null) {
        				preXTableLine = line;
        				continue;
        			}
        		}
        		if(preXTableLine!=null) {
    				if(Math.abs(preXTableLine.getX() - line.getX())>tableBorderWidth) {//不是同一列
    					if(line.getX()-preXTableLine.getWidth()-preXTableLine.getX()>tableBorderWidth) {//间隙
    						boolean isExist = false;
    						if(splitX.size()>0) {
    							for(int xpoint : splitX) {
    								TableLine xPointLine = yTableLine.get(xpoint);
    								if(Math.abs(line.getX() - xPointLine.getX())<tableBorderWidth) {
    									isExist = true;
    									break;
    								}
    							}
    						}
    						if(!isExist) {
    							splitX.add(x);
    						}
        					
        				}
    					if(line.getWidth()>tableBorderWidth) {
    						preXTableLine = line;
    					}
    					
    				}else {//一列的线条挑选 宽度最小的线条
    					if(line.getWidth()>tableBorderWidth) {
    						if(preXTableLine.getWidth()>line.getWidth()) {
    							preXTableLine = line;
    						}
    					}
    					
    				} 
    			}
        	}
        	//根据X轴分析 将线条分成几块
            ArrayList<ArrayList<TableLine>> xTableLines = new ArrayList<ArrayList<TableLine>>();
            if(splitX.size()<=0) {
            	xTableLines.add(yTableLine);
            	tableLines.add(yTableLine);
            }else{
            	int splitxStart = 0,splitxEnd = 0;
                for(int splitx = 0;splitx<splitX.size()+1;splitx++) {
                	if(splitx==splitX.size()) {//最后一个
                		splitxEnd = yTableLine.size();
                	}else {
                		splitxEnd = splitX.get(splitx);
                	}
                	ArrayList<TableLine> subList = new ArrayList<TableLine>(yTableLine.subList(splitxStart, splitxEnd));
                	xTableLines.add(subList);
                	tableLines.add(subList);
                	splitxStart = splitxEnd;
                }
            }
            
           System.out.println("[X分割]"+splitX);
        }
       

        /*for(ArrayList<TableLine>  tableLine : tableLines) {
        	System.out.println("------------");
        	for(TableLine line : tableLine) {
        		System.out.println("X:"+line.getX()+" Y:"+line.getY()+" WIDTH:"+line.getWidth()+" HEIGHT:"+line.getHeight());
        	}
        }*/
       
       for(ArrayList<TableLine>  tableLine : tableLines) {
    	   try {
			Table table  = analysTableLines(tableLine);
			if(table!=null) {
				tables.add(table);
			}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
       }
        
    	
       return tables;
    }
    
    /**
     * 分析单个表格线条
     * @param lines
     * @return
     * @throws InvalidPasswordException
     * @throws IOException
     */
    public static Table analysTableLines(List<TableLine> lines) throws InvalidPasswordException, IOException {
    	
        
        if(lines==null || lines.size()<=0) {
        	return null;
        }else {
        	int xLineNum =0,yLineNum=0;
        	for(TableLine line : lines) {
        		if(line.getWidth()>tableBorderWidth) {
        			xLineNum ++ ;
        		}
        		if(line.getHeight()>tableBorderWidth) {
        			yLineNum ++ ;
        		}
        	}
        	if(xLineNum==0 || yLineNum==0)return null;
        }
        
        //对y进行从小到大排序
        Collections.sort(lines,new Comparator<TableLine>() {
			@Override
			public int compare(TableLine o1, TableLine o2) {
				// TODO Auto-generated method stub
				if(o1.getY()>o2.getY()) {
					return 1;
				}else if(o1.getY() == o2.getY()) {
					return 0;
				}else {
					return -1;
				}
			}
		});
        
        ArrayList<ArrayList<TableLine>> rows = new ArrayList<ArrayList<TableLine>>();//行线条 ,每行都有多条线条
        TableLine preTableLine = lines.get(0);//前一条线条
        ArrayList<TableLine> rowLines = new ArrayList<TableLine>();
        rows.add(rowLines);
        rowLines.add(preTableLine);
        int rowNum = 1;//第一行
        for(int lineNum = 1;lineNum<lines.size();lineNum++){
        	TableLine tableLine = lines.get(lineNum);
        	if(Math.abs(tableLine.getY()-preTableLine.getY())<tableBorderWidth) {//当前行
        		rows.get(rowNum-1).add(tableLine);//当前行加入线条
        	}else {//
        		if(rows.size()<=rowNum) {//新建一行
        			rowNum++ ;
        			ArrayList<TableLine> newRowLines = new ArrayList<TableLine>();
        			rows.add(newRowLines);
        			rows.get(rowNum-1).add(tableLine);//当前行加入线条
        		}else {//否则行已经建立，直接加入
        			rows.get(rowNum-1).add(tableLine);//当前行加入线条
        		}
        	}
        	preTableLine = tableLine;
        }
        //分析每行 有 多少列
        ArrayList<ArrayList<ArrayList<TableLine>>> cols = new ArrayList<ArrayList<ArrayList<TableLine>>>(rows.size());//每行 所属列的线条
        System.out.println("行数："+rows.size());
        for(int i = 0;i<rows.size();i++) {
        	List<TableLine> ls  = rows.get(i); 
        	//对行中间的线条根据X进行排序 从小到大
        	Collections.sort(ls, new Comparator<TableLine>() {

				@Override
				public int compare(TableLine o1, TableLine o2) {
					// TODO Auto-generated method stub
					if(o1.getX()>o2.getX()) {
						return 1;
					}else if(o1.getX() == o2.getX()) {
						return 0;
					}else {
						return -1;
					}
				}
			});
        	ArrayList<ArrayList<TableLine>> colsOfLines = new ArrayList<ArrayList<TableLine>>();//每行所属列的线条
        	ArrayList<TableLine> colLines = new ArrayList<TableLine>();
        	TableLine preLine = ls.get(0);
        	colLines.add(preLine);
        	colsOfLines.add(colLines);
        	
        	int colNum = 1;
        	for(int lineNum = 1;lineNum<ls.size();lineNum++) {
        		TableLine line = ls.get(lineNum);
        		if(Math.abs(line.getX()-preLine.getX())<tableBorderWidth) {
        			colsOfLines.get(colNum-1).add(line);
        		}else {
        			if(colsOfLines.size()<=colNum) {//新建一列
        				colNum++ ;
            			ArrayList<TableLine> newColsLines = new ArrayList<TableLine>();
            			colsOfLines.add(newColsLines);
            			colsOfLines.get(colNum-1).add(line);//当前列加入线条
            		}else {//否则列已经建立，直接加入
            			colsOfLines.get(colNum-1).add(line);//当前行加入线条
            		}
        		}
        		preLine = line;
        	}
        	cols.add(colsOfLines);
        }
        //解析表格行列数，最大
        int tableRowNum = cols.size()-1;//表格行数 
        int tableColNum = 0;//表格列数
        int baseRowNum = 0;//基准行号
        for(int irow=0;irow<cols.size();irow++) {
        	ArrayList<ArrayList<TableLine>> icols = cols.get(irow);
        	if(tableColNum <=icols.size()) {//取每行最大的列数
        		tableColNum = icols.size();
        		baseRowNum = irow;
        	}
    	}
        tableColNum -=1;
        System.out.println("tableRowNum:" + tableRowNum + " tableColNum:"+tableColNum+" baseRowNum:"+baseRowNum);
        Table table = new  Table(tableRowNum, tableColNum);
        //分析表格是否有合并的单元格
        //根据标准行来分析其他行 
        //分析X line 列的基准表格
        List<TableLine> xLines = analysXLines(lines);
        table.setxBaseLine(xLines);
        for(int irow=0;irow<tableRowNum;irow++) {//屏蔽掉最后一行
        	ArrayList<ArrayList<TableLine>> icols = cols.get(irow);
        	int realCol = 0;
        	for(int jcol=0;jcol<tableColNum;jcol++) {
        		try {
        			ArrayList<TableLine> colLines = icols.get(realCol);
        			if(Math.abs((xLines.get(jcol)).getX()-colLines.get(0).getX())<tableBorderWidth) {//X轴匹配
        				realCol++;
        				TableCell tableCell = new TableCell();//单元格
                		double x=0,y=0,width = 0,height=0;//单元格宽高
//                		for(int cel=0;cel<colLines.size();cel++) {
                		if(colLines!=null && colLines.size()>0) {
                			/*if(colLines.size()==1) {//只有一条，要合并
                				//判断是横线 还是 竖线
                				if(colLines.get(0).getHeight()>tableBorderWidth) {//竖线
                					
                				}else {//横线
                					
                					
                				}
                			}*/
                        	for(TableLine tableLine : colLines) {
                        		x += tableLine.getX();
                        		y += tableLine.getY();
                        		if(width<=tableLine.getWidth()) {
                        			width = tableLine.getWidth();
                        		}
                        		if(height <= tableLine.getHeight()) {
                        			height = tableLine.getHeight();
                        		}
                        	}
                        	x = x/colLines.size();
                        	y = y/colLines.size();
                        	if(width<tableBorderWidth)width=0;
                        	if(height<tableBorderWidth)height=0;
                        	tableCell.setX(x);
                        	tableCell.setY(y);
                        	tableCell.setHeight(height);
                        	tableCell.setWidth(width);
                        	table.getTableData().put(irow+","+jcol, tableCell);
                		}
        			}else {
        				// TODO: 插入空白单元格
    					TableCell tableCell = new TableCell();//单元格
    					table.getTableData().put(irow+","+jcol, tableCell);
        			}
            		
				} catch (IndexOutOfBoundsException e) {//超出界限 异常
					// TODO: 插入空白单元格
					TableCell tableCell = new TableCell();//单元格
					table.getTableData().put(irow+","+jcol, tableCell);
				}
        		
        	}
    	}
        for(int row=0;row<table.getRownum();row++) {
        	for(int col=0;col<table.getColnum();col++) {
        		TableCell cell = table.getTableData().get(row+","+col);
        		if(cell.getHeight()==0 && cell.getWidth()==0) {//宽高都为0 则向左合并
        			table.getMatrix()[row][col] = table.getMatrix()[row][col-1];
        		}else if(cell.getHeight()==0) {//高为0 则向左合并
        			table.getMatrix()[row][col] = table.getMatrix()[row][col-1];
        		}else if(cell.getWidth()==0) {//宽为0  则向上合并
        			table.getMatrix()[row][col] = table.getMatrix()[row-1][col];
        		}
        	}
        }

        //分析Y line 行的基准表格
        List<TableLine> yLines = analysYLines(lines);
        table.setyBaseLine(yLines);
       /* System.out.println("-----------------------------------------");
        for(TableLine tableLine : yLines) {
			System.out.println("X:"+tableLine.getX()+" Y:"+tableLine.getY()+
             		" WIDTH:"+tableLine.getWidth()+" HEIGHT:"+tableLine.getHeight());
		}*/
        
        table.analsysMergeCell();
        //分析表格数量，表格上下排列，根据最后行中的线条不包含向下的纵向线条来判断
       /* for(int irow=0;irow<cols.size();irow++) {
        	System.out.println("第"+(irow+1) +"行--------");
        	ArrayList<ArrayList<TableLine>> icols = cols.get(irow);
        	for(int jcol=0;jcol<icols.size();jcol++) {
        		System.out.println("第"+(jcol+1)+"列---");
        		for(TableLine tableLine : icols.get(jcol)) {
        			System.out.println("X:"+tableLine.getX()+" Y:"+tableLine.getY()+
                     		" WIDTH:"+tableLine.getWidth()+" HEIGHT:"+tableLine.getHeight());
        		}
        	}
    		
    	}*/
        return table;
    }

    
    /**
     * 分析 横向 线条 获取横向的线条
     * @param lines
     */
    private static  List<TableLine> analysXLines(List<TableLine> lines) {
    	List<TableLine> retXLines = new ArrayList<TableLine>();
    	List<TableLine> xLines = new ArrayList<TableLine>();
    	for(TableLine tableLine : lines) {
    		//过滤掉纵向线条
    		if(tableLine.getHeight()<tableBorderWidth) {
    			xLines.add(tableLine);
    		}
    	}
    	
    	//线条排序 x坐标排序
		Collections.sort(xLines, new Comparator<TableLine>() {

			@Override
			public int compare(TableLine o1, TableLine o2) {
				// TODO Auto-generated method stub
				if(o1.getX()>o2.getX()) {
					return 1;
				}else if(o1.getX() == o2.getX()) {
					return 0;
				}else {
					return -1;
				}
			}
		});
		//过滤掉重复的线条
		TableLine preLine = xLines.get(0);
//		retXLines.add(preLine);
		for(int i=0;i<xLines.size();i++) {
			TableLine line = xLines.get(i);
//			System.out.println("X:"+line.getX()+" Y:"+line.getY()+" WIDTH:"+line.getWidth()+" HEIGHT:"+line.getHeight());
			if(Math.abs(preLine.getX() - line.getX())>tableBorderWidth) {
				retXLines.add(preLine);
				preLine = line;
			}else {//一列的线条挑选 宽度最小的线条
				if(preLine.getWidth()>line.getWidth()) {
					preLine = line;
				}
			} 
		}
		retXLines.add(preLine);
    	
    	return retXLines;
    }
    
    /**
     * 分析 纵向 线条 获取纵向的线条
     * @param lines
     */
    private static  List<TableLine> analysYLines(List<TableLine> lines) {
    	List<TableLine> retYLines = new ArrayList<TableLine>();
    	List<TableLine> yLines = new ArrayList<TableLine>();
    	for(TableLine tableLine : lines) {
    		//过滤掉纵向线条
    		if(tableLine.getWidth()<tableBorderWidth) {
    			yLines.add(tableLine);
    		}
    	}
    	
    	//线条排序 x坐标排序
		Collections.sort(yLines, new Comparator<TableLine>() {

			@Override
			public int compare(TableLine o1, TableLine o2) {
				// TODO Auto-generated method stub
				if(o1.getY()>o2.getY()) {
					return 1;
				}else if(o1.getY() == o2.getY()) {
					return 0;
				}else {
					return -1;
				}
			}
		});
		//过滤掉重复的线条
		TableLine preLine = yLines.get(0);
//		retYLines.add(preLine);
		for(int i=0;i<yLines.size();i++) {
			TableLine line = yLines.get(i);
//			System.out.println("X:"+line.getX()+" Y:"+line.getY()+" WIDTH:"+line.getWidth()+" HEIGHT:"+line.getHeight());
			if(Math.abs(preLine.getY() - line.getY())>tableBorderWidth) {
				retYLines.add(preLine);
				preLine = line;
			} else {//一列的线条挑选 宽度最小的线条
				if(preLine.getHeight()>line.getHeight()) {
					preLine = line;
				}
			} 
		}
		retYLines.add(preLine);
    	return retYLines;
    }
    /**
     * Example PDFRenderer subclass, uses MyPageDrawer for custom rendering.
     */
    private static class MyPDFRenderer extends PDFRenderer
    {
    	private ArrayList<TableLine> lineList = new ArrayList<TableLine>();
    	private double pageHeight;
        MyPDFRenderer(PDDocument document,double pageHeight)
        {
            super(document);
            this.pageHeight = pageHeight;
        }

        @Override
        protected PageDrawer createPageDrawer(PageDrawerParameters parameters) throws IOException
        {
            return new MyPageDrawer(parameters,lineList,pageHeight);
        }

		public ArrayList<TableLine> getLineList() {
			return lineList;
		}

		public void setLineList(ArrayList<TableLine> lineList) {
			this.lineList = lineList;
		}
        
        
    }

    /**
     * Example PageDrawer subclass with custom rendering.
     */
    private static class MyPageDrawer extends PageDrawer
    {
    	private List<TableLine> lineList = null;
    	private double pageHeight;
        MyPageDrawer(PageDrawerParameters parameters,List<TableLine> lineList,double pageHeight) throws IOException
        {
            super(parameters);
            this.lineList = lineList;
            this.pageHeight = pageHeight;
        }

        /**
         * Color replacement.
         */
        @Override
        protected Paint getPaint(PDColor color) throws IOException
        {
            // if this is the non-stroking color
            if (getGraphicsState().getNonStrokingColor() == color)
            {
                // find red, ignoring alpha channel
                if (color.toRGB() == (Color.RED.getRGB() & 0x00FFFFFF))
                {
                    // replace it with blue
                    return Color.BLUE;
                }
            }
            return super.getPaint(color);
        }

        /**
         * Glyph bounding boxes.
         */
        
        @Override
        protected void showGlyph(Matrix textRenderingMatrix, PDFont font, int code, String unicode,
                                 Vector displacement) throws IOException
        {
            // draw glyph
            super.showGlyph(textRenderingMatrix, font, code, unicode, displacement);
            
            // bbox in EM -> user units
            Shape bbox = new Rectangle2D.Float(0, 0, font.getWidth(code) / 1000, 1);
            AffineTransform at = textRenderingMatrix.createAffineTransform();
            bbox = at.createTransformedShape(bbox);
            
            // save
            Graphics2D graphics = getGraphics();
            Color color = graphics.getColor();
            Stroke stroke = graphics.getStroke();
            Shape clip = graphics.getClip();

            // draw
            graphics.setClip(graphics.getDeviceConfiguration().getBounds());
            graphics.setColor(Color.RED);
            graphics.setStroke(new BasicStroke(.5f));
            graphics.draw(bbox);
            
            // restore
            graphics.setStroke(stroke);
            graphics.setColor(color);
            graphics.setClip(clip);
            
            
        }

        /**
         * Filled path bounding boxes.
         */
        int num = 1;
        @Override
        public void fillPath(int windingRule) throws IOException
        {
        	
            // bbox in user units
            Shape bbox = getLinePath().getBounds2D();
           
            // draw path (note that getLinePath() is now reset)
            super.fillPath(windingRule);
            //对线条进行过滤
            System.out.println("X:"+bbox.getBounds2D().getX()+" Y:"+(pageHeight-bbox.getBounds2D().getY()-bbox.getBounds2D().getHeight())+
            		" MinX:"+bbox.getBounds2D().getMinX()+" MaxX:"+bbox.getBounds2D().getMaxX()+
            		" MinY:"+(pageHeight-bbox.getBounds2D().getMinY())+" MaxY:"+(840.96-bbox.getBounds2D().getMaxY())+
            		" WIDTH:"+bbox.getBounds2D().getWidth()+" HEIGHT:"+bbox.getBounds2D().getHeight());
            if((bbox.getBounds2D().getWidth()>tableBorderWidth&& bbox.getBounds2D().getHeight()<tableBorderWidth) || (bbox.getBounds2D().getWidth()<tableBorderWidth&& bbox.getBounds2D().getHeight()>tableBorderWidth)) {
            	// save
                Graphics2D graphics = getGraphics();
                Color color = graphics.getColor();
                Stroke stroke = graphics.getStroke();
                Shape clip = graphics.getClip();
                
                // draw
                graphics.setClip(graphics.getDeviceConfiguration().getBounds());
                if(num==1) {
                	graphics.setColor(Color.GREEN);
                }else {
                	graphics.setColor(Color.YELLOW);
                }
                num++;
                graphics.setStroke(new BasicStroke(.5f));
                graphics.draw(bbox);
                
                // restore
                graphics.setStroke(stroke);
                graphics.setColor(color);
                graphics.setClip(clip);
                
                
                TableLine tableLine = new TableLine();
                tableLine.setX(bbox.getBounds2D().getX());
                tableLine.setY(pageHeight-bbox.getBounds2D().getY()-bbox.getBounds2D().getHeight());//转换为 top left 坐标系
                tableLine.setWidth(bbox.getBounds2D().getWidth());
                tableLine.setHeight(bbox.getBounds2D().getHeight());
                lineList.add(tableLine);
                
                /*System.out.println("X:"+bbox.getBounds2D().getX()+" Y:"+(840.96-bbox.getBounds2D().getY()-bbox.getBounds2D().getHeight())+
                		" MinX:"+bbox.getBounds2D().getMinX()+" MaxX:"+bbox.getBounds2D().getMaxX()+
                		" MinY:"+(840.96-bbox.getBounds2D().getMinY())+" MaxY:"+(840.96-bbox.getBounds2D().getMaxY())+
                		" WIDTH:"+bbox.getBounds2D().getWidth()+" HEIGHT:"+bbox.getBounds2D().getHeight());*/
                
            }
            
            
            
        }

        /**
         * Custom annotation rendering.
         */
        @Override
        public void showAnnotation(PDAnnotation annotation) throws IOException
        {
            // save
            saveGraphicsState();
            
            // 35% alpha
            getGraphicsState().setNonStrokeAlphaConstant(0.35);
            super.showAnnotation(annotation);
            
            // restore
            restoreGraphicsState();
        }

		
        
        
        
    }
}
