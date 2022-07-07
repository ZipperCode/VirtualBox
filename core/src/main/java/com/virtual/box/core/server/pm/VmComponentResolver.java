package com.virtual.box.core.server.pm;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Debug;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import androidx.core.content.PackageManagerCompat;

import com.virtual.box.core.helper.PackageHelper;
import com.virtual.box.core.server.pm.resolve.IntentResolver;
import com.virtual.box.core.server.pm.resolve.VmPackage;

import java.util.ArrayList;
import java.util.List;


/**
 * 应用组件解析程序
 */
public class VmComponentResolver {
    public static final String TAG = "ComponentResolver";

    private final Object mLock = new Object();

    /**
     * All available activities, for your resolving pleasure.
     */
    private final ActivityIntentResolver mActivities = new ActivityIntentResolver();

    /**
     * All available providers, for your resolving pleasure.
     */
    private final ProviderIntentResolver mProviders = new ProviderIntentResolver();

    /**
     * All available receivers, for your resolving pleasure.
     */
    private final ActivityIntentResolver mReceivers = new ActivityIntentResolver();

    /**
     * All available services, for your resolving pleasure.
     */
    private final ServiceIntentResolver mServices = new ServiceIntentResolver();
    /**
     * Mapping from provider authority [first directory in content URI codePath) to provider.
     */
    private final ArrayMap<String, VmPackage.Provider> mProvidersByAuthority = new ArrayMap<>();

    public VmComponentResolver() { }

    /**
     * 添加包内的所有组件
     * @param vmPackage 自实现包信息
     */
    public void addAllComponents(VmPackage vmPackage) {
        final ArrayList<VmPackage.ActivityIntentInfo> newIntents = new ArrayList<>();
        synchronized (mLock) {
            addActivitiesLocked(vmPackage, newIntents);
            addServicesLocked(vmPackage);
            addProvidersLocked(vmPackage);
            addReceiversLocked(vmPackage);
        }
    }

    /**
     * 通过包移除组件
     * @param vmPackage 自实现包信息
     */
    public void removeAllComponents(VmPackage vmPackage) {
        synchronized (mLock) {
            removeAllComponentsLocked(vmPackage);
        }
    }

    private void removeAllComponentsLocked(VmPackage aPackage) {
        int componentSize;
        StringBuilder r;
        int i;

        componentSize = aPackage.activities.size();
        r = null;
        for (i = 0; i < componentSize; i++) {
            VmPackage.Activity a = aPackage.activities.get(i);
            mActivities.removeActivity(a, "activity");
        }
        componentSize = aPackage.providers.size();
        r = null;
        for (i = 0; i < componentSize; i++) {
            VmPackage.Provider p = aPackage.providers.get(i);
            mProviders.removeProvider(p);
            if (p.info.authority == null) {
                // Another content provider with this authority existed when this app was
                // installed, so this authority is null. Ignore it as we don't have to
                // unregister the provider.
                continue;
            }
            String[] names = p.info.authority.split(";");
            for (int j = 0; j < names.length; j++) {
                if (mProvidersByAuthority.get(names[j]) == p) {
                    mProvidersByAuthority.remove(names[j]);
                }
            }
        }

        componentSize = aPackage.receivers.size();
        r = null;
        for (i = 0; i < componentSize; i++) {
            VmPackage.Activity a = aPackage.receivers.get(i);
            mReceivers.removeActivity(a, "receiver");
        }

        componentSize = aPackage.services.size();
        r = null;
        for (i = 0; i < componentSize; i++) {
            VmPackage.Service s = aPackage.services.get(i);
            mServices.removeService(s);
        }
    }

    private void addActivitiesLocked(VmPackage vmPackage,
                                     List<VmPackage.ActivityIntentInfo> newIntents) {
        final int activitiesSize = vmPackage.activities.size();
        for (int i = 0; i < activitiesSize; i++) {
            VmPackage.Activity a = vmPackage.activities.get(i);
            if (TextUtils.isEmpty(a.info.processName)){
                a.info.processName = vmPackage.applicationInfo.processName;
            }
            mActivities.addActivity(a, "activity", newIntents);
        }
    }

