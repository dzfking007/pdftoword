package application;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.ld.myutils.LoggerUtil;
import com.sun.jndi.url.corbaname.corbanameURLContextFactory;

import application.beans.ConvertFile;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ZhqjWinController implements Initializable {
	private static Logger logger = LoggerUtil.getJdkLog();
	private Stage stage;
	@FXML
	private TextField zhqj_startPage;
	@FXML
	private TextField zhqj_endPage;
	@FXML
	private Button zhqj_okBtn;
	
	private ConvertFile covertFile;//转换文件 
	
	


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		zhqj_startPage.setText("1");
		zhqj_endPage.setText("100");
		
	}


	@FXML
	protected void ok() {
		int startPage = Integer.parseInt(zhqj_startPage.getText())-1;
		int endPage = Integer.parseInt(zhqj_endPage.getText());
		covertFile.setStartPage(startPage);
		covertFile.setEndPage(endPage);
		stage.close();
		

	}
	
	public void initStartEndPage(String startPage,String endPage) {
		zhqj_startPage.setText(startPage);
		zhqj_endPage.setText(endPage);
		
	}


	public ConvertFile getCovertFile() {
		return covertFile;
	}


	public void setCovertFile(ConvertFile covertFile) {
		this.covertFile = covertFile;
	}


	public Stage getStage() {
		return stage;
	}


	public void setStage(Stage stage) {
		this.stage = stage;
	}



}
