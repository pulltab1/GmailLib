import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rennyu.RFile;

public class GmailExporter {
	private GmailReceiver mail;
	private Calendar convertDate(Date date){
		if(date==null)return null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	}
	private String subLineSeparator(String src){
		src=src.replaceAll("\r\n","");
		src=src.replaceAll("\n\r","");
		src=src.replaceAll("\r","");
		src=src.replaceAll("\n","");
		return src;
	}
	private String subCSVSeparator(String src){
		return src.replaceAll(",","");
	}
	private String subFromAttaribute(String src){
		String regex = "<.*?>";
		String target = src;
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(target);
		if(matcher.find()){
			src=matcher.group(0).replaceAll("<","").replaceAll(">","");
		}
		src=src.replaceAll("\\=.+\\=", "");
		return src;
	}
	private String subContentAttaribute(String src){
		src=src.replaceAll("\t", "");
		src=src.replaceAll("\\[.+\\]", "");
		src=src.replaceAll("\\<.+\\>", "");
		return src;
	}
	private String getDateString(Date date){
		Calendar cal=convertDate(date);
		if(cal!=null){
			StringBuilder buf=new StringBuilder();
			buf.append(String.format("%04d",cal.get(Calendar.YEAR)));
			buf.append(String.format("%02d",cal.get(Calendar.MONTH)));
			buf.append(String.format("%02d",cal.get(Calendar.DAY_OF_MONTH)));
			return buf.toString();
		}
		return "N/A";
	}
	public GmailExporter(String address,String password,String folder) throws Exception{
		mail=new GmailReceiver(GmailReceiver.PROTOCOL_POP3);
		mail.connect(address,password,folder);
	}
	public void export(String dest) throws Exception{
		RFile file=new RFile("SJIS");
		String str[]=new String[mail.getSize()+1];
		str[0]=new String("sentdate,from,subject,context");
		for(int i=0;i<mail.getSize();i++){
			str[i+1]=
					subCSVSeparator(getDateString(mail.getSentDate(i)))+","+
					subFromAttaribute(subCSVSeparator(mail.getFrom(i)[0]))+","+
					subCSVSeparator(mail.getSubject(i))+","+
					subContentAttaribute(subLineSeparator(subCSVSeparator(mail.getContent(i))));
		}
		file.writeStrings(str,dest);
	}
}
