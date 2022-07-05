## uid、userId、appId 区别
+ uid (应用包相关)
应用安装过程中生成的，applicationInfo.uid 当前用户中同一应用程序中uid是共享的，当Manifest中申明了sharedUserId时，uid也可以在多个应用程序间共享
uid的取值：`Process.FIRST_APPLICATION_UID = 10000` < uid < `Process.LAST_APPLICATION_UID`
  
+ userId (系统用户相关)
系统中的用户，多用户情况下，如机主、访客等
虚拟化中，实现双开应用时，应用被分配在不用的用户下（虚拟用户）

+ appId (应用相关但与系统用户无关——包名相同即为同一个appId)


install_[userId] > MMKV 存储安装包的key

app_data_[userId] > MMKV 存储的用户数据key


install_[userId]_[pkg] > 安装包数据

app_data_index_0 > [app_data_0_{pkg}_{UUID} > 系统的用户数据索引

app_data_[userId]_[pkg] > 包的用户数据_

+ 包管理
    + 用户安装列表
        + 系统用户
            + 包1
            + 包2
            + 包3
        + 用户1
            + 包1
            + 包2
            + 包3

    + 用户数据列表
        + 系统用户
            + data1
            + data2
            + data3
        + 用户1
            + data1
            + data2
            + data3