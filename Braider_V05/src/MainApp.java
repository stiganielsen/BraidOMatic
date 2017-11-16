
import processing.core.PApplet;

public class MainApp extends PApplet{

 public static void main(String[] args){
     PApplet.main("MainApp", args);
 }
    PFont metaBold;

    ArrayList driverList;
    ArrayList optPosList;
    ArrayList swiList;
    ArrayList carList;
    int driverSize=100;  //the diameter of a driver
    int closestOptpos=0;
    int selectedModule=0;
    int altModule=0;
    int [] a={0,1,2,3,4,5,6,7};
    float [] ppx={0,1,2,3,4,5,6,7};
    float [] ppy={0,1,2,3,4,5,6,7};
    int rotation = 0;
    int numberOfTracks=12;
    int modulesInTracks=25;
    int [][] tracks = new int [numberOfTracks][modulesInTracks]; //twelve tracks of up to 12 modules (these are used bidirectionally)
    int totalCollisions;


    public void settings() {

        size(900, 900);
        metaBold = loadFont("fontMS.vlw");
        textFont(metaBold, 40);

    public void setup() {
        driverList    = new ArrayList();
        optPosList    = new ArrayList();
        swiList       = new ArrayList();
        carList       = new ArrayList();
        //add one driver to start from

        driverList.add(new driver (1,true, 300, 300,a,ppx,ppy));
        //  swiList.add(new swi(5, 9, 10, 0, 0, 5, true, 0, 0));

        textSize(16);
        LoadTracks();


    }
    public void draw() {
        ellipseMode(CENTER);
        background(0);
        fill(0);
        rect(0, 0, 600, 600);
        DrawDrivers();
        SelectClosestModule();
        SuggestDriverLocation(selectedModule);
        DrawNearestOptionalModule();
        DrawCurrentSelectedModule();
        DrawSwitches();
        LocateCars();
        DrawCars();
        Collision();
    }




    void mouseReleased() {
        if (mouseButton==RIGHT && driverList.size()-1!=selectedModule) { //if its a right mousebutton then the selected module is deleted
            driverList.remove(selectedModule);
        }
        if (mouseButton==LEFT) {
            DropInDriverModule();
        }
    }

    void keyPressed() {

        if (key=='r'||key=='R') {

            //  updateCars();
            RotateOne();
            StepCarInModulesAndSetSwitches();

            EndTrackHandle();
            StepCarInDriver();
        }
        if(key=='p'|| key=='P'){
            ListSwitches();
        }
        if(key=='s'|| key=='S'){
            RecreateSwitches();
        }
        if(key=='i'|| key=='I'){
            InsertCars();
        }
    }
}