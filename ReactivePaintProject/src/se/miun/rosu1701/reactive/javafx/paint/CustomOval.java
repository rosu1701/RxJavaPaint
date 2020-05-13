package se.miun.rosu1701.reactive.javafx.paint;


import javafx.scene.shape.Ellipse;

import javafx.scene.canvas.GraphicsContext;

@SuppressWarnings("restriction")
public class CustomOval implements CustomShapes{
	
	Ellipse elipse = new Ellipse();
	public void start(double x, double y)
	{
		elipse.setCenterX(x);
		elipse.setCenterY(y);
	}
	
	public String getType()
	{
		return "OVAL";
	}
	
	public void combine(double x, double y)
	{
		elipse.setRadiusX(Math.abs(x - elipse.getCenterX()));
		elipse.setRadiusY(Math.abs(y - elipse.getCenterY()));
        
        if(elipse.getCenterX() > x) {
        	elipse.setCenterX(x);
        }
        if(elipse.getCenterY() > y) {
        	elipse.setCenterY(y);
        }
	}
	public void draw(GraphicsContext grapCont)
	{
		System.out.println("Drawing OVAL");
		grapCont.strokeOval(elipse.getCenterX(), elipse.getCenterY(), elipse.getRadiusX(), elipse.getRadiusY());
	}
	
	public void drawFreehand(double x, double y)
	{
		
	}

}
