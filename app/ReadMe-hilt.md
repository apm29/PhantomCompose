## 引入Hilt

> project gradle
```groovy
buildscript {
    dependencies {
        classpath 'com.google.dagger:hilt-android-gradle-plugin:2.28-alpha'
    }
}
```

> app gradle
```groovy
apply plugin: 'kotlin-kapt'
apply plugin: 'dagger.hilt.android.plugin'

android {
}

dependencies {
    implementation "com.google.dagger:hilt-android:2.28-alpha"
    kapt "com.google.dagger:hilt-android-compiler:2.28-alpha"
}
```

## 使用

* ### @HiltAndroidApp注解 -- 应用Application类

生成的这一 Hilt 组件会附加到 Application 对象的生命周期，并为其提供依赖项。此外，它也是应用的父组件，这意味着，其他组件可以访问它提供的依赖项。

* ### 使用 @AndroidEntryPoint 为某个 Android 类添加注释

Hilt 目前支持以下 Android 类：

1.Application（通过使用 @HiltAndroidApp）
2.Activity
3.Fragment
4.View
5.Service
6.BroadcastReceiver



* ### 使用 `@Inject` 注释执行字段注入

  ```kotlin
  @AndroidEntryPoint
  class ExampleActivity : AppCompatActivity() {
  
    @Inject lateinit var analytics: AnalyticsAdapter
    ...
  }
  ```

  

* ### 某个类的构造函数中使用 `@Inject` 注释，以告知 Hilt 如何提供该类的实例：

  ```kotlin
  class AnalyticsAdapter @Inject constructor(
    private val service: AnalyticsService
  ) { ... }
  ```

* ### Hilt 模块是一个带有 `@Module` 注释的类。与 [Dagger 模块](https://developer.android.google.cn/training/dependency-injection/dagger-android#dagger-modules)一样，它会告知 Hilt 如何提供某些类型的实例。与 Dagger 模块不同的是，您必须使用 `@InstallIn` 为 Hilt 模块添加注释，以告知 Hilt 每个模块将用在或安装在哪个 Android 类中。

* ### 使用 @Binds 注入接口实例

  在 Hilt 模块内创建一个带有 `@Binds` 注释的抽象函数。

  `@Binds` 注释会告知 Hilt 在需要提供接口的实例时要使用哪种实现。

  带有注释的函数会向 Hilt 提供以下信息：

  - 函数返回类型会告知 Hilt 函数提供哪个接口的实例。
  - 函数参数会告知 Hilt 要提供哪种实现。

  ```kotlin
  interface AnalyticsService {
    fun analyticsMethods()
  }
  
  // Constructor-injected, because Hilt needs to know how to
  // provide instances of AnalyticsServiceImpl, too.
  class AnalyticsServiceImpl @Inject constructor(
    ...
  ) : AnalyticsService { ... }
  
  @Module
  @InstallIn(ActivityComponent::class)
  abstract class AnalyticsModule {
  
    @Binds
    abstract fun bindAnalyticsService(
      analyticsServiceImpl: AnalyticsServiceImpl
    ): AnalyticsService
  }
  ```

  

* ### 使用 @Provides 注入实例

  告知 Hilt 如何提供此类型的实例，方法是在 Hilt 模块内创建一个函数，并使用 `@Provides` 为该函数添加注释。

  带有注释的函数会向 Hilt 提供以下信息：

  - 函数返回类型会告知 Hilt 函数提供哪个类型的实例。
  - 函数参数会告知 Hilt 相应类型的依赖项。
  - 函数主体会告知 Hilt 如何提供相应类型的实例。每当需要提供该类型的实例时，Hilt 都会执行函数主体。

```kotlin
@Module
@InstallIn(ActivityComponent::class)
object AnalyticsModule {

  @Provides
  fun provideAnalyticsService(
    // Potential dependencies of this type
  ): AnalyticsService {
      return Retrofit.Builder()
               .baseUrl("https://example.com")
               .build()
               .create(AnalyticsService::class.java)
  }
}
```



* ### 为同一类型提供多个绑定

如果您需要让 Hilt 以依赖项的形式提供同一类型的不同实现，必须向 Hilt 提供多个绑定。您可以使用限定符为同一类型定义多个绑定。

限定符是一种注释，当为某个类型定义了多个绑定时，您可以使用它来标识该类型的特定绑定。

