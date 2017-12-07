import processing.core.PApplet;
import java.util.ArrayList;

public class Draw
{
	PApplet parent;
	Draw(PApplet p)
	{
		parent = p;
	}
	//Select the closest module from the driverList related to the mouse position
	int SelectClosestModule(ArrayList<driver> driverList)
	{
		float distance=10000;
		float distanceNear=10000;
		int closestMod=0;
		for (int i =0; i<driverList.size (); i++)
		{
			driver drLook = driverList.get(i);
			distance= PApplet.dist(parent.mouseX,parent.mouseY,drLook.posx,drLook.posy-drLook.size);
			if (distanceNear>distance)
			{
				distanceNear=distance;
				closestMod=i;
			}
		}
		return closestMod;
	}

	void SuggestDriverLocation(int current, ArrayList<driver> driverList,ArrayList<optPos> optPosList)
	{
		//keeps a list of optional locations, starting from x,0 counting clockwise
		optPosList.clear();
		//get the location of the driver in question -the last driver - and later on any driver
		driver drLook = driverList.get(current); //TODO later get a specific clicked one...
		//just finding the radial distance to surrounding center points.
		float dist= parent.sqrt ( (drLook.size/2)*(drLook.size/2)+(drLook.size/2)*(drLook.size/2));
		optPosList.add(new optPos(drLook.posx+drLook.size, drLook.posy, true, true)); //0
		optPosList.add(new optPos(drLook.posx+dist, drLook.posy+dist, true, true)); //1
		optPosList.add(new optPos(drLook.posx, drLook.posy+drLook.size, true, true));//2
		optPosList.add(new optPos(drLook.posx-dist, drLook.posy+dist, true, true));//3
		optPosList.add(new optPos(drLook.posx-drLook.size, drLook.posy, true, true));//4
		optPosList.add(new optPos(drLook.posx-dist, drLook.posy-dist, true, true));//5
		optPosList.add(new optPos(drLook.posx, drLook.posy-drLook.size, true, true)); //6
		optPosList.add(new optPos(drLook.posx+dist, drLook.posy-dist, true, true));//7
	}

	//Highlight nearest optional place in GREEN color or RED if not viable location
	void DrawNearestOptionalModule()
	{
		float distance=10000;
		float distanceNear=10000;
		parent.strokeWeight(3);
		parent.stroke(255);
		for (int i = 0; i<optPosList.size (); i++) {
			optPos opLook = (optPos) optPosList.get(i);

			for (int j = 0; j<driverList.size (); j++) {
				driver drLook = (driver) driverList.get(j);
				float distClose = PApplet.dist(opLook.posx, opLook.posy, drLook.posx, drLook.posy);
				if (distClose<1) {
					opLook.taken=false;
				}
				if (distClose<driverSize) opLook.viable=false; //if another module is too close, the place is not viable
			}

			distance= PApplet.dist(parent.mouseX, parent.mouseY, opLook.posx, opLook.posy-driverSize);
			if (distanceNear>distance) {
				distanceNear=distance;
				closestOptpos=i;
			}
		}
		optPos opNear = (optPos) optPosList.get(closestOptpos);
		if (opNear.taken && opNear.viable)
			parent.stroke(0, 200, 0);
		else
			parent.stroke(222, 0, 0);
		parent.ellipse(opNear.posx, opNear.posy, driverSize, driverSize);
	}

/*
	public void DropInDriverModule()
	{
		optPos opPlace = (optPos) optPosList.get(closestOptpos);
		int []at={0, 0, 0, 0, 0, 0, 0, 0}; //setup an array for ording the positions on the driver wheel being dropped
		float []ppx={0, 0, 0, 0, 0, 0, 0, 0};
		float []ppy={0, 0, 0, 0, 0, 0, 0, 0};

		if (opPlace.viable) {
			driver drSele = (driver) driverList.get(selectedModule); //fetch the selected/'building upon' module
			boolean directionThisMod=true;
			int starta= drSele.att[closestOptpos];  //this is the previous drivers position that we attach to.
			println("starta: "+starta);
			if (drSele.dir) directionThisMod=false; //switch direction of the next module if the drSele is opposite.
			if (directionThisMod) {  //turning CV so the next module must be numbered oppositely direction CCV
				println("direction of this module is TRUE therefore going CV");
				for (int a=0; a<8; a++)
				{
					at[(closestOptpos+a+4)%8]=(a+starta)%8;
				}
			}
			else
			{
				println("direction of This module is FALSE going CCV now");
				int cnt=0;
				for (int a=8; a>0; a--)
				{
					at[(closestOptpos+a+4) % 8] = (((starta-a) % 8)+8)%8 ;//(starta-cnt)%8 ;
					cnt++;
				}
			}
			println(at);
			driverList.add(new driver (1, directionThisMod, opPlace.posx, opPlace.posy, at,ppx,ppy));
			driver drTjek = (driver) driverList.get(driverList.size()-1); //fetch the selected/'building upon' module
			for (int i =0; i<8; i++) {
				println("abspos: "+i+" pos:"+drTjek.att[i]);
			}
			FindNeighbourModules(driverList.size()-1);  //calling the switch placer on the latest module
		} else {
			println("This location is not allowed");
			println("selected Module is now: "+altModule);
		}
	}*/
}
