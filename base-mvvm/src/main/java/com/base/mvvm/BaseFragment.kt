package com.base.mvvm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.base.util.onClick
import java.lang.reflect.ParameterizedType

/**
 * Base Fragment Support ViewModel and Data Binding.
 * Guide to app architecture: https://developer.android.com/jetpack/docs/guide
 */
abstract class BaseFragment<VM : BaseViewModel, B : ViewDataBinding>(@LayoutRes private val layoutRes: Int) :
    Fragment() {

    protected lateinit var viewModel: VM
    protected lateinit var binding: B

    /**
     * Set BR.variable ID for data binding.
     */
    abstract fun brVariableId(): Int

    /**
     * Initiate view. This method is called in child classes for initiate view of child fragment
     */
    abstract fun initView(binding: B)

    /**
     * Do logic observe data.
     */
    open fun observeData() {
    }


    /**
     * Override this function to handle event from ViewModel.
     */
    open fun onEvent(event: Event) {
        when (event.type) {

            Type.NAVIGATE_SCREEN -> {   //Handle navigate screen.
                if (event.data is NavDirections) {
                    findNavController().navigate(event.data as NavDirections)
                } else if (event.data is Pair<*, *>) {
                    (event.data as Pair<*, *>?)?.let {
                        if (it.first is @IdRes Int && it.second is Bundle?) {
                            findNavController().navigate(it.first as Int, it.second as Bundle?)
                        }
                    }
                }
            }


        }
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
        return setupView(inflater, container, layoutRes)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(binding)
        observeData()
        viewModel.viewEvent.observe(viewLifecycleOwner, Observer {
            onEvent(it) //Observe ViewModel event.
        })
    }

    private fun setupView(inflater: LayoutInflater, container: ViewGroup?, layoutId: Int): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        viewModel =
            if (isFragmentScopeViewModel()) ViewModelProvider(this).get(getGenericType(javaClass) as Class<VM>)
            else ViewModelProvider(activity!!).get(getGenericType(javaClass) as Class<VM>)

        viewModel.args = arguments?: bundleOf()
        viewLifecycleOwner.lifecycle.addObserver(viewModel)

        binding.setVariable(brVariableId(), viewModel)
        binding.lifecycleOwner = this

        return binding.root
    }


    /**
     * Use this method to request a permission.
     * @param permission: Manifest.permission.
     * @param onPermissionResult : callback when request permission is done.
     */
    fun requestPermission(permission: String, onPermissionResult: (granted: Boolean) -> Unit) {
        if (activity == null || activity !is BaseActivity) return
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
    fun <T> LiveData<T?>?.observe(callBack: (data: T?) -> Unit) {
        this?.observe(viewLifecycleOwner, Observer {
            callBack.invoke(it)
        })
    }

    private fun getGenericType(clazz: Class<*>): Class<*> {
        val type = clazz.genericSuperclass
        val paramType = type as ParameterizedType
        return paramType.actualTypeArguments[0] as Class<*>
    }

    /**
     * Set onCLick with suspend function. The view will be disabled until suspend fun complete.
     */
    private fun View?.onClick(suspendFun: suspend () -> Unit) {
        this.onClick(lifecycleScope) {
            suspendFun.invoke()
        }
    }
}
