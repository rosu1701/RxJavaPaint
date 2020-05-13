package se.miun.rosu1701.reactive.javafx.paint;


/*
 * @Class CustomDrawing
 * 
 * Stores all neccessary information for recreating a shape
 * 
 * Used for sending shape information between client and server
 * */
public class CustomDrawing {
	private double startX,startY;
	private String type;
	private double endX,endY;
	private String id;
	private boolean done = false;
	private double thickness;
	private String color;
	public CustomDrawing()
	{
		
	}
	public CustomDrawing(double x, double y, String type, String id, double thick,String color)
	{
		startX = x;
		startY = y;
		this.id = id;
		this.type = type;
		thickness = thick;
		this.color = color;
	}
	
	public void setEnd(double x, double y)
	{
		endX = x;
		endY = y;
		done = true;
	}
	
	public void setStart(double x, double y)
	{
		startX = x;
		startY = y;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}
	
	public void setStart(String id)
	{
		this.id = id;
	}
	
	public boolean ready()
	{
		return done;
	}
	
	public String getID()
	{
		return id;
	}
	
	public double getStartX()
	{
		return startX;
	}
	
	public double getStartY()
	{
		return startY;
	}
	
	public double getEndX()
	{
		return endX;
	}
	
	public double getEndY()
	{
		return endY;
	}
	
	public String getType()
	{
		return type;
	}
	
	public String getColor()
	{
		return color;
	}
	
	public double getThickness()
	{
		return thickness;
	}
}
