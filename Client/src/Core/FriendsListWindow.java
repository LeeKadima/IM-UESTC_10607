package Core;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import javax.swing.*;

import network.commonClass.Account;
import network.messageOperate.MessageOperate;
/**
 * 好友列表窗口
 * @author Murrey
 * @version 3.0
 * 【删减】部分冗余的成员变量
 * 【修改】构造函数
 * 【添加】消息收发队列方法
 * 【添加】更新个人信息的方法
 */
public class FriendsListWindow extends JFrame{	
	private static final int 	i_window_width = 320,i_window_height = 700;
	private int 				i_loc_X,i_loc_Y;
	
	private Account 			account_mine;
	
	private int 				i_friends_sum;
	private int 				i_online_count;
	
	private JScrollPane 		scroll_friends_list;
	private JPanel 				panel_top,panel_middle,panel_bottom,
								panel_middle_friends,panel_middle_groups,panel_middle_else;
	private JLabel 				label_head_image,label_name,label_sign;
	private JTabbedPane 		tabbed_pane;
	private JList<String> 		jList_str_friendsName;
	private GridBagLayout 		gbLayout_top;
	private GridBagConstraints 	gbConstr_top;
	private JButton				btn_logout,btn_logoff;
	
	private ArrayList<Account> 	arrayList_account_friends;
	private Account 			account_newWindow;
	
	/**
	 * FriendsListWindow 构造函数
	 */
	public FriendsListWindow(){				
		/* 设置窗口基本信息 */
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);					// 设置关闭键操作
		this.setIconImage((new ImageIcon("image/chick.png")).getImage());		// 设置图标
		setWindowSize();			// 设置窗口尺寸
		setWindowRight();			// 设置窗口位于屏幕右侧
		this.setResizable(false);	// 是否可拖拽
		this.setTitle("KIM");		// 窗口名称
		
		topPaneSet(); 
		middlePaneSet(); 
		bottomPaneSet();
		
		this.setAlwaysOnTop(true);	// 是否置顶
		this.setVisible(true);		// 是否可视化
		
