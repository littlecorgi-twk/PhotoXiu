# PhotoXiu

# 简介
个人项目。

目前已有功能：
1. 图片裁剪
2. 主页banner和下面的RecyclerView展示
3. 主页的按钮点击以及ARouter跳转
4. 图片亮度、色调、饱和度设置

# 使用框架
- UI美化设计：  
[hwding/android-art](https://github.com/hwding/android-art)  
- 组件化：  
[renxuelong/ComponentDemo](https://github.com/renxuelong/ComponentDemo)  
- ARouter：  
[alibaba/ARouter](https://github.com/alibaba/ARouter)  
- 屏幕适配：  
[JessYanCoding/AndroidAutoSize](https://github.com/JessYanCoding/AndroidAutoSize)  
- 图片裁剪：
    - [msdx/clip-image](https://github.com/msdx/clip-image)  
    - [Yalantis/uCrop](https://github.com/Yalantis/uCrop)
- 权限:  
[permissions-dispatcher/PermissionsDispatcher](https://github.com/permissions-dispatcher/PermissionsDispatcher)

# Change Log

## v0.0.4-alpha (2019-08-13 21:11:16)
+ 加入了修改图片的亮度、色调、饱和度三个功能
+ 在app的build.gradle添加了onlyRunCompile之后Build会报错，于是删除
+ 涉及到了Android6.0权限问题，想加入PermissionsDispatcher，但是导包并且在代码中加入注解之后MakeProject会报错，找不到kaptDebugKotlin
+ 发现的新bug，主页下面展示图片的RecyclerView，我使用3个手机进行测试，三星S8+(Android9.0)、一加7(Android9.0)滑动流畅，但是锤子坚果pro2(Android9.0)滑动卡顿


## v0.0.3-alpha (2019-08-09 10:01:57)
+ 添加了主页的Scrolling Activity
+ 添加了主页的banner，加入自动切换
+ 添加了主页下面展示图片的瀑布流
+ 添加了主页上方的功能点击按钮
+ 设置了AppBarLayout滚动监听时间
+ 添加了裁剪页的下方的横向滑动选项窗口

## v0.0.2-alpha (2019-08-06 15:41:18)
- 加入了uCrop，完成了图片裁剪

## v0.0.1-alpha (2019-07-30 16:25:57)
项目开始
- 搭好组件化框架
- 加入屏幕适配