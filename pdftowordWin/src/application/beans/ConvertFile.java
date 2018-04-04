package application.beans;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;

/**
 * 转换文件对象
 * @author Seven
 * @date 2017年10月10日
 */
public class ConvertFile {
	private CheckBox select;//是否选择
	private final SimpleStringProperty filepath;//文件路径
	private final SimpleStringProperty status;//状态
	private final Button deleteBtn;//删除操作
	private String wordfilepath;//word文件路径
	private Button openBtn;//打开
	private Button colZhqjBtn;//转换区间
	private int startPage;//开始页面
	private int endPage;//结束页面
	private int totalPage;//总页数
	
	public ConvertFile(String filepath, String status) {
		this.select = new CheckBox();
		this.filepath = new SimpleStringProperty(filepath);
		this.status = new SimpleStringProperty(status);
		this.deleteBtn = new Button("删除");
		this.openBtn = new Button("打开");
		this.colZhqjBtn = new Button("设置区间");
		
	}
	public CheckBox getSelect() {
		return select;
	}
	public void setSelect(CheckBox select) {
		this.select = select;
	}
	public String getFilepath() {
		return filepath.get();
	}
	public void setFilepath(String filepath) {
		this.filepath.set(filepath);
	}
	public String getStatus() {
		return status.get();
	}
	public void setStatus(String status) {
		this.status.set(status);
	}
	
	public Button getDeleteBtn() {
		return deleteBtn;
	}
	public String getWordfilepath() {
		return wordfilepath;
	}
	public void setWordfilepath(String wordfilepath) {
		this.wordfilepath = wordfilepath;
	}
	public Button getOpenBtn() {
		return openBtn;
	}
	public void setOpenBtn(Button openBtn) {
		this.openBtn = openBtn;
	}
	public Button getColZhqjBtn() {
		return colZhqjBtn;
	}
	public void setColZhqjBtn(Button colZhqjBtn) {
		this.colZhqjBtn = colZhqjBtn;
	}
	public int getStartPage() {
		return startPage;
	}
	public void setStartPage(int startPage) {
		this.startPage = startPage;
	}
	public int getEndPage() {
		return endPage;
	}
	public void setEndPage(int endPage) {
		this.endPage = endPage;
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	
	
}
