package se.miun.rosu1701.reactive.javafx.paint;


import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

@SuppressWarnings("restriction")
public interface CustomShapes{

	 void start(double x, double y);
	
	 void combine(double x, double y);
	
	 void draw(GraphicsContext grapCont);
	
	 void drawFreehand(double x, double y);
	 
	 String getType();
		
}

