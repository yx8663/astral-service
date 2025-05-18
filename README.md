# AstralService

🌍
*[简体中文](README.md)*

基于`Java:8`和`SpringBoot:2.7.18`的 [Astral3DEditor](https://github.com/mlt131220/Astral3DEditor) 项目后端代码.

![Static Badge](https://img.shields.io/badge/Java-8-green)
![Static Badge](https://img.shields.io/badge/SpringBoot-2.7.18-8732D7)
![Static Badge](https://img.shields.io/badge/license-MIT-blue)

## 快速开始
```shell
   git clone https://github.com/yx8663/astral-service.git
```
### 配置
* 数据库使用 MySQL，表结构数据文件位于： `static/sql/astral.sql`；
* 配置文件路径： `astral-web/src/main/resources/application.yml`;
* 修改配置文件下 `spring.datasource` 项为自己的数据库连接；
* 修改配置文件下 `astral.uploadType` 项为自己使用的文件存储方式：`本地-local 又拍云-upyun`；
* 修改配置文件下 `astral.uploadDir` 项为自己使用的文件存储方式对应的存储目录：
    * `本地-local`存储推荐修改为此项目下的`static`文件夹；
    * `又拍云-upyun`存储按需配置存储路径；
* 如果使用`又拍云-upyun`存储，还需修改配置文件下 `upyun`项配置：
    * `bucket` 为对应存储桶名；
    * `operator` 为在又拍云为该桶配置的操作员账号；
    * `password` 为对应操作员密码；
    * `domain` 为该存储使用的加速域名（为保证又拍云信息不在前端泄露，故不使用在`HTTP Header`/`HTTP Body`签名认证的方式鉴权，而是配置对应存储的加速域名，将前端请求重定向来通过该加速域名获取资源）；
* 修改配置文件下 `dev` 项配置：
    * `currentAbPath` 为本项目绝对路径地址；
    * `cadDwgConverterAbPath` 为本地CAD Dwg转换器执行程序文件夹绝对路径，转换程序使用 libreDWG(已包含在项目static/lib/libredwg文件夹)；
    * `temporaryFolder` 为临时文件夹地址，推荐配置为本项目地址下的`static/tmp`文件夹；

## 运行

### 方式一
> IDEA中直接运行@SpringBootApplication注解的类的main方法： <br />
> `astral-web/src/main/java/com.astral.web/AstralWebApplication/AstralWebApplication`

### 方式二
> cmd中执行命令 `mvn spring-boot:run`

### 方式三(打包)
> cmd中执行命令：
>   * 生成jar包: mvn clean package
>   * 进入生成的jar包目录：cd astral-web/target
>   * 运行项目：java -jar astral-web-exec.jar

## Star History
[![Star History Chart](https://api.star-history.com/svg?repos=yx8663/astral-service&type=Date)](https://star-history.com/#yx8663/astral-service&Date)