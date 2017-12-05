import processing.core.PApplet;
import java.util.ArrayList;

public class Draw extends PApplet
{
	public void DrawDrivers(ArrayList driverList, int driverSize, int rotation) {
		//drawing the drivers as white ellipses
		print(driverList.size());
/*		for (int i=0; i<driverList.size(); i++) {
			driver drLook =(driver)driverList.get(i);
//			noFill();
			drLook.dir
			if (drLook.dir)
				drLook.stroke(255);
			else
				drLook.stroke(200);
/*
//			strokeWeight((int)driverSize/17);
			ellipse(drLook.posx, drLook.posy, driverSize, driverSize); //the white driver main circle
			fill(110, 110, 255);
			if (i<10) text(i, drLook.posx-5, drLook.posy+5);
			else text(i, drLook.posx-9, drLook.posy+5);
			for (int p=0; p<8; p++) {
				float slot=p;
				float xx =drLook.posx+( ((driverSize/2)-5) * cos(radians(360*(slot/8))));
				float yy =drLook.posy+( ((driverSize/2)-5) * sin(radians(360*(slot/8))));


				if(drLook.dir)
				{
					fill(222, (255-(drLook.att[(p-rotation+8)%8]*30)), 0);
					stroke(drLook.att[(p-rotation+8)%8]*30);
				}
				else
				{
					fill(222, (255-(drLook.att[(p+rotation+8)%8]*30)), 0);
					stroke(drLook.att[(p+rotation+8)%8]*30);
				}
				strokeWeight((int)driverSize/4);
				point (xx, yy);
				text(drLook.att[p], xx-5, yy+5);
				noFill();
				drLook.pox[drLook.att[p]]=xx;
				drLook.poy[drLook.att[p]]=yy;
				// print(" in:"+p+ " at:"+ drLook.att[p] +" x:"+ round(drLook.pox[p]));
			}*/
		//}
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
