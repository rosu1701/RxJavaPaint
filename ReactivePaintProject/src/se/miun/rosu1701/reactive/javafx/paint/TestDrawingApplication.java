package se.miun.rosu1701.reactive.javafx.paint;

import javafx.application.Application;
import reactor.blockhound.BlockHound;

public class TestDrawingApplication {

	public static void main(String[] args) {
		// Testing Blocking violations
		
		// Run GUI
	BlockHound.install();	
	System.out.println("Main working!");
	Application.launch(JavaFXSurface.class,args);
	}

}
