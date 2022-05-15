package com.virtual.box

import android.content.ComponentName
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.virtual.box.base.util.AppExecutors
import com.virtual.box.core.VirtualBox
import com.virtual.box.core.server.pm.entity.VmPackageInstallOption
import java.io.File
import java.io.FileOutputStream

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppExecutors.get().executeMultiThreadWithLock{
            val file = File(filesDir,"test1.apk")
            assets.open("test1.apk").use { input ->
                FileOutputStream(file).use { output ->
                    output.write(input.readBytes())
                    output.flush()
                }
            }
            val installedPackageInfo = VirtualBox.get().getInstalledPackageInfo()

            findViewById<Button>(R.id.btn_install).setOnClickListener {
                VirtualBox.get().installPackage(VmPackageInstallOption.installByStorage(file.absolutePath))
            }

            findViewById<Button>(R.id.btn_launch).setOnClickListener {
                val intent = Intent()
                intent.component = ComponentName("com.sinyee.babybus.world","com.sinyee.babybus.SplashAct")
                VirtualBox.get().launchApp(intent)
            }
        }

    }
}