一 创建Springboot

VblogService 后端框架

技术栈：
Maven 3.8.3
Jdk 8
Web
Redis
Mysql

Freemarker 模板引擎 
Devtools 项目的热加载重启插件
Lombok 简化代码的工具

![](pic/create.png)

 

启动类
@SpringBootApplication

SpringApplication.run(***Application.class)


二、配置Mybatis Plus

1.	导入jar包
涉及代码生成，需要导入页面模板引擎Freemarker
2.	配置文件
appliaction.yml
	datasource
	mybatis-plus
3. MyBatisPlusConfig 配置类 开启mapper接口扫描，添加分页插件

通过@mapperScan注解指定要变成实现类的接口所在的包，然后包下面的所有接口在编译之后都会生成相应的实现类。

PaginationInterceptor 分页插件。


4. 直接根据数据库表信息生成entity、service、mapper等接口和实现类。
category,comment,post,user,user_action,user_message,user_collection
CodeGenerator


三、统一返回结果封装类

RestResponse

异步统一返回结果封装
	
	
	status : 结果是否成功 code
	message : 结果消息
	data : 返回结果数据
	

四、 整合shiro+jwt 并会话共享

shiro缓存会话消息 redis存储数据
（集群、负载均衡需要会话共享）
在前后端分离 token或者jwt作为跨域身份校验解决方案 在整合shiro过程中，引入jwt的身份验证过程

1. 快速整合shiro-redis 导入shiro-redis jwt hutool（简化开发） 工具包

2. 编写配置 ShiroConfig

		1. 引入RedisSessionDAO 和 RedisCacheManager 解决shiro的权限数据，会话信息能够保存到redis中实现会话共享

		2. 重写SessionManager和DefaultWebSecurityManager，同时在DefaultWebSecurityManager中关闭shiro自带的session方式，用户不能通过session方式登录shiro。采用jwt凭证登录

		3. 在ShiroFilterChainDefinition中，不通过编码形式拦截Controller访问路径，而是所有路由都需要通过JwtFilter过滤器，判断请求头中是否含有jwt信息 
			有 登录
			无 跳过 Controller中的shiro注解进行再次拦截 @RequireAuthentictaion 控制权限访问

	AccountRealm
		AccountRealm是shiro进行登录或者权限校验的逻辑
			重写三个方法：
			1.	supports 让realm支持jwt的凭证校验
				shiro默认supports为UsernamePasswordToken， 采用jwt方式，需要自定义JwtToekn来完成shiro的supports方法
			2.	doGetAuthorizationInfo 权限校验
			3.	doGetAuthenticationInfo 登录认证校验 通过jwt获取到用户信息，判断用户的状态，如果异常抛出对应的异常信息，否则封装为SImpleAuthenticationInfo返回给shiro.
	
	JwtToekn
		自定义JwtToekn来完成shiro的supports方法

	JwtUtils 
		生成和校验jwt的工具类，其中jwt相关的密钥信息根据项目配置文件的中配置
		@ConfigurationProperties springboot 提供读取配置文件的注解
		实现了BeanPostProcessor接口，在bean被实例化后，会调用后置处理，递归的查找属性，通过反射注入值，对大多数属性而言强制需提供其setter和getter方法。

	AccountProfile
		在登录成功后返回给用户信息的载体

3. application.yml 配置基本配置

	shiro-redis

	vblog:
		jwt:
			加密密钥
			token过期时间
			header头
	
4. spring-boot-devtools增加 resources/META-INF/spring-devtools.properties
热重启不报错
	
	```
	restart.include.shiro-redis=/shiro-[\\w-\\.]+jar
	```


5. 定义Jwt的过滤器JwtFilter

	继承shiro内置的AuthenticatingFilter 内置自动登录方法的过滤器

	重写方法：
		1. createToken: 实现登录，生成自定义支持的JwtToken

		2. onAccesDenied: 拦截校验，当Header没有x-user-token时，直接通过不需要自动登录；当带有x-user-token时，校验jwt的有效性，然后执行executeLogin方法实现自动登录

		3. onLoginFailur: 登录异常时候进入方法，直接将异常信息封装抛出

		4. preHandle: 拦截器的前置拦截，拦截器需要提供跨域支持，不能在进入Controller之前被限制



		使用shiro框架的时候所有请求经过过滤器Filter 来到onPreHandle方法
			· isAccessAllowed 判断是否登录
				登录情况下走此方法，返回true直接访问控制器
				如果isAccessAllowed方法返回True，则不会再调用onAccessDenied方法，如果isAccessAllowed方法返回Flase,则会继续调用onAccessDenied方法。
			· onAccessDenied 是否拒绝登录
				没有登录情况下走此方法
				onAccessDenied方法里面则是具体执行登陆的地方。由于我们已经登陆，所以此方法就会返回True(filter放行),所以上面的onPreHandle方法里面的onAccessDenied方法就不会被执行。





五、异常处理

	服务器报错处理，配置异常处理机制

	使用@ControllerAdvice 统一异常处理
	@ExceptionHandler(value=RuntimeException.class) 指定捕获的Exception 该处理是全局的

	所有异常处理都会在common.exception.GlobalExceptionHandler

	定义全局异常处理
		@ControllerAdvice 定义全局控制器异常处理
		@ExceptionHandler 针对性异常处理，对每种异常针对性处理

			· ShiroException：shiro抛出的异常，比如没有权限，用户登录异常
			· IllegalArgumentException：处理Assert的异常
			· MethodArgumentNotValidException：处理实体校验的异常
			· RuntimeException：捕捉其他异常


	@RequestBody主要用来接收前端传递给后端的json字符串中的数据的(请求体中的数据的)；

