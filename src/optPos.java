public class optPos {
    public float posx; //number
    public float posy;
    public boolean taken; //is the location occupied? true: free location / false: occupied loc
    public boolean viable;
    optPos(float px, float py, boolean ta, boolean vi) {
        posx=px;
        posy=py;
        taken=ta;
        viable=vi;
    }
}
