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
    public interface AdminControl{
        String DEVICE_ADMIN_DISABLE = "com.android.settings/.applications.specialaccess.deviceadmin.DeviceAdminAdd";
        List<String> SUPPORTED_LIST = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add(DEVICE_ADMIN_DISABLE);
            }
        });
    }

    public interface MessagingQuery{
        String SAMSUNG_MESSENGER = "com.samsung.android.messaging";
        String GOOGLE_MESSENGER = "com.google.android.apps.messaging";
        String SAMSUNG_MESSENGER_CONVERSATION = "com.android.mms.ui.ConversationComposer";
        String GOOGLE_MESSENGER_CONVERSATION = "com.google.android.apps.messaging.ui.conversation.ConversationActivity";
        List<String> SUPPORTED_LIST = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add(SAMSUNG_MESSENGER);
                add(GOOGLE_MESSENGER);
                add(SAMSUNG_MESSENGER_CONVERSATION);
                add(GOOGLE_MESSENGER_CONVERSATION);
            }
        });
    }
    public interface DiallerQuery{
        String SAMSUNG_DIALER = "com.samsung.android.incallui";
        String GOOGLE_DIALER = "com.google.android.dialer";
        String ANDROID_DIALER = "com.android.dialer";
        List<String> SUPPORTED_LIST = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add(SAMSUNG_DIALER);
                add(GOOGLE_DIALER);
                add(ANDROID_DIALER);
            }
        });
    }

    public interface TelecomApps {
        String SAMSUNG_DIALER = "com.samsung.android.incallui";
        String GOOGLE_DIALER = "com.google.android.dialer";
        String ANDROID_DIALER = "com.android.dialer";
        String SAMSUNG_MESSENGER = "com.samsung.android.messaging";
        String GOOGLE_MESSENGER = "com.google.android.apps.messaging";

        List<String> SUPPORTED_LIST = Collections.unmodifiableList(new ArrayList<String>() {
            {
                add(SAMSUNG_DIALER);
                add(GOOGLE_DIALER);
                add(ANDROID_DIALER);
                add(SAMSUNG_MESSENGER);
                add(GOOGLE_MESSENGER);
            }
        });
    }
}