    private void addProvidersLocked(VmPackage vmPackage) {
        final int providersSize = vmPackage.providers.size();
        for (int i = 0; i < providersSize; i++) {
            VmPackage.Provider p = vmPackage.providers.get(i);
            if (TextUtils.isEmpty(p.info.processName)){
                p.info.processName = vmPackage.applicationInfo.processName;
            }
            mProviders.addProvider(p);
            if (p.info.authority != null) {
                String[] names = p.info.authority.split(";");
                p.info.authority = null;
                for (String name : names) {
                    if (!mProvidersByAuthority.containsKey(name)) {
                        mProvidersByAuthority.put(name, p);
                        if (p.info.authority == null) {
                            p.info.authority = name;
                        } else {
                            p.info.authority = p.info.authority + ";" + name;
                        }
                    } else {
                        final VmPackage.Provider other =
                                mProvidersByAuthority.get(name);
                        final ComponentName component =
                                (other != null && other.getComponentName() != null)
                                        ? other.getComponentName() : null;
                        final String packageName =
                                component != null ? component.getPackageName() : "?";

                        Log.println(Log.WARN, TAG, "Skipping provider name " + name
                                + " (in package " + vmPackage.applicationInfo.packageName + ")"
                                + ": name already used by " + packageName);
                    }
                }
            }
        }
    }

    private void addReceiversLocked(VmPackage vmPackage) {
        final int receiversSize = vmPackage.receivers.size();
        for (int i = 0; i < receiversSize; i++) {
            VmPackage.Activity a = vmPackage.receivers.get(i);
            if (TextUtils.isEmpty(a.info.processName)){
                a.info.processName = vmPackage.applicationInfo.processName;
            }
            mReceivers.addActivity(a, "receiver", null);
        }
    }

    private void addServicesLocked(VmPackage vmPackage) {
        final int servicesSize = vmPackage.services.size();
        for (int i = 0; i < servicesSize; i++) {
            VmPackage.Service s = vmPackage.services.get(i);
            if (TextUtils.isEmpty(s.info.processName)){
                s.info.processName = vmPackage.applicationInfo.processName;
            }
            mServices.addService(s);
        }
    }


    /**
     * Returns the given activity
     */
    public VmPackage.Activity getActivity(ComponentName component) {
        synchronized (mLock) {
            return mActivities.mActivities.get(component);
        }
    }

    /**
     * Returns the given provider
     */
    public VmPackage.Provider getProvider(ComponentName component) {
        synchronized (mLock) {
            return mProviders.mProviders.get(component);
        }
    }

    /**
     * Returns the given receiver
     */
    public VmPackage.Activity getReceiver(ComponentName component) {
        synchronized (mLock) {
            return mReceivers.mActivities.get(component);
        }
    }

    /**
     * Returns the given service
     */
    public VmPackage.Service getService(ComponentName component) {
        synchronized (mLock) {
            return mServices.mServices.get(component);
        }
    }

    public List<ResolveInfo> queryActivities(Intent intent, String resolvedType, int flags, int userId) {
        synchronized (mLock) {
            return mActivities.queryIntent(intent, resolvedType, flags, userId);
        }
    }

    public List<ResolveInfo> queryActivities(Intent intent, String resolvedType, int flags,
                                      List<VmPackage.Activity> activities, int userId) {
        synchronized (mLock) {
            return mActivities.queryIntentForPackage(
                    intent, resolvedType, flags, activities, userId);
        }
    }

    public List<ResolveInfo> queryProviders(Intent intent, String resolvedType, int flags, int userId) {
        synchronized (mLock) {
            return mProviders.queryIntent(intent, resolvedType, flags, userId);
        }
    }

    public List<ResolveInfo> queryProviders(Intent intent, String resolvedType, int flags,
                                     List<VmPackage.Provider> providers, int userId) {
        synchronized (mLock) {
            return mProviders.queryIntentForPackage(intent, resolvedType, flags, providers, userId);
        }
    }

    public List<ProviderInfo> queryProviders(String processName, String metaDataKey, int flags,
                                      int userId) {
        List<ProviderInfo> providerList = new ArrayList<>();
        synchronized (mLock) {
            for (int i = mProviders.mProviders.size() - 1; i >= 0; --i) {
                final VmPackage.Provider p = mProviders.mProviders.valueAt(i);
                if (p.info.authority == null) {
                    continue;
                }
                if (processName != null && (!p.info.processName.equals(processName))) {
                    continue;
                }
                // See PM.queryContentProviders()'s javadoc for why we have the metaData parameter.
                if (metaDataKey != null
                        && (p.metaData == null || !p.metaData.containsKey(metaDataKey))) {
                    continue;
                }
                final ProviderInfo info = PackageHelper.generateProviderInfo(p, flags, userId);
                if (info == null) {
                    continue;
                }
//                if (providerList == null) {
//                    providerList = new ArrayList<>(i + 1);
//                }
                providerList.add(info);
            }
        }
        return providerList;
    }

