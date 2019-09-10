# PhotoXiu

# 简介
个人项目。

目前已有功能：
1. 图片裁剪
2. 主页`banner`和下面的`RecyclerView`展示
3. 主页的按钮点击以及`ARouter`跳转
4. 图片亮度、色调、饱和度设置
5. 简单的OpenGL ES 2.0 Demo

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
- 图片加载:
[bumptech/glide](https://github.com/bumptech/glide)
- OpenGL ES 2.0

# Change Log

## v0.0.50alpha (2019-09-10 22:16:04)
+ 添加OpenGL ES 2.0，并写了一个冰球1.0版本(只有基本界面，没有颜色)
+ 通过使用`Glide`替换`setImageResource`解决了锤子手机`RecyclerView`滑动卡顿问题
+ 解决图片调色之后会将图片保存在本地的问题(因为`activity`传递图片我是用过`uri`传递的，所以会暂时把中间文件保存在本地)
+ 加入了`app`的`build.gradle`的依赖问题
+ 解决了每个`module`资源明明重复问题(限定必须以限定的名称开头)

## v0.0.4-alpha (2019-08-13 21:11:16)
+ 加入了修改图片的亮度、色调、饱和度三个功能
+ 在`app`的`build.gradle`添加了`onlyRunCompile`之后`Build`会报错，于是删除
+ 涉及到了`Android6.0`权限问题，想加入`PermissionsDispatcher`，但是导包并且在代码中加入注解之后`MakeProject`会报错，找不到`kaptDebugKotlin`
+ 发现的新bug，主页下面展示图片的`RecyclerView`，我使用3个手机进行测试，三星S8+(Android9.0)、一加7(Android9.0)滑动流畅，但是锤子坚果pro2(Android9.0)滑动卡顿


## v0.0.3-alpha (2019-08-09 10:01:57)
+ 添加了主页的`Scrolling Activity`
+ 添加了主页的`banner`，加入自动切换
+ 添加了主页下面展示图片的瀑布流
+ 添加了主页上方的功能点击按钮
+ 设置了`AppBarLayout`滚动监听时间
+ 添加了裁剪页的下方的横向滑动选项窗口

## v0.0.2-alpha (2019-08-06 15:41:18)
- 加入了`uCrop`，完成了图片裁剪

## v0.0.1-alpha (2019-07-30 16:25:57)
项目开始
- 搭好组件化框架
- 加入屏幕适配