package com.alex.edudus.utility;

public class Const
{

	public static final int REQ_GPS_EXTERNAL_PERMISSION = 0x1002;
	public static final int REQ_ANDROID12_BLUETOOTH = 0x1003;

	public class Broadcast
	{
		public static final String PEN_RSSI = "pen_rssi";
		public static final String PEN_ADDRESS = "pen_address";
        public static final String PEN_NAME = "pen_NAME";
		public static final String ACTION_PEN_MESSAGE = "action_pen_message";
		public static final String MESSAGE_TYPE = "message_type";
		public static final String CONTENT = "content";

		public static final String ACTION_SYNC_PROGRESS = "action_ACTION_SYNC_PROGRESS";
		public static final String ACTION_PEN_DOT = "action_pen_dot";
		public static final String ACTION_PEN_DOTD = "action_pen_dotD";
		public static final String EXTRA_DOT = "dot";
		public static final String EXTRA_DOTD = "dotD";

        public static final String ACTION_FIND_DEVICE = "ACTION_FIND_DEVICE";
	}

	public class VAR
	{
		public static final boolean SDKPressureSupport = true;
		public static final int DEFAULT_PAPER_A5W = 148;
		public static final int DEFAULT_PAPER_A5H = 210;


		public static final int LINE_WIDTH_LV1 = 210;
	}
}
