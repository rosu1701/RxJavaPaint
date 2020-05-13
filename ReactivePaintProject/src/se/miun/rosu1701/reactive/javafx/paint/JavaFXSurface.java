package se.miun.rosu1701.reactive.javafx.paint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import io.reactivex.Observable;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import javafx.beans.value.*;


@SuppressWarnings("restriction")
public class JavaFXSurface extends Application {
	
	CustomDrawing drawing;
	Observable<CustomDrawing> obDrawing = Observable.just(new CustomDrawing());
	//ObservableValue<CustomDrawing> obDrawing;
	
	// Unique client id
	final static String clientID = UUID.randomUUID().toString();
	// Shape number, will be incremented and added to the client id for every new shape
	int shapeID = 0;
	// Canvas for drawing
	Canvas canvas = new Canvas(800,800);
	GraphicsContext grapCont;
	// ColorPicker for "pen"
	ColorPicker coloPick = new ColorPicker();
	
	StackPane pane = new StackPane();
	Scene scene = new Scene(pane,800,800);
	// Used for line thickness adjustments
	Slider slider = new Slider();
	// Label for slider. Default value is default width of grapCont
	Label sliderThickness = new Label("1.0");
	
	// GridPane for topPlacement
	GridPane gridPane = new GridPane();
	// pane for options
	GridPane leftGrid = new GridPane();
	// Menu selections for rectangle, straight line, ovals and freehand
	VBox options = new VBox();
	Observable<VBox> optionsObs;
	
	// Observables for mouse events. will fire Everytime an event of that type happends
	Observable<MouseEvent> mousePressedObserver;
	Observable<MouseEvent> mouseHoldObserver;
	Observable<MouseEvent> mouseReleasedObserver;
	
	// Observable of all the shapes being used
	Observable<CustomShapes> shapes;
	// The shape currently being used
	
	CustomShapes currentShape = new CustomRect();
	
	@SuppressWarnings("restriction")
	// Button Obeseravle
	// Togglegroup for keeping track of the toggles for the selection of shapes. Only one toggle at the time can be activated
	ToggleGroup toggleGroup = new ToggleGroup();
	// Observable for the actual togglebuttons
	Observable<ToggleButton> optionsObservable;
	// Observable for clear button
	Observable<Button> clearButton;
	
	// Serverconnection
	Client client;
	@Override
	public void start(Stage stage) throws Exception {
		
		
		
		try
		{
		// Create the GUI
		grapCont = canvas.getGraphicsContext2D();
		// default pen-color is black
		grapCont.setStroke(Color.BLACK);
		// Default line thickness
		grapCont.setLineWidth(1);
		
		// Initialize current shape. Will listen for changes in toggle and
		// set the current shape to the selected toggletype.
		currentShapeIni();
		// Initializes the custom shapes which will be drawn.
		InitializeShapes();
		// Initialize the toggleButtons, set the toggle group and adds them to the VBox.
		// Will also set actionEvent for clearbutton and add it to the VBox
		InitializeButtonsObs();
		// Initialize the mouseEventObservables to do actions on the current shape selected
		InitializeMouseEvents();
		// Creates Menu out of VBox
		createMenu();
		// Initializes the left grid containing the VBox
		InitializeLeftGrid();
		
		// Used for changing color. Can perhaps be changed to Observable in the future
		coloPick.setValue(Color.BLACK);
		coloPick.setOnAction(c->{
			grapCont.setStroke(coloPick.getValue());
		});
		

		
		// Set slider values. Slider can perhaps be changed to Observable in the future
		slider.setMin(1);
		slider.setMax(100);
		slider.setShowTickLabels(true);
		slider.setShowTickMarks(true);
		// Set listener to lineThickness
		slider.valueProperty().addListener(s -> {
			double thickness = slider.getValue();
			// String for value that is restricted to 1 decimal
			String strThick = String.format("%.1f", thickness);
			// Set labeltext
			sliderThickness.setText(strThick);
			//Set thickness
			grapCont.setLineWidth(thickness);
		});
		// Adds a pane to the top
		gridPane.addRow(0,coloPick,slider,sliderThickness);
		// Set gap
		gridPane.setHgap(20);
		// Set pane position
		gridPane.setAlignment(Pos.TOP_CENTER);
		// Set padding
		gridPane.setPadding(new Insets(10,10,10,10));
		
		pane.getChildren().addAll(canvas,leftGrid);//leftGrid
		
		stage.setTitle("Reactive Paint for Real Artists! RPRA");
		stage.setScene(scene);
		stage.show();
		
		// Testing server connection
		connectToServer();
		}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		
	}
		
