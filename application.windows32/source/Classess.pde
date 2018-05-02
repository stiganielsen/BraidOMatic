class driver {
  int mod; //number 
  boolean dir;
  float posx;//position
  float posy;
public  int [] att=new int[8];
  float [] pox=new float[8];
  float [] poy=new float[8];


  driver(int mo, boolean di, float px, float py, int []at,float []ppx,float []ppy) {
    mod=mo;
    dir=di;
    posx=px;
    posy=py;
    att = at;
    pox = ppx;
    poy = ppy;
  }
  ArrayList positList = new ArrayList();


}

  class posit{
    float posx;
    float posy;
   posit(float pox,float poy){
    posx=pox;
    posy=poy;
   }
  }
/////////////////////////////////////////////////////////
class optPos {
  float posx; //number 
  float posy;
  boolean taken; //is the location occupied? true: free location / false: occupied loc
  boolean viable;
  optPos(float px, float py, boolean ta, boolean vi) {
    posx=px;
    posy=py;
    taken=ta;
    viable=vi;
  }
}