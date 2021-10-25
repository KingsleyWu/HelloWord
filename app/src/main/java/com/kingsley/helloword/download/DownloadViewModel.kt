package com.kingsley.helloword.download

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kingsley.base.BaseViewModel
import com.kingsley.download.client.RoomClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DownloadViewModel : BaseViewModel() {

    val appListData: MutableLiveData<MutableList<App>> by lazy {
        MutableLiveData<MutableList<App>>()
    }

    val appList: MutableList<App> = mutableListOf<App>().apply {
        add(
            App(
                "QQ",
                "https://pp.myapp.com/ma_icon/0/icon_6633_1584375640/96",
                "https://imtt.dd.qq.com/16891/apk/C10B2237586138DF3909E47B981B73F9.apk"
            )
        )

        add(
            App(
                "微信",
                "https://pp.myapp.com/ma_icon/0/icon_10910_1583380721/96",
                "https://bfe83cbc93de57db38c2a7f930798dec.dd.cdntips.com/imtt.dd.qq.com/16891/apk/A9CF9330B8F98FDA0702745A0EA2BDFC.apk"
            )
        )

        add(
            App(
                "京东",
                "https://pp.myapp.com/ma_icon/0/icon_7193_1584605936/96",
                "https://35535601a69582a5f12422ce8f6f16ba.dd.cdntips.com/imtt.dd.qq.com/16891/apk/DAC2368DE0F2674DE18C6C627AA29EDB.apk"
            )
        )

        add(
            App(
                "王者荣耀",
                "https://pp.myapp.com/ma_icon/0/icon_12127266_1584332679/96",
                "https://imtt.dd.qq.com/16891/apk/5755AA338BFC61D5A9F7B84302947E50.apk"
            )
        )

        add(
            App(
                "天天酷跑",
                "https://pp.myapp.com/ma_icon/0/icon_10099632_1585033745/96",
                "https://f987ffa6a84bbc922dbdc26201e518e7.dd.cdntips.com/imtt.dd.qq.com/16891/apk/24B43654F6BEA24F5A81A679563D5E13.apk"
            )
        )

        add(
            App(
                "和平精英",
                "https://pp.myapp.com/ma_icon/0/icon_52575843_1578017596/96",
                "https://ab253dfb3b9c9f7e2580baeb3aa0c165.dd.cdntips.com/imtt.dd.qq.com/16891/apk/5AA1A2800257848F3195B2D0F41D5F3E.apk"
            )
        )

        add(
            App(
                "仙剑奇侠传",
                "https://pp.myapp.com/ma_icon/0/icon_11041005_1582009231/96",
                "https://ab253dfb3b9c9f7e2580baeb3aa0c165.dd.cdntips.com/imtt.dd.qq.com/16891/apk/8D86EFC3789D6E4DA6152408DF84471E.apk"
            )
        )

        add(
            App(
                "全民突击",
                "https://pp.myapp.com/ma_icon/0/icon_11285571_1583377568/96",
                "https://a62617b189ba6d736ab3c520ee0d13d3.dd.cdntips.com/imtt.dd.qq.com/16891/apk/3112E5A4F186646C981DBFD2EF341609.apk"
            )
        )

        add(
            App(
                "欢乐斗地主",
                "https://pp.myapp.com/ma_icon/0/icon_97949_1582180721/96",
                "https://imtt.dd.qq.com/16891/apk/53087BD86B84CB6F728E9B57256B3A30.apk"
            )
        )

        add(
            App(
                "天天爱消除(葫芦兄弟版)",
                "https://pp.myapp.com/ma_icon/0/icon_10098215_1583908068/96",
                "https://imtt.dd.qq.com/16891/apk/D36983B2255AA65BE5121B66B7FAE8A9.apk"
            )
        )

        add(
            App(
                "火柴人联盟-新年红包",
                "https://pp.myapp.com/ma_icon/0/icon_12001263_1555092897/96",
                "https://imtt.dd.qq.com/16891/74B5D3819E693020A5513410032C097B.apk"
            )
        )

        add(
            App(
                "阴阳师",
                "https://pp.myapp.com/ma_icon/0/icon_42317517_1584671764/96",
                "https://ab253dfb3b9c9f7e2580baeb3aa0c165.dd.cdntips.com/imtt.dd.qq.com/16891/apk/B20AD9A014A9CCA09DBAD4EA18A56FD9.apk"
            )
        )

        add(
            App(
                "植物大战僵尸2高清版",
                "https://pp.myapp.com/ma_icon/0/icon_12265870_1584343866/96",
                "https://imtt.dd.qq.com/16891/apk/BC1986D2E9B28DFB56761AC526609026.apk"
            )
        )
    }

    fun requestAppListData() {
        appListData.value = appList
        viewModelScope.launch(Dispatchers.IO) {
            val queryLoadingUrls = RoomClient.dataBase.downloadDao().queryLoading()
            for (queryLoadingUrl in queryLoadingUrls) {
                Log.w("111", "$queryLoadingUrl")
            }
        }
    }
}