		RecvSendController.addToSendQueue(MessageOperate.packageAskMyselfInfoMsg()); // 请求个人信息
		RecvSendController.addToSendQueue(MessageOperate.packageAskFriendListMsg()); // 请求好友列表
	}
	
	/**
	 * 顶部Pane设置
	 */
	private void topPaneSet(){
		/* top面板各组件设置 */
		panel_top = new JPanel();
		label_head_image = new JLabel();							// 头像图标
		label_head_image.setIcon((new ImageIcon("image/p70_piano.jpg")));
		label_head_image.setToolTipText("123木头人");
		//head_image_label.setPreferredSize(new Dimension(20,20));
//		label_name = new JLabel("昵称："+this.getMine_nickName());	// 昵称
		label_name = new JLabel("昵称：等待服务器应答...");
//		label_sign = new JLabel(this.getMine_signature());			// 个性签名	
		label_sign = new JLabel("签名档：等待服务器应答...");
		// 面板布局设置
		gbLayout_top = new GridBagLayout();
		gbConstr_top = new GridBagConstraints();
		Insets ins = new Insets(0,0,0,0);
		gbConstr_top.fill = GridBagConstraints.BOTH;
		gbConstr_top.weightx = 1.0;
		gbConstr_top.weighty = 1.0;
		panel_top.setLayout(gbLayout_top);
		// 组件布局
		gbConstr_top.gridx = 0; gbConstr_top.gridy = 0;				// 头像
		gbConstr_top.gridheight = 2; gbConstr_top.gridwidth = 2;
		ins.set(10, 5, 10, 5);
		gbConstr_top.insets = ins;
		gbLayout_top.setConstraints(label_head_image, gbConstr_top);
		panel_top.add(label_head_image);
		gbConstr_top.gridx = 2; gbConstr_top.gridy = 0;				// 昵称
		gbConstr_top.gridheight = 1; gbConstr_top.gridwidth = 4;
		ins.set(0,0,0,0);
		gbLayout_top.setConstraints(label_name, gbConstr_top);
		panel_top.add(label_name);
		gbConstr_top.gridx = 2; gbConstr_top.gridy = 1;				// 签名档
		gbConstr_top.gridheight = 1; gbConstr_top.gridwidth = 4;
		ins.set(0, 0, 0, 0);
		gbConstr_top.insets = ins;
		gbLayout_top.setConstraints(label_sign, gbConstr_top);
		panel_top.add(label_sign);
		
		/* 将面板添加到容器中 */
		this.add(panel_top,BorderLayout.NORTH);
	}
	
	/**
	 * 顶部Pane设置_测试版
	 */
	private void topPaneSet_t() {
		/* top面板各组件设置 */
		panel_top = new JPanel();
		label_head_image = new JLabel();							// 头像图标
		label_head_image.setIcon((new ImageIcon("image/p70_piano.jpg")));
		label_head_image.setToolTipText("123木头人");
		//head_image_label.setPreferredSize(new Dimension(20,20));
		label_name = new JLabel("昵称：等待服务器应答...");
		label_sign = new JLabel("签名档：等待服务器应答...");

		panel_top.add(label_head_image,BorderLayout.SOUTH);
		panel_top.add(label_name,BorderLayout.NORTH);
		panel_top.add(label_sign,BorderLayout.SOUTH);
		
		/* 将面板添加到容器中 */
		this.add(panel_top,BorderLayout.NORTH);
	}
	
	/**
	 * 中部Pane设置
	 */
	private void middlePaneSet(){
		/* 组件初始化 */
		tabbed_pane = new JTabbedPane();
		panel_middle = new JPanel();
		panel_middle_friends = new JPanel();
		panel_middle_groups = new JPanel();
		panel_middle_else = new JPanel();
		jList_str_friendsName = new JList<String>();
		
		jList_str_friendsName.setListData(new String[]{"正在拉取好友列表..."});	
		jList_str_friendsName.setPreferredSize(new Dimension(i_window_width-60, (int)(i_window_height*0.7)));
		jList_str_friendsName.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(jList_str_friendsName.getSelectedIndex() != -1) {
					if(e.getClickCount() == 2){
						setNewWindowResource(arrayList_account_friends.get(jList_str_friendsName.getSelectedIndex()));
						WindowProducer.addWindowRequest(WindowProducer.CHAT_WIND);
					}
				}
			}
		});	
		scroll_friends_list = new JScrollPane(jList_str_friendsName);
		scroll_friends_list.setPreferredSize(new Dimension(i_window_width-60,440));	
		scroll_friends_list.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll_friends_list.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		
		
		/* Panel面板设置 */
		panel_middle_groups.setBackground(Color.green);
		panel_middle_else.setBackground(Color.red);
		
		/* Panel面板添加 */
		panel_middle.add(tabbed_pane);
		tabbed_pane.add("好友",panel_middle_friends);
		tabbed_pane.add("群组",panel_middle_groups);
		tabbed_pane.add("其他",panel_middle_else);
		panel_middle_friends.add(scroll_friends_list);
		
		/* 添加到容器  */
		this.add(panel_middle,BorderLayout.CENTER);
	}
	
	/**
	 * 底层Pane设置
	 */
	private void bottomPaneSet(){
		panel_bottom = new JPanel();
		btn_logout = new JButton("退出");
		btn_logout.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});		
