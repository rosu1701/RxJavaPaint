package se.miun.rosu1701.reactive.javafx.paint;

import javafx.scene.shape.Shape;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.canvas.GraphicsContext;

@SuppressWarnings("restriction")
public class CustomRect implements CustomShapes{

	private Rectangle rect = new Rectangle();
	public void start(double x, double y)
	{
		rect.setX(x);
		rect.setY(y);
	}
	
	public String getType()
	{
		return "RECT";
	}
	public void combine(double x, double y)
	{
		rect.setWidth(Math.abs((x - rect.getX())));
		rect.setHeight(Math.abs((y - rect.getY())));
		
		if(rect.getY() > y)
		{
			rect.setY(y);
		}
		if(rect.getX() > x)
		{
			rect.setX(x);
		}
	}
	
	public void draw(GraphicsContext grapCont)
	{
		System.out.println("Drawing RECT");
		grapCont.strokeRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
	}

	public void drawFreehand(double x, double y)
	{
		
	}
}