	/* Connect to server
	 
	 * */
	public void connectToServer()
	{
		// Create new client
		 client = new Client();
		// make CustomDrawing serializeable
		 client.getKryo().register(CustomDrawing.class);
		
		 // Add listener to client.
		 // Draws the received drawings
		 client.addListener(new Listener() {
			 @Override
		       public void received (Connection connection, Object object) {
		    	   System.out.println("OBJECT: " + object);
		    	      if(object instanceof CustomDrawing)
		    		   {
		    		   
		    		  CustomDrawing drawing = (CustomDrawing) object;
		    		  obDrawing.subscribe(e-> e = drawing);
		    		 
		    	//	   toggleToggleButton(drawing.getType());
		    		   // Set current shape to the shape of the received drawing
		    		   // Set thickness and color so the received shape matches the sent
		    		    grapCont.setLineWidth(drawing.getThickness());
		    		    grapCont.setStroke(Color.valueOf(drawing.getColor()));
		    		     Observable<CustomShapes> receivedShape = Observable.create(s ->{
		    			   switch(drawing.getType())
		    			   {
		    			   case "OVAL": {s.onNext(new CustomOval());}
		    			   break;
		    			   case "RECT": {s.onNext(new CustomRect());}
		    			   break;
		    			   case "FREE": {s.onNext(new CustomFreehand(grapCont));}
		    			   break;
		    			   case "LINE":{s.onNext(new CustomLine());}
		    			   break;
		    			   default: {s.onNext(new CustomRect());}
		    			   }
		    			   s.onComplete();
		    		   });
		    		   
		    		   receivedShape.subscribe(e-> {
		    			   e.start(drawing.getStartX(), drawing.getStartY());
		    			   e.combine(drawing.getEndX(),drawing.getEndY());
		    			   if(!e.getType().equals("FREE"))
		    				   e.draw(grapCont);
		    			   else
		    				   e.drawFreehand(drawing.getEndX(), drawing.getEndY());
		    		   });
		    		
		    		   
			         } 
			         
		       }   
		    });
		 
		 
		 // Start client and then connect to server
		 client.start();
		 
		 try {
			client.connect(5000, "127.0.0.1", 54555, 54777);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Could not connect to server! ");
		}
		 
		 
		
		
		
	}
	
	
		/*
		 * Vbox menu 
		 */
	 	private void createMenu()
	 	{
	 	
	 		options.setPadding(new Insets(5));
	 		options.setStyle("-fx-backgorund-color: #100");
	 		options.setPrefWidth(100);
	 		
	 	} 
		
	
	 	/*
	 	 * Initializes leftgrid which will have the gridPane Object.
	 	 * This is then used for the different options such as VBox item, color and size.
	 	 * */
	 	private void InitializeLeftGrid()
	 	{
	 		// Adds a pane to the top
			leftGrid.addRow(0,options,this.gridPane);
			// Set gap
			leftGrid.setHgap(20);
			// Set pane position
			leftGrid.setAlignment(Pos.TOP_LEFT);
			// Set padding
			leftGrid.setPadding(new Insets(10,10,10,10));
	 	}
	 	
	 	/*
	 	 * Initializes the currentShape Observable.
	 	 * Everytime the toggle changes so will the currentShape.
	 	 * 
	 	 * */
	 	public void currentShapeIni()
	 	{
	 		JavaFxObservable.valuesOf(toggleGroup.selectedToggleProperty()).map(t -> 
	 		{
	 			List<CustomShapes> list = shapes.toList().blockingGet();
	 		
	 			
	 			return list.get(toggleGroup.getToggles().indexOf(t));
	 			 
	 		}).subscribe(e->currentShape =  e);
	 	}
	 	
	 	public void currentReceivedDrawing()
	 	{
	 		JavaFxObservable.valuesOf(obDrawing)
	 		.subscribe(System.out::println);
	 	}
	 	