//		btn_logoff = new JButton("注销");
//		btn_logoff.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
//				RecvSendController.closeSocket();
//				FriendsListWindow.this.dispose();
//				WindowProducer.addWindowRequest(WindowProducer.LOGIN_WIND);
//			}
//		});	
		
		FlowLayout flowLayout_Bottom = new FlowLayout();
		flowLayout_Bottom.setAlignment(FlowLayout.RIGHT);	// 居右
		panel_bottom.setLayout(flowLayout_Bottom);

		panel_bottom.add(btn_logoff);
		panel_bottom.add(btn_logout);
		this.add(panel_bottom,BorderLayout.SOUTH);
	}
	
	/**
	 * 设置窗口尺寸
	 */
	private void setWindowSize(){
//		i_window_height = (screenSize.height/3*2);		// 取2/3倍屏幕高度
//		i_window_width = (screenSize.width/3/3*1.5);
		this.setSize(i_window_width,i_window_height);
	}
	
	/**
	 * 设置窗口位置
	 */
	private void setWindowRight(){
		/* 获取屏幕相关信息 */
		Toolkit tool_kit=Toolkit.getDefaultToolkit();
		Dimension screenSize=tool_kit.getScreenSize();
		
		int i_screen_width = screenSize.width;
		int i_screen_height = screenSize.height;
		i_loc_X = (i_screen_width/3*2)+(i_screen_width/3 - i_window_width)/2;	// 位于屏幕右侧
		i_loc_Y = (i_screen_height - i_window_height)/2;
		this.setLocation(i_loc_X,i_loc_Y);
	}
	
	/**
	 * 根据传入好友信息数组 设置到自身成员属性
	 * 并内部进行在线状态的排序 最后将排序后的结果予以显示
	 * @param inArrList 从服务器获取到的好友信息数组
	 */
	public void updateFriendsList(ArrayList<Account> inArrList){
		setFriendsList(inArrList);			// 将传进来的好友列表设置到成员属性中
		sortFriendsListByOnline();			// 对成员属性进行排序 依据在线情况
		this.i_online_count = countOnlineNums();	// 计算在线人数
		friendsListShow();		// 在窗口中显示最新的好友列表
	}
	
	/**
	 * 更新用户信息-成员属性
	 * @param myselfAccount 用户信息
	 */
	public void updateMineInfo(Account myselfAccount) {
		this.account_mine = new Account(myselfAccount.getId(),myselfAccount.getNikeName(),
				myselfAccount.getOnline(),myselfAccount.getSignature());		
		
		/* 对窗口相关标签进行更新 */
		label_name.setText("昵称："+this.account_mine.getNikeName());	// 昵称
		label_sign.setText(this.account_mine.getSignature());	// 个性签名		
		// 头像
	}
	
	/**
	 * 将在线好友置顶
	 * @param inArrList 好友列表
	 * @return 排好序的好友列表
	 */
	private void sortFriendsListByOnline(){
		Comparator<Account> cmptor = new Comparator<Account>(){
			public int compare(Account a, Account b) {
				int status_A = a.getOnline()?1:-1;
				int status_B = b.getOnline()?1:-1;
				if(status_A <= status_B) return 1;
				else return -1;
			}
		};
		Collections.sort(arrayList_account_friends,cmptor);	// 根据在线状态对好友列表进行排序
	}
	
	/**
	 * 将好友信息列表设置到JList组件中 予以展示
	 */
	public void friendsListShow(){		
		Vector<String> vec_str_friendsName = new Vector<String>();
		this.i_friends_sum =  arrayList_account_friends.size();
		for(int i=0;i<this.i_friends_sum;i++)
			vec_str_friendsName.add(arrayList_account_friends.get(i).getNikeName());
		
		/* JList列表设置 */
		jList_str_friendsName.setListData(vec_str_friendsName);
		FriendsListCellRenderer flcr = new FriendsListCellRenderer(this.i_online_count,Color.RED);
		jList_str_friendsName.setCellRenderer(flcr);	// 应用自定义的列表样式显示器
		if(this.i_friends_sum >= 15)	// 对滚动条进行设置
			scroll_friends_list.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		else
			scroll_friends_list.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
	}
	
	/**
	 * 计数在线个数
	 * 前提：必须调用排序算法
	 * @param inArrList
	 * @return count 在线人数
	 */
	private int countOnlineNums(){
		int count = 0;
		for(int i=0;i<arrayList_account_friends.size();i++){
			if(arrayList_account_friends.get(i).getOnline()) count++;
			else break;
		}
		return count;
	}
	
	/**
	 * 调试用
	 * 打印当前好友列表信息
	 */
	public void printFriendsArrList(){
		for(int i=0;i<arrayList_account_friends.size();i++){
			System.out.println("ListInfo: Get the friend "+(i+1)+" - "+arrayList_account_friends.get(i).getNikeName());
			System.out.println("ListInfo: Get the friend "+(i+1)+" - "+arrayList_account_friends.get(i).getSignature());
			System.out.println("ListInfo: Get the friend "+(i+1)+" - "+arrayList_account_friends.get(i).getId());
			System.out.println("ListInfo: Get the friend "+(i+1)+" - "+arrayList_account_friends.get(i).getOnline());
		}
	}

	/**************** setter and getter ****************/	
	public void setFriendsList(ArrayList<Account> inArrList){
		this.arrayList_account_friends = new ArrayList<Account>(inArrList);
	}
	
	private void setNewWindowResource(Account friend_account){
		this.account_newWindow = new Account();
		this.account_newWindow = friend_account;
	}
	
	public Account getNewWindowResource(){
		return this.account_newWindow;
	}
	
	public Account getMineAccount() {
		return this.account_mine;
	}
}