仍然接着前面的例子来讲。如果需要拦截对 `AnalyticsService` 的调用，您可以使用带有[拦截器](https://square.github.io/okhttp/interceptors/)的 `OkHttpClient` 对象。对于其他服务，您可能需要以不同的方式拦截调用。在这种情况下，您需要告知 Hilt 如何提供两种不同的 `OkHttpClient` 实现。

首先，定义要用于为 `@Binds` 或 `@Provides` 方法添加注释的限定符

```kotlin
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthInterceptorOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OtherInterceptorOkHttpClient
```

然后，Hilt 需要知道如何提供与每个限定符对应的类型的实例。在这种情况下，您可以使用带有 `@Provides` 的 Hilt 模块。这两种方法具有相同的返回类型，但限定符将它们标记为两个不同的绑定：

```kotlin
@Module
@InstallIn(ApplicationComponent::class)
object NetworkModule {

  @AuthInterceptorOkHttpClient
  @Provides
  fun provideAuthInterceptorOkHttpClient(
    authInterceptor: AuthInterceptor
  ): OkHttpClient {
      return OkHttpClient.Builder()
               .addInterceptor(authInterceptor)
               .build()
  }

  @OtherInterceptorOkHttpClient
  @Provides
  fun provideOtherInterceptorOkHttpClient(
    otherInterceptor: OtherInterceptor
  ): OkHttpClient {
      return OkHttpClient.Builder()
               .addInterceptor(otherInterceptor)
               .build()
  }
}
```

您可以通过使用相应的限定符为字段或参数添加注释来注入所需的特定类型：

```kotlin
// As a dependency of another class.
@Module
@InstallIn(ActivityComponent::class)
object AnalyticsModule {

  @Provides
  fun provideAnalyticsService(
    @AuthInterceptorOkHttpClient okHttpClient: OkHttpClient
  ): AnalyticsService {
      return Retrofit.Builder()
               .baseUrl("https://example.com")
               .client(okHttpClient)
               .build()
               .create(AnalyticsService::class.java)
  }
}

// As a dependency of a constructor-injected class.
class ExampleServiceImpl @Inject constructor(
  @AuthInterceptorOkHttpClient private val okHttpClient: OkHttpClient
) : ...

// At field injection.
@AndroidEntryPoint
class ExampleActivity: AppCompatActivity() {

  @AuthInterceptorOkHttpClient
  @Inject lateinit var okHttpClient: OkHttpClient
}
```



* ### Hilt 中的预定义限定符

Hilt 提供了一些预定义的限定符。例如，由于您可能需要来自应用或 Activity 的 `Context` 类，因此 Hilt 提供了 `@ApplicationContext` 和 `@ActivityContext` 限定符。

* ### 为 Android 类生成的组件

对于您可以从中执行字段注入的每个 Android 类，都有一个关联的 Hilt 组件，您可以在 `@InstallIn` 注释中引用该组件。每个 Hilt 组件负责将其绑定注入相应的 Android 类。

前面的示例演示了如何在 Hilt 模块中使用 `ActivityComponent`。

Hilt 提供了以下组件

| Hilt 组件                   | 注入器面向的对象                           |
| :-------------------------- | :----------------------------------------- |
| `ApplicationComponent`      | `Application`                              |
| `ActivityRetainedComponent` | `ViewModel`                                |
| `ActivityComponent`         | `Activity`                                 |
| `FragmentComponent`         | `Fragment`                                 |
| `ViewComponent`             | `View`                                     |
| `ViewWithFragmentComponent` | 带有 `@WithFragmentBindings` 注释的 `View` |
| `ServiceComponent`          | `Service`                                  |



* ### 组件生命周期

Hilt 会按照相应 Android 类的生命周期自动创建和销毁生成的组件类的实例。

| 生成的组件                  | 创建时机                 | 销毁时机                  |
| :-------------------------- | :----------------------- | :------------------------ |
| `ApplicationComponent`      | `Application#onCreate()` | `Application#onDestroy()` |
| `ActivityRetainedComponent` | `Activity#onCreate()`    | `Activity#onDestroy()`    |
| `ActivityComponent`         | `Activity#onCreate()`    | `Activity#onDestroy()`    |
| `FragmentComponent`         | `Fragment#onAttach()`    | `Fragment#onDestroy()`    |
| `ViewComponent`             | `View#super()`           | 视图销毁时                |
| `ViewWithFragmentComponent` | `View#super()`           | 视图销毁时                |
| `ServiceComponent`          | `Service#onCreate()`     | `Service#onDestroy()`     |



* ### 组件作用域

默认情况下，Hilt 中的所有绑定都未限定作用域。这意味着，每当应用请求绑定时，Hilt 都会创建所需类型的一个新实例。

在本例中，每当 Hilt 提供 `AnalyticsAdapter` 作为其他类型的依赖项或通过字段注入提供它（如在 `ExampleActivity` 中）时，Hilt 都会提供 `AnalyticsAdapter` 的一个新实例。

不过，Hilt 也允许将绑定的作用域限定为特定组件。Hilt 只为绑定作用域限定到的组件的每个实例创建一次限定作用域的绑定，对该绑定的所有请求共享同一实例。

下表列出了生成的每个组件的作用域注释：

| Android 类                                 | 生成的组件                  | 作用域                   |
| :----------------------------------------- | :-------------------------- | :----------------------- |
| `Application`                              | `ApplicationComponent`      | `@Singleton`             |
| `View Model`                               | `ActivityRetainedComponent` | `@ActivityRetainedScope` |
| `Activity`                                 | `ActivityComponent`         | `@ActivityScoped`        |
| `Fragment`                                 | `FragmentComponent`         | `@FragmentScoped`        |
| `View`                                     | `ViewComponent`             | `@ViewScoped`            |
| 带有 `@WithFragmentBindings` 注释的 `View` | `ViewWithFragmentComponent` | `@ViewScoped`            |
| `Service`                                  | `ServiceComponent`          | `@ServiceScoped`         |