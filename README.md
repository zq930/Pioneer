<h1 style="text-align: center; margin: 30px 0 30px; font-weight: bold;">Pioneer</h1>
<div style="text-align: center;">
	<a target="_blank" href="https://gitee.com/zq930/Pioneer/blob/master/LICENSE">
		<img src="https://img.shields.io/:license-MIT-blueviolet.svg"/>
	</a>
	<a target="_blank" href="https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html">
		<img src="https://img.shields.io/badge/JDK-8.0.201-orange.svg"/>
	</a>
</div>

## 简介
<div>
一套基于SpringBoot2.6.x + Vue3.x 前后端分离的Java快速开发框架，此项目为后端。
<p>
    <a href="https://gitee.com/zq930/Pioneer">
        <img src="https://img.shields.io/badge/后端项目-Pioneer-yellow.svg">
    </a>
    <a href="https://gitee.com/zq930/PioneerUI">
    <img src="https://img.shields.io/badge/前端项目-PioneerUI-green.svg">
    </a>
</p>
</div>

### 开发部署

1. 安装jdk8+
2. 安装Maven3.6+
3. 安装Redis3.0+
4. 安装数据库，推荐Mysql8.0版本，创建数据库后执行sql/pioneer.sql
5. 开发部署需要配置application.yml环境，以及对应yml中的数据库、redis、文件路径等参数

### 内置功能

1. 用户管理：用户是系统操作者，该功能主要完成系统用户配置。
2. 角色管理：角色菜单权限分配，设置角色按机构进行数据范围权限划分。
3. 菜单管理：配置系统菜单，操作权限，按钮权限标识等。
4. 部门管理：配置系统组织机构，树结构展现支持数据权限。
5. 岗位管理：配置系统用户所属担任职务。
6. 字典管理：对系统中经常使用的一些较为固定的数据进行维护。
7. 参数管理：对系统动态配置常用参数。
8. 通知公告：系统通知公告信息发布维护。
9. 操作日志：系统正常操作日志记录和查询；系统异常信息日志记录和查询。
10. 登录日志：系统登录日志记录查询包含登录异常。
11. 在线用户：当前系统中活跃用户状态监控。
12. 服务监控：监视当前系统CPU、内存、磁盘、堆栈等相关信息。
13. 缓存监控：对系统的缓存信息查询，命令统计等。
14. 代码生成：前后端代码的生成（java、html、xml、sql)支持CRUD下载 。
