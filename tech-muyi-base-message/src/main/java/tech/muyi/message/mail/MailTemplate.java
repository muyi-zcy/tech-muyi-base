package tech.muyi.message.mail;

import tech.muyi.exception.MyException;
import tech.muyi.exception.enumtype.CommonErrorCodeEnum;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @Author: muyi
 * @Date: 2021/1/11 23:52
 */
public class MailTemplate  implements Serializable {
    //    smtp服务器
    private String host;

    //    发送人邮箱
    private String sendMail;

    //	  发送人昵称
    private String sendName;

    //    发送邮箱密码
    private String pwd;

    //    发送人地址
    private String from;

    private boolean debug;

    public MailTemplate(String host, String sendMail, String sendName, String pwd, String from,boolean debug) {
        this.host = host;
        this.sendMail = sendMail;
        this.sendName = sendName;
        this.pwd = pwd;
        this.from = from;
        this.debug = debug;
    }
    public MailTemplate(String host, String sendMail, String sendName, String pwd, String from) {
        this.host = host;
        this.sendMail = sendMail;
        this.sendName = sendName;
        this.pwd = pwd;
        this.from = from;
        this.debug = false;
    }

    /**
     * 发送多份邮件
     * @param mailList
     * @return
     */
    public void SendMailMessage(List<MailDTO> mailList){
        for(MailDTO mailDTO:mailList){
            try {
                sendMail(mailDTO);
            }catch (Exception e){
                e.printStackTrace();
                throw new MyException(CommonErrorCodeEnum.UNKNOWN_EXCEPTION.getResultCode(),CommonErrorCodeEnum.UNKNOWN_EXCEPTION.getResultMsg());
            }
        }
    }

    /**
     * 发生单个邮件（可群发）
     * @param mailDTO
     * @return
     */
    public void SendMailMessage(MailDTO mailDTO){
        try {
            sendMail(mailDTO);
        }catch (Exception e){
            e.printStackTrace();
            throw new MyException(CommonErrorCodeEnum.UNKNOWN_EXCEPTION.getResultCode(),CommonErrorCodeEnum.UNKNOWN_EXCEPTION.getResultMsg());
        }
    }
    //    发送邮件接口
    private void sendMail(MailDTO... receiveMail) throws Exception{

        // 1. 创建参数配置, 用于连接邮件服务器的参数配置
        Properties props = new Properties();                    // 参数配置
        props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", host);   // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");            // 需要请求认证

        final String smtpPort = "465";
        props.setProperty("mail.smtp.port", smtpPort);
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", smtpPort);


        // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getDefaultInstance(props);
        session.setDebug(false);                                 // 设置为debug模式, 可以查看详细的发送 log

        // 3. 根据 Session 获取邮件传输对象
        Transport transport = session.getTransport();
        // 4. 使用 邮箱账号 和 密码 连接邮件服务器, 这里认证的邮箱必须与 message 中的发件人邮箱一致, 否则报错
        transport.connect(sendMail, pwd);

        for(int i = 0;i<receiveMail.length;i++) {
            // 创建邮件
            MimeMessage message = createMessage(session, receiveMail[i]);
            // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
            transport.sendMessage(message, message.getAllRecipients());
        }
        // 7. 关闭连接
        transport.close();

    }


    /**
     *
     * 功能描述: 创建邮件（不抄送）
     *
     * @param:
     * @return:
     * @auther:
     * @date:
     */
    private MimeMessage createMessage(Session session, MailDTO mailDTO) throws Exception {
        // 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);
        // 2. From: 发件人
        message.setFrom(new InternetAddress(sendMail, mailDTO.getName() == null ? sendName: mailDTO.getName(), "UTF-8"));
        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        if(mailDTO.getToMail()!=null){
            message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(mailDTO.getToMail().getMail(), mailDTO.getToMail().getName(), "UTF-8"));
        }
        if(mailDTO.getToMailList()!=null){
            message.setRecipients(MimeMessage.RecipientType.TO, dealSendToAddress(mailDTO.getToMailList()));
        }

        if(mailDTO.getCcMail()!=null){
            message.setRecipient(MimeMessage.RecipientType.CC, new InternetAddress(mailDTO.getCcMail().getMail(), mailDTO.getCcMail().getName(), "UTF-8"));
        }
        if(mailDTO.getCcMailList()!=null){
            message.setRecipients(MimeMessage.RecipientType.CC, dealSendToAddress(mailDTO.getCcMailList()));
        }
        if(mailDTO.getBccMail()!=null){
            message.setRecipient(MimeMessage.RecipientType.BCC, new InternetAddress(mailDTO.getToMail().getMail(), mailDTO.getToMail().getName(), "UTF-8"));
        }
        if(mailDTO.getBccMailList()!=null){
            message.setRecipients(MimeMessage.RecipientType.BCC, dealSendToAddress(mailDTO.getBccMailList()));
        }
        // 4. Subject: 邮件主题
        message.setSubject(mailDTO.getTheme(), "UTF-8");
        // 5. Content: 邮件正文（可以使用html标签）
        message.setContent(mailDTO.getContent() ,"text/html;charset=UTF-8");
        // 6. 设置发件时间
        if(mailDTO.getSendTime()!=null){
            message.setSentDate(mailDTO.getSendTime());
        }else{
            message.setSentDate(new Date());
        }
        // 7. 保存设置
        message.saveChanges();

        return message;
    }

    private InternetAddress[]  dealSendToAddress(List<ReceiveUserDTO> receiveUserDTOS) throws UnsupportedEncodingException {
        InternetAddress[] sendTo = new InternetAddress[receiveUserDTOS.size()];
        for(int i = 0;  i < receiveUserDTOS.size(); i++){
            ReceiveUserDTO receiveUserDTO = receiveUserDTOS.get(i);
            sendTo[i] =  new InternetAddress(receiveUserDTO.getMail(), receiveUserDTO.getName(), "UTF-8");
        }
        return sendTo;
    }

}
