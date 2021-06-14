package com.enernet.eg.lib;

import android.content.Context;
import androidx.fragment.app.FragmentActivity;

import com.enernet.eg.R;
import com.enernet.eg.StringUtil;

/**
 * Baseclass of all Activities of the Demo Application.
 * 
 * @author Philipp Jahoda
 */
public abstract class MPChartBase extends FragmentActivity {
		private Context realContext = null;
	 	
		protected String mMonths(Context realContext, int i) {
	 		System.out.println("realContext is null ? " + realContext);
	 		String[] months = new String[] {
		            "1" + StringUtil.getString(realContext, R.string.tv_month), "2" + StringUtil.getString(realContext, R.string.tv_month),
		            "3" + StringUtil.getString(realContext, R.string.tv_month), "4" + StringUtil.getString(realContext, R.string.tv_month), 
		            "5" + StringUtil.getString(realContext, R.string.tv_month), "6" + StringUtil.getString(realContext, R.string.tv_month), 
		            "7" + StringUtil.getString(realContext, R.string.tv_month), "8" + StringUtil.getString(realContext, R.string.tv_month), 
		            "9" + StringUtil.getString(realContext, R.string.tv_month), "10" + StringUtil.getString(realContext, R.string.tv_month), 
		            "11" + StringUtil.getString(realContext, R.string.tv_month), "12" + StringUtil.getString(realContext, R.string.tv_month)
		    };
	 		
	 		return months[i];
	 	}
	 	
		protected String mWeeks(Context realContext, int i) {
		 	 //"일", "월", "화", "수", "목", "금", "토"
		 	String[] weeks = new String[] {
		 			StringUtil.getString(realContext, R.string.tv_sun), StringUtil.getString(realContext, R.string.tv_mon), StringUtil.getString(realContext, R.string.tv_tue),
		 			StringUtil.getString(realContext, R.string.tv_wed), StringUtil.getString(realContext, R.string.tv_thu), StringUtil.getString(realContext, R.string.tv_fri),
		 			StringUtil.getString(realContext, R.string.tv_sat)
		 	};
	 		return weeks[i];
	 	}
	 	
		protected String mParties(Context realContext, int i) {
	 		String[] parties = new String[] {
		            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
		            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
		            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
		            "Party Y", "Party Z"
		    };
	 		return parties[i];
	 	}
	 	
//	 	protected String[] months = new String[] {
//	 			
//	            "1" + StringUtil.getString(realContext, R.string.tv_month), "2" + StringUtil.getString(realContext, R.string.tv_month), 
//	            "3" + StringUtil.getString(realContext, R.string.tv_month), "4" + StringUtil.getString(realContext, R.string.tv_month), 
//	            "5" + StringUtil.getString(realContext, R.string.tv_month), "6" + StringUtil.getString(realContext, R.string.tv_month), 
//	            "7" + StringUtil.getString(realContext, R.string.tv_month), "8" + StringUtil.getString(realContext, R.string.tv_month), 
//	            "9" + StringUtil.getString(realContext, R.string.tv_month), "10" + StringUtil.getString(realContext, R.string.tv_month), 
//	            "11" + StringUtil.getString(realContext, R.string.tv_month), "12" + StringUtil.getString(realContext, R.string.tv_month)
//	    };

//	 	/**
//	 	 * "일", "월", "화", "수", "목", "금", "토"
//	 	 */
//	 	protected String[] weeks = new String[] {
//	 			StringUtil.getString(realContext, R.string.tv_sun), StringUtil.getString(realContext, R.string.tv_mon), StringUtil.getString(realContext, R.string.tv_tue),
//	 			StringUtil.getString(realContext, R.string.tv_wed), StringUtil.getString(realContext, R.string.tv_thu), StringUtil.getString(realContext, R.string.tv_fri),
//	 			StringUtil.getString(realContext, R.string.tv_sat)
//	 	};

//	    protected String[] parties = new String[] {
//	            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
//	            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
//	            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
//	            "Party Y", "Party Z"
//	    };

	    @Override
	    public void onBackPressed() {
	        super.onBackPressed();
	        overridePendingTransition(R.anim.move_left_in_activity, R.anim.move_right_out_activity);
	    }
}
