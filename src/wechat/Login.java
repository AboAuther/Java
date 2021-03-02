package wechat;

import Constant.ConstantData;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;


public class Login extends JFrame implements ActionListener {
    String url = "jdbc:sqlserver://127.0.0.1:1433;databaseName=StaffData;user=sa;password=245574";
    private final ChatClient chatClient;
    //用户名Label及输入框
    private final JLabel UserName = new JLabel(ConstantData.USERNAME);
    private final JTextField  Name = new JTextField();
    //密码Label及输入框
    private final JLabel UserPaw = new JLabel (ConstantData.PASSWORD);
    private final JPasswordField Paw = new JPasswordField();

    //按钮
    private final JButton Load = new JButton(ConstantData.LOGIN);
    private final JButton Quit = new JButton(ConstantData.QUIT);

    /**
     * 主函数
     */
    public static void main(String[] args) {
        new ChatClient();
    }

    /**
     * 构造函数
     */
    public Login(ChatClient chatClient){
        this.chatClient=chatClient;
    }


    /**
     * 和父窗体交互函数
     */
    public void open(){
        loadFrame();
    }

    /**
     * 加载窗体
     */
    public void loadFrame() {
        this.setTitle(ConstantData.LOGIN_TITLE);
        this.setVisible(true);
        this.setLayout(null);
        this.setBounds(500, 150, 520, 300);
        this.setResizable(false);
        Container c = this.getContentPane();

        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
                System.exit(0);
            }
        });
        //用户名及输入框
        UserName.setBounds(135, 50, 150, 30);
        UserName.setFont(new java.awt.Font("黑体", Font.BOLD, 18));
        c.add(UserName);
        Name.setBounds(250, 50, 120, 30);
        c.add(Name);
        //密码及输入框
        UserPaw.setBounds(135, 110, 150, 30);
        UserPaw.setFont(new java.awt.Font("黑体", Font.BOLD, 18));
        c.add(UserPaw);
        Paw.setBounds(250, 110, 120, 30);
        c.add(Paw);

        //登录
        Load.setBounds(140,200,80,40);
        c.add(Load);
        //退出
        Quit.setBounds(280,200,80,40);
        c.add(Quit);
        Load.addActionListener(this);
        Quit.addActionListener(this);
    }


    /**
     * 格式化消息返回函数
     */
    public void returnMessage(String message) {
        JOptionPane.showMessageDialog(null, message, ConstantData.ERROR_TITLE, JOptionPane.ERROR_MESSAGE);
    }


    /**
     * 事件处理函数
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == Load) {
            String name = Name.getText().trim();
            String passwd = new String(Paw.getPassword()).trim();
            if(name.isEmpty() || passwd.isEmpty()){
                this.returnMessage(ConstantData.ISNULL);
            }
			if(!isValidUser(name, passwd)){
				this.returnMessage(ConstantData.ISWRONG);
			}
            else
            {
                chatClient.gettextUsername().setText(name);
                this.dispose();
                chatClient.getChatClient().setVisible(true);
            }
        }
        else if (e.getSource() == Quit) {
            this.dispose();
            chatClient.getChatClient().setVisible(true);
        }
    }


    /**
     * 身份验证函数
     */
    public boolean isValidUser(String name,String password) {

        Connection con=null;//连接字符串
        PreparedStatement ps=null;//准备状态
        ResultSet rs=null;//返回状态集
        boolean IsValid=false;//布尔型返回值
        try{
            String className = "com.microsoft.sqlserver.jdbc.SQLServerDriver";//数据库驱动字符
            Class.forName(className);//加载数据库驱动
            System.out.println("数据库驱动加载成功！");
            con = DriverManager.getConnection(url);//使用系统函数获取数据库的链接
            //查询字符串，返回结果为布尔型
            String queryStr = "select * from staffmessage WHERE name='"+name+"' and password='"+password+"'";
            ps = con.prepareStatement(queryStr);//利用系统函数设置准备执行状态
            rs = ps.executeQuery();//查看返回集
            if (rs.next())//如果有结果的话，函数返回值为true
                IsValid = true;
        } catch(Exception exc){
            exc.printStackTrace();
        } finally{
            if (rs != null) try { rs.close();}catch (SQLException ignore) {}
            if (ps != null) try{ ps.close();}catch (SQLException ignore) {}
            if (con != null) try{con.close();}catch (SQLException ignore) {}
        }
        return IsValid;
    }
}