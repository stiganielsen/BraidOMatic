import processing.core.PApplet;
public class driver
{
	public int mod; //number
	public boolean dir;
	public float posx;//position
	public float posy;
	public int [] att;//= new int[8];
	public float [] pox;//=new float[8];
	public float [] poy;//=new float[8];
	public int size;
	public int rotation;
	PApplet parent;
	//public ArrayList positList = new ArrayList();
	driver(PApplet p, int mo, boolean di, float px, float py, int []at,float []ppx,float []ppy, int driversize,int rotationnum) {
		parent = p;
		mod=mo;
		dir=di;
		posx=px;
		posy=py;
		att = at;
		pox = ppx;
		poy = ppy;
		size = driversize;
		rotation = rotationnum;
	}
	public void display()
	{
		parent.noFill();
		if(dir)
			parent.stroke(255);
		else
			parent.stroke(200);
		parent.strokeWeight((int)size/17);
		parent.ellipse(posx, posy, size, size);
		parent.fill(110, 110, 255);
		WriteNumbers();
/*
		if (i<10)
			text(i, drLook.posx-5, drLook.posy+5);
		else
			text(i, drLook.posx-9, drLook.posy+5);*/

	}
	//Lock the ring into place
	public void Lock()
	{
		dir = true;
		parent.stroke(255);
		WriteNumbers();
	}

	//Write the Numbers around the ring
	public void WriteNumbers()
	{
		for (int p=0; p<8; p++)
		{
			float slot=p;
			float xx =posx+( ((size/2)-5) * parent.cos(parent.radians(360*(slot/8))));
			float yy =posy+( ((size/2)-5) * parent.sin(parent.radians(360*(slot/8))));

			if(dir)
			{
				parent.fill(222, (255-(att[(p-rotation+8)%8]*30)), 0);
				parent.stroke(att[(p-rotation+8)%8]*30);
			}
			else
			{
				parent.fill(222, (255-(att[(p+rotation+8)%8]*30)), 0);
				parent.stroke(att[(p+rotation+8)%8]*30);
			}

			parent.strokeWeight((int)size/4);
			parent.point (xx, yy);
			parent.text(att[p], xx-5, yy+5);
			parent.noFill();
			pox[att[p]]=xx;
			poy[att[p]]=yy;
			// print(" in:"+p+ " at:"+ drLook.att[p] +" x:"+ round(drLook.pox[p]));
		}
	}
}

