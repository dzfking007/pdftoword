package application;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.ld.myutils.LoggerUtil;
import com.ld.pdftoword.Callback;
import com.ld.pdftoword.PrintImageLocations;

import application.beans.ConvertFile;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class PdfToWordController implements Initializable {
	private static Logger logger = LoggerUtil.getJdkLog();
	@FXML
	private TableView<ConvertFile> FileTable;
	@FXML
	private TableColumn colSelect;
	@FXML
	private TableColumn colFileName;
	@FXML
	private TableColumn colStatus;
	@FXML
	private TableColumn colDel;
	@FXML
	private TableColumn colOpen;
	@FXML
	private TableColumn colZhqj;//设置换换区间

	@FXML
	private TableView<ConvertFile> FileTableOCR;
	@FXML
	private TableColumn colSelectOCR;
	@FXML
	private TableColumn colFileNameOCR;
	@FXML
	private TableColumn colStatusOCR;
	@FXML
	private TableColumn colDelOCR;
	@FXML
	private TableColumn colOpenOCR;
	
	private String defaultFileOpenPath;// 默认开启目录
	private final ObservableList<ConvertFile> fileTableData = FXCollections.observableArrayList();// 文件表格数据
	private final ObservableList<ConvertFile> fileTableDataOCR = FXCollections.observableArrayList();// 文件表格数据
	private List<String> pdfFilepaths = new ArrayList<String>();

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		// 初始化表格
		colSelect.setCellValueFactory(new PropertyValueFactory<>("select"));
		colFileName.setCellValueFactory(new PropertyValueFactory<>("filepath"));
		colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
		colDel.setCellValueFactory(new PropertyValueFactory<>("deleteBtn"));
		colOpen.setCellValueFactory(new PropertyValueFactory<>("openBtn"));
		colZhqj.setCellValueFactory(new PropertyValueFactory<>("colZhqjBtn"));
		FileTable.setItems(fileTableData);
		//
		colSelectOCR.setCellValueFactory(new PropertyValueFactory<>("select"));
		colFileNameOCR.setCellValueFactory(new PropertyValueFactory<>("filepath"));
		colStatusOCR.setCellValueFactory(new PropertyValueFactory<>("status"));
		colDelOCR.setCellValueFactory(new PropertyValueFactory<>("deleteBtn"));
		colOpenOCR.setCellValueFactory(new PropertyValueFactory<>("openBtn"));
		FileTableOCR.setItems(fileTableDataOCR);
	}

	

	// Called when a project is unselected.
	private void projectUnselected(String oldProjectName) {
		if (oldProjectName != null) {

		}
	}

	// Called when a project is selected.
	private void projectSelected(String newProjectName) {
		if (newProjectName != null) {

		}
	}

	private final ListChangeListener<String> projectIssuesListener = new ListChangeListener<String>() {

		@Override
		public void onChanged(Change<? extends String> c) {

		}
	};

	@FXML
	protected void addFile() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("选择PDF文件");
		if (defaultFileOpenPath == null) {
			fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		} else {
			fileChooser.setInitialDirectory(new File(defaultFileOpenPath));
		}

		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("pdf", "*.pdf"), new FileChooser.ExtensionFilter("PDF", "*.PDF"));

		File file = fileChooser.showOpenDialog(Main.stage);
		if (file != null) {
			defaultFileOpenPath = file.getParent();
			pdfFilepaths.add(file.getPath());
			//获取总页数
			PrintImageLocations imageLocations;
			int totalPage = 0;
			try {
				imageLocations = new PrintImageLocations(null);
				totalPage = imageLocations.getPdfTotalPage(file.getPath());
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				return ;
			}
			
			ConvertFile cf = new ConvertFile(file.getPath(), "等待转换(供"+totalPage+"页)");
			cf.setTotalPage(totalPage);
			cf.getDeleteBtn().setOnAction((ActionEvent e) -> {
				fileTableData.remove(cf);
				refreshTable(FileTable);
			});
			
			//打开按钮
			cf.getOpenBtn().setOnAction((ActionEvent e)->{
				Desktop de = Desktop.getDesktop();
				try {
					de.open(new File(file.getParent()));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
			//设置转换区间按钮
			cf.getColZhqjBtn().setOnAction((ActionEvent e)->{
				try {
					openZhqjWin(cf);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
			fileTableData.add(cf);
		}

	}

	@FXML
	protected void addFileOCR() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("选择PDF文件");
		if (defaultFileOpenPath == null) {
			fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
		} else {
			fileChooser.setInitialDirectory(new File(defaultFileOpenPath));
		}

		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("jpg", "*.jpg"), new FileChooser.ExtensionFilter("png", "*.png"));

		File file = fileChooser.showOpenDialog(Main.stage);
		if (file != null) {
			defaultFileOpenPath = file.getParent();
			pdfFilepaths.add(file.getPath());
			ConvertFile cf = new ConvertFile(file.getPath(), "等待转换");
			cf.getDeleteBtn().setOnAction((ActionEvent e) -> {
				fileTableDataOCR.remove(cf);
				refreshTable(FileTableOCR);
			});
			//打开按钮
			cf.getOpenBtn().setOnAction((ActionEvent e)->{
				Desktop de = Desktop.getDesktop();
				try {
					de.open(new File(file.getParent()));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			});
			fileTableDataOCR.add(cf);
		}

	}
	/**
	 * 转换pdf 到 word
	 */
	@FXML
	protected void convertPdfToWord() {
		for (int i = 0; i < fileTableData.size(); i++) {
			logger.info("第" + i + "行");
			ConvertFile t = fileTableData.get(i);
			System.out.println(t.getEndPage());
			int startPage = t.getStartPage();
			int endPage = t.getEndPage();
			// TODO Auto-generated method stub
			t.setStatus("转换中...");
			refreshTable(FileTable);// 刷新内容
			int tableIndex = i;
			String pdffilepath = t.getFilepath();
			String wordfilepath = pdffilepath.split("\\.")[0] + "_香蕉转换.doc";
			Task<Void> task = new Task<Void>() {
				boolean ret = false;
				@Override
				protected Void call() throws Exception {
					// TODO Auto-generated method stub
					ret = pdfToWord(startPage,endPage,pdffilepath, wordfilepath,tableIndex);
					return null;
				}

				@Override
				protected void running() {
					// TODO Auto-generated method stub
					super.running();
				}

				@Override
				protected void succeeded() {
					// TODO Auto-generated method stub
					super.succeeded();
					if (ret) {
						t.setStatus("转换成功");
					} else {
						t.setStatus("转换失败");
					}
					refreshTable(FileTable);// 刷新内容
				}

				@Override
				protected void failed() {
					// TODO Auto-generated method stub
					super.failed();
					t.setStatus("转换失败");
					refreshTable(FileTable);// 刷新内容
				}

				
				
			};
			new Thread(task).start();
			
			/*Platform.runLater(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					boolean ret = pdfToWord(pdffilepath, wordfilepath);

					if (ret) {
						t.setStatus("转换成功");
					} else {
						t.setStatus("转换失败");
					}
					refreshTable(FileTable);// 刷新内容
				}
			});*/
			refreshTable(FileTable);// 刷新内容
		}
	}

	
	/**
	 * 转换pdf 到 word
	 */
	@FXML
	protected void convertOCR() {
		for (int i = 0; i < fileTableDataOCR.size(); i++) {
			logger.info("第" + i + "行");
			ConvertFile t = fileTableDataOCR.get(i);
			// TODO Auto-generated method stub
			t.setStatus("转换中...");
			refreshTable(FileTableOCR);// 刷新内容
			int tableIndex = i;
			String pdffilepath = t.getFilepath();
			String wordfilepath = pdffilepath.split("\\.")[0] + "_香蕉转换.doc";
			Task<Void> task = new Task<Void>() {
				boolean ret = false;
				
				@Override
				protected Void call() throws Exception {
					// TODO Auto-generated method stub
					//ret = pdfToWord(pdffilepath, wordfilepath,tableIndex);
					return null;
				}

				@Override
				protected void running() {
					// TODO Auto-generated method stub
					super.running();
				}

				@Override
				protected void succeeded() {
					// TODO Auto-generated method stub
					super.succeeded();
					if (ret) {
						t.setStatus("转换成功");
					} else {
						t.setStatus("转换失败");
					}
					refreshTable(FileTableOCR);// 刷新内容
				}

				@Override
				protected void failed() {
					// TODO Auto-generated method stub
					super.failed();
					t.setStatus("转换失败");
					refreshTable(FileTableOCR);// 刷新内容
				}

				
				
			};
			new Thread(task).start();
			
			/*Platform.runLater(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					boolean ret = pdfToWord(pdffilepath, wordfilepath);

					if (ret) {
						t.setStatus("转换成功");
					} else {
						t.setStatus("转换失败");
					}
					refreshTable(FileTable);// 刷新内容
				}
			});*/
			refreshTable(FileTableOCR);// 刷新内容
		}
	}
	/**
	 * 转换
	 * 
	 * @param pdffilepath
	 * @param wordfilepath
	 * @return
	 * @throws IOException 
	 */
	private boolean pdfToWord(int startPage,int endPage,String pdffilepath, String wordfilepath,int tableIndex) throws IOException {
		PrintImageLocations imageLocations = new PrintImageLocations(new Callback() {
			
			@Override
			public void convertCallBack(int curPage, int totalPage) {
				// TODO Auto-generated method stub
				logger.info("当前执行页面"+curPage + totalPage);
				ConvertFile t = fileTableData.get(tableIndex);
				t.setStatus(curPage +"/"+ totalPage);
				refreshTable(FileTable);// 刷新内容
				
			}
		});
		boolean ret = imageLocations.convert(startPage,endPage,pdffilepath, wordfilepath);
		return ret;
	}

	/**
	 * 刷新表格
	 * 
	 * @param tableView
	 */
	private void refreshTable(TableView tableView) {
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
		        //更新JavaFX的主线程的代码放在此处
		    	for (int i = 0; i < tableView.getColumns().size(); i++) {
					((TableColumn) (tableView.getColumns().get(i))).setVisible(false);
					((TableColumn) (tableView.getColumns().get(i))).setVisible(true);
				}
		    }
		});
		
	}
	
	/**
	 * 打开转换区间对话框
	 * @throws IOException 
	 */
	private void openZhqjWin(ConvertFile cf) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ZhqjWin.fxml"));
		Parent target = fxmlLoader.load();//载入窗口B的定义文件；<span style="white-space:pre">	</span>
		Scene scene = new Scene(target); //创建场景；
		Stage stg=new Stage();//创建舞台；
		stg.initStyle(StageStyle.UTILITY);
		stg.setTitle("选择转换区间");
		stg.setFullScreen(false);
		stg.setMaximized(false);
		stg.setResizable(false);
		stg.setScene(scene); //将场景载入舞台；
		ZhqjWinController controller  = fxmlLoader.getController();
		controller.setCovertFile(cf);
		controller.setStage(stg);
		controller.initStartEndPage("1", String.valueOf(cf.getTotalPage()));
		stg.show(); //显示窗口；
		
	}
	
	/**
	 * 打开网址
	 */
	@FXML
	public void linkRed214() {
		System.out.println(11);
		try {
			java.awt.Desktop.getDesktop().browse(new URI("http://www.red214.com"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  ;
	}

}
