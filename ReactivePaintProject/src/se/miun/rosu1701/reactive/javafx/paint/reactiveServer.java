package se.miun.rosu1701.reactive.javafx.paint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import io.reactivex.Observable;
import javafx.application.Application;
import javafx.scene.shape.Shape;
import reactor.blockhound.BlockHound;
import javafx.scene.shape.Rectangle;

public class reactiveServer {
	
	// Observable storing the most recent received shape
	private Observable<CustomDrawing> incomingDrawing;
	// Observable for recent connection to the server
	private Observable<Connection> connectionOb;
	private Server server;
	//CustomDrawing testDrawing;
	// ConccurentHashMap for storing the drawings in a unique manner
	private Observable<ConcurrentHashMap<String,CustomDrawing>> listOfDrawings;
	reactiveServer()
	{
		
		
		listOfDrawings = Observable.just(new ConcurrentHashMap<String,CustomDrawing>());
	}
	
	private void createCustomDrawingObservable()
	{
		// Create a listener for receiving objects from the client
		// Make incomingDrawing the received drawing
		incomingDrawing = Observable.create(source -> server.addListener(new Listener() {
		    @Override  
			public void received (Connection connection, Object object) {
		    	
		         if(object instanceof CustomDrawing)
		         {
		       
		        	 CustomDrawing drawing = (CustomDrawing)object;
		        	 if(drawing.ready())
		        	 source.onNext(drawing);
		         }
		       }
		      }
		));
		// Send the received drawing to all connected clients
		incomingDrawing.subscribe(server::sendToAllTCP);
		// Add the new drawing to the collection of received drawings
		incomingDrawing.subscribe(e -> {
			
			listOfDrawings.subscribe(drawing -> {
				drawing.put(e.getID(), e);
			});
		});
	}
	
	// Create a listener for when clients connects to the server
	private void createConnectionObservable()
	{
		connectionOb = Observable.create(source -> server.addListener(new Listener() {
			@Override
			public void connected(Connection connection) {
				source.onNext(connection);
			
			}
		}
		));
		// Sends all previous drawings to the newly connected client
		connectionOb.subscribe(e -> {
			listOfDrawings.subscribe(drawing -> {
		       drawing.forEach((k,v) -> {
		    	   
		    	   e.sendTCP(v);
		       
		        					});
		    	});
		    
		     
		});

	}
	

	public void startServer() throws IOException
	{
		
		
		server = new Server();
		server.getKryo().register(CustomDrawing.class);
		server.start();
		server.bind(54555, 54777);
		
		
		createCustomDrawingObservable();
		createConnectionObservable();
	
		System.out.println("Starting server");
	}
	
	

	
	public static void main(String[] args) {
		BlockHound.install();
		reactiveServer server = new reactiveServer();
		
		try {
			server.startServer();
			while(true) {}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
