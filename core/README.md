## uid、userId、appId 区别
+ uid (应用包相关)
应用安装过程中生成的，applicationInfo.uid 当前用户中同一应用程序中uid是共享的，当Manifest中申明了sharedUserId时，uid也可以在多个应用程序间共享
uid的取值：`Process.FIRST_APPLICATION_UID = 10000` < uid < `Process.LAST_APPLICATION_UID`
  
+ userId (系统用户相关)
系统中的用户，多用户情况下，如机主、访客等
虚拟化中，实现双开应用时，应用被分配在不用的用户下（虚拟用户）

+ appId (应用相关但与系统用户无关——包名相同即为同一个appId)