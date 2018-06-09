package controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;

import controller.MainController.AppCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.*;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.sf.image4j.codec.ico.ICODecoder;
import screenLocker.Application;
import screenLocker.loader.Loader;

public class SettingController implements Initializable {
	
	static class AppCell extends ListCell<Application> {
        private HBox _hbox;
        private ImageView _icon;
        private Application _lastItem;

        public AppCell() {
            super();
            _hbox = new HBox();
            _icon = new ImageView();
            _hbox.getChildren().add(_icon);
            _hbox.setStyle("-fx-padding: 0px 0px 0px 10px;");
            getStylesheets().add(this.getClass().getResource("/stylesheets/_appListView.css").toExternalForm());
        }
        
        @Override
        protected void updateItem(Application _item, boolean _empty) {
            super.updateItem(_item, _empty);
            setText(null);
            if (_empty) {
                _lastItem = null;
                setGraphic(null);
            } else {
                _lastItem = _item;
                if (_item.GetDisplayName().length() > 15)
                	setText("  " + _item.GetDisplayName().substring(0, 15) + "...");
                else
                	setText("  " + _item.GetDisplayName());
                try {
                	if (_lastItem.GetIconPath() != null && _lastItem.GetIconPath() != "" ) {
		                if (_lastItem.GetIconPath().indexOf(".ico") < 0) {
		                	// get the exe icon file.
		                	File _file = new File(_lastItem.GetIconPath());
		                    sun.awt.shell.ShellFolder _sf =
		                            sun.awt.shell.ShellFolder.getShellFolder(_file);
		                    javax.swing.Icon _iconImage = new ImageIcon(_sf.getIcon(true));
		                    BufferedImage _bufferedImage = new BufferedImage(_iconImage.getIconWidth(), _iconImage.getIconHeight(),
		                    		BufferedImage.TYPE_INT_ARGB);
		                    _iconImage.paintIcon(null, _bufferedImage.getGraphics(), 0, 0);
		                    _icon.setImage(SwingFXUtils.toFXImage(_bufferedImage, null));
		                } else {
		                	File _file = new File(_lastItem.GetIconPath());
		                    List<BufferedImage> _images = ICODecoder.read(_file);
		                    for(BufferedImage _iter : _images) {
		                    	if (_iter.getWidth() > 24 && _iter.getWidth() < 48) {
		                    		_icon.setImage(SwingFXUtils.toFXImage(_iter, null));
		                    		break;
		                    	}
		                    }
		                }
                	} else {
	                	// get the default exe icon file.
	                	File _file = new File(this.getClass().getResource("/images/_iconExe.ico").getPath());
	                	List<BufferedImage> _images = ICODecoder.read(_file);
	                    for(BufferedImage _iter : _images) {
	                    	if (_iter.getWidth() > 24 && _iter.getWidth() < 48) {
	                    		_icon.setImage(SwingFXUtils.toFXImage(_iter, null));
	                    		break;
	                    	}
	                    }
                	}
                } catch (Exception e) {
                	System.out.println(e.getMessage());
                	// get the default exe icon file.
                	File _file = new File(this.getClass().getResource("/images/_iconExe.ico").getPath());
                	List<BufferedImage> _images;
					try {
						_images = ICODecoder.read(_file);
						for(BufferedImage _iter : _images) {
	                    	if (_iter.getWidth() > 24 && _iter.getWidth() < 48) {
	                    		_icon.setImage(SwingFXUtils.toFXImage(_iter, null));
	                    		break;
	                    	}
	                    }
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                    
                }
                setGraphic(_hbox);
            }
        }
    }
	
	
    private double _x, _y;
    @FXML
    private Button _shrinkButton;
    @FXML
    private Button _enlargeButton;
    @FXML
    private Button _closeButton;
    @FXML
    private Button _returnButton;
    @FXML
    private GridPane _rightItems;
    @FXML
    private ListView _appListView;
    
	@FXML
    public void Draged(MouseEvent event) {
    	Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    	stage.setX(event.getScreenX() - _x);
    	stage.setY(event.getScreenY() - _y);
    }

    @FXML
    public void Pressed(MouseEvent event) {
    	_x = event.getSceneX();
    	_y = event.getSceneY();
    }
    
    @FXML
    public void Close(MouseEvent event) {
    	Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    	stage.close();
    }
    @FXML
    public void Shrink(MouseEvent event) {
    	Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    	stage.setIconified(true);
    }

    @FXML
    public void ToGUIMain(ActionEvent event) {
		// switch to main scene.
		Stage _stage = (Stage) _shrinkButton.getScene().getWindow();
		Event _event = new WindowsTransferEvent(this, _stage, WindowsTransferEvent.TransferToMain);
		_shrinkButton.fireEvent(_event);
    }
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		_rightItems.setVisible(false);
		ArrayList<Application> _appList = new ArrayList<Application>();
        //ListView<String> _appList = new ListView<>();
        for(Application _iter : Loader.GetInstance().GetApplication()) {
        	_appList.add(_iter);
        }
        ObservableList _observableList = FXCollections.observableArrayList(_appList);
        _appListView.setItems(_observableList);
        _appListView.setPrefHeight(35 * 8);
        _appListView.setCellFactory(new Callback<ListView<Application>, ListCell<Application>>() {
            @Override
            public ListCell<Application> call(ListView<Application> param) {
            	return new AppCell();

            }
        });
	}
	
	

}