package ru.liner.sodadrunk.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author : "Line'R"
 * @mailto : serinity320@mail.com
 * @created : 16.04.2022, суббота
 **/
public class WatchingApps {
    public interface AdminControl {
        String DEVICE_ADMIN_DISABLE = "com.android.settings/.applications.specialaccess.deviceadmin.DeviceAdminAdd";
        String DEVICE_ADMIN_DISABLE2 = "com.android.settings/com.android.settings.applications.specialaccess.deviceadmin.DeviceAdminAdd";
        List<String> SUPPORTED_LIST = Collections.synchronizedList(new ArrayList<String>() {
            {
                add(DEVICE_ADMIN_DISABLE);
                add(DEVICE_ADMIN_DISABLE2);
            }
        });
    }

    public interface MessagingQuery {
        String SAMSUNG_MESSENGER = "com.samsung.android.messaging";
        String GOOGLE_MESSENGER = "com.google.android.apps.messaging";
        String SAMSUNG_MESSENGER_CONVERSATION = "com.android.mms.ui.ConversationComposer";
        String GOOGLE_MESSENGER_CONVERSATION = "com.google.android.apps.messaging.ui.conversation.ConversationActivity";
        String HUAWEI_MESSENGER = "com.android.mms.ui.ConversationList";
        String HUAWEI_MESSENGER_VER1 = "com.android.mms.ui.ComposeMessageActivity";
        List<String> SUPPORTED_LIST = Collections.synchronizedList(new ArrayList<String>() {
            {
                add(SAMSUNG_MESSENGER);
                add(GOOGLE_MESSENGER);
                add(SAMSUNG_MESSENGER_CONVERSATION);
                add(GOOGLE_MESSENGER_CONVERSATION);
                add(HUAWEI_MESSENGER);
                add(HUAWEI_MESSENGER_VER1);
            }
        });
    }

    public interface DiallerQuery {
        String DEFAULT_DIALER = "com.android.dialer/com.android.dialer.app.DialtactsActivity";
        String GOOGLE_DIALER = "com.google.android.dialer/com.google.android.dialer.extensions.GoogleDialtactsActivity";
        String ASUS_DIALER = "com.asus.contacts/com.android.contacts.activities.DialtactsActivity";
        String HTC_DIALER = "com.android.incallui/com.android.incallui.DialerLauncher";
        String HUAWEI_DIALER = "com.android.contacts/com.android.contacts.activities.DialtactsActivity";
        String LENOVO_DIALER = "com.lenovo.ideafriend/com.lenovo.ideafriend.alias.DialtactsActivity";
        String ONEPLUS_DIALER = "com.android.dialer/com.oneplus.contacts.activities.OPDialtactsActivity";
        String SAMSUNG_DIALER = "com.samsung.android.contacts/com.android.dialer.DialtactsActivity";
        String SONY_DIALER = "com.sonymobile.android.dialer/com.android.dialer.DialtactsActivity";
        String SONY_DIALER_COMPAT = "com.sonymobile.android.dialer/com.android.dialer.app.DialtactsActivity";
        String XIAOMI_DIALER = "com.android.contacts/com.android.contacts.TwelveKeyDialer";
        String XIAOMI_DIALER_COMPAT = "com.android.contacts/com.android.contacts.activities.TwelveKeyDialer";


        List<String> ACTIVITY_LIST = Collections.synchronizedList(new ArrayList<String>() {
            {
                add(DEFAULT_DIALER);
                add(GOOGLE_DIALER);
                add(ASUS_DIALER);
                add(HTC_DIALER);
                add(HUAWEI_DIALER);
                add(LENOVO_DIALER);
                add(ONEPLUS_DIALER);
                add(SAMSUNG_DIALER);
                add(SONY_DIALER);
                add(SONY_DIALER_COMPAT);
                add(XIAOMI_DIALER);
                add(XIAOMI_DIALER_COMPAT);
            }
        });
        List<String> PACKAGE_LIST = Collections.synchronizedList(new ArrayList<String>() {
            {
                add(DEFAULT_DIALER.split("/")[0]);
                add(GOOGLE_DIALER.split("/")[0]);
                add(ASUS_DIALER.split("/")[0]);
                add(HTC_DIALER.split("/")[0]);
                add(HUAWEI_DIALER.split("/")[0]);
                add(LENOVO_DIALER.split("/")[0]);
                add(ONEPLUS_DIALER.split("/")[0]);
                add(SAMSUNG_DIALER.split("/")[0]);
                add(SONY_DIALER.split("/")[0]);
                add(SONY_DIALER_COMPAT.split("/")[0]);
                add(XIAOMI_DIALER.split("/")[0]);
                add(XIAOMI_DIALER_COMPAT.split("/")[0]);
            }
        });
    }
}
