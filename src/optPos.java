public class optPos {
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
