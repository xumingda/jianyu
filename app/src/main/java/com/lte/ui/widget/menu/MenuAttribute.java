package com.lte.ui.widget.menu;

import android.os.Handler;

import com.lte.data.table.BlackListTable;
import com.lte.data.table.ImsiDataTable;
import com.lte.data.table.WhiteListTable;

public class MenuAttribute {

	public BlackListTable blackListTable;

	public WhiteListTable whiteListTable;

	public ImsiDataTable imsiDataTable;

	public long resId = -1;
	public long parentId = -1;
	/** 是否显示删除。 自建歌单和本地歌曲需要显示删除，其他显示播放MV */
	public boolean isShowDel;
	/** 在线歌曲资源类型 */
	public int resType = -1;
	/** 歌曲类型： 本地歌曲和在线歌曲 */
	public int musicType = -1;
	public String musicName;
	public String songerName;

	/** 自建歌单ID */
	public long playlistId;
	/**是否已经下载*/
	public boolean isDownload;
	public Handler handler;
	/**我的彩铃的播放模式*/
	public int playmode=1;
	public String desc;
	
	/**0:普通栏目类型(默认)；1:彩铃栏目; 2:自建歌单 ；3，4，5，6，7，8，9，10，11，17，100，101，200，201，300，400
	 * 18:主播放器页面playeractivity
	 * */
	public int type=0;
	/**来源，如来于新歌速递*/
	/**执行其他操作(可自行定义类型，然后回调onOtherItem(int otherType)方法)*/
	public int otherTag[];
	
	public String parentPath;

	// 分享相关
	public String sharePic;
	public String shareContent;
	public String shareUrl;
	public int shareType;
	public String memberId;

}
