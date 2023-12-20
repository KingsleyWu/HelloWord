  1、声明 xxxEvent
  2、发送 Application Scope 事件
```
    // 在 Activity 中发送 Application Scope 事件
    postEvent(xxxEvent(any))
    // 在 Fragment 中发送 Application Scope 事件
    postEvent(xxxEvent(any))
```
  3、发送 Activity Scope 事件
```
    // 在 Activity 中发送 Activity Scope 事件
    postEvent(this, xxxEvent(any))
    // 在 Fragment 中发送 Activity Scope 事件
    postEvent(requireActivity(), xxxEvent(any))
```
  5、发送 Fragment Scope 事件
```
    // 在 Fragment 中发送 Fragment Scope 事件
    postEvent(this, xxxEvent(any))
```
  6、监听事件
```
    // 监听全局事件
    observeEvent<xxxEvent> {
       // 监听全局回调
    }

    //在 Fragment 监听 Activity 事件
    observeEvent<xxxEvent>(scope = requireActivity(), isSticky = true) {
        // 监听Activity 的粘性事件回调
    }

    observeEvent<xxxEvent>(
        scope = requireActivity(),
        minActiveState = Lifecycle.State.STARTED
    ) {
        Log.d(MainActivity.TAG, "TestFragment received FragmentEvent 3:${it.name}")
    }

    observeEvent<xxxEvent>(scope = requireActivity(), Dispatchers.IO) {
        Log.d(MainActivity.TAG, "TestFragment received FragmentEvent 4:${it.name}")
    }

    observeEvent<xxxEvent>(
        scope = requireActivity(),
        Dispatchers.IO,
        Lifecycle.State.STARTED
    ) {
        Log.d(MainActivity.TAG, "TestFragment received FragmentEvent 5:${it.name}")
    }
```
  