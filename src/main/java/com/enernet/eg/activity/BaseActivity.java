package com.enernet.eg.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.enernet.eg.CaApplication;
import com.enernet.eg.CaEngine;
import com.enernet.eg.CaPref;
import com.enernet.eg.EgDialog;
import com.enernet.eg.EgDialogLogout;
import com.enernet.eg.R;
import com.enernet.eg.StringUtil;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.holder.StringHolder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class BaseActivity extends AppCompatActivity {

    public Drawer m_Drawer;

    private EgDialogLogout m_dlgLogout;
    private EgDialog m_dlgInform;
    private static Typeface mTypeface = null;
    
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        if (mTypeface == null) {
            // font_open_sans_regular or font_nanumgothic
            mTypeface = Typeface.createFromAsset(this.getAssets(), StringUtil.getString(this, R.string.font_open_sans_regular)); // 외부폰트 사용

            //mTypeface = Typeface.MONOSPACE; // 내장 폰트 사용
        }
        setGlobalFont(getWindow().getDecorView());
        // 또는
        // View view = findViewById(android.R.id.content);
        // setGlobalFont(view);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void setGlobalFont(View view) {
        if (view != null) {
            if(view instanceof ViewGroup){
                ViewGroup vg = (ViewGroup)view;
                int vgCnt = vg.getChildCount();
                for(int i=0; i < vgCnt; i++){
                    View v = vg.getChildAt(i);
                    if(v instanceof TextView){
                        ((TextView) v).setTypeface(mTypeface);
                    }
                    setGlobalFont(v);
                }
            }
        }
    }

    public Date parseDate(String str) {
        Calendar cal=new GregorianCalendar(1970, 1, 1, 0, 0, 0);

        Date dt;

        try {
            dt = CaApplication.m_Info.m_dfStd.parse(str);
        }
        catch (ParseException e) {
           // e.printStackTrace();
            dt=cal.getTime();
        }

        return dt;
    }

    public void prepareDrawer() {
        String strAddress=CaApplication.m_Info.m_strSiteName + " " + CaApplication.m_Info.m_strAptDongName + "동 " + CaApplication.m_Info.m_strAptHoName + "호";
        String strMember=CaApplication.m_Info.m_strMemberName + " 님";

        AccountHeaderBuilder ahb = new AccountHeaderBuilder();

        ahb.withActivity(this);
        ahb.withSelectionListEnabled(false);
        ahb.addProfiles(
                new ProfileDrawerItem().withName(strAddress).withIcon(getResources().getDrawable(R.drawable.gg_1)).withEmail(strMember));

        AccountHeader ah=ahb.build();

        PrimaryDrawerItem itemUsage = new PrimaryDrawerItem();
        itemUsage.withIdentifier(CaEngine.MENU_USAGE);
        itemUsage.withName("홈");
        itemUsage.withTextColor(Color.rgb(255, 255, 255));
        itemUsage.withSelectable(false);
        itemUsage.withIcon(R.drawable.menu_usage);
        itemUsage.withDescription("우리집 전기 사용");
        itemUsage.withDescriptionTextColor(Color.rgb(255, 255, 255));

        SecondaryDrawerItem itemUsageDaily=new SecondaryDrawerItem();
        itemUsageDaily.withIdentifier(CaEngine.MENU_USAGE_DAILY);
        itemUsageDaily.withName("- 일일 상세 정보");
        itemUsageDaily.withLevel(6);
        itemUsageDaily.withTextColor(Color.rgb(255, 255, 255));

        SecondaryDrawerItem itemUsageMonthly=new SecondaryDrawerItem();
        itemUsageMonthly.withIdentifier(CaEngine.MENU_USAGE_MONTHLY);
        itemUsageMonthly.withName("- 월간 상세 정보");
        itemUsageMonthly.withLevel(6);
        itemUsageMonthly.withTextColor(Color.rgb(255, 255, 255));

        SecondaryDrawerItem itemUsageYearly=new SecondaryDrawerItem();
        itemUsageYearly.withIdentifier(CaEngine.MENU_USAGE_YEARLY);
        itemUsageYearly.withName("- 연간 상세 정보");
        itemUsageYearly.withLevel(6);
        itemUsageYearly.withTextColor(Color.rgb(255, 255, 255));

        PrimaryDrawerItem itemSiteState = new PrimaryDrawerItem();
        itemSiteState.withIdentifier(CaEngine.MENU_SITE_STATE);
        itemSiteState.withName("우리 아파트 전기 사용");
        itemSiteState.withTextColor(Color.rgb(255, 255, 255));
        itemSiteState.withSelectable(false);
        itemSiteState.withIcon(R.drawable.menu_site_state);

        /*
        PrimaryDrawerItem itemPoint = new PrimaryDrawerItem();
        itemPoint.withIdentifier(CaEngine.MENU_POINT);
        itemPoint.withName("EG 포인트");
        itemPoint.withTextColor(Color.rgb(255, 255, 255));
        itemPoint.withSelectable(false);
        itemPoint.withIcon(R.drawable.menu_point);

        PrimaryDrawerItem itemElecInfoSave = new PrimaryDrawerItem();
        itemElecInfoSave.withIdentifier(CaEngine.MENU_ELEC_INFO_SAVE);
        itemElecInfoSave.withName("전기 상식");
        itemElecInfoSave.withTextColor(Color.rgb(255, 255, 255));
        itemElecInfoSave.withSelectable(false);
        itemElecInfoSave.withIcon(R.drawable.menu_info);
        itemElecInfoSave.withDescription("전기 절약 상식");
        itemElecInfoSave.withDescriptionTextColor(Color.rgb(200, 200, 200));

        PrimaryDrawerItem itemElecInfoPrice = new PrimaryDrawerItem();
        itemElecInfoPrice.withIdentifier(CaEngine.MENU_ELEC_INFO_PRICE);
        itemElecInfoPrice.withName("전기 상식");
        itemElecInfoPrice.withTextColor(Color.rgb(255, 255, 255));
        itemElecInfoPrice.withSelectable(false);
        itemElecInfoPrice.withIcon(R.drawable.menu_info);
        itemElecInfoPrice.withDescription("전기 요금 상식");
        itemElecInfoPrice.withDescriptionTextColor(Color.rgb(200, 200, 200));
*/

        BadgeStyle bs=new BadgeStyle(Color.rgb(255, 0, 0), Color.rgb(255, 0, 0)).withTextColor(Color.rgb(255, 255, 255));

        PrimaryDrawerItem itemAlarm = new PrimaryDrawerItem();
        itemAlarm.withIdentifier(CaEngine.MENU_ALARM);
        itemAlarm.withName("알림");
        itemAlarm.withTextColor(Color.rgb(255, 255, 255));
        itemAlarm.withSelectable(false);
        itemAlarm.withIcon(R.drawable.menu_alarm);
        itemAlarm.withBadgeStyle(bs);

        PrimaryDrawerItem itemNotice = new PrimaryDrawerItem();
        itemNotice.withIdentifier(CaEngine.MENU_NOTICE);
        itemNotice.withName("공지사항");
        itemNotice.withTextColor(Color.rgb(255, 255, 255));
        itemNotice.withSelectable(false);
        itemNotice.withIcon(R.drawable.menu_notice);
        itemNotice.withBadgeStyle(bs);

        PrimaryDrawerItem itemFaq = new PrimaryDrawerItem();
        itemFaq.withIdentifier(CaEngine.MENU_FAQ);
        itemFaq.withName("자주묻는질문 (FAQ)");
        itemFaq.withTextColor(Color.rgb(255, 255, 255));
        itemFaq.withSelectable(false);
        itemFaq.withIcon(R.drawable.menu_faq);
        itemFaq.withBadgeStyle(bs);

        PrimaryDrawerItem itemQna = new PrimaryDrawerItem();
        itemQna.withIdentifier(CaEngine.MENU_QNA);
        itemQna.withName("고객센터 (Q & A)");
        itemQna.withTextColor(Color.rgb(255, 255, 255));
        itemQna.withSelectable(false);
        itemQna.withIcon(R.drawable.menu_qna);
        itemQna.withBadgeStyle(bs);

        PrimaryDrawerItem itemSetting = new PrimaryDrawerItem();
        itemSetting.withIdentifier(CaEngine.MENU_SETTING);
        itemSetting.withName("설정");
        itemSetting.withTextColor(Color.rgb(255, 255, 255));
        itemSetting.withSelectable(false);
        itemSetting.withIcon(R.drawable.menu_setting);

        PrimaryDrawerItem itemLogout = new PrimaryDrawerItem();
        itemLogout.withIdentifier(CaEngine.MENU_LOGOUT);
        itemLogout.withName("로그아웃");
        itemLogout.withTextColor(Color.rgb(255, 255, 255));
        itemLogout.withSelectable(false);
        itemLogout.withIcon(R.drawable.menu_logout);

        final BaseActivity This=this;
        final Context Ctx=getApplicationContext();

        m_Drawer = new DrawerBuilder()
                .withActivity(this).withSliderBackgroundColor(getResources().getColor(R.color.eg_menu_blue)).withAccountHeader(ah)
                .addDrawerItems(itemUsage, itemUsageDaily, itemUsageMonthly, itemUsageYearly,
                        itemSiteState,
                        //itemPoint, itemElecInfoSave, itemElecInfoPrice,
                        itemAlarm, itemNotice, itemFaq, itemQna, itemSetting, itemLogout)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        int nId=(int)drawerItem.getIdentifier();
                        Log.i("Drawer", "position="+position+", id="+nId);

                        switch (nId) {
                            case CaEngine.MENU_USAGE: {
                                Intent it = new Intent(This, ActivityUsage.class);
                                startActivity(it);
                            }
                            break;

                            case CaEngine.MENU_USAGE_DAILY: {
                                Intent it = new Intent(This, ActivityUsageDaily.class);
                                startActivity(it);
                            }
                            break;

                            case CaEngine.MENU_USAGE_MONTHLY: {
                                Intent it = new Intent(This, ActivityUsageMonthly.class);
                                startActivity(it);
                            }
                            break;

                            case CaEngine.MENU_USAGE_YEARLY: {
                                Intent it = new Intent(This, ActivityUsageYearly.class);
                                startActivity(it);
                            }
                            break;

                            case CaEngine.MENU_SITE_STATE: {
                                Intent it = new Intent(This, ActivitySiteState.class);
                                startActivity(it);
                            }
                            break;

                            case CaEngine.MENU_POINT: {
                                Intent it = new Intent(This, ActivityPoint.class);
                                startActivity(it);
                            }
                            break;

                            case CaEngine.MENU_ALARM: {
                                Intent it = new Intent(This, ActivityAlarm.class);
                                startActivity(it);
                            }
                            break;

                            case CaEngine.MENU_NOTICE: {
                                Intent it = new Intent(This, ActivityNoticeList.class);
                                startActivity(it);
                            }
                            break;

                            case CaEngine.MENU_FAQ: {
                                Intent it = new Intent(This, ActivityFaq.class);
                                startActivity(it);
                            }
                            break;

                            case CaEngine.MENU_QNA: {

                                Intent it = new Intent(This, ActivityQnaList.class);
                                startActivity(it);

                                /*
                                View.OnClickListener LsnConfirm=new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        m_dlgInform.dismiss();
                                    }
                                };

                                m_dlgInform=new EgDialog(This, R.layout.dialog02, "준비중입니다.\n잠시만 기다려 주세요", LsnConfirm);
                                m_dlgInform.show();
                                */


                            }
                            break;

                            case CaEngine.MENU_SETTING: {
                                Intent it = new Intent(This, ActivitySetting.class);
                                startActivity(it);
                            }
                            break;

                            case CaEngine.MENU_LOGOUT: {

                                View.OnClickListener LsnConfirmYes=new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    Log.i("BaseActivity", "Yes button clicked...");
                                    m_dlgLogout.dismiss();

                                    CaPref pref = new CaPref(Ctx);

                                    pref.setValue(CaPref.PREF_MEMBER_ID, "");
                                    pref.setValue(CaPref.PREF_PASSWORD, "");

                                    final Class Clazz=ActivityLogin.class;

                                    Intent nextIntent = new Intent(Ctx, Clazz);
                                    startActivity(nextIntent);
                                    }
                                };

                                View.OnClickListener LsnConfirmNo=new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                    Log.i("BaseActivity", "No button clicked...");
                                    m_dlgLogout.dismiss();
                                    }
                                };

                                m_dlgLogout=new EgDialogLogout(This, LsnConfirmYes, LsnConfirmNo);
                                m_dlgLogout.setOnKeyListener(new DialogInterface.OnKeyListener() {
                                    @Override
                                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                        if (keyCode==KeyEvent.KEYCODE_BACK) {
                                            dialog.dismiss();
                                            return true;
                                        }
                                        return false;
                                    }
                                });

                                m_dlgLogout.show();
                            }
                            break;

                            default: {
                                Log.d("Drawer", "Unknon menu with id="+nId);
                            }
                            break;
                        }
                        m_Drawer.setSelection(-1);
                        m_Drawer.closeDrawer();

                        return true;
                    }
                }).withSelectedItem(-1).withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        Log.d("Drawer", "onDrawerOpened called...");

                        // alarm badge
                        int nCountUnreadAlarm=CaApplication.m_Info.getUnreadAlarmCount();

                        if (nCountUnreadAlarm==0) {
                            m_Drawer.updateBadge(CaEngine.MENU_ALARM, null);
                        }
                        else {
                            String strCount=Integer.toString(nCountUnreadAlarm);
                            m_Drawer.updateBadge(CaEngine.MENU_ALARM, new StringHolder(strCount));
                        }

                        // notice badge
                        int nCountUnreadNotice=CaApplication.m_Info.getUnreadNoticeCount();

                        if (nCountUnreadNotice==0) {
                            m_Drawer.updateBadge(CaEngine.MENU_NOTICE, null);
                        }
                        else {
                            String strCount=Integer.toString(nCountUnreadNotice);
                            m_Drawer.updateBadge(CaEngine.MENU_NOTICE, new StringHolder(strCount));
                        }

                        // qna badge
                        int nCountUnreadQna=CaApplication.m_Info.getUnreadQnaCount();

                        if (nCountUnreadQna==0) {
                            m_Drawer.updateBadge(CaEngine.MENU_QNA, null);
                        }
                        else {
                            String strCount=Integer.toString(nCountUnreadQna);
                            m_Drawer.updateBadge(CaEngine.MENU_QNA, new StringHolder(strCount));
                        }

                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        Log.d("Drawer", "onDrawerClosed called...");
                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        if (m_Drawer.isDrawerOpen()) Log.d("Drawer", "onDrawerSlide called...in Drawer open state");
                        else Log.d("Drawer", "onDrawerSlide called...in Drawer close state");
                    }
                })
                .build();

    }
}