    public ProviderInfo queryProvider(String authority, int flags, int userId) {
        synchronized (mLock) {
            final VmPackage.Provider p = mProvidersByAuthority.get(authority);
            if (p == null) {
                return null;
            }
            return PackageHelper.generateProviderInfo(p, flags, userId);
        }
    }

    public List<ResolveInfo> queryReceivers(Intent intent, String resolvedType, int flags, int userId) {
        synchronized (mLock) {
            return mReceivers.queryIntent(intent, resolvedType, flags, userId);
        }
    }

    public List<ResolveInfo> queryReceivers(Intent intent, String resolvedType, int flags,
                                     List<VmPackage.Activity> receivers, int userId) {
        synchronized (mLock) {
            return mReceivers.queryIntentForPackage(intent, resolvedType, flags, receivers, userId);
        }
    }

    public List<ResolveInfo> queryServices(Intent intent, String resolvedType, int flags, int userId) {
        synchronized (mLock) {
            return mServices.queryIntent(intent, resolvedType, flags, userId);
        }
    }

    public List<ResolveInfo> queryServices(Intent intent, String resolvedType, int flags,
                                    List<VmPackage.Service> services, int userId) {
        synchronized (mLock) {
            return mServices.queryIntentForPackage(intent, resolvedType, flags, services, userId);
        }
    }


    private static final class ServiceIntentResolver extends IntentResolver<VmPackage.ServiceIntentInfo, ResolveInfo> {

        @Override
        public List<ResolveInfo> queryIntent(Intent intent, String resolvedType,
                                             boolean defaultOnly, int userId) {
            mFlags = defaultOnly ? PackageManager.MATCH_DEFAULT_ONLY : 0;
            return super.queryIntent(intent, resolvedType, defaultOnly, userId);
        }

        List<ResolveInfo> queryIntent(Intent intent, String resolvedType, int flags,
                                      int userId) {
            mFlags = flags;
            return super.queryIntent(intent, resolvedType,
                    (flags & PackageManager.MATCH_DEFAULT_ONLY) != 0,
                    userId);
        }

        List<ResolveInfo> queryIntentForPackage(Intent intent, String resolvedType,
                                                int flags, List<VmPackage.Service> packageServices, int userId) {
            if (packageServices == null) {
                return null;
            }
            mFlags = flags;
            final boolean defaultOnly = (flags & PackageManager.MATCH_DEFAULT_ONLY) != 0;
            final int servicesSize = packageServices.size();
            ArrayList<VmPackage.ServiceIntentInfo[]> listCut = new ArrayList<>(servicesSize);

            ArrayList<VmPackage.ServiceIntentInfo> intentFilters;
            for (int i = 0; i < servicesSize; ++i) {
                intentFilters = packageServices.get(i).intents;
                if (intentFilters != null && intentFilters.size() > 0) {
                    VmPackage.ServiceIntentInfo[] array =
                            new VmPackage.ServiceIntentInfo[intentFilters.size()];
                    intentFilters.toArray(array);
                    listCut.add(array);
                }
            }
            return super.queryIntentFromList(intent, resolvedType, defaultOnly, listCut, userId);
        }

        void addService(VmPackage.Service s) {
            mServices.put(s.getComponentName(), s);
            final int intentsSize = s.intents.size();
            int j;
            for (j = 0; j < intentsSize; j++) {
                VmPackage.ServiceIntentInfo intent = s.intents.get(j);
                addFilter(intent);
            }
        }

        void removeService(VmPackage.Service s) {
            mServices.remove(s.getComponentName());
            final int intentsSize = s.intents.size();
            int j;
            for (j = 0; j < intentsSize; j++) {
                VmPackage.ServiceIntentInfo intent = s.intents.get(j);
                removeFilter(intent);
            }
        }

        @Override
        protected boolean isPackageForFilter(String packageName,
                                             VmPackage.ServiceIntentInfo info) {
            return packageName.equals(info.service.owner.packageName);
        }

        @Override
        protected VmPackage.ServiceIntentInfo[] newArray(int size) {
            return new VmPackage.ServiceIntentInfo[size];
        }

