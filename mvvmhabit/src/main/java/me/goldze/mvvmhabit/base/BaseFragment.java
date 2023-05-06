package me.goldze.mvvmhabit.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hzc.public_method.PageMethod;
import com.trello.rxlifecycle2.components.support.RxFragment;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import me.goldze.mvvmhabit.R;
import me.goldze.mvvmhabit.base.BaseViewModel.ParameterField;
import me.goldze.mvvmhabit.bus.Messenger;
import me.goldze.mvvmhabit.utils.KLog;
import me.goldze.mvvmhabit.utils.MaterialDialogUtils;
import me.goldze.mvvmhabit.widget.TouchLinearLayout;

/**
 * Created by goldze on 2017/6/15.
 */
public abstract class BaseFragment<V extends ViewDataBinding, VM extends BaseViewModel> extends RxFragment implements IBaseView {
    protected V binding;
    protected VM viewModel;
    private int viewModelId;
    private MaterialDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initParam();
    }

    private float startX;
    private float startY;
    boolean isBack;
    private long beforeTime;
    //每毫秒初始宽度
    private float comeWidth;
    //private View showView;
    //当前事件是否被消耗,0未消耗，1横向，2竖向
    private int eventEat;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, initContentView(inflater, container, savedInstanceState), container, false);
        View view = binding.getRoot();

        startX = 0;
        startY = 0;
        isBack = false;
        comeWidth = 0;

        view.setBackgroundColor(binding.getRoot().getContext().getColor(R.color.windowBackground));
        TouchLinearLayout touchLinearLayout = new TouchLinearLayout(getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(layoutParams);
        //ViewGroup.addView导致宽高失效问题,https://www.jianshu.com/p/8c2072232607
        touchLinearLayout.addView(view);
        //防止事件穿透,设置手势侧滑
        FragmentManager manager = requireActivity().getSupportFragmentManager();
        if(manager.getBackStackEntryCount() > 0){
            onTouchClose(touchLinearLayout);
        }

        return touchLinearLayout;
    }


    public void onTouchClose(View view){
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent ev) {
                if(startX == 0 && startY == 0){
                    eventEat = 0;
                    startX = ev.getRawX();
                    startY = ev.getRawY();
                    isBack = false;
                    beforeTime = System.currentTimeMillis();
                    comeWidth = 0;
                }
                switch (ev.getAction()){
                    case MotionEvent.ACTION_MOVE:
                        //来到新的坐标
                        float endX = ev.getRawX();
                        float endY = ev.getRawY();
                        //计算与点击时坐标的偏移量
                        float distanceX = endX - startX;
                        float distanceY = endY - startY;
                        if(eventEat == 0){
                            if(Math.abs(distanceX) > 100){
                                eventEat = 1;
                            }else if(Math.abs(distanceY) > 100){
                                eventEat = 2;
                            }
                        }
                        //水平方向滑动
                        if(eventEat == 1){
                            //右滑
                            if(distanceX > 0){
                                if(view != null){
                                    view.setTranslationX(distanceX);
                                    long lastTime = System.currentTimeMillis();
                                    if(lastTime - beforeTime > 100){
                                        beforeTime = lastTime;
                                        //每毫秒变化宽度
                                        float moveWidth = distanceX - comeWidth;
                                        comeWidth = distanceX;
                                        if(moveWidth > 100){
                                            isBack = true;
                                        }else{
                                            isBack = false;
                                        }
                                    }
                                    if(distanceX > view.getMeasuredWidth() * 1.0 / 2){
                                        isBack = true;
                                    }
                                }
                            }
                        //竖直方向滑动
                        }else if(eventEat == 2){
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if(isBack){
                            PageMethod.onBackPressed(view, requireActivity(), new PageMethod.BackCallback() {
                                @Override
                                public void onBack() {
                                    PageMethod.isTouch = true;
                                    requireActivity().onBackPressed();
                                }
                            });
                        }else{
                            if(view != null){
                                view.setTranslationX(0);
                            }
                        }
                        startX = 0;
                        startY = 0;

                        if(view != null){
                            view.performClick();
                        }
                        break;
                    default:
                        break;
                }
                //拦截水平方向事件
                /*if((eventEat == 1)){
                    //移除链表路径,使触摸事件只执行到外层
                    if(view != null){
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                        view.onTouchEvent(ev);
                    }
                }*/
                return true;
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //解除Messenger注册
        Messenger.getDefault().unregister(viewModel);
        if (viewModel != null) {
            viewModel.removeRxBus();
        }
        if (binding != null) {
            binding.unbind();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //私有的初始化Databinding和ViewModel方法
        initViewDataBinding();
        //私有的ViewModel与View的契约事件回调逻辑
        registorUIChangeLiveDataCallBack();
        //页面数据初始化方法
        initData();
        //页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initViewObservable();
        //注册RxBus
        viewModel.registerRxBus();
    }


    /**
     * 注入绑定
     */
    private void initViewDataBinding() {
        viewModelId = initVariableId();
        viewModel = initViewModel();
        if (viewModel == null) {
            Class modelClass;
            Type type = getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                modelClass = (Class) ((ParameterizedType) type).getActualTypeArguments()[1];
            } else {
                //如果没有指定泛型参数，则默认使用BaseViewModel
                modelClass = BaseViewModel.class;
            }
            viewModel = (VM) createViewModel(this, modelClass);
        }
        binding.setVariable(viewModelId, viewModel);
        //支持LiveData绑定xml，数据改变，UI自动会更新
        binding.setLifecycleOwner(this);
        //让ViewModel拥有View的生命周期感应
        getLifecycle().addObserver(viewModel);
        //注入RxLifecycle生命周期
        viewModel.injectLifecycleProvider(this);
    }

    /**
     * =====================================================================
     **/
    //注册ViewModel与View的契约UI回调事件
    protected void registorUIChangeLiveDataCallBack() {
        //加载对话框显示
        viewModel.getUC().getShowDialogEvent().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String title) {
                showDialog(title);
            }
        });
        //加载对话框消失
        viewModel.getUC().getDismissDialogEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                dismissDialog();
            }
        });
        //跳入新页面
        viewModel.getUC().getStartActivityEvent().observe(this, new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(@Nullable Map<String, Object> params) {
                Class<?> clz = (Class<?>) params.get(ParameterField.CLASS);
                Bundle bundle = (Bundle) params.get(ParameterField.BUNDLE);
                startActivity(clz, bundle);
            }
        });
        //跳入ContainerActivity
        viewModel.getUC().getStartContainerActivityEvent().observe(this, new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(@Nullable Map<String, Object> params) {
                String canonicalName = (String) params.get(ParameterField.CANONICAL_NAME);
                Bundle bundle = (Bundle) params.get(ParameterField.BUNDLE);
                startContainerActivity(canonicalName, bundle);
            }
        });
        //关闭界面
        viewModel.getUC().getFinishEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                if(manager.getBackStackEntryCount() > 0){
                    //manager.popBackStack();
                    getActivity().onBackPressed();
                }else{
                    getActivity().finish();
                }
                try {
                    final Class homeActivity = Class.forName("com.hzc.coolcatmusic.ui.main.HomeActivity");
                    Method method = homeActivity.getMethod("setMEdgeSize",boolean.class);
                    method.invoke(null,true);
                } catch (Exception e) {
                    KLog.e("fragment error:" + e.toString());
                    e.printStackTrace();
                }
            }
        });
        //关闭上一层
        viewModel.getUC().getOnBackPressedEvent().observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void v) {
                getActivity().onBackPressed();
            }
        });
    }

    public void showDialog(String title) {
        if (dialog != null) {
            dialog = dialog.getBuilder().title(title).build();
            dialog.show();
        } else {
            MaterialDialog.Builder builder = MaterialDialogUtils.showIndeterminateProgressDialog(getActivity(), title, true);
            dialog = builder.show();
        }
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * 跳转页面
     *
     * @param clz 所跳转的目的Activity类
     */
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(getContext(), clz));
    }

    /**
     * 跳转页面
     *
     * @param clz    所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(getContext(), clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     */
    public void startContainerActivity(String canonicalName) {
        startContainerActivity(canonicalName, null);
    }

    /**
     * 跳转容器页面
     *
     * @param canonicalName 规范名 : Fragment.class.getCanonicalName()
     * @param bundle        跳转所携带的信息
     */
    public void startContainerActivity(String canonicalName, Bundle bundle) {
        Intent intent = new Intent(getContext(), ContainerActivity.class);
        intent.putExtra(ContainerActivity.FRAGMENT, canonicalName);
        if (bundle != null) {
            intent.putExtra(ContainerActivity.BUNDLE, bundle);
        }
        startActivity(intent);
    }

    /**
     * =====================================================================
     **/

    //刷新布局
    public void refreshLayout() {
        if (viewModel != null) {
            binding.setVariable(viewModelId, viewModel);
        }
    }

    @Override
    public void initParam() {

    }

    /**
     * 初始化根布局
     *
     * @return 布局layout的id
     */
    public abstract int initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    /**
     * 初始化ViewModel的id
     *
     * @return BR的id
     */
    public abstract int initVariableId();

    /**
     * 初始化ViewModel
     *
     * @return 继承BaseViewModel的ViewModel
     */
    public VM initViewModel() {
        return null;
    }

    @Override
    public void initData() {

    }

    @Override
    public void initViewObservable() {

    }

    public boolean isBackPressed() {
        return false;
    }

    /**
     * 创建ViewModel
     *
     * @param cls
     * @param <T>
     * @return
     */
    public <T extends ViewModel> T createViewModel(Fragment fragment, Class<T> cls) {
        return ViewModelProviders.of(fragment).get(cls);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        try{
            viewModel.isHidden = hidden;
            super.onHiddenChanged(hidden);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
