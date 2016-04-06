
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GmailSender {
	/**接続時の各種設定用オブジェクト*/
	private Properties property;
	/**接続用のセッション*/
	private Session session;
	/**送信元アドレス*/
	private Address from;
	/**
	 * Gmailメール送信用クラスのコンストラクタ。
	 * @throws Exception 何かしらの例外が発生した場合スローされます。
	 */
	public GmailSender() throws Exception{
		property=getProperty();
	}
	/**
	 * 接続を開始します。
	 * @param ac 使用するメールアドレス
	 * @param pw 使用するメールアドレスのパスワード
	 * @throws Exception 何かしらの例外が発生した場合スローされます。
	 */
	public void connect(String address,String password) throws Exception{
		session = Session.getInstance(property,new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication(){
                return new PasswordAuthentication(address,password);
            }
		});
		from=new InternetAddress(address);
	}
	/**
	 * メールを転送します
	 * @param to 宛先
	 * @param subject 件名
	 * @param content 本文
	 * @throws Exception
	 */
	public void send(String to,String subject,String content) throws Exception{
		MimeMessage message = new MimeMessage(session);
		//宛先の設定
		message.setRecipients(MimeMessage.RecipientType.TO,to);

		//送信元の設定
		message.setFrom(from);

		//題名、本文の設定
		message.setSubject(subject);
		message.setText(content);

		//変更を保存
		message.saveChanges();
		Transport.send(message);
	}
	/**
	 * 接続用プロパティを取得します。
	 * @return 接続に使用するプロパティー
	 * @throws Exception 何かしらの例外が発生した場合スローされます。
	 */
	private Properties getProperty()throws Exception{
		// 各種設定
		Properties prop = new Properties();
		prop.setProperty("mail.smtp.host", "smtp.gmail.com");
		prop.setProperty("mail.smtp.auth", "true");
		prop.setProperty("mail.smtp.port", "465");
		prop.setProperty("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		prop.setProperty("mail.smtp.socketFactory.fallback", "false");
		prop.setProperty("mail.smtp.socketFactory.port", "465");
		return prop;
	}
}
