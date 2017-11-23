import processing.core.PApplet;
import processing.core.PFont;
import java.util.*;
public class MainApp extends PApplet
{
	public driver Driver = new driver();

	private PFont metaBold;
	private ArrayList driverList;
	private ArrayList optPosList;
	private ArrayList swiList;
	private ArrayList carList;
	private int driverSize=100;  //the diameter of a driver
	private int closestOptpos=0;
	private int selectedModule=0;
	private int altModule=0;
	public int[] a={0, 1, 2, 3, 4, 5, 6, 7};
	public float[] ppx={0, 1, 2, 3, 4, 5, 6, 7};
	public float[] ppy={0, 1, 2, 3, 4, 5, 6, 7};
	public int rotation=0;
	private int numberOfTracks=12;
	private int modulesInTracks=25;
	public int[][] tracks=new int[numberOfTracks][modulesInTracks]; //twelve tracks of up to 12 modules (these are used bidirectionally)
	public int totalCollisions;

	public void settings(){
		size(900, 900);
		metaBold=loadFont("fontMS.vlw");
		try
		{
			textFont(metaBold);
		}
		catch(Exception e)
		{
			println(e);
		}
	}

	public void draw(){
		ellipseMode(CENTER);
		background(0);
		fill(0);
		rect(0, 0, 600, 600);
		Driver.DrawDrivers();
		//SelectClosestModule();
		//SuggestDriverLocation(selectedModule);
		//DrawNearestOptionalModule();
		//DrawCurrentSelectedModule();
		//DrawSwitches();
		//LocateCars();
		//DrawCars();
		//Collision();
	}

	public void setup()
	{
		driverList = new ArrayList();
		optPosList = new ArrayList();
		swiList = new ArrayList();
		carList = new ArrayList();
		//add one driver to start from
		//driverList.add(new driver(1, true, 300, 300, a, ppx, ppy));
		//  swiList.add(new swi(5, 9, 10, 0, 0, 5, true, 0, 0));
		textSize(16);
		//LoadTracks();
	}

	public void keyPressed()
	{
		if (key == 'r' || key == 'R') {
			//  updateCars();
			//RotateOne();
			//StepCarInModulesAndSetSwitches();

			//EndTrackHandle();
			//StepCarInDriver();
		}
		if (key == 'p' || key == 'P') {
			//ListSwitches();
		}
		if (key == 's' || key == 'S') {
			//RecreateSwitches();
		}
		if (key == 'i' || key == 'I') {
			//InsertCars();
		}
	}
	public void mouseReleased()
	{
/*		if (mouseButton == RIGHT && driverList.size() - 1 != selectedModule)
		{
			//if its a right mousebutton then the selected module is deleted
			//driverList.remove(selectedModule);
		}*/
		if (mouseButton == LEFT)
		{
			//DropInDriverModule();
		}
	}

	public static void main(String[] args)
	{
		PApplet.main("MainApp");
	}
}