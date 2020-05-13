package se.miun.rosu1701.reactive.javafx.paint;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Line;

@SuppressWarnings("restriction")
public class CustomLine implements CustomShapes{

	private Line line = new Line();
	
	public void start(double x, double y)
	{
		line.setStartX(x);
        line.setStartY(y);
	}
	
	public String getType()
	{
		return "LINE";
	}
	
	public void combine(double x, double y)
	{
		 line.setEndX(x);
         line.setEndY(y);
	}
	
	public void draw(GraphicsContext grapCont)
	{
		 System.out.println("Drawing LINE");
		 grapCont.strokeLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY());
	}
	
	public void drawFreehand(double x, double y)
	{
		
	}
}