	 	/*
	 	 * Initializes the mouseEvent Observables.
	 	 * Everytime the mouse does one of the three actions
	 	 * the currentShape object will be manipulated.
	 	 * 
	 	 * */
	 	private void InitializeMouseEvents()
	 	{
	 		
	 		mousePressedObserver = JavaFxObservable.eventsOf(pane, MouseEvent.MOUSE_PRESSED);
	 		mouseHoldObserver = JavaFxObservable.eventsOf(pane, MouseEvent.MOUSE_DRAGGED);
	 		mouseReleasedObserver = JavaFxObservable.eventsOf(pane, MouseEvent.MOUSE_RELEASED);
	 		
	 		mousePressedObserver.subscribe(e-> {
	 			currentShape.start(e.getX(), e.getY());
	 			drawing = new CustomDrawing(e.getX(), e.getY(),currentShape.getType(),clientID + Integer.toString(shapeID),slider.getValue(),coloPick.getValue().toString());
	 			//client.sendTCP(drawing);
			});
	 		
	 		mouseHoldObserver.subscribe(e-> {
	 			if(currentShape.getType().equals("FREE"))
	 			{
	 			currentShape.drawFreehand(e.getX(), e.getY());
	 			//drawing.setStart(e.getX(), e.getY());
	 			drawing.setEnd(e.getX(), e.getY());
	 			drawing.setStart(e.getX(), e.getY());
	 			client.sendTCP(drawing);
	 			}
	 		});
	 		
	 		mouseReleasedObserver.subscribe(e-> {
	 			currentShape.combine(e.getX(), e.getY());
	 			currentShape.draw(grapCont);
	 			drawing.setEnd(e.getX(), e.getY());
	 			client.sendTCP(drawing);
	 			System.out.println("Color: " + coloPick.getValue().toString());
	 			// increment shape id from this client
	 			shapeID++;
	 		});
	 	
	 	}
	 	
	 	
	 	/*
	 	 * Creates the ToggleButtons, adds them to the ToggleGroup and then to the VBox.
	 	 * Will also create the Clear button, set the event to use and add it to the VBox.
	 	 * 
	 	 * */
	 	public void InitializeButtonsObs()
	 	{
	 		optionsObservable = Observable.just(new ToggleButton("RECT"),
	 				new ToggleButton("OVAL"),
	 				new ToggleButton("LINE"),
	 				new ToggleButton("FREE"));
			
	 		
	 		optionsObservable.subscribe(s -> {s.setToggleGroup(toggleGroup);
	 			options.getChildren().add(s);
	 		
	 		});
	 		
	 		clearButton = Observable.just(new Button("CLEAR"));
	 		
	 		clearButton.subscribe(e -> {
	 			
	 			e.setOnAction(new EventHandler<ActionEvent>()
 				{

					@Override
					public void handle(ActionEvent event) {
	 			
						grapCont.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
					}
 				});
 				options.getChildren().add(e);
	 		});
	 		
	 	
	 		
	 		
	 		
	 	}
	 	
	 	/*
	 	 * Initializes the Shapes for the shapes Observable.
	 	 * The Shapes Used are CustomShapes designed to draw themself's given a GraphicContext
	 	 * */
	 	private void InitializeShapes()
	 		{
	 			shapes = Observable.just(new CustomRect(),new CustomOval(), new CustomLine(),new CustomFreehand(grapCont));
	 				
	 		}
	 	
	 	
	
	 	/*
	 	 * Returns an Observable of the CurrentShape selected By taking the index of
	 	 * the toggled button and takes the shape from shapes according to that index.
	 	 * Will only work if ToggleGroup and shapes are the same size and initialized in the
	 	 * same order. Could perhaps be a better way of doing this.
	 	 * If ToggleGroup is bigger there is a change for a IndexOutOfBoundsException
	 	 * If They are initialized in a different order the shape will not be what is expected.
	 	 * */
	 	public Observable<CustomShapes> getSelectedShape()
	 	{
	 		return JavaFxObservable.valuesOf(toggleGroup.selectedToggleProperty()).map(t -> 
	 		{
	 			List<CustomShapes> list = shapes.toList().blockingGet();
	 		
	 			
	 			return list.get(toggleGroup.getToggles().indexOf(t));
	 			 
	 		});
	 	}
	 	
	 	
	 	/*
	 	 *Returns an Observable to the currently selected ToggleButton from ToggleGroup
	 	 */
	 	public Observable<ToggleButton> getToggle()
	 	{
	 		return JavaFxObservable.valuesOf(toggleGroup.selectedToggleProperty()).map(t -> 
	 		{
	 			return (ToggleButton)t;
	 		});
	 	}
	 	
	 	
	 	public void toggleToggleButton(String type)
	 	{
	 		
	 		optionsObservable.subscribe(e-> {
	 			
	 			if(e.getText().equals(type))
	 			{
	 				
	 				e.setSelected(true);
	 				return;
	 			}
	 		});
	 	}
	 
	 	
}
