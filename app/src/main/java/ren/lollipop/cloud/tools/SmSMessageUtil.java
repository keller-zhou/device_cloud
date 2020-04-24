package ren.lollipop.cloud.tools;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.provider.ContactsContract;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ren.lollipop.cloud.model.SmSMessageBean;

/**
 * 获取短信信息
 */

public class SmSMessageUtil {


    /**
     * 获取短信数据
     *
     * @param lastPostTime 最后一次提交到数据库的时间
     * @return
     */
    public static List<SmSMessageBean> getSmsInPhone(long lastPostTime, Context context) {
        //TODO:是否加权限判断
        List<SmSMessageBean> messageList = new ArrayList<>();

        final String SMS_URI_ALL = "content://sms/";
        final String SMS_URI_INBOX = "content://sms/inbox";
        final String SMS_URI_SEND = "content://sms/sent";
        final String SMS_URI_DRAFT = "content://sms/draft";

        StringBuilder smsBuilder = new StringBuilder();

        try {
            ContentResolver cr = context.getContentResolver();
            String[] projection = new String[]{"_id", "address", "person",
                    "body", "date", "type"};
            if (lastPostTime == 0) {
                lastPostTime = System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000l;//只查询10天的
            }

            String where = " date >  "
                    + lastPostTime;
            Uri uri = Uri.parse(SMS_URI_ALL);
            Cursor cur = cr.query(uri, projection, where, null, "date desc");

            if (cur != null && cur.moveToFirst()) {
                String name;
                String phoneNumber;
                String smsbody;
                String date;
                String type;

                int phoneNumberColumn = cur.getColumnIndex("address");
                int smsbodyColumn = cur.getColumnIndex("body");
                int dateColumn = cur.getColumnIndex("date");
                int typeColumn = cur.getColumnIndex("type");

                do {
                    phoneNumber = cur.getString(phoneNumberColumn);
                    smsbody = cur.getString(smsbodyColumn);

                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyy-MM-dd hh:mm:ss");
                    Date d = new Date(Long.parseLong(cur.getString(dateColumn)));
                    date = dateFormat.format(d);

                    int typeId = cur.getInt(typeColumn);
                    if (typeId == 1) {
                        type = "1";
                    } else {
                        type = "2";
                    }

                    name = getPeopleNameFromPerson(phoneNumber, context);

                    SmSMessageBean message = new SmSMessageBean();
                    message.setName(name);
                    message.setType(type);
                    message.setPhoneNumber(phoneNumber);
                    message.setSmsBody(smsbody);
                    message.setDate(date);
                    messageList.add(message);
                    if (smsbody == null) smsbody = "";
                } while (cur.moveToNext());
            } else {
            }

        } catch (SQLiteException ex) {
        }
        return messageList;
    }


    // 通过address手机号关联Contacts联系人的显示名字
    private static String getPeopleNameFromPerson(String address, Context context) {
        if (address == null || address == "") {
            return "( no address )\n";
        }

        String strPerson = "null";
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};

        Uri uri_Person = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI, address);  // address 手机号过滤
        Cursor cursor = context.getContentResolver().query(uri_Person, projection, null, null, null);

        if (cursor.moveToFirst()) {
            int index_PeopleName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            String strPeopleName = cursor.getString(index_PeopleName);
            strPerson = strPeopleName;
        }
        cursor.close();

        return strPerson;
    }
}
