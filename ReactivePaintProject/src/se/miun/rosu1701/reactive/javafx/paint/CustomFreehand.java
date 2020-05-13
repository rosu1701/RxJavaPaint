package se.miun.rosu1701.reactive.javafx.paint;

import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.geometry.Point2D;
import javafx.scene.shape.StrokeLineCap;

@SuppressWarnings("restriction")
public class CustomFreehand implements CustomShapes {

	private GraphicsContext grapCont;
	private ArrayList<Point2D> list = new ArrayList<Point2D>();
	public CustomFreehand(GraphicsContext grapCont)
	{
		this.grapCont = grapCont;
	}
	
	public String getType()
	{
		return "FREE";
	}
	
	public void start(double x, double y)
	{
		grapCont.setLineCap(StrokeLineCap.ROUND);
		grapCont.beginPath();
		grapCont.lineTo(x, y);
		grapCont.stroke();
		list.add(new Point2D(x,y));
	}
	public void combine(double x, double y)
	{
		
	}
	
	public void draw(GraphicsContext grapCont)
	{
		
	}
	
	public void drawFreehand(double x, double y)
	{
		grapCont.lineTo(x, y);
		grapCont.stroke();
		list.add(new Point2D(x,y));
		System.out.println("Drawing FREE");
	}
	
	public ArrayList<Point2D> getArray()
	{
		return this.list;
	}
}