六、实体校验
	表单数据提交时
		前端校验使用JQuery Valitdate js插件实现
		后端校验使用Hibernate validatior校验

	springboot 自动集成 Hibernate validatior

	1.  在实体的属性上添加对应的校验规则
		@TableId
		@NotBlank
		@Email

	2. 使用@Validated注解，如果注解不符合要求，系统抛出异常MethodArgumentNotValidException
		在Controller 接口@RequestBody 入参使用


七、 后台进行全局跨域处理
	后台进行全局跨域处理
		CorsConfig implements WebMvcConfigurer 自定义Handler, Interceptor, ViewResolver, MessageConverter等对SpringMVC框架进行配置


八、登录接口开发

	登录逻辑 接受账号密码，根据用户id生成jwt，返回前端（为了后续的jwt延期，把jwt放到header）

	```
		退出登陆不是jwt失效了，一样可以登陆的，这是jwt的弊端
		需要把jwt状态化，比如存在redis中，存在说明可以登陆，退出就清除，但这样就失去了jwt的无状态得特性了
	```

	@CrossOrign
	是用来处理跨域请求的注解，在Controller中添加此注解

	跨域，指的是浏览器不能执行其他网站的脚本。它是由浏览器的同源策略造成的，是浏览器对JavaScript施加的安全限制。所谓同源是指，域名，协议，端口均相同
	(**localhost和127.0.0.1虽然都指向本机，但也属于跨域。)
	

八、主页接口开发


	分类 "/category/{id:\\d*}" (\d* 正则表达式 0个或者多个数字)

	博客页面 "/post/{id:\\d*}"  

		考虑点击量 利用redis

		返回PostVo post前端封装体




九、VUE前端页面开发

1. vue 前端

	vue
	elements-ui
	axios
	mavon-editor
	markdown-it
	github-markdown-cssr

	
2. 环境准备
	node.js
	npm

	vue环境

	```
	# 安装淘宝npm
	npm install -g cnpm --registry=https://registry.npm.taobao.org
	# vue-cli 安装依赖包
	cnpm install --g vue-cli

	```

3. 新建vue项目vblogwebsite
	```
	# 打开vue的可视化管理工具界面
	vue ui
	```

	淘宝npm(cnpm) 提高安装依赖速度
	vue ui是@vue /cli3.0 新增的可视化项目管理工具，可以运行项目，打包项目，检查等操作。


	http://localhost:8080 
	
	创建vue项目 创建目录和运行vue ui同一级 方便管理和切换

	【手动】-> 勾选路由Router、状态管理Vuex, 去掉js校验
	-> 【Use history mode for router】->【创建项目，不保存预设】

	vblogwebsite项目结构

	```
	├── README.md            项目介绍
	├── index.html           入口页面
	├── build              构建脚本目录
	│  ├── build-server.js         运行本地构建服务器，可以访问构建后的页面
	│  ├── build.js            生产环境构建脚本
	│  ├── dev-client.js          开发服务器热重载脚本，主要用来实现开发阶段的页面自动刷新
	│  ├── dev-server.js          运行本地开发服务器
	│  ├── utils.js            构建相关工具方法
	│  ├── webpack.base.conf.js      wabpack基础配置
	│  ├── webpack.dev.conf.js       wabpack开发环境配置
	│  └── webpack.prod.conf.js      wabpack生产环境配置
	├── config             项目配置
	│  ├── dev.env.js           开发环境变量
	│  ├── index.js            项目配置文件
	│  ├── prod.env.js           生产环境变量
	│  └── test.env.js           测试环境变量
	├── mock              mock数据目录
	│  └── hello.js
	├── package.json          npm包配置文件，里面定义了项目的npm脚本，依赖包等信息
	├── src               源码目录 
	│  ├── main.js             入口js文件
	│  ├── app.vue             根组件
	│  ├── components           公共组件目录
	│  │  └── title.vue
	│  ├── assets             资源目录，这里的资源会被wabpack构建
	│  │  └── images
	│  │    └── logo.png
	│  ├── routes             前端路由
	│  │  └── index.js
	│  ├── store              应用级数据（state）状态管理
	│  │  └── index.js
	│  └── views              页面目录
	│    ├── hello.vue
	│    └── notfound.vue
	├── static             纯静态资源，不会被wabpack构建。
	└── test              测试文件目录（unit&e2e）
	  └── unit              单元测试
	    ├── index.js            入口脚本
	    ├── karma.conf.js          karma配置文件
	    └── specs              单测case目录
	      └── Hello.spec.js

	```

4. 安装element-ui(element.eleme.cn) 组件

	```
	# 切换到项目根目录
	cd vueblog-vue
	# 安装element-ui
	cnpm install element-ui --save
	```
	在src目录下main.js 引入element-ui依赖

5. 安装axios（www.axios-js.com)

	axios基于promise的HTTP库，进行前后端对接时，可以提高开发效率

	安装命令

	```
	cnpm install axios --save
	```

	main.js中全局引入axios

	```
	import axios from 'axios'
	Vue.prototype.$axios = axios //
	```

6. 页面路由

	定义路由和页面

	views文件夹下定义页面：
		Post.vue

	路由中心配置
	router\index.js

