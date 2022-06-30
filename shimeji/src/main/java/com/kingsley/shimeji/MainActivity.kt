package com.kingsley.shimeji

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import com.kingsley.shimeji.data.TeamListingService
import com.kingsley.shimeji.databinding.MainActivityLayoutBinding
import com.kingsley.shimeji.mascot.animations.Animation
import com.kingsley.shimeji.mascotlibrary.MascotListing
import com.kingsley.shimeji.purchases.PayFeatures.list
import com.kingsley.shimeji.service.ShimejiService
import org.solovyev.android.checkout.*
import org.solovyev.android.checkout.Checkout.EmptyListener
import org.solovyev.android.checkout.Inventory.Products
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {
    private val mCheckout = Checkout.forActivity(this, MyApplication.get().billing)
    private var mCropImageUri: Uri? = null
    private var mDrawerTitle: CharSequence? = null
    private var mTitle: CharSequence? = null
    var mascots: List<MascotListing>? = null
    private var teamService: TeamListingService? = null
    private fun copyToInternalStorage(paramFile: File) {
        val str = AppConstants.BACKGROUND_FILE_NAME
        try {
            val fileInputStream = FileInputStream(paramFile)
            val fileOutputStream: FileOutputStream = openFileOutput(str, 0)
            val arrayOfByte = ByteArray(1024)
            while (true) {
                val i: Int = fileInputStream.read(arrayOfByte)
                if (i > 0) {
                    fileOutputStream.write(arrayOfByte, 0, i)
                    continue
                }
                fileInputStream.close()
                fileOutputStream.close()
                return
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    private val permission: Unit
        get() {
            if (Build.VERSION.SDK_INT >= 23) {
                val stringBuilder = StringBuilder()
                stringBuilder.append("package:")
                stringBuilder.append(packageName)
                startActivityForResult(
                    Intent(
                        "android.settings.action.MANAGE_OVERLAY_PERMISSION",
                        Uri.parse(stringBuilder.toString())
                    ), 5463
                )
            }
        }

    private lateinit var mViewBinding: MainActivityLayoutBinding

    private fun hasPermissionToDraw(): Boolean {
        return Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this)
    }

    private fun warnIfNoShimejiAreSelected() {
//        if (teamService?.activeMascots?.size ?: 0 < 1) Toast.makeText(this, getString(2131558443), 1).show()
    }

    fun checkInventory() {
        mCheckout.makeInventory()
            .load(Inventory.Request.create().loadAllPurchases().loadSkus("inapp", list), InventoryCallback())
    }

    override fun onActivityResult(paramInt1: Int, paramInt2: Int, paramIntent: Intent?) {
        super.onActivityResult(paramInt1, paramInt2, paramIntent)
        mCheckout.onActivityResult(paramInt1, paramInt2, paramIntent)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onCreate(paramBundle: Bundle?) {
        super.onCreate(paramBundle)
        mViewBinding = MainActivityLayoutBinding.inflate(layoutInflater)
        setContentView(mViewBinding.root)
        mTitle = "Menu"
        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setHomeButtonEnabled(true)
        }
        teamService = TeamListingService.getInstance(this)
        mCheckout.start()
        mCheckout.createPurchaseFlow(PurchaseListener())
        mViewBinding.tvContent.text = "tvContent"
        mViewBinding.tvContent.setOnClickListener {
            startShimejiService()
        }
    }

    override fun onDestroy() {
        mCheckout.stop()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        paramInt: Int,
        paramArrayOfString: Array<String>,
        paramArrayOfint: IntArray
    ) {
        super.onRequestPermissionsResult(paramInt, paramArrayOfString, paramArrayOfint)
        if (paramInt == 201) {
            val uri: Uri? = mCropImageUri
            if (uri != null && paramArrayOfint.isNotEmpty() && paramArrayOfint[0] == 0) {

            } else {
//                Toast.makeText(this, "Cancelling, required permissions are not granted", 1).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkInventory()
    }

    override fun setTitle(paramCharSequence: CharSequence) {
        mTitle = paramCharSequence
        if (supportActionBar != null) supportActionBar!!.title = mTitle
    }

    fun startShimejiService(): Boolean {
        if (hasPermissionToDraw()) {
            warnIfNoShimejiAreSelected()
            startService(
                Intent(this, ShimejiService::class.java)
                    .setAction(ShimejiService.ACTION_START)
            )
            return true
        }
        permission
        return false
    }

    fun stopShimejiService() {
        stopService(Intent(this, ShimejiService::class.java).setAction(ShimejiService.ACTION_STOP))
    }

    fun upgradeExtraAnimations(paramView: View?) {
        mCheckout.whenReady((object : EmptyListener() {
            override fun onReady(param1BillingRequests: BillingRequests) {
                param1BillingRequests.purchase("inapp", "extra_animations", null, mCheckout.purchaseFlow)
            }
        } as Checkout.Listener))
    }

    fun upgradePhysics(paramView: View?) {
        mCheckout.whenReady((object : EmptyListener() {
            override fun onReady(param1BillingRequests: BillingRequests) {
                param1BillingRequests.purchase("inapp", "physics_upgrade", null, mCheckout.purchaseFlow)
            }
        } as Checkout.Listener))
    }

    fun upgradeShimejiNumber(paramView: View?) {
        mCheckout.whenReady((object : EmptyListener() {
            override fun onReady(param1BillingRequests: BillingRequests) {
                param1BillingRequests.purchase("inapp", "extra_slots", null, mCheckout.purchaseFlow)
            }
        } as Checkout.Listener))
    }

    private inner class InventoryCallback : Inventory.Callback {
        override fun onLoaded(param1Products: Products) {
            val list1: List<Purchase> = param1Products["inapp"].purchases
            val list2: List<Sku> = param1Products["inapp"].skus
            teamService?.activeMascots?.setMascotLimit(2)
            for (purchase in list1) {
                if (purchase.state == Purchase.State.PURCHASED || purchase.state == Purchase.State.REFUNDED) {
                    val str = purchase.sku
                    var b: Int = -1
                    when (str.hashCode()) {
                        1708130630 -> {
                            if (str != "extra_slots") break
                            b = 2
                        }
                        -366678786 -> {
                            if (str != "extra_animations") break
                            b = 1
                        }
                        -426905564 -> {
                            if (str != "physics_upgrade") break
                            b = 0
                        }
                    }
                    when (b) {
                        2 -> {
                            teamService?.activeMascots?.setMascotLimit(6)
                            continue
                        }
                        1 -> {
                            Animation.paidEnabled = true
                            continue
                        }
                        0 -> {
                        }
                        else -> continue
                    }
                    Animation.flingEnabled = true
                }
            }
        }
    }

    private inner class PurchaseListener() : EmptyRequestListener<Purchase>() {
        override fun onSuccess(param1Purchase: Purchase) {
            Toast.makeText(this@MainActivity, "????????", Toast.LENGTH_SHORT).show()
            checkInventory()
        }
    }

    companion object {
        const val REQUEST_PERMISSION = 5463
        private const val WALLPAPER_SET = 9991
        fun getApplicationName(context: Context): String {
            val str: String
            val applicationInfo: ApplicationInfo = context.applicationInfo
            val i = applicationInfo.labelRes
            str = if (i == 0) {
                applicationInfo.nonLocalizedLabel.toString()
            } else {
                context.getString(i)
            }
            return str
        }
    }
}
