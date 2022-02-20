package com.alimoradi.elitefilebrowser.settings

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter
import com.alimoradi.elitefilebrowser.settings_android_q.SettingsAndroidQView
import com.alimoradi.elitefilebrowser.settings_authentication.SettingsAuthenticationView
import com.alimoradi.elitefilebrowser.settings_ram.SettingsRamView
import com.alimoradi.elitefilebrowser.settings_storage.SettingsStorageView
import com.alimoradi.elitefilebrowser.settings_theme.SettingsThemeView

class SettingsAdapter : ListDelegationAdapter<List<Any>>() {

    init {
        delegatesManager.addDelegate(SettingsThemeAdapterDelegate() as AdapterDelegate<List<Any>>)
        delegatesManager.addDelegate(SettingsStorageAdapterDelegate() as AdapterDelegate<List<Any>>)
        delegatesManager.addDelegate(SettingsRamAdapterDelegate() as AdapterDelegate<List<Any>>)
        delegatesManager.addDelegate(SettingsAuthenticationAdapterDelegate() as AdapterDelegate<List<Any>>)
        delegatesManager.addDelegate(SettingsAndroidQAdapterDelegate() as AdapterDelegate<List<Any>>)
    }

    fun populate(list: List<Any>) {
        setItems(list)
        notifyDataSetChanged()
    }

    //region SettingsTheme
    class SettingsTheme

    private class SettingsThemeAdapterDelegate : AbsListItemAdapterDelegate<Any, Any, SettingsThemeViewHolder>() {

        override fun isForViewType(o: Any, list: List<Any>, i: Int) = o is SettingsTheme

        override fun onCreateViewHolder(viewGroup: ViewGroup): SettingsThemeViewHolder {
            val view = SettingsThemeView(viewGroup.context)
            view.layoutParams = createDefaultRecyclerViewLayoutParam()
            return SettingsThemeViewHolder(view)
        }

        override fun onBindViewHolder(model: Any, titleViewHolder: SettingsThemeViewHolder, list: List<Any>) {}
    }

    private class SettingsThemeViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view)
    //endregion SettingsTheme

    //region SettingsStorage
    class SettingsStorage

    private class SettingsStorageAdapterDelegate :
        AbsListItemAdapterDelegate<Any, Any, SettingsStorageViewHolder>() {

        override fun isForViewType(o: Any, list: List<Any>, i: Int) = o is SettingsStorage

        override fun onCreateViewHolder(viewGroup: ViewGroup): SettingsStorageViewHolder {
            val view = SettingsStorageView(viewGroup.context)
            view.layoutParams = createDefaultRecyclerViewLayoutParam()
            return SettingsStorageViewHolder(view)
        }

        override fun onBindViewHolder(model: Any, titleViewHolder: SettingsStorageViewHolder, list: List<Any>) {}
    }

    private class SettingsStorageViewHolder(view: View) : RecyclerView.ViewHolder(view)
    //endregion SettingsStorage

    //region SettingsRam
    class SettingsRam

    private class SettingsRamAdapterDelegate :
        AbsListItemAdapterDelegate<Any, Any, SettingsRamViewHolder>() {

        override fun isForViewType(o: Any, list: List<Any>, i: Int) = o is SettingsRam

        override fun onCreateViewHolder(viewGroup: ViewGroup): SettingsRamViewHolder {
            val view = SettingsRamView(viewGroup.context)
            view.layoutParams = createDefaultRecyclerViewLayoutParam()
            return SettingsRamViewHolder(view)
        }

        override fun onBindViewHolder(model: Any, titleViewHolder: SettingsRamViewHolder, list: List<Any>) {}
    }

    private class SettingsRamViewHolder(view: View) : RecyclerView.ViewHolder(view)
    //endregion SettingsRam



    //region SettingsAuthentication
    class SettingsAuthentication

    private class SettingsAuthenticationAdapterDelegate :
        AbsListItemAdapterDelegate<Any, Any, SettingsAuthenticationViewHolder>() {

        override fun isForViewType(o: Any, list: List<Any>, i: Int) = o is SettingsAuthentication

        override fun onCreateViewHolder(viewGroup: ViewGroup): SettingsAuthenticationViewHolder {
            val view = SettingsAuthenticationView(viewGroup.context)
            view.layoutParams = createDefaultRecyclerViewLayoutParam()
            return SettingsAuthenticationViewHolder(view)
        }

        override fun onBindViewHolder(model: Any, titleViewHolder: SettingsAuthenticationViewHolder, list: List<Any>) {}
    }

    private class SettingsAuthenticationViewHolder(view: View) : RecyclerView.ViewHolder(view)
    //endregion SettingsAuthentication

    //region SettingsAndroidQ
    class SettingsAndroidQ

    private class SettingsAndroidQAdapterDelegate :
        AbsListItemAdapterDelegate<Any, Any, SettingsAndroidQViewHolder>() {

        override fun isForViewType(o: Any, list: List<Any>, i: Int) = o is SettingsAndroidQ

        override fun onCreateViewHolder(viewGroup: ViewGroup): SettingsAndroidQViewHolder {
            val view = SettingsAndroidQView(viewGroup.context)
            view.layoutParams = createDefaultRecyclerViewLayoutParam()
            return SettingsAndroidQViewHolder(view)
        }

        override fun onBindViewHolder(model: Any, titleViewHolder: SettingsAndroidQViewHolder, list: List<Any>) {}
    }

    private class SettingsAndroidQViewHolder(view: View) : RecyclerView.ViewHolder(view)
    //endregion SettingsAndroidQ


    //region SettingsAbout


    companion object {

        private fun createDefaultRecyclerViewLayoutParam() = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }
}
