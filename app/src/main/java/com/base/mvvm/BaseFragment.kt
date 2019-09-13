package com.base.mvvm

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.base.util.L
import com.base.util.PermissionUtil
import com.base.util.ifGranted
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
    abstract fun getLayoutId(): Int


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
    open fun isFragmentScopeViewModel(): Boolean = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return setupView(inflater, container, getLayoutId())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeData()
        mViewModel.viewEvent.observe(this, Observer {
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


        //TODO: Uncomment this line after build succeed
        //mViewBinding.setVariable(BR.viewModel, mViewModel)
        mViewBinding.lifecycleOwner = this
        view = mViewBinding.root

        return view
    }


    private val permissionMap = HashMap<Int, (Boolean) -> Unit>()
    /**
     * Use this method to request a permission.
     * @param permission: Manifest.permission.
     * @param onPermissionResult : callback when request permission is done.
     */
    fun requestPermission(permission: String, onPermissionResult: (granted: Boolean) -> Unit) {
        if (activity == null) return
        if (requireContext().ifGranted(permission)) {
            onPermissionResult(true)
            return
        } else {
            val requestCode = PermissionUtil.getRequestCode(permission)
            permissionMap[requestCode] = onPermissionResult

            requestPermissions(arrayOf(permission), requestCode)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        L.d("requestCode: $requestCode permissions: $permissions grantResults: $grantResults")
        if (!permissionMap.contains(requestCode)) return

        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            // permission was granted
            permissionMap[requestCode]!!.invoke(true)
        } else {
            // permission denied
            permissionMap[requestCode]!!.invoke(false)
        }
        permissionMap.remove(requestCode)

    }


    private fun getGenericType(clazz: Class<*>): Class<*> {
        val type = clazz.genericSuperclass
        val paramType = type as ParameterizedType
        return paramType.actualTypeArguments[0] as Class<*>
    }
}