        @Override
        protected ResolveInfo newResult(VmPackage.ServiceIntentInfo filter, int match, int userId) {
            final VmPackage.ServiceIntentInfo info = (VmPackage.ServiceIntentInfo) filter;
            final VmPackage.Service service = info.service;
            ServiceInfo si = PackageHelper.generateServiceInfo(service, mFlags, userId);

            final ResolveInfo res = new ResolveInfo();
            res.serviceInfo = si;
            if ((mFlags & PackageManager.GET_RESOLVED_FILTER) != 0) {
                res.filter = filter.intentFilter;
            }
            res.priority = info.intentFilter.getPriority();
            res.preferredOrder = service.owner.mPreferredOrder;
            res.match = match;
            res.isDefault = info.hasDefault;
            res.labelRes = info.labelRes;
            res.nonLocalizedLabel = info.nonLocalizedLabel;
            res.icon = info.icon;
            return res;
        }

        // Keys are String (activity class name), values are Activity.
        private final ArrayMap<ComponentName, VmPackage.Service> mServices = new ArrayMap<>();
        private int mFlags;
    }


    private static final class ActivityIntentResolver extends IntentResolver<VmPackage.ActivityIntentInfo, ResolveInfo> {

        @Override
        public List<ResolveInfo> queryIntent(Intent intent, String resolvedType,
                                             boolean defaultOnly, int userId) {
            mFlags = (defaultOnly ? PackageManager.MATCH_DEFAULT_ONLY : 0);
            return super.queryIntent(intent, resolvedType, defaultOnly, userId);
        }

        List<ResolveInfo> queryIntent(Intent intent, String resolvedType, int flags,
                                      int userId) {
            mFlags = flags;
            return super.queryIntent(intent, resolvedType,
                    (flags & PackageManager.MATCH_DEFAULT_ONLY) != 0,
                    userId);
        }

        List<ResolveInfo> queryIntentForPackage(Intent intent, String resolvedType,
                                                int flags, List<VmPackage.Activity> packageActivities, int userId) {
            if (packageActivities == null) {
                return null;
            }
            mFlags = flags;
            final boolean defaultOnly = (flags & PackageManager.MATCH_DEFAULT_ONLY) != 0;
            final int activitiesSize = packageActivities.size();
            ArrayList<VmPackage.ActivityIntentInfo[]> listCut = new ArrayList<>(activitiesSize);

            ArrayList<VmPackage.ActivityIntentInfo> intentFilters;
            for (int i = 0; i < activitiesSize; ++i) {
                intentFilters = packageActivities.get(i).intents;
                if (intentFilters != null && intentFilters.size() > 0) {
                    VmPackage.ActivityIntentInfo[] array =
                            new VmPackage.ActivityIntentInfo[intentFilters.size()];
                    intentFilters.toArray(array);
                    listCut.add(array);
                }
            }
            return super.queryIntentFromList(intent, resolvedType, defaultOnly, listCut, userId);
        }

        private void addActivity(VmPackage.Activity a, String type,
                                 List<VmPackage.ActivityIntentInfo> newIntents) {
            mActivities.put(a.getComponentName(), a);
            final int intentsSize = a.intents.size();
            for (int j = 0; j < intentsSize; j++) {
                VmPackage.ActivityIntentInfo intent = a.intents.get(j);
                if (newIntents != null && "activity".equals(type)) {
                    newIntents.add(intent);
                    addFilter(intent);
                }
            }
        }

        private void removeActivity(VmPackage.Activity a, String type) {
            mActivities.remove(a.getComponentName());
            final int intentsSize = a.intents.size();
            for (int j = 0; j < intentsSize; j++) {
                VmPackage.ActivityIntentInfo intent = a.intents.get(j);
                removeFilter(intent);
            }
        }

        @Override
        protected boolean isPackageForFilter(String packageName,
                                             VmPackage.ActivityIntentInfo info) {
            return packageName.equals(info.activity.owner.packageName);
        }

        @Override
        protected VmPackage.ActivityIntentInfo[] newArray(int size) {
            return new VmPackage.ActivityIntentInfo[size];
        }

        @Override
        protected ResolveInfo newResult(VmPackage.ActivityIntentInfo info, int match, int userId) {
            final VmPackage.Activity activity = info.activity;
            ActivityInfo ai = PackageHelper.generateActivityInfo(activity, mFlags,userId);
            final ResolveInfo res = new ResolveInfo();
            res.activityInfo = ai;
            if ((mFlags & PackageManager.GET_RESOLVED_FILTER) != 0) {
                res.filter = info.intentFilter;
            }
            res.priority = info.intentFilter.getPriority();
            res.preferredOrder = activity.owner.mPreferredOrder;
            //System.out.println("Result: " + res.activityInfo.className +
            //                   " = " + res.priority);
            res.match = match;
            res.isDefault = info.hasDefault;
            res.labelRes = info.labelRes;
            res.nonLocalizedLabel = info.nonLocalizedLabel;
            res.icon = info.icon;
            return res;
        }

