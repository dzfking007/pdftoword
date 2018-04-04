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

import java.awt.geom.Point2D.Float;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.sound.midi.Synthesizer;

import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.DrawObject;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.contentstream.operator.state.Concatenate;
import org.apache.pdfbox.contentstream.operator.state.Restore;
import org.apache.pdfbox.contentstream.operator.state.Save;
import org.apache.pdfbox.contentstream.operator.state.SetGraphicsStateParameters;
import org.apache.pdfbox.contentstream.operator.state.SetMatrix;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.util.Matrix;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHeightRule;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STVerticalJc;

import com.ld.myutils.LoggerUtil;
import com.ld.myutils.poi.CreateWord;

/**
 * This is an example on how to get the x/y coordinates of image locations.
 *
 * @author Ben Litchfield
 */
public class PrintImageLocations extends PDFStreamEngine {
	private static Logger logger = LoggerUtil.getJdkLog();
	private static List<PdfLine> pdfContentList;
	private static CreateWord createWord = null;
	private static float pageWidth = 0.0f;
	private static float pageHeight = 0.0f;
	private String filebasepath;// 基础路径
	private Callback callback;//回调函数

	/**
	 * Default constructor.
	 *
	 * @throws IOException
	 *             If there is an error loading text stripper properties.
	 */
	public PrintImageLocations(List<PdfLine> pdfContentList, CreateWord createWord, String filebasepath,Callback callback) throws IOException {
		addOperator(new Concatenate());
		addOperator(new DrawObject());
		addOperator(new SetGraphicsStateParameters());
		addOperator(new Save());
		addOperator(new Restore());
		addOperator(new SetMatrix());
		this.pdfContentList = pdfContentList;
		this.createWord = createWord;
		this.filebasepath = filebasepath;
		this.callback = callback;
	}
	
	public PrintImageLocations(Callback callback) throws IOException {
		this.callback = callback;
	}

