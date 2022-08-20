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


五、异常处理

	服务器报错处理，配置异常处理机制

	使用@ControllerAdvice 统一异常处理
	@ExceptionHandler(value=RuntimeException.class) 指定捕获的Exception 该处理是全局的

	所有异常处理都会在common.exception.GlobalExceptionHandler