        // Keys are String (activity class name), values are Activity.
        private final ArrayMap<ComponentName, VmPackage.Activity> mActivities =
                new ArrayMap<>();
        private int mFlags;
    }

    private static final class ProviderIntentResolver
            extends IntentResolver<VmPackage.ProviderIntentInfo, ResolveInfo> {
        @Override
        public List<ResolveInfo> queryIntent(Intent intent, String resolvedType,
                                             boolean defaultOnly, int userId) {
            mFlags = defaultOnly ? PackageManager.MATCH_DEFAULT_ONLY : 0;
            return super.queryIntent(intent, resolvedType, defaultOnly, userId);
        }

        List<ResolveInfo> queryIntent(Intent intent, String resolvedType, int flags,
                                      int userId) {
            mFlags = flags;
            return super.queryIntent(intent, resolvedType,
                    (flags & PackageManager.MATCH_DEFAULT_ONLY) != 0,
                    userId);
        }

        List<ResolveInfo> queryIntentForPackage(Intent intent, String resolvedType,
                                                int flags, List<VmPackage.Provider> packageProviders, int userId) {
            if (packageProviders == null) {
                return null;
            }
            mFlags = flags;
            final boolean defaultOnly = (flags & PackageManager.MATCH_DEFAULT_ONLY) != 0;
            final int providersSize = packageProviders.size();
            ArrayList<VmPackage.ProviderIntentInfo[]> listCut = new ArrayList<>(providersSize);

            ArrayList<VmPackage.ProviderIntentInfo> intentFilters;
            for (int i = 0; i < providersSize; ++i) {
                intentFilters = packageProviders.get(i).intents;
                if (intentFilters != null && intentFilters.size() > 0) {
                    VmPackage.ProviderIntentInfo[] array =
                            new VmPackage.ProviderIntentInfo[intentFilters.size()];
                    intentFilters.toArray(array);
                    listCut.add(array);
                }
            }
            return super.queryIntentFromList(intent, resolvedType, defaultOnly, listCut, userId);
        }

        void addProvider(VmPackage.Provider p) {
            mProviders.put(p.getComponentName(), p);
            final int intentsSize = p.intents.size();
            int j;
            for (j = 0; j < intentsSize; j++) {
                VmPackage.ProviderIntentInfo intent = p.intents.get(j);
                addFilter(intent);
            }
        }

        void removeProvider(VmPackage.Provider p) {
            mProviders.remove(p.getComponentName());
            final int intentsSize = p.intents.size();
            int j;
            for (j = 0; j < intentsSize; j++) {
                VmPackage.ProviderIntentInfo intent = p.intents.get(j);
                removeFilter(intent);
            }
        }

        @Override
        protected boolean allowFilterResult(
                VmPackage.ProviderIntentInfo filter, List<ResolveInfo> dest) {
            ProviderInfo filterPi = filter.provider.info;
            for (int i = dest.size() - 1; i >= 0; i--) {
                ProviderInfo destPi = dest.get(i).providerInfo;
                if (destPi.name.equals(filterPi.name)
                        && destPi.packageName.equals(filterPi.packageName)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        protected VmPackage.ProviderIntentInfo[] newArray(int size) {
            return new VmPackage.ProviderIntentInfo[size];
        }

        @Override
        protected boolean isPackageForFilter(String packageName,
                                             VmPackage.ProviderIntentInfo info) {
            return packageName.equals(info.provider.owner.packageName);
        }

        @Override
        protected ResolveInfo newResult(VmPackage.ProviderIntentInfo filter, int match, int userId) {
            final VmPackage.ProviderIntentInfo info = filter;
            final VmPackage.Provider provider = info.provider;

            ProviderInfo pi = PackageHelper.generateProviderInfo(provider, mFlags, userId);
            final ResolveInfo res = new ResolveInfo();
            res.providerInfo = pi;
            if ((mFlags & PackageManager.GET_RESOLVED_FILTER) != 0) {
                res.filter = filter.intentFilter;
            }
            res.priority = info.intentFilter.getPriority();
            res.preferredOrder = provider.owner.mPreferredOrder;
            res.match = match;
            res.isDefault = info.hasDefault;
            res.labelRes = info.labelRes;
            res.nonLocalizedLabel = info.nonLocalizedLabel;
            res.icon = info.icon;
            return res;
        }

        private final ArrayMap<ComponentName, VmPackage.Provider> mProviders = new ArrayMap<>();
        private int mFlags;
    }
}
