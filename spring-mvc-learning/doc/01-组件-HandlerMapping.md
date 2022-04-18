## 一、HandlerMapping介绍

**HandlerMapping** 的作用是根据请求（request）找到相应的处理器 **Handler** 和 **Interceptors**。然后封装成一个 **HandlerExecutionChain** 对象返回。

```java
HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception;
```

![HandlerMapping](https://image.xuguoliang.top/2022/04/18/HandlerMapping_580t1i.png)

可以看出HandlerMapping可以分成两个分支

一个是继承 **AbstractUrlHandlerMapping**

一个是继承 **AbstractHandlerMethodMapping**

而这两只都继承自抽象类 **AbstractHandlerMapping**



## 二、AbstractHandlerMapping

### 2.1 AbstractHandlerMapping初始化

`AbstractHandlerMapping` 是 `HandlerMapping` 的抽象实现。所有的 `HandlerMapping` 都继承 `AbstractHandlerMapping`。

`AbstractHandlerMapping` 通过模板模式设计了 `HandlerMapping` 实现的整体结构。子类通过末班方法提供一些初始值或者具体算法即可。

`AbstractHandlerMapping` 继承了 `WebApplicationObjectSupport` ，间接继承 `ApplicationObjectSupport` ，初始化的时候回自动调用模板方法 `initApplicationContext`。`AbstractHandlerMapping` 就是在 `initApplicationContext` 方法里面实现的。

```java
@Override
	protected void initApplicationContext() throws BeansException {
		extendInterceptors(this.interceptors);
		detectMappedInterceptors(this.adaptedInterceptors);
		initInterceptors();
	}
```

- **extendInterceptors** 

```java
protected void extendInterceptors(List<Object> interceptors) {
}
```

`extendInterceptors` 模板方法，用于提供给子类一个添加修改`Interceptors`的入口，不过在 SpringMVC 的具体实现中，其实这个方法并没有在子类中进行实现。

- **detectMappedInterceptors**

```java 
protected void detectMappedInterceptors(List<HandlerInterceptor> mappedInterceptors) {
		mappedInterceptors.addAll(BeanFactoryUtils.beansOfTypeIncludingAncestors(
				obtainApplicationContext(), MappedInterceptor.class, true, false).values());
	}
```

`detectMappedInterceptors` 方法会从 SpringMVC 容器以及 Spring 容器中查找所有 `MappedInterceptor` 类型的 `Bean`，查找到之后添加到 `mappedInterceptors` 属性中（其实就是全局的 `adaptedInterceptors` 属性）。

- **initInterceptors**

```java
protected void initInterceptors() {
		if (!this.interceptors.isEmpty()) {
			for (int i = 0; i < this.interceptors.size(); i++) {
				Object interceptor = this.interceptors.get(i);
				if (interceptor == null) {
					throw new IllegalArgumentException("Entry number " + i + " in interceptors array is null");
				}
				this.adaptedInterceptors.add(adaptInterceptor(interceptor));
			}
		}
	}
```

`initInterceptors` 方法主要是进行拦截器的初始化操作，具体内容是将 `interceptors` 集合中的拦截器添加到 `adaptedInterceptors` 集合中。

至此，我们看到，所有拦截器最终都会被存入 **`adaptedInterceptors`** 变量中。

⚠️ `AbstractHandlerMapping` 的初始化其实也就是拦截器的初始化过程。

### 2.2 通过 getHandler 获取处理器 Handler和Interceptor

❓`AbstractHandlerMapping#getHandler` 是怎么实现的

```java
	public final HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
    //调用 getHandlerInternal 方法去尝试获取处理器
		Object handler = getHandlerInternal(request);
		//如果没找到相应的处理器，则调用 getDefaultHandler 方法获取默认的处理器，我们在配置 HandlerMapping 的时候可以配置默认的处理器。
    if (handler == null) {
			handler = getDefaultHandler();
		}
		if (handler == null) {
			return null;
		}
		// 如果找到的处理器是一个字符串，则根据该字符串找去 SpringMVC 容器中找到对应的 Bean。
		if (handler instanceof String) {
			String handlerName = (String) handler;
			handler = obtainApplicationContext().getBean(handlerName);
		}

    //找到 handler 之后，接下来再调用 getHandlerExecutionChain 方法获取 HandlerExecutionChain 对象。
		HandlerExecutionChain executionChain = getHandlerExecutionChain(handler, request);

		if (logger.isTraceEnabled()) {
			logger.trace("Mapped to " + handler);
		}
		else if (logger.isDebugEnabled() && !request.getDispatcherType().equals(DispatcherType.ASYNC)) {
			logger.debug("Mapped to " + executionChain.getHandler());
		}

    //进行跨域处理的，获取到跨域的相关配置，然后进行验证&配置，检查是否允许跨域。
		if (CorsUtils.isCorsRequest(request)) {
			CorsConfiguration globalConfig = this.corsConfigurationSource.getCorsConfiguration(request);
			CorsConfiguration handlerConfig = getCorsConfiguration(handler, request);
			CorsConfiguration config = (globalConfig != null ? globalConfig.combine(handlerConfig) : handlerConfig);
			executionChain = getCorsHandlerExecutionChain(request, executionChain, config);
		}

		return executionChain;
	}
```

🤔 执行流程：

1. 调用 `getHandlerInternal` 方法去尝试获取处理器，`getHandlerInternal` 方法也是一个模版方法，该方法将在子类中实现。
2. 如果没找到相应的处理器，则调用 `getDefaultHandler` 方法获取默认的处理器，我们在配置 `HandlerMapping` 的时候可以配置默认的处理器。
3. 如果找到的处理器是一个字符串，则根据该字符串找去 SpringMVC 容器中找到对应的 Bean。
4. 确保 `lookupPath` 存在，一会找对应的拦截器的时候会用到。
5. 找到 handler 之后，接下来再调用 `getHandlerExecutionChain` 方法获取 `HandlerExecutionChain` 对象。
6. 进行跨域处理的，获取到跨域的相关配置，然后进行验证&配置，检查是否允许跨域。

🤔 我们再来看看第五步的 `getHandlerExecutionChain` 方法的执行逻辑，正是在这个方法里边把 `handler` 变成了`HandlerExecutionChain`。

```java
protected HandlerExecutionChain getHandlerExecutionChain(Object handler, HttpServletRequest request) {
		HandlerExecutionChain chain = (handler instanceof HandlerExecutionChain ?
				(HandlerExecutionChain) handler : new HandlerExecutionChain(handler));

		String lookupPath = this.urlPathHelper.getLookupPathForRequest(request);
		for (HandlerInterceptor interceptor : this.adaptedInterceptors) {
			if (interceptor instanceof MappedInterceptor) {
				MappedInterceptor mappedInterceptor = (MappedInterceptor) interceptor;
				if (mappedInterceptor.matches(lookupPath, this.pathMatcher)) {
					chain.addInterceptor(mappedInterceptor.getInterceptor());
				}
			}
			else {
				chain.addInterceptor(interceptor);
			}
		}
		return chain;
	}
```

这里直接根据已有的 `handler` 创建一个新的 `HandlerExecutionChain` 对象，然后遍历 `adaptedInterceptors` 集合，该集合里存放的都是拦截器，如果拦截器的类型是 `MappedInterceptor`，则调用 matches 方法去匹配一下，看一下是否是拦截当前请求的拦截器，如果是，则调用 `chain.addInterceptor` 方法加入到 `HandlerExecutionChain` 对象中；如果就是一个普通拦截器，则直接加入到 `HandlerExecutionChain` 对象中。



