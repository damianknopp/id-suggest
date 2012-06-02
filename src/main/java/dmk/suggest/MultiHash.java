package dmk.suggest;


/////////////////////////////////////////
//This came from:
//http://www.cs.nyu.edu/courses/summer01/G22.3033-001/MultiHash.java
/////////////////////////////////////////
public class MultiHash {
    static java.util.Random r = new java.util.Random();
    int mask;
    int rangeval;
    public int seeds[][];
    public MultiHash(int nfuncs, int amask, int range) {
	rangeval = range;
	seeds = new int[nfuncs][4098];
	for (int i = 0; i < nfuncs; i++)
	    for (int j = 0; j < 4098; j++)
		seeds[i][j] = r.nextInt(rangeval);

	mask = amask;
    }
    public int hash(int hindex, String key){
	int i;
	int len = key.length();
	int hashval= len;
	for (i = 0; i<(len<<3); i+=8) {
	    int k = Character.getNumericValue(key.charAt(i>>3));
	    if ((k&0x01) > 0) hashval ^= seeds[hindex][i+0];
	    if ((k&0x02) > 0) hashval ^= seeds[hindex][i+1];
	    if ((k&0x04) > 0) hashval ^= seeds[hindex][i+2];
	    if ((k&0x08) > 0) hashval ^= seeds[hindex][i+3];
	    if ((k&0x10) > 0) hashval ^= seeds[hindex][i+4];
	    if ((k&0x20) > 0) hashval ^= seeds[hindex][i+5];
	    if ((k&0x40) > 0) hashval ^= seeds[hindex][i+6];
	    if ((k&0x80) > 0) hashval ^= seeds[hindex][i+7];
	}
	return (hashval & mask) % rangeval;
    }
}