	/**
	 * This will print the documents data.
	 *
	 * @param args
	 *            The command line arguments.
	 *
	 * @throws IOException
	 *             If there is an error parsing the document.
	 */
	public static void main(String[] args) throws IOException {
		logger.info(System.getProperty("user.dir"));

		String path = System.getProperty("user.dir") + File.separator + "pdffile";

		PDDocument document = null;
		// File pdffile = new
		// File(path,"17-0591-100354定制报告（KDM2410M-V21标配1T硬盘换成2T硬盘）.pdf");
		File pdffile = new File("D:\\", "17-0591-100354定制报告（KDM2410M-V21标配1T硬盘换成2T硬盘）.pdf");
		try {
			document = PDDocument.load(pdffile);
			pageWidth = document.getPage(0).getBBox().getWidth();
			pageHeight = document.getPage(0).getBBox().getHeight();
			createWord = new CreateWord(pageWidth, pageHeight);
			List<XWPFTable> xwpfTables = null;
			// for( int pageNum =16;pageNum<17;pageNum++ )
			for (int pageNum = 0; pageNum < document.getPages().getCount(); pageNum++) {
				logger.info("第" + pageNum + "页--------------------------");
				List<PdfLine> pdfContentList = new ArrayList<PdfLine>();
				// 分析表格
				CustomPageDrawer customPageDrawer = new CustomPageDrawer();
				List<Table> tables = customPageDrawer.analysTable(pdffile.getPath(), pageNum);

				if (tables != null && tables.size() > 0) {
					for (Table table : tables) {
						PdfElem tablePdfElem = new PdfElem();
						tablePdfElem.setX((float) table.getxBaseLine().get(0).getX());
						tablePdfElem.setY((float) table.getyBaseLine().get(0).getY());
						tablePdfElem.setType(PdfElem.TYPE_TABLE);
						tablePdfElem.setTable(table);
						PdfLine tableLine = new PdfLine();
						tableLine.setX(tablePdfElem.getX());
						tableLine.setY(tablePdfElem.getY());
						tableLine.setType(PdfElem.TYPE_TABLE);
						tableLine.getElemList().add(tablePdfElem);
						pdfContentList.add(tableLine);
					}

					// table.printMatrix();
					// table.printMergeCell();

					/*
					 * CreateWord1 createWord1 = new CreateWord1(pageWidth, pageHeight); try {
					 * createWord1.createTable1(table); } catch (Exception e) { // TODO
					 * Auto-generated catch block e.printStackTrace(); }
					 */
				}
				//
				PDPage page = document.getPages().get(pageNum);

				logger.info(String.valueOf(pageHeight));
				logger.info("Processing page: " + pageNum);
				PrintImageLocations printeImageLocations = new PrintImageLocations(pdfContentList, createWord, pdffile.getParent(),new Callback() {
					
					@Override
					public void convertCallBack(int curNum, int totalNum) {
						// TODO Auto-generated method stub
						
					}
				});
				printeImageLocations.processPage(page);
				PDFTextStripper pdfTextStripper = new PrintTextLocations(pdfContentList);
				// stripper.setSortByPosition( true );
				pdfTextStripper.setStartPage(pageNum + 1);
				pdfTextStripper.setEndPage(pageNum + 1);
				String content = pdfTextStripper.getText(document);
				logger.info(content);
				// 文件读取完毕
				// 根据坐标y进行排序
				PdfLineComparator pdfLineComparator = new PdfLineComparator();
				Collections.sort(pdfContentList, pdfLineComparator);
				// 生成word文件
				boolean isLineStart = false;// 是否行开始
				for (int line = 0; line < pdfContentList.size(); line++) {
					isLineStart = true;
					PdfLine pdfLine = pdfContentList.get(line);
					logger.info("text size:" + pdfLine.getElemList().size());
					if (pdfLine.getType() != PdfElem.TYPE_TABLE) {
						// 设置行左侧缩进
						boolean isInTable = printeImageLocations.detectTextLineIsInTable(tables, pdfLine);
						if (!isInTable) {// 不在表格中 则插入左侧缩进
							createWord.insertZcsj(pdfLine.getX());
						}

						if (line == 0) {// 第一行要设置段前缩进
							logger.info("pdfLine.getY()=" + pdfLine.getY());
							createWord.insertDqsj(pdfLine.getY());
						}
					}

					//
					// for(PdfElem pdfElem : pdfLine.getElemList()) {//遍历元素
					for (int lineElemIndex = 0; lineElemIndex < pdfLine.getElemList().size(); lineElemIndex++) {// 遍历元素
						PdfElem pdfElem = pdfLine.getElemList().get(lineElemIndex);
						if (pdfLine.getType() == PdfElem.TYPE_IMAGE) {// 图片
							// 插入图片
							createWord.insertImage(pdfElem.getImagePath(), pdfElem.getImageName(), pdfElem.getImageType(), pdfElem.getImageWidth(), pdfElem.getImageHeight());
						} else if (pdfLine.getType() == PdfElem.TYPE_TEXT) {// 文字

							// 插入文字
							// logger.info(pdfElem.getTextContent());
							// 判断是否有表格 在表格中插入文字
							if (tables != null && tables.size() > 0) {
								// 判断文字集中那个单元格
								boolean isintable = printeImageLocations.setTableCellContent(tables, pdfElem, isLineStart);
								if (!isintable) {
									createWord.insertWords(pdfElem.getTextContent(), pdfElem.getFontsize(), pdfElem.isFontBold(), pdfElem.getFontname());
								}
							} else {
								createWord.insertWords(pdfElem.getTextContent(), pdfElem.getFontsize(), pdfElem.isFontBold(), pdfElem.getFontname());
							}

						} else if (pdfElem.getType() == pdfElem.TYPE_TABLE) {
							// 创建表格
							printeImageLocations.createTable(pdfElem.getTable());
							// 检测该表右侧是不是还有表格 如果有就创建
							logger.info("表格数：" + tables.size());
							for (Table table : tables) {
								if (table.getxBaseLine().get(0).getX() != pdfElem.getTable().getxBaseLine().get(0).getX()
										&& table.getyBaseLine().get(0).getY() != pdfElem.getTable().getyBaseLine().get(0).getY()) {// 不是自己
									double table1Y1 = pdfElem.getTable().getyBaseLine().get(0).getY();
									double table1Y2 = pdfElem.getTable().getyBaseLine().get(pdfElem.getTable().getyBaseLine().size() - 1).getY()
											+ pdfElem.getTable().getyBaseLine().get(pdfElem.getTable().getyBaseLine().size() - 1).getHeight();
									double table2Y1 = table.getyBaseLine().get(0).getY();
									double table2Y2 = table.getyBaseLine().get(table.getyBaseLine().size() - 1).getY()
											+ table.getyBaseLine().get(table.getyBaseLine().size() - 1).getHeight();
									if (Math.abs(table2Y1 - table1Y1) < (table1Y2 - table1Y1)) {// 在右侧
										if (table.getXwpfTable() == null) {
											printeImageLocations.createTable(table);
										}
									}
								}
							}

						}
						if (isLineStart)
							isLineStart = false;
					}

				}
				// 合并单元格
				printeImageLocations.mergeTable(tables);
				// 插入空白页
				createWord.addNewPage();
			}
		} finally {
			if (document != null) {
				document.close();
			}
		}

		logger.info(pdffile.getPath());
		String docpath = pdffile.getPath().split("\\.")[0] + ".doc";
		logger.info(docpath);
		createWord.saveFile(docpath);

	}

