package com.base.mvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.base.util.toast
import java.lang.reflect.ParameterizedType

/**
 * Base Fragment Support ViewModel and Data Binding.
 * Guide to app architecture: https://developer.android.com/jetpack/docs/guide
 */
abstract class BaseFragment<VM : BaseViewModel, B : ViewDataBinding> : Fragment() {

    protected lateinit var mViewModel: VM
    protected lateinit var mViewBinding: B

    /**
     * Get layout resource id for fragment
     */
    @LayoutRes
    abstract fun layoutId(): Int

    /**
     * Set BR.variable ID for data binding.
     */
    abstract fun brVariableId():Int


    /**
     * Initiate view. This method is called in child classes for initiate view of child fragment
     */
    abstract fun initView()

    /**
     * Do logic observe data.
     */
    open fun observeData() {
    }


    /**
     * Override this function to handle event from ViewModel.
     */
    open fun onEvent(event: Event) {

    }

    /**
     * Override this method if you want your ViewModel only live in fragment's lifecycle.
     * @return true: viewModel fragment scope. false: viewModel activity scope.
     */
    open fun isFragmentScopeViewModel(): Boolean = true


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setupView(inflater, container, layoutId())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeData()
        mViewModel.viewEvent.observe(viewLifecycleOwner, Observer {
            onEvent(it) //Observe ViewModel event.
        })
    }


    private fun setupView(inflater: LayoutInflater, container: ViewGroup?, layoutId: Int): View? {
        val view: View?
        mViewBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        mViewModel =
            if (isFragmentScopeViewModel()) ViewModelProvider(this).get(getGenericType(javaClass) as Class<VM>)
            else ViewModelProvider(activity!!).get(getGenericType(javaClass) as Class<VM>)

        viewLifecycleOwner.lifecycle.addObserver(mViewModel)

        mViewBinding.setVariable(brVariableId(), mViewModel)
        mViewBinding.lifecycleOwner = this
        view = mViewBinding.root

        return view
    }


    /**
     * Use this method to request a permission.
     * @param permission: Manifest.permission.
     * @param onPermissionResult : callback when request permission is done.
     */
    fun requestPermission(permission: String, onPermissionResult: (granted: Boolean) -> Unit) {
        if (activity == null||activity !is BaseActivity) return
        (activity as BaseActivity).requestPermission(permission, onPermissionResult)
    }


    /**
     * Use this method to request multi permissions.
     * @param permissions: Multiple Manifest.permission.
     * @param onPermissionsResult : callback when request permissions is done.
     */
    fun requestMultiPermissions(
        vararg permissions: String,
        onPermissionsResult: (allGranted: Boolean) -> Unit
    ) {
        if (activity == null || activity !is BaseActivity) return
        (activity as BaseActivity).requestMultiPermissions(*permissions) {
            onPermissionsResult(it)
        }
    }


    /**
     * Observe LiveData in a cleaner way.
     */
    fun <T> LiveData<T>?.observe(callBack: (data: T) -> Unit) {
        this?.observe(viewLifecycleOwner, Observer {
            callBack.invoke(it)
        })
    }

    private fun getGenericType(clazz: Class<*>): Class<*> {
        val type = clazz.genericSuperclass
        val paramType = type as ParameterizedType
        return paramType.actualTypeArguments[0] as Class<*>
    }
}
