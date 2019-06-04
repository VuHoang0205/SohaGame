package com.s.sdk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LEGEND on 11/30/2017.
 */

public class PermissionUtils {
    public static boolean requestPermissionFragment(final Fragment fragment, final String permission, final int requestCodeAskPermission) {
        int hasReadExternalPermission = ContextCompat.checkSelfPermission(fragment.getActivity(),
                permission);
        //neu permission bi deny thi hoi lai
        if (hasReadExternalPermission != PackageManager.PERMISSION_GRANTED) {
            fragment.requestPermissions(new String[]{permission}, requestCodeAskPermission);
            return false;
        }
        return true;
    }

    public static boolean requestListPermissionFragment(final Fragment fragment, final String[] listPermission, final int requestCodeAskPermission) {
        final List<String> permissionsNeeded = new ArrayList<>();
        for (int i = 0, leng = listPermission.length; i < leng; i++) {
            addPermission(fragment.getActivity(), permissionsNeeded, listPermission[i]);
        }
        if (permissionsNeeded.size() > 0) {
            fragment.requestPermissions(permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                    requestCodeAskPermission);
            return false;
        }
        return true;
    }
    private static void addPermission(Context context, List<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
        }
    }

    public static boolean requestPermissionActivity(final Activity activity, final String permission, final int requestCodeAskPermission) {
        int hasReadExternalPermission = ContextCompat.checkSelfPermission(activity,
                permission);
        //neu permission bi deny thi hoi lai
        if (hasReadExternalPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{permission},
                    requestCodeAskPermission);
            return false;
        }
        return true;
    }
}
