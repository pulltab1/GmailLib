
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;

/**
 * メール情報
 * @author ogura
 */
public class GmailReceiver{
	/**受信プロトコルをPOP3にする場合に使用*/
	public static final byte PROTOCOL_POP3=0x01;
	/**受信プロトコルをIMAPにする場合に使用*/
	public static final byte PROTOCOL_IMAP=0x02;
	/**接続用オブジェクト*/
	private Store store;
	/**フォルダ用オブジェクト*/
	private Folder folder;
	/**全メール用オブジェクト*/
	private Message[] message;
	/**接続時の各種設定用オブジェクト*/
	private Properties property;
	/**現在使用しているプロトコル*/
	private byte protocol;

	/**
	 * Gmailメール閲覧用クラスのコンストラクタ。
	 * 通信プロトコルはPOP3かIMAPか設定します。
	 * 引数に設定する場合は、フィールドにあるPROTOCOL_POP3かPROTOCOL_IMAPを利用してください。
	 * @param prot 使用プロトコル
	 * @throws Exception 何かしらの例外が発生した場合スローされます。
	 */
	public GmailReceiver(byte prot) throws Exception{
		protocol=prot;
		switch(protocol){
		case PROTOCOL_POP3:
			property=getProperty_pop3();
			break;
		case PROTOCOL_IMAP:
			property=getProperty_imap();
			break;
		default:
			throw new Exception("GmailReciver.UndefineProtocolException");
		}
	}
	/**
	 * 接続を開始します。
	 * このタイミングで全てのメールを取得、リストに格納します。
	 * @param ac 使用するメールアドレス
	 * @param pw 使用するメールアドレスのパスワード
	 * @param fld 使用するメールアドレスの取得フォルダ
	 * @throws Exception 何かしらの例外が発生した場合スローされます。
	 */
	public void connect(String ac,String pw,String fld) throws Exception{
		Session session = Session.getInstance(property);
		switch(protocol){
		case PROTOCOL_POP3:
			store=session.getStore("pop3");
			store.connect("pop.gmail.com",ac,pw);
			break;
		case PROTOCOL_IMAP:
			store=session.getStore("imap");
			store.connect("imap.gmail.com",ac,pw);
			break;
		default:
			throw new Exception("GmailReciver.UndefineProtocolException");
		}
		folder=store.getFolder(fld);
		folder.open(Folder.READ_ONLY);
		message=folder.getMessages();
	}
	/**
	 * 接続を終了します。
	 * @throws Exception  何かしらの例外が発生した場合スローされます。
	 */
	public void close() throws Exception{
		store.close();
		folder.close(false);
	}
	/**
	 * 指定したインデックスのメールの本文を取得します
	 * @param index インデックス
	 * @return 取得したテキスト
	 * @throws IOException 例外が発生した場合
	 */
	public String getContent(int index) throws Exception{
		if(index<message.length){
			return getText(message[index].getContent());
		}
		return null;
	}
	/**
	 * 指定したインデックスのメールの件名を取得します
	 * @param index インデックス
	 * @return 取得したテキスト
	 * @throws Exception 
	 */
	public String getSubject(int index) throws Exception{
		if(index<message.length){
			return message[index].getSubject();
		}
		return null;
	}
	/**
	 * 指定したインデックスのメールの送信元を取得します
	 * @param index インデックス
	 * @return 取得したテキスト
	 * @throws Exception 
	 */
	public String[] getFrom(int index) throws Exception{
		
		if(index<message.length){
			String[] str=new String[message[index].getFrom().length];
			for(int i=0;i<str.length;i++){
				str[i]=message[index].getFrom()[i].toString();
				if(str[i]==null)str[i]="";
			}
			return str;
		}
		return null;
	}
	/**
	 * 指定したインデックスのメールの送信先を取得します
	 * @param index インデックス
	 * @return 取得したテキスト
	 * @throws MessagingException 
	 */
	public String[] getTo(int index) throws MessagingException{
		if(index<message.length){
			String[] str=new String[message[index].getAllRecipients().length];
			for(int i=0;i<str.length;i++){
				str[i]=message[index].getAllRecipients()[i].toString();
				if(str[i]==null)str[i]="";
			}
			return str;
		}
		return null;
	}
	/**
	 * 指定したインデックスのメールの返信先を取得します
	 * @param index インデックス
	 * @return 取得したテキスト
	 * @throws Exception 
	 */
	public String[] getReplyTo(int index) throws Exception{
		if(index<message.length){
			String[] str=new String[message[index].getReplyTo().length];
			for(int i=0;i<str.length;i++){
				str[i]=message[index].getReplyTo()[i].toString();
				if(str[i]==null)str[i]="";
			}
			return str;
		}
		return null;
	}
	/**
	 * 指定したインデックスのメールの送信日付を取得します
	 * @param index インデックス
	 * @return 取得した日付
	 * @throws Exception 
	 */
	public Date getSentDate(int index) throws Exception{
		if(index<message.length){
			return message[index].getSentDate();
		}
		return null;
	}
	/**
	 * 指定したインデックスのメールの受信日付を取得します
	 * @param index インデックス
	 * @return 取得した日付
	 * @throws Exception 
	 */
	public Date getReceivedDate(int index) throws Exception{
		if(index<message.length){
			return message[index].getReceivedDate();
		}
		return null;
	}
	/**
	 * メールの総件数を取得します。
	 * @return メールの件数
	 */
	public int getSize(){
		return message.length;
	}
	/**
	 * POP3プロトコルに合わせた接続用プロパティを取得します。
	 * @return 接続に使用するプロパティー
	 * @throws Exception 何かしらの例外が発生した場合スローされます。
	 */
	private Properties getProperty_pop3()throws Exception{
		// 各種設定
		Properties prop = new Properties();
		prop.put("mail.pop3.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		prop.put("mail.pop3.socketFactory.fallback", "false");
		prop.put("mail.pop3.socketFactory.port", "995");
		return prop;
	}
	/**
	 * IMAPプロトコルに合わせた接続用プロパティを取得します。
	 * @return 接続に使用するプロパティー
	 * @throws Exception 何かしらの例外が発生した場合スローされます。
	 */
	private Properties getProperty_imap()throws Exception{
		 // 各種設定
		Properties prop = new Properties();
        prop.put("mail.imap.auth", "true");
        prop.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        prop.put("mail.imap.socketFactory.fallback", "false");
        prop.put("mail.imap.socketFactory.port", "993");
        prop.put("mail.imap.starttls.enable","true");
		return prop;
	}
	/**
	 * POP3プロトコルに合わせた接続用プロパティを取得します。
	 * @return 接続に使用するプロパティー
	 * @throws Exception 何かしらの例外が発生した場合スローされます。
	 */
	private String getText(Object content) throws IOException, MessagingException {
        String text = null;
        StringBuffer sb = new StringBuffer();

        if (content instanceof String) {
            sb.append((String) content);
        } else if (content instanceof Multipart) {
            Multipart mp = (Multipart) content;
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart bp = mp.getBodyPart(i);
                sb.append(getText(bp.getContent()));
            }
        }

        text = sb.toString();
        return text;
    }

}