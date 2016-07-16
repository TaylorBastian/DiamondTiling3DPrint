/*******************************************************          
* Name: DiamondApp                                     *
* Date: Summer 2016                                    *
*                                                      *   
* Author: Bastian, Taylor                              *   
*                                                      *   
* Purpose:  Demonstration of 3d Diamond Tiling         *
*                                                      *                                                                                               
*******************************************************/

package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class DiamondApp extends Application {

	final Stage stage = new Stage();
	final Group rootR = new Group();
	final VBox rootG = new VBox();
	final Group rootW = new Group();
	final Util util = new Util();
	final Xform axisGroup = new Xform();
	final Xform diamondGroup = new Xform();
	final Xform world = new Xform();
	final PerspectiveCamera camera = new PerspectiveCamera(true);
	final Xform cameraXform = new Xform();
	final Xform cameraXform2 = new Xform();
	final Xform cameraXform3 = new Xform();
	final Text errorText = new Text();
	private static final int WINDOW_WIDTH = 600;
	private static final int WINDOW_HEIGTH = 600;
	private static final double CAMERA_INITIAL_DISTANCE = -450;
	private static final double CAMERA_INITIAL_X_ANGLE = 0;
	private static final double CAMERA_INITIAL_Y_ANGLE = 0;
	private static final double CAMERA_NEAR_CLIP = 0.1;
	private static final double CAMERA_FAR_CLIP = 10000.0;
	private static final double CONTROL_MULTIPLIER = 0.1;
	private static final double SHIFT_MULTIPLIER = 10.0;
	private static final double MOUSE_SPEED = 0.2;
	private static final double ROTATION_SPEED = 2.0;
	private static final double TRACK_SPEED = 0.3;
	private static final double AXIS_LENGTH = 2000;

	double mousePosX;
	double mousePosY;
	double mouseOldX;
	double mouseOldY;
	double mouseDeltaX;
	double mouseDeltaY;

	private double nodeSize = 5;
	private String[] yCord;
	private String[] xCord;
	private String[] zCord;
	private Integer[] colorLine;
	private String fileLocation = "src/test_file.txt";
	private Sphere sphere;
	private double xSpacing = 0;
	private double ySpacing = 0;
	private double zSpacing = 0;

	private void buildCamera() {
		// clear children
		cameraXform.getChildren().clear();
		cameraXform2.getChildren().clear();
		cameraXform3.getChildren().clear();
		rootR.getChildren().add(cameraXform);

		// Set up Xforms
		cameraXform.getChildren().add(cameraXform2);
		cameraXform2.getChildren().add(cameraXform3);
		cameraXform3.getChildren().add(camera);

		cameraXform3.setRotateZ(180.0);
		camera.setNearClip(CAMERA_NEAR_CLIP);
		camera.setFarClip(CAMERA_FAR_CLIP);
		camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
		cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
		cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
		System.out.println("buildCamera() Complete");
	}

	private void handleMouse(SubScene scene, final Node root) {
		scene.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				mousePosX = me.getSceneX();
				mousePosY = me.getSceneY();
				mouseOldX = me.getSceneX();
				mouseOldY = me.getSceneY();
			}
		});
		scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent me) {
				mouseOldX = mousePosX;
				mouseOldY = mousePosY;
				mousePosX = me.getSceneX();
				mousePosY = me.getSceneY();
				mouseDeltaX = (mousePosX - mouseOldX);
				mouseDeltaY = (mousePosY - mouseOldY);

				double modifier = 1.0;

				if (me.isControlDown()) {
					modifier = CONTROL_MULTIPLIER;
				}
				if (me.isShiftDown()) {
					modifier = SHIFT_MULTIPLIER;
				}
				if (me.isPrimaryButtonDown()) {
					cameraXform.ry.setAngle(
							cameraXform.ry.getAngle() - mouseDeltaX * MOUSE_SPEED * modifier * ROTATION_SPEED);
					cameraXform.rx.setAngle(
							cameraXform.rx.getAngle() + mouseDeltaY * MOUSE_SPEED * modifier * ROTATION_SPEED);
				} else if (me.isSecondaryButtonDown()) {
					double z = camera.getTranslateZ();
					double newZ = z + mouseDeltaX * MOUSE_SPEED * modifier;
					camera.setTranslateZ(newZ);
				} else if (me.isMiddleButtonDown()) {
					cameraXform2.t.setX(cameraXform2.t.getX() + mouseDeltaX * MOUSE_SPEED * modifier * TRACK_SPEED);
					cameraXform2.t.setY(cameraXform2.t.getY() + mouseDeltaY * MOUSE_SPEED * modifier * TRACK_SPEED);
				}
			}
		});
	}

	private void handleKeyboard(SubScene scene, final Node root) {
		scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				switch (event.getCode()) {
				case Z:
					cameraXform2.t.setX(0.0);
					cameraXform2.t.setY(0.0);
					camera.setTranslateZ(CAMERA_INITIAL_DISTANCE);
					cameraXform.ry.setAngle(CAMERA_INITIAL_Y_ANGLE);
					cameraXform.rx.setAngle(CAMERA_INITIAL_X_ANGLE);
					break;
				case X:
					axisGroup.setVisible(!axisGroup.isVisible());
					break;
				case V:
					diamondGroup.setVisible(!diamondGroup.isVisible());
					break;
				default:
					break;
				}
			}
		});
	}

	private void buildDiamond() throws IOException {
		if (getData()) {

			Xform diamondXform = new Xform();
			Xform sphereXform = new Xform();

			diamondXform.getChildren().clear();
			sphereXform.getChildren().clear();
			diamondGroup.getChildren().clear();

			PhongMaterial material = new PhongMaterial();

			final PhongMaterial blackMaterial = new PhongMaterial();
			blackMaterial.setDiffuseColor(Color.BLACK);
			blackMaterial.setSpecularColor(Color.DARKGREY);

			final PhongMaterial whiteMaterial = new PhongMaterial();
			whiteMaterial.setDiffuseColor(Color.WHITE);
			whiteMaterial.setSpecularColor(Color.LIGHTSLATEGRAY);

			final PhongMaterial blueMaterial = new PhongMaterial();
			blueMaterial.setDiffuseColor(Color.BLUE);
			blueMaterial.setSpecularColor(Color.LIGHTBLUE);

			final PhongMaterial redMaterial = new PhongMaterial();
			redMaterial.setDiffuseColor(Color.RED);
			redMaterial.setSpecularColor(Color.LIGHTPINK);

			final PhongMaterial orangeMaterial = new PhongMaterial();
			orangeMaterial.setDiffuseColor(Color.ORANGE);
			orangeMaterial.setSpecularColor(Color.ORANGERED);

			final PhongMaterial yellowMaterial = new PhongMaterial();
			yellowMaterial.setDiffuseColor(Color.YELLOW);
			yellowMaterial.setSpecularColor(Color.LIGHTYELLOW);

			final PhongMaterial greenMaterial = new PhongMaterial();
			greenMaterial.setDiffuseColor(Color.GREEN);
			greenMaterial.setSpecularColor(Color.LIGHTGREEN);

			final PhongMaterial purpleMaterial = new PhongMaterial();
			purpleMaterial.setDiffuseColor(Color.PURPLE);
			purpleMaterial.setSpecularColor(Color.MEDIUMPURPLE);
			int b = 0;
			int n = 0;
			if (yCord.length == xCord.length && yCord.length == zCord.length) {
				for (int i = 0; i < xCord.length; i++) {
					sphereXform = new Xform();
					sphere = new Sphere(nodeSize);
					sphere.setTranslateX(
							xSpacing*10* (Double.parseDouble(yCord[i]) - ((util.getUpper_bound_j() / 2)) - 1));
					sphere.setTranslateY(
							zSpacing*10* (Double.parseDouble(xCord[i]) - ((util.getUpper_bound_T() / 2)) - 1));
					sphere.setTranslateZ(
							ySpacing*10* (Double.parseDouble(zCord[i]) - ((util.getUpper_bound_i() / 2)) - 1));

					if (colorLine[b] == i) {
						switch (n) {
						case (0):
							material = blackMaterial;
							break;
						case (1):
							material = whiteMaterial;
							break;
						case (2):
							material = redMaterial;
							break;

						case (3):
							material = blueMaterial;
							break;

						case (4):
							material = orangeMaterial;
							break;

						case (5):
							material = greenMaterial;
							break;

						case (6):
							material = yellowMaterial;
							break;

						case (7):
							material = purpleMaterial;
							break;
						}

						if (n == 7) {
							n = 0;
						} else {
							n++;
						}
						b++;
					}
					sphere.setMaterial(material);
					sphereXform.getChildren().add(sphere);
					diamondXform.getChildren().add(sphereXform);
				}

			} else {
				errorText.setText("Error with file. \n Make sure data is correct");
			}
			diamondGroup.getChildren().add(diamondXform);
			world.getChildren().add(diamondGroup);
			System.out.println("buildDiamond() complete");
		}

	}

	private boolean getData() {
		String input = "";
		String[] parts;
		if (this.fileLocation != null) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(this.fileLocation));
			} catch (FileNotFoundException e) {
				errorText.setText("Cannot find file");
			}
			StringBuilder sb = new StringBuilder();
			String line = null;
			try {
				line = br.readLine();
				// Read each line in text file
				while (line != null) {
					sb.append(line);
					sb.append("\n");
					line = br.readLine();
				}
				br.close();
			} catch (IOException e) {
				errorText.setText("File is corrupt");
			}

			// String builder to string
			input = sb.toString();
		} else {
			util.generateData();
			input = util.getData();
		}
		// Split at space
		String[] tokens = input.split("[~]+");
		int j = 0;
		int b = 0;
		int x;
		int y;
		int z;
		boolean buffer = true;
		int dataPoints = 0;
		int colorPoints = 0;
		for (int i = 0; i < tokens.length; i++) {
			// check if empty
			if (!tokens[i].isEmpty() || tokens[i] != null) {
				// Check if white Space
				if (tokens[i].length() > 1) {
					tokens[i].replaceAll("\\s+", "");
					tokens[i].replace("\n", "").replace("\r", "");
					tokens[i] = tokens[i].substring(0, tokens[i].length() - 1);
					dataPoints++;
				} else {
					tokens[i] = "~";
					tokens[i].replaceAll("\\s+", "");
					colorPoints++;
				}

			}
		}
		colorLine = new Integer[colorPoints];
		yCord = new String[dataPoints];
		xCord = new String[dataPoints];
		zCord = new String[dataPoints];
		for (int i = 0; i < tokens.length; i++) {
			// check if empty
			if (!tokens[i].isEmpty() || tokens[i] != null) {
				// check if shift color
				if (tokens[i].length() > 1) {
					// split at comma
					parts = tokens[i].split(",");
					// assign new value based on file
					try {
						x = Integer.parseInt(parts[0]);
						y = Integer.parseInt(parts[1]);
						z = Integer.parseInt(parts[2]);

						xCord[j] = Integer.toString(x);
						yCord[j] = Integer.toString(y);
						zCord[j] = Integer.toString(z);
						System.out.print(j + ":");
						System.out.print(Integer.toString(x) + " ");
						System.out.print(Integer.toString(y) + " ");
						System.out.println(Integer.toString(z));

						j++;
						buffer = true;
					} catch (ArrayIndexOutOfBoundsException e) {
						errorText.setText("Data is wrong 1: \n Check file");
						e.printStackTrace();
						return false;
					} catch (NumberFormatException e) {
						errorText.setText("Data is wrong 2: \n Check file");
						e.printStackTrace();
						return false;
					} catch (NullPointerException e) {
						errorText.setText("Data is wrong 3: \n Check file");
						e.printStackTrace();
						return false;
					}
				} else {
					if (buffer) {
						colorLine[b] = j;
						System.out.println(j + ": colorchange " + b);
						b++;
						buffer = false;
					}
				}
			}
		}
		System.out.println("getData() Complete");
		return true;
	}

	private void buildGUI() {
		FileChooser browser = new FileChooser();
		browser.setTitle("Pick Data File");

		// Create Button
		Button btn = new Button("Generate");
		Button btnB = new Button("Browse");

		// Create Text
		Text filePath = new Text();
		Text textBrowser = new Text("Browse for txt file");
		Text xsText = new Text("Spacing x");
		Text ysText = new Text("Spacing y");
		Text zsText = new Text("Spacing z");
		Text textTau = new Text("Tau");
		Text textX = new Text("Max X");
		Text textY = new Text("Max Y");
		Text textT = new Text("Time Steps");
		Text text = new Text("Size of Sphere");
		Text title = new Text("Settings");
		title.setUnderline(true);
		title.setTranslateX(15);
		title.setFont(Font.font(15));
		Text data = new Text("Data Source");

		// Create Text field
		TextField xsField = new TextField();
		TextField ysField = new TextField();
		TextField zsField = new TextField();
		TextField tauField = new TextField();
		TextField xField = new TextField();
		TextField yField = new TextField();
		TextField tField = new TextField();
		TextField numberField = new TextField();
		numberField.setText("5");
		xsField.setText("1");
		ysField.setText("1");
		zsField.setText("1");
		xField.setText("16");
		yField.setText("16");
		tField.setText("10");
		tauField.setText("12");
		errorText.setFill(Color.RED);
		// Create Choice Box
		ChoiceBox<String> cb = new ChoiceBox<String>(FXCollections.observableArrayList("Generate", "From File"));

		// Hide unused elements
		xsText.setVisible(false);
		xsText.setManaged(false);
		ysText.setVisible(false);
		ysText.setManaged(false);
		zsText.setVisible(false);
		zsText.setManaged(false);
		ysField.setVisible(false);
		ysField.setManaged(false);
		zsField.setVisible(false);
		zsField.setManaged(false);
		xsField.setVisible(false);
		xsField.setManaged(false);
		filePath.setVisible(false);
		filePath.setManaged(false);
		textBrowser.setVisible(false);
		textBrowser.setManaged(false);
		tauField.setVisible(false);
		tauField.setManaged(false);
		xField.setVisible(false);
		xField.setManaged(false);
		yField.setVisible(false);
		yField.setManaged(false);
		tField.setVisible(false);
		tField.setManaged(false);

		textTau.setVisible(false);
		textTau.setManaged(false);
		textX.setVisible(false);
		textX.setManaged(false);
		textY.setVisible(false);
		textY.setManaged(false);
		textT.setVisible(false);
		textT.setManaged(false);

		btnB.setVisible(false);
		btnB.setManaged(false);
		text.setVisible(false);
		numberField.setVisible(false);
		btn.setVisible(false);
		text.setManaged(false);
		numberField.setManaged(false);
		btn.setManaged(false);

		// Handle choice box events
		cb.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

				// If Generate selected
				if (newValue.intValue() == 0) {
					fileLocation = null;
					xsText.setVisible(true);
					xsText.setManaged(true);
					ysText.setVisible(true);
					ysText.setManaged(true);
					zsText.setVisible(true);
					zsText.setManaged(true);
					tauField.setVisible(true);
					tauField.setManaged(true);
					xField.setVisible(true);
					xField.setManaged(true);
					yField.setVisible(true);
					yField.setManaged(true);
					tField.setVisible(true);
					tField.setManaged(true);
					ysField.setVisible(true);
					ysField.setManaged(true);
					zsField.setVisible(true);
					zsField.setManaged(true);
					xsField.setVisible(true);
					xsField.setManaged(true);

					textTau.setVisible(true);
					textTau.setManaged(true);
					textX.setVisible(true);
					textX.setManaged(true);
					textY.setVisible(true);
					textY.setManaged(true);
					textT.setVisible(true);
					textT.setManaged(true);

					textBrowser.setVisible(false);
					textBrowser.setManaged(false);

					filePath.setVisible(false);
					filePath.setManaged(false);

					btnB.setVisible(false);
					btnB.setManaged(false);

					text.setVisible(true);
					text.setManaged(true);

					numberField.setVisible(true);
					numberField.setManaged(true);

					btn.setVisible(true);
					btn.setManaged(true);
				}
				// If From File selected
				if (newValue.intValue() == 1) {
					xsText.setVisible(false);
					xsText.setManaged(false);
					ysText.setVisible(false);
					ysText.setManaged(false);
					zsText.setVisible(false);
					zsText.setManaged(false);
					tauField.setVisible(false);
					tauField.setManaged(false);
					textTau.setVisible(false);
					textTau.setManaged(false);
					textX.setVisible(false);
					textX.setManaged(false);
					textY.setVisible(false);
					textY.setManaged(false);
					textT.setVisible(false);
					textT.setManaged(false);
					xField.setVisible(false);
					xField.setManaged(false);
					yField.setVisible(false);
					yField.setManaged(false);
					tField.setVisible(false);
					tField.setManaged(false);

					ysField.setVisible(false);
					ysField.setManaged(false);
					zsField.setVisible(false);
					zsField.setManaged(false);
					xsField.setVisible(false);
					xsField.setManaged(false);

					filePath.setVisible(true);
					filePath.setManaged(true);

					textBrowser.setVisible(true);
					textBrowser.setManaged(true);

					text.setVisible(true);
					text.setManaged(true);

					btnB.setVisible(true);
					btnB.setManaged(true);

					numberField.setVisible(true);
					numberField.setManaged(true);

					btn.setVisible(true);
					btn.setManaged(true);
				}
			}
		});

		browser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("txt", "*.txt"));

		// Handle Button event
		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {

				try {
					errorText.setText("");
					xSpacing= Integer.parseInt(xsField.getText());
					ySpacing= Integer.parseInt(ysField.getText());
					zSpacing= Integer.parseInt(zsField.getText());
					nodeSize = Integer.parseInt(numberField.getText());
					util.setTau(Integer.parseInt(tauField.getText()));
					util.setUpper_bound_i(Integer.parseInt(xField.getText()));
					util.setUpper_bound_j(Integer.parseInt(yField.getText()));
					util.setUpper_bound_T(Integer.parseInt(tField.getText()));
					rootR.getChildren().clear();
					world.getChildren().clear();
					buildAxes();
					buildCamera();
					try {
						buildDiamond();
					} catch (IOException e) {
						errorText.setText("Data is wrong: Check file");
					}
					rootR.getChildren().add(world);
				} catch (NullPointerException e) {
					errorText.setText("Make sure you input all \n settings");
				} catch (NumberFormatException e) {
					errorText.setText("Make sure you input all \n settings");
				}

			}
		});

		btnB.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				File file = browser.showOpenDialog(stage);
				if (null != file) {
					fileLocation = file.getPath();
					filePath.setText(fileLocation);
				}
			}
		});
		rootG.getChildren().add(title);
		rootG.getChildren().add(data);
		rootG.getChildren().add(cb);

		rootG.getChildren().add(textTau);
		rootG.getChildren().add(tauField);
		
		rootG.getChildren().add(textX);
		rootG.getChildren().add(xField);
		rootG.getChildren().add(xsText);
		rootG.getChildren().add(xsField);
		
		rootG.getChildren().add(textY);
		rootG.getChildren().add(yField);
		rootG.getChildren().add(ysText);
		rootG.getChildren().add(ysField);
		
		rootG.getChildren().add(textT);
		rootG.getChildren().add(tField);
		rootG.getChildren().add(zsText);
		rootG.getChildren().add(zsField);

		rootG.getChildren().add(filePath);
		rootG.getChildren().add(textBrowser);
		rootG.getChildren().add(btnB);
		rootG.getChildren().add(text);
		rootG.getChildren().add(numberField);
		rootG.getChildren().add(btn);
		rootG.getChildren().add(errorText);
		rootG.setPadding(new Insets(20));
		rootG.setSpacing(10);
		System.out.println("buildGUI() Complete");
	}

	private void buildAxes() {
		final PhongMaterial redMaterial = new PhongMaterial();
		redMaterial.setDiffuseColor(Color.DARKRED);
		redMaterial.setSpecularColor(Color.RED);

		final PhongMaterial greenMaterial = new PhongMaterial();
		greenMaterial.setDiffuseColor(Color.DARKGREEN);
		greenMaterial.setSpecularColor(Color.GREEN);

		final PhongMaterial blueMaterial = new PhongMaterial();
		blueMaterial.setDiffuseColor(Color.DARKBLUE);
		blueMaterial.setSpecularColor(Color.BLUE);

		final Box xAxis = new Box(AXIS_LENGTH, 1, 1);
		final Box yAxis = new Box(1, AXIS_LENGTH, 1);
		final Box zAxis = new Box(1, 1, AXIS_LENGTH);
		yAxis.setTranslateX(-(10 * ((util.getUpper_bound_j() / 2) + 1)));
		yAxis.setTranslateY(-(10 * ((util.getUpper_bound_T() / 2) + 1)));
		yAxis.setTranslateZ(-(10 * ((util.getUpper_bound_i() / 2) + 1)));

		xAxis.setTranslateX(-(10 * ((util.getUpper_bound_j() / 2) + 1)));
		xAxis.setTranslateY(-(10 * ((util.getUpper_bound_T() / 2) + 1)));
		xAxis.setTranslateZ(-(10 * ((util.getUpper_bound_i() / 2) + 1)));

		zAxis.setTranslateX(-(10 * ((util.getUpper_bound_j() / 2) + 1)));
		zAxis.setTranslateY(-(10 * ((util.getUpper_bound_T() / 2) + 1)));
		zAxis.setTranslateZ(-(10 * ((util.getUpper_bound_i() / 2) + 1)));

		xAxis.setMaterial(redMaterial);
		yAxis.setMaterial(greenMaterial);
		zAxis.setMaterial(blueMaterial);

		axisGroup.getChildren().clear();
		axisGroup.getChildren().addAll(xAxis, yAxis, zAxis);
		axisGroup.setVisible(true);
		world.getChildren().addAll(axisGroup);
		System.out.println("buildAxes() Complete");
	}

	@Override
	public void start(Stage primaryStage) throws IOException, InvocationTargetException {

		SubScene sceneR = new SubScene(rootR, WINDOW_WIDTH, WINDOW_HEIGTH, true, SceneAntialiasing.BALANCED);
		sceneR.setFill(Color.WHITE);

		SubScene sceneG = new SubScene(rootG, 150, 600);
		Scene scene = new Scene(rootW, WINDOW_WIDTH, WINDOW_HEIGTH, true);

		// Set sceneR to take up all of scene basically full screen within group
		sceneG.heightProperty().bind(scene.heightProperty());
		sceneR.widthProperty().bind(scene.widthProperty());
		sceneR.heightProperty().bind(scene.heightProperty());

		// add Scenes to group hierarchy
		rootR.getChildren().add(world);
		rootW.getChildren().add(sceneR);
		rootW.getChildren().add(sceneG);

		// call methods and Application
		buildGUI();
		buildAxes();
		buildCamera();
		handleKeyboard(sceneR, world);
		handleMouse(sceneR, world);

		primaryStage.setTitle("Diamond Visualization");
		primaryStage.setScene(scene);
		primaryStage.show();
		sceneR.setCamera(camera);

	}

	public static void main(String[] args) {
		launch(args);
	}

}
