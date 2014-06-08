package artiano.core.structure;

public class Test {
	public static void main(String[] agrs){
		Domain[] d=Domain.getArray("[-20,1] (-20,1] (-20,1) (B,1] [-20,B)");
		for(int con=0;con<d.length;con++){
			System.out.println(d[con].minCD+" "+d[con].min+","+d[con].max+" "+d[con].maxCD);
			System.out.println(d[con].isIn(3));
			System.out.println(d[con].isIn(1));
			System.out.println(d[con].isIn(0));
			System.out.println(d[con].isIn(-20));
		}

	}

}