	/**
	 * 获取pdf总共的页数
	 * @param pdffilepath 文件件路径
	 * @return
	 */
	public int getPdfTotalPage(String pdffilepath) {
		int totalPage = 0;
		File pdffile = new File(pdffilepath);
		PDDocument document = null;
		try {
			document = PDDocument.load(pdffile);
			totalPage = document.getPages().getCount();
		} catch (InvalidPasswordException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			if(document!=null) {
				try {
					document.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return totalPage;
		
	}
	/**
	 * 转换文件路径
	 * 
	 * @param filepath
	 * @return
	 * @throws IOException
	 */
	public boolean convert(int startPage,int endPage,String pdffilepath, String wordfilepath) {
		boolean ret = false;
		PDDocument document = null;
		File pdffile = new File(pdffilepath);
		try {
			document = PDDocument.load(pdffile);
			pageWidth = document.getPage(0).getBBox().getWidth();
			pageHeight = document.getPage(0).getBBox().getHeight();
			createWord = new CreateWord(pageWidth, pageHeight);
			List<XWPFTable> xwpfTables = null;
			// for( int pageNum =16;pageNum<17;pageNum++ )
			int totalNum = document.getPages().getCount();
			for (int pageNum = startPage; pageNum <endPage; pageNum++) {
				try {
					logger.info("第" + pageNum + "页--------------------------");
					callback.convertCallBack(pageNum, totalNum);//回调
					List<PdfLine> pdfContentList = new ArrayList<PdfLine>();
					// 分析表格
					CustomPageDrawer customPageDrawer = new CustomPageDrawer();
					List<Table> tables = customPageDrawer.analysTable(pdffile.getPath(), pageNum);

					if (tables != null && tables.size() > 0) {
						for (Table table : tables) {
							PdfElem tablePdfElem = new PdfElem();
							tablePdfElem.setX((float) table.getxBaseLine().get(0).getX());
							tablePdfElem.setY((float) table.getyBaseLine().get(0).getY());
							tablePdfElem.setType(PdfElem.TYPE_TABLE);
							tablePdfElem.setTable(table);
							PdfLine tableLine = new PdfLine();
							tableLine.setX(tablePdfElem.getX());
							tableLine.setY(tablePdfElem.getY());
							tableLine.setType(PdfElem.TYPE_TABLE);
							tableLine.getElemList().add(tablePdfElem);
							pdfContentList.add(tableLine);
						}

						// table.printMatrix();
						// table.printMergeCell();

						/*
						 * CreateWord1 createWord1 = new CreateWord1(pageWidth, pageHeight); try {
						 * createWord1.createTable1(table); } catch (Exception e) { // TODO
						 * Auto-generated catch block e.printStackTrace(); }
						 */
					}
					//
					PDPage page = document.getPages().get(pageNum);

					logger.info(String.valueOf(pageHeight));
					logger.info("Processing page: " + pageNum);
					PrintImageLocations printeImageLocations = new PrintImageLocations(pdfContentList, createWord, pdffile.getParent(),callback);
					printeImageLocations.processPage(page);
					PDFTextStripper pdfTextStripper = new PrintTextLocations(pdfContentList);
					// stripper.setSortByPosition( true );
					pdfTextStripper.setStartPage(pageNum + 1);
					pdfTextStripper.setEndPage(pageNum + 1);
					String content = pdfTextStripper.getText(document);
					logger.info(content);
					// 文件读取完毕
					// 根据坐标y进行排序
					PdfLineComparator pdfLineComparator = new PdfLineComparator();
					Collections.sort(pdfContentList, pdfLineComparator);
					// 生成word文件
					boolean isLineStart = false;// 是否行开始
					for (int line = 0; line < pdfContentList.size(); line++) {
						isLineStart = true;
						PdfLine pdfLine = pdfContentList.get(line);
						logger.info("text size:" + pdfLine.getElemList().size());
						if (pdfLine.getType() != PdfElem.TYPE_TABLE) {
							// 设置行左侧缩进
							boolean isInTable = printeImageLocations.detectTextLineIsInTable(tables, pdfLine);
							if (!isInTable) {// 不在表格中 则插入左侧缩进
								createWord.insertZcsj(pdfLine.getX());
							}

							if (line == 0) {// 第一行要设置段前缩进
								logger.info("pdfLine.getY()=" + pdfLine.getY());
								createWord.insertDqsj(pdfLine.getY());
							}
						}

						//
						// for(PdfElem pdfElem : pdfLine.getElemList()) {//遍历元素
						for (int lineElemIndex = 0; lineElemIndex < pdfLine.getElemList().size(); lineElemIndex++) {// 遍历元素
							PdfElem pdfElem = pdfLine.getElemList().get(lineElemIndex);
							if (pdfLine.getType() == PdfElem.TYPE_IMAGE) {// 图片
								// 插入图片
								createWord.insertImage(pdfElem.getImagePath(), pdfElem.getImageName(), pdfElem.getImageType(), pdfElem.getImageWidth(), pdfElem.getImageHeight());
							} else if (pdfLine.getType() == PdfElem.TYPE_TEXT) {// 文字

								// 插入文字
								// logger.info(pdfElem.getTextContent());
								// 判断是否有表格 在表格中插入文字
								if (tables != null && tables.size() > 0) {
									// 判断文字集中那个单元格
									boolean isintable = printeImageLocations.setTableCellContent(tables, pdfElem, isLineStart);
									if (!isintable) {
										createWord.insertWords(pdfElem.getTextContent(), pdfElem.getFontsize(), pdfElem.isFontBold(), pdfElem.getFontname());
									}
								} else {
									createWord.insertWords(pdfElem.getTextContent(), pdfElem.getFontsize(), pdfElem.isFontBold(), pdfElem.getFontname());
								}

							} else if (pdfElem.getType() == pdfElem.TYPE_TABLE) {
								// 创建表格
								printeImageLocations.createTable(pdfElem.getTable());
								// 检测该表右侧是不是还有表格 如果有就创建
								logger.info("表格数：" + tables.size());
								for (Table table : tables) {
									if (table.getxBaseLine().get(0).getX() != pdfElem.getTable().getxBaseLine().get(0).getX()
											&& table.getyBaseLine().get(0).getY() != pdfElem.getTable().getyBaseLine().get(0).getY()) {// 不是自己
										double table1Y1 = pdfElem.getTable().getyBaseLine().get(0).getY();
										double table1Y2 = pdfElem.getTable().getyBaseLine().get(pdfElem.getTable().getyBaseLine().size() - 1).getY()
												+ pdfElem.getTable().getyBaseLine().get(pdfElem.getTable().getyBaseLine().size() - 1).getHeight();
										double table2Y1 = table.getyBaseLine().get(0).getY();
										double table2Y2 = table.getyBaseLine().get(table.getyBaseLine().size() - 1).getY()
												+ table.getyBaseLine().get(table.getyBaseLine().size() - 1).getHeight();
										if (Math.abs(table2Y1 - table1Y1) < (table1Y2 - table1Y1)) {// 在右侧
											if (table.getXwpfTable() == null) {
												printeImageLocations.createTable(table);
											}
										}
									}
								}

							}
							if (isLineStart)
								isLineStart = false;
						}

					}
					// 合并单元格
					printeImageLocations.mergeTable(tables);
//					createWord.saveFile(wordfilepath);
					// 插入空白页
					createWord.addNewPage();
				} catch (Exception e) {
					// TODO: handle exception
//					createWord.saveFile(wordfilepath);
					e.printStackTrace();
					continue;
				}
				
			}
			ret = true;
		} catch (InvalidPasswordException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (document != null) {
				try {
					document.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		createWord.saveFile(wordfilepath);
		return ret;
	}

	/**
	 * 创建表格
	 * 
	 * @param table
	 */
	public void createTable(Table table) {
		if (table.getXwpfTable() != null)
			return;
		// createWord.insertZcsj((float)table.getxBaseLine().get(0).getX());
		// createWord.insertDqsj((float)table.getyBaseLine().get(0).getY());
		int colWidths[] = new int[table.getColnum()];
		int widthTable = 0;

		for (TableLine line : table.getxBaseLine()) {
			widthTable += line.getWidth();
		}
		XWPFTable xwpfTable = createWord.createTable(table.getRownum(), table.getColnum(), false, null);
		xwpfTable.setCellMargins(0, 0, 0, 0);
		createWord.setTableWidthAndHAlign(xwpfTable, String.valueOf(widthTable * 20), STJc.CENTER);

		logger.info(String.valueOf(widthTable));

		for (int rowNum = 0; rowNum < table.getRownum(); rowNum++) {
			XWPFTableRow row = xwpfTable.getRow(rowNum);
			logger.info("行高：" + String.valueOf((int) table.getyBaseLine().get(rowNum).getHeight()));
			// 设置行高
			createWord.setRowHeight(row, String.valueOf((int) table.getyBaseLine().get(rowNum).getHeight() * 20), STHeightRule.AUTO);
			// 设置列宽
			for (int colNum = 0; colNum < table.getxBaseLine().size(); colNum++) {
				colWidths[colNum] = (int) table.getxBaseLine().get(colNum).getWidth();
				widthTable += colWidths[colNum];
				XWPFTableCell cell = row.getCell(colNum);
				createWord.setCellWidthAndVAlign(cell, String.valueOf((int) table.getxBaseLine().get(colNum).getWidth() * 20), STTblWidth.DXA, STVerticalJc.TOP);
				logger.info("colnum width:" + colWidths[colNum]);
			}

		}
		table.setXwpfTable(xwpfTable);
	}

	/**
	 * 设置单元格内容
	 * 
	 * @param table
	 * @param pdfElem
	 * @return 返回是否在表格中 在就true 否就false
	 */
	public boolean setTableCellContent(List<Table> tables, PdfElem pdfElem, boolean isLineStart) {
		float elemX = pdfElem.getX();
		float elemY = pdfElem.getY();
		boolean isintable = false;
		// logger.info("表格数："+tables.size());
		for (Table table : tables) {
			XWPFTable xwpfTable = table.getXwpfTable();
			if (xwpfTable == null)
				continue;
			for (int row = 0; row < table.getRownum(); row++) {
				for (int col = 0; col < table.getColnum(); col++) {
					double p1x = table.getxBaseLine().get(col).getX();
					double p1y = table.getyBaseLine().get(row).getY();
					double p2x = table.getxBaseLine().get(col).getX() + table.getxBaseLine().get(col).getWidth();
					double p2y = table.getyBaseLine().get(row).getY() + table.getyBaseLine().get(row).getHeight();
					if (elemX > p1x && elemX < p2x && elemY > p1y && elemY < p2y) {// 在单元格中
						isintable = true;
						logger.info("字体：" + (int) pdfElem.getTextContent().charAt(0));
						createWord.setCellNewContent(isLineStart, xwpfTable, row, col, pdfElem.getTextContent(), pdfElem.getFontname(), pdfElem.getFontsize(),
								pdfElem.isFontBold());
						// createWord.setParagraphRunFontInfo(isLineStart,xwpfTable, row, col,
						// pdfElem.getTextContent(), pdfElem.getFontname(), null,
						// String.valueOf(pdfElem.getFontsize()), pdfElem.isFontBold(), false, false,
						// false, null, null, 0, 0, 0);
					}
				}
			}
		}

		return isintable;
	}

	/**
	 * 检测文本行是否在表格中
	 * 
	 * @param table
	 * @param pdfElem
	 * @return 返回是否在表格中 在就true 否就false
	 */
	public boolean detectTextLineIsInTable(List<Table> tables, PdfLine pdfLine) {
		float lineX = pdfLine.getX();
		float lineY = pdfLine.getY();
		boolean isintable = false;
		for (Table table : tables) {
			XWPFTable xwpfTable = table.getXwpfTable();
			if (xwpfTable == null)
				continue;
			for (int row = 0; row < table.getRownum(); row++) {
				for (int col = 0; col < table.getColnum(); col++) {
					double p1x = table.getxBaseLine().get(col).getX();
					double p1y = table.getyBaseLine().get(row).getY();
					double p2x = table.getxBaseLine().get(col).getX() + table.getxBaseLine().get(col).getWidth();
					double p2y = table.getyBaseLine().get(row).getY() + table.getyBaseLine().get(row).getHeight();
					if (lineX > p1x && lineX < p2x && lineY > p1y && lineY < p2y) {// 在单元格中
						isintable = true;
					}
				}
			}
		}

		return isintable;
	}

	/**
	 * \ 合并单元格
	 * 
	 * @param table
	 */
	public void mergeTable(List<Table> tables) {
		for (Table table : tables) {
			XWPFTable xwpfTable = table.getXwpfTable();
			if (table.getMergeTableCells() != null && table.getMergeTableCells().size() > 0) {
				for (MergeTableCell mergeTableCell : table.getMergeTableCells()) {
					if (mergeTableCell.getFromRow() == mergeTableCell.getToRow()) {// 行相等，列合并
						// 把内容填充到第一个单元格
						String content = "";
						for (int col = mergeTableCell.getFromCol() + 1; col <= mergeTableCell.getToCol(); col++) {
							content += xwpfTable.getRow(mergeTableCell.getFromRow()).getCell(col).getText();
						}
						createWord.setMergeCellNewContent(xwpfTable, mergeTableCell.getFromRow(), mergeTableCell.getFromCol(), content);
						createWord.mergeCellsHorizontal(xwpfTable, mergeTableCell.getFromRow(), mergeTableCell.getFromCol(), mergeTableCell.getToCol());
					} else if (mergeTableCell.getFromCol() == mergeTableCell.getToCol()) {// 列相等，行合并
						// 把内容填充到第一个单元格
						String content = "";
						for (int row = mergeTableCell.getFromRow() + 1; row <= mergeTableCell.getToRow(); row++) {
							content += xwpfTable.getRow(row).getCell(mergeTableCell.getFromCol()).getText();
						}
						createWord.setMergeCellNewContent(xwpfTable, mergeTableCell.getFromRow(), mergeTableCell.getFromCol(), content);
						createWord.mergeCellsVertically(xwpfTable, mergeTableCell.getFromCol(), mergeTableCell.getFromRow(), mergeTableCell.getToRow());
					} else if (mergeTableCell.getFromRow() != mergeTableCell.getToRow() && mergeTableCell.getFromCol() != mergeTableCell.getToCol()) {// 行列都不相等
						// 填充内容
						String content = "";
						for (int row = mergeTableCell.getFromRow(); row <= mergeTableCell.getToRow(); row++) {
							for (int col = mergeTableCell.getFromCol(); col <= mergeTableCell.getToCol(); col++) {
								if (row == mergeTableCell.getFromRow() && col == mergeTableCell.getFromCol())
									continue;
								content += xwpfTable.getRow(row).getCell(col).getText();
							}
						}
						createWord.setMergeCellNewContent(xwpfTable, mergeTableCell.getFromRow(), mergeTableCell.getFromCol(), content);
						// 先每行列进行合并
						System.out.printf("[fromRow:%d toRow:%d fromCol:%d toCol:%d ]\r\n", mergeTableCell.getFromRow() + 1, mergeTableCell.getToRow() + 1,
								mergeTableCell.getFromCol() + 1, mergeTableCell.getToCol() + 1);
						for (int row = mergeTableCell.getFromRow(); row <= mergeTableCell.getToRow(); row++) {
							createWord.mergeCellsHorizontal(xwpfTable, row, mergeTableCell.getFromCol(), mergeTableCell.getToCol());
						}
						// 合并列
						for (int col = mergeTableCell.getFromCol(); col <= mergeTableCell.getToCol(); col++) {
							createWord.mergeCellsVertically(xwpfTable, col, mergeTableCell.getFromRow(), mergeTableCell.getToRow());
						}
					}
				}
			}
		}

	}

	/**
	 * 表格中插入元素
	 * 
	 * @param table
	 * @param pdfElem
	 * @return
	 */
	private static boolean insertElemToTable(Table table, PdfElem pdfElem) {
		// 判断元素击中那个单元格

		return false;
	}

	@Override
	public void showTextString(byte[] string) throws IOException {
		// TODO Auto-generated method stub
		logger.info(String.valueOf(string));
		super.showTextString(string);
	}

	/**
	 * This is used to handle an operation.
	 *
	 * @param operator
	 *            The operation to perform.
	 * @param operands
	 *            The list of arguments.
	 *
	 * @throws IOException
	 *             If there is an error processing the operation.
	 */
	@Override
	protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {
		String operation = operator.getName();
		// logger.info(operation+" : "+operands.toString());
		if ("Do".equals(operation)) {
			COSName objectName = (COSName) operands.get(0);
			PDXObject xobject = getResources().getXObject(objectName);
			if (xobject instanceof PDImageXObject) {
				PDImageXObject image = (PDImageXObject) xobject;
				int imageWidth = image.getWidth();
				int imageHeight = image.getHeight();
				logger.info("*******************************************************************");
				logger.info("Found image [" + objectName.getName() + "]");
				Matrix ctmNew = getGraphicsState().getCurrentTransformationMatrix();
				float imageXScale = ctmNew.getScalingFactorX();
				float imageYScale = ctmNew.getScalingFactorY();

				// position in user space units. 1 unit = 1/72 inch at 72 dpi
				float x = ctmNew.getTranslateX();
				float y = (pageHeight - ctmNew.getTranslateY() - imageYScale);
				logger.info("position in PDF = " + x + ", " + y + " in user space units");
				logger.info("position in PDF = " + ctmNew.getTranslateX() + ", " + ctmNew.getTranslateY() + " in user space units");
				// raw size in pixels
				logger.info("raw image size  = " + imageWidth + ", " + imageHeight + " in pixels");
				// displayed size in user space units
				logger.info("displayed size  = " + imageXScale + ", " + imageYScale + " in user space units");
				// displayed size in inches at 72 dpi rendering
				imageXScale /= 72;
				imageYScale /= 72;
				logger.info("displayed size  = " + imageXScale + ", " + imageYScale + " in inches at 72 dpi rendering");
				// displayed size in millimeters at 72 dpi rendering
				imageXScale *= 25.4;
				imageYScale *= 25.4;
				logger.info("displayed size  = " + imageXScale + ", " + imageYScale + " in millimeters at 72 dpi rendering");
				BufferedImage bi = image.getImage();
				ImageIO.write(bi, "jpg", new File(filebasepath + objectName.getName() + ".jpg"));
				// 如果是图片则插入行内容
				PdfLine pdfLine = new PdfLine();
				pdfLine.setX(x);
				pdfLine.setY(y);
				pdfLine.setType(PdfElem.TYPE_IMAGE);
				PdfElem pdfElem = new PdfElem();
				pdfElem.setX(x);
				pdfElem.setY(y);
				pdfElem.setImageName(objectName.getName());
				pdfElem.setImagePath(filebasepath + objectName.getName() + ".jpg");
				pdfElem.setImageType(Document.PICTURE_TYPE_JPEG);
				pdfElem.setImageWidth((int) ctmNew.getScalingFactorX());
				pdfElem.setImageHeight((int) ctmNew.getScalingFactorY());
				pdfLine.getElemList().add(pdfElem);
				pdfContentList.add(pdfLine);
			} else if (xobject instanceof PDFormXObject) {

				PDFormXObject form = (PDFormXObject) xobject;
				showForm(form);
			}
		} else {

			super.processOperator(operator, operands);
		}
	}

	@Override
	public Float transformedPoint(float x, float y) {
		// TODO Auto-generated method stub
		return super.transformedPoint(x, y);
	}

	/**
	 * This will print the usage for this document.
	 */
	private static void usage() {
		System.err.println("Usage: java " + PrintImageLocations.class.getName() + " <input-pdf>");
	}

}
