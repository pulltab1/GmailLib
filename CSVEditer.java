import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import rennyu.RFile;

public class CSVEditer{
	private RFile file=new RFile("SJIS");
	private ArrayList<String> csv;
	private String title;
	ArrayList<Csv> list;
	private class CsvComparator implements Comparator<Csv> {
		boolean desc;
		public CsvComparator(boolean de){
			desc=de;
		}
		public int compare(Csv a, Csv b) {
			String s1 = a.target;
			String s2 = b.target;
			if (s1.compareTo(s2)>0) {
				if(desc)return -1;
				return 1;
			}
			else if(s1.compareTo(s2)<0){
				if(desc)return 1;
				return -1;
			}
			else{
				return 0;
			}
		}
	}
	private class Csv{
		public String target;
		public String buffer;
		public Csv(String text,String tar){
			buffer=text;
			target=tar;
		}
	}
	private void sort(ArrayList<String> text,int index,boolean desc){
		ArrayList<Csv> list=new ArrayList<Csv>();
		String[][] str=new String[text.size()][];
		for(int i=0;i<str.length;i++){
			str[i]=text.get(i).split(",");
			list.add(new Csv(text.get(i),str[i][index]));
		}
		Collections.sort(list,new CsvComparator(desc));
		for(int i=0;i<str.length;i++){
			text.set(i,list.get(i).buffer);
		}
	}
	public CSVEditer(String path){
		String[] str=file.readStrings(path);
		title=str[0];
		csv=new ArrayList<String>();
		for(int i=1;i<str.length;i++){
			csv.add(str[i]);
		}
	}
	public void refine(String word,int index){
		for(int i=0;i<csv.size();i++){
			if(csv.get(i).split(",")[index].indexOf(word)==-1){
				csv.remove(i);
				i--;
			}
		}
	}
	public void refine(String word){
		for(int i=0;i<csv.size();i++){
			if(csv.get(i).indexOf(word)==-1){
				csv.remove(i);
				i--;
			}
		}
	}
	public int count(String word,int index){
		int con=0;
		for(int i=0;i<csv.size();i++){
			if(csv.get(i).split(",")[index].indexOf(word)==-1){
				con++;
			}
		}
		return con;
	}
	public int count(String word){
		int con=0;
		for(int i=0;i<csv.size();i++){
			if(csv.get(i).indexOf(word)==-1){
				con++;
			}
		}
		return con;
	}
	public int count(){
		return csv.size();
	}
	public String[] getStrings(){
		String str[]=new String[csv.size()+1];
		str[0]=title;
		for(int i=0;i<csv.size();i++){
			str[i+1]=csv.get(i);
		}
		return str;
	}
	public String getString(int index){
		String str[]=new String[csv.size()];
		str[0]=title;
		for(int i=1;i<str.length;i++){
			str[i]=csv.get(i);
		}
		return str[index];
	}
	public void export(String path){
		file.writeStrings(getStrings(),path);
	}
	public void sort(int index,boolean desc){
		sort(csv,index,desc);
	}
}
