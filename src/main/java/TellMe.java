import org.apache.commons.cli.*;

import java.util.Date;
import java.util.Properties;

import org.apache.commons.cli.ParseException;

import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class TellMe {

    private static void sendMail(Properties cmdProperties) throws Exception {
        //1、连接邮件服务器的参数配置
        Properties props = new Properties();
        //设置用户的认证方式
        props.setProperty("mail.smtp.auth", "true");
        //设置传输协议
        props.setProperty("mail.transport.protocol", "smtp");
        //设置发件人的SMTP服务器地址
        props.setProperty("mail.smtp.host", cmdProperties.getProperty("server"));
        props.setProperty("mail.smtp.starttls.enable", "true");
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                String userName = cmdProperties.getProperty("username");
                String password = cmdProperties.getProperty("password");
                return new PasswordAuthentication(userName, password);
            }
        };

        //2、创建定义整个应用程序所需的环境信息的 Session 对象
        Session session = Session.getInstance(props,authenticator);
        //设置调试信息在控制台打印出来
        session.setDebug(true);
        //3、创建邮件的实例对象
        MimeMessage msg = new MimeMessage(session);
        //设置发件人地址
        msg.setFrom(new InternetAddress(cmdProperties.getProperty("username")));
        /*
         * 设置收件人地址（可以增加多个收件人、抄送、密送），即下面这一行代码书写多行
         * MimeMessage.RecipientType.TO:发送
         * MimeMessage.RecipientType.CC：抄送
         * MimeMessage.RecipientType.BCC：密送
         */
        msg.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(cmdProperties.getProperty("target")));
        //设置邮件主题
        msg.setSubject(cmdProperties.getProperty("subject"), "UTF-8");
        //设置邮件正文
        msg.setText(cmdProperties.getProperty("content"), "UTF-8");
        // msg.setContent(cmdProperties.getProperty("content"), "text/html;charset=UTF-8");
        //设置邮件的发送时间,默认立即发送
        msg.setSentDate(new Date());
        //4、根据session对象获取邮件传输对象Transport
        Transport.send(msg);
//        Transport transport = session.getTransport();
        //设置发件人的账户名和密码
//        transport.connect(cmdProperties.getProperty("username"), cmdProperties.getProperty("password"));
        //发送邮件，并发送到所有收件人地址，message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        // transport.sendMessage(msg, msg.getAllRecipients());
        //如果只想发送给指定的人，可以如下写法
//        transport.sendMessage(msg, new Address[]{new InternetAddress(cmdProperties.getProperty("target"))});
        //5、关闭邮件连接
//        transport.close();
    }

    private static Properties paraseCommandLine(String[] args) throws Exception {
        CommandLineParser parser = new DefaultParser();
        HelpFormatter helpFormatter = new HelpFormatter();
        Options options = new Options();
        options.addOption("h", "help", false, "print usage information");
        options.addOption("u", "user", true, "username of email passport");
        options.addOption("p", "password", true, "password of email passport");
        options.addOption("s", "server", true, "mail server address");
        options.addOption("t", "target", true, "target mail");
        options.addOption("j", "subject", true, "mail subject");
        options.addOption("c", "content", true, "mail content");
        // Parse the program arguments
        CommandLine commandLine = parser.parse(options, args);
        // Set the appropriate variables based on supplied options

        if (commandLine.hasOption('h')) {
            helpFormatter.printHelp("tellme", options);
            System.exit(1);
        }

        if (!commandLine.hasOption('u') && !commandLine.hasOption("user")) {
            System.err.println("no username");
            helpFormatter.printHelp("tellme", options);
            System.exit(1);
        }

        if (!commandLine.hasOption('p') && !commandLine.hasOption("password")) {
            System.err.println("no password");
            helpFormatter.printHelp("tellme", options);
            System.exit(1);
        }

        if (!commandLine.hasOption('s') && !commandLine.hasOption("server")) {
            System.err.println("no server address");
            helpFormatter.printHelp("tellme", options);
            System.exit(1);
        }

        if (!commandLine.hasOption('t') && !commandLine.hasOption("target")) {
            System.err.println("no target mail");
            helpFormatter.printHelp("tellme", options);
            System.exit(1);
        }

        if (!commandLine.hasOption('c') && !commandLine.hasOption("content")) {
            System.err.println("no mail content");
            helpFormatter.printHelp("tellme", options);
            System.exit(1);
        }

        if (!commandLine.hasOption('j') && !commandLine.hasOption("subject")) {
            System.err.println("no mail subject");
            helpFormatter.printHelp("tellme", options);
            System.exit(1);
        }

        Properties properties = new Properties();

        String username = !commandLine.getOptionValue('u').equals("") ? commandLine.getOptionValue('u') : commandLine.getOptionValue("username");
        String password = !commandLine.getOptionValue('p').equals("") ? commandLine.getOptionValue('p') : commandLine.getOptionValue("password");
        String server = !commandLine.getOptionValue('s').equals("") ? commandLine.getOptionValue('s') : commandLine.getOptionValue("server");
        String target = !commandLine.getOptionValue('t').equals("") ? commandLine.getOptionValue('t') : commandLine.getOptionValue("target");
        String content = !commandLine.getOptionValue('c').equals("") ? commandLine.getOptionValue('c') : commandLine.getOptionValue("content");
        String subject = !commandLine.getOptionValue('j').equals("") ? commandLine.getOptionValue('c') : commandLine.getOptionValue("subject");


        properties.setProperty("username", username);
        properties.setProperty("password", password);
        properties.setProperty("server", server);
        properties.setProperty("target", target);
        properties.setProperty("subject", subject);

        properties.setProperty("content", content);

        return properties;

    }

    public static void main(String[] args) {
        try {
            // 解析命令行
            Properties cmdProperties = paraseCommandLine(args);
            // 发送命令
            sendMail(cmdProperties);
        } catch (ParseException e) {
            System.err.println("can't parse command line.");
            System.exit(1);
        } catch (MessagingException e) {
            System.err.println("send mail failed.");
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("send mail success.");
    }

}
