public class NeighbourModules
{
	void FindNeighbourModules(int lookFrom)
	{
		//lists and prints the modules that are neighbours to the input 'lookFrom' module
		driver drFrom = (driver) driverList.get(lookFrom);
		int place=8;
		for (int i=0; i<driverList.size (); i++)
		{
			driver drLook = (driver) driverList.get(i); //later get a specific clicked one...
			float distance = dist(drFrom.posx, drFrom.posy, drLook.posx, drLook.posy);
			if (driverSize-1<distance && distance<driverSize+1)
			{ //means it is within range .. now find which position
				float swiposx=drFrom.posx;//(drFrom.posx+drLook.posx)/2;
				float swiposy=drFrom.posy;//(drFrom.posy+drLook.posy)/2;
				float lgtDia=sqrt((driverSize*driverSize)*2);

				if (drFrom.posx+1<drLook.posx)
				{
					//this tells us the drLook is to the right somewhere
					if (drFrom.posy+1<drLook.posy)
						place=1; //down right
					else if (drFrom.posy-1>drLook.posy)
						place=7; //up right
					else
						place=0; //straight right
				}
				else if (drFrom.posx-1>drLook.posx)
				{
					if (drFrom.posy+1<drLook.posy)
						place=3; //Down left
					else if (drFrom.posy-1>drLook.posy)
						place=5; //Up left
					else
						place=4; //Straight left
				}
				else if (drFrom.posy<drLook.posy)
					place= 2; //Stright below
				else
					place= 6; //straight above

				int swiPos=drLook.att[(place+4)%8];
				//println("module "+lookFrom+" connects to "+i+" at its "+swiPos+" pos");
				//find location between lookFrom and drLook
				switch (place)
				{
					//depending where the neigbour module is connected (placed) and what direction the driver is turning, then a switch is placed.
					case 0:
						if (drFrom.dir)
						{
							swiposx+=driverSize/2;
							swiposy+=driverSize/2;
						}
						else
					    {
							swiposx+=driverSize/2;
							swiposy-=driverSize/2;
						}
						break;
					case 1: //other is down right
						if (drFrom.dir)
							swiposy+=lgtDia/2;
						else
							swiposx+=lgtDia/2;
						break;
					case 2: //Other drv is straight below
						if (drFrom.dir)
						{
							swiposx-=driverSize/2;
							swiposy+=driverSize/2;
						}
						else
					    {
							swiposx+=driverSize/2;
							swiposy+=driverSize/2;
						}
						break;
					case 3: //other drv is down left
						if (drFrom.dir)
							swiposx-=lgtDia/2;
						else
							swiposy+=lgtDia/2;
						break;
					case 4: //other is straight to the LEFT
						if(drFrom.dir)
						{
							swiposx-=driverSize / 2;
							swiposy-=driverSize / 2;
						}
						else
						{
							swiposx-=driverSize / 2;
							swiposy+=driverSize / 2;
						}
						break;
					case 5: //other is UP LEFT
						if(drFrom.dir)
							swiposy-=lgtDia / 2;
						else
							swiposx-=lgtDia / 2;
						break;
					case 6: //other is Straight UP
						if (drFrom.dir)
						{
							swiposx+=driverSize/2;
							swiposy-=driverSize/2;
						}
						else
						{
							swiposx-=driverSize/2;
							swiposy-=driverSize/2;
						}
						break;
					case 7: //other is UP right
						if(drFrom.dir)
							swiposx+=lgtDia / 2;
						else
							swiposy-=lgtDia / 2;
						break;
				}
				boolean add=true; //checks whether to add the switch later or if its already added to another switch
				for (int s=0; s<swiList.size(); s++)
				{
					swi swiLook = (swi) swiList.get(s);
					if (dist(swiLook.posx,swiLook.posy,swiposx,swiposy)<1)
					{
						//if the difference in location for x and y is smaller than 1
						add=false;

						if (drFrom.dir)
						{
							swiLook.bmoR=lookFrom;
							swiLook.bmoL=i ;
						}
						else
						{
							swiLook.bmoR=i;
							swiLook.bmoL=lookFrom;
						}
						println("COMBINING to swi: "+s+" bmod(RL): "+i+","+lookFrom);
					}
				}
				if (add)
				{
					if (drFrom.dir)
					{
						swiList.add(new swi(lookFrom, i, -1, -1, swiPos, true, swiposx, swiposy));
					}
					else
					{
						swiList.add(new swi(i, lookFrom, -1, -1, swiPos, true, swiposx, swiposy));
					}
				}
			}
		}
	}
}
