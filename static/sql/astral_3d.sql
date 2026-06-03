SET FOREIGN_KEY_CHECKS = 0;
SET NAMES utf8mb4;
-- astral_3d_assets_category DDL
CREATE TABLE `astral_3d_assets_category` (`id` INT NOT NULL AUTO_INCREMENT,
`type` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL Comment "类型",
`pcode` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL Comment "父级编码",
`code` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL Comment "编码",
`name` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL Comment "文件名",
`sort_num` INT NULL Comment "序号",
`delTag` TINYINT NOT NULL DEFAULT 0 Comment "删除标记，0 未删除 1 已删除",
`createTime` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
`updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
`delTime` DATETIME NULL,
PRIMARY KEY (`id`)) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci AUTO_INCREMENT = 70100 ROW_FORMAT = Dynamic COMMENT = "资产分类";
-- astral_3d_assets_info DDL
CREATE TABLE `astral_3d_assets_info` (`id` INT NOT NULL AUTO_INCREMENT,
`name` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL Comment "文件名",
`type` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL Comment "类型",
`category` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL Comment "类别",
`thumbnail` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL Comment "缩略图",
`size` VARCHAR(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL Comment "文件大小",
`tags` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL Comment "标签",
`file` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL Comment "附件地址",
`delTag` TINYINT NOT NULL DEFAULT 0 Comment "删除标记，0 未删除 1 已删除",
`createTime` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
`updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
`delTime` DATETIME NULL,
PRIMARY KEY (`id`)) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci AUTO_INCREMENT = 161 ROW_FORMAT = Dynamic COMMENT = "资产信息";
-- astral_3d_cad DDL
CREATE TABLE `astral_3d_cad` (`id` INT NOT NULL AUTO_INCREMENT,
`file_name` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL Comment "文件名",
`thumbnail` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL Comment "缩略图",
`file_path` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL Comment "源文件路径",
`converter_file_path` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL Comment "转换后的文件路径",
`conversion_status` INT NOT NULL Comment "0 转换中 1 转换完成 2 转换失败",
`delTag` TINYINT NOT NULL DEFAULT 0 Comment "删除标记，0 未删除 1 已删除",
`createTime` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
`updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
`delTime` DATETIME NULL,
PRIMARY KEY (`id`)) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci AUTO_INCREMENT = 376 ROW_FORMAT = Dynamic COMMENT = "cad解析";
-- astral_3d_scenes DDL
CREATE TABLE `astral_3d_scenes` (`id` VARCHAR(36) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL Comment "主键ID,UUID",
`sceneType` VARCHAR(24) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL Comment "场景类型",
`sceneName` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL Comment "场景名称",
`sceneVersion` INT NULL DEFAULT 1 Comment "场景版本",
`sceneIntroduction` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL Comment "场景描述",
`coverPicture` VARCHAR(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL Comment "保存场景时自动生成的封面图url",
`hasDrawing` INT NOT NULL DEFAULT 0 Comment "场景是否包含图纸 0:false  1:true",
`zip` VARCHAR(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL Comment "场景zip包",
`zipSize` VARCHAR(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '0KB' Comment "场景zip包大小",
`exampleSceneId` VARCHAR(36) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL Comment "创建项目时来源于哪一个示例模板项目，null代表从空项目创建。（fk）",
`projectType` INT NOT NULL DEFAULT 0 Comment "项目类型。0：Web3D  1：WebGIS",
`delTag` TINYINT NOT NULL DEFAULT 0 Comment "删除标记，0 未删除 1 已删除",
`createTime` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
`updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
`delTime` DATETIME NULL,
PRIMARY KEY (`id`)) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci ROW_FORMAT = Dynamic COMMENT = "场景zip压缩包信息表";
-- astral_3d_scenes_example DDL
CREATE TABLE `astral_3d_scenes_example` (`id` VARCHAR(36) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL Comment "主键ID,UUID",
`sceneType` VARCHAR(24) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL Comment "场景类型",
`sceneName` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL Comment "场景名称",
`sceneVersion` INT NULL DEFAULT 1 Comment "场景版本",
`sceneIntroduction` VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL Comment "场景描述",
`coverPicture` VARCHAR(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL Comment "保存场景时自动生成的封面图url",
`hasDrawing` INT NOT NULL DEFAULT 0 Comment "场景是否包含图纸 0:false  1:true",
`zip` VARCHAR(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL Comment "场景zip包",
`zipSize` VARCHAR(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '0KB' Comment "场景zip包大小",
`projectType` INT NOT NULL DEFAULT 0 Comment "示例项目类型。0：Web3D  1：WebGIS",
`delTag` TINYINT NOT NULL DEFAULT 0 Comment "删除标记，0 未删除 1 已删除",
`createTime` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
`updateTime` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP(0),
`delTime` DATETIME NULL,
PRIMARY KEY (`id`)) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci ROW_FORMAT = Dynamic COMMENT = "新建场景时的示例表";
-- astral_3d_assets_category DML
INSERT INTO `astral_3d_assets_category` (`id`,`type`,`pcode`,`code`,`name`,`sort_num`,`delTag`,`createTime`,`updateTime`,`delTime`) VALUES (1,'Model',NULL,'Building','建筑物',1,0,'2025-07-22 17:42:58','2025-07-22 18:09:54',NULL),(2,'Model',NULL,'Plant','植物',2,0,'2025-07-22 17:43:50','2025-07-22 18:09:54',NULL),(3,'Model',NULL,'Animal','动物',3,0,'2025-07-22 17:45:02','2025-07-22 18:09:55',NULL),(4,'Model',NULL,'Traffic','交通',4,0,'2025-07-22 17:45:37','2025-07-22 18:30:05',NULL),(5,'Model',NULL,'Furniture','家具',5,0,'2025-07-22 17:46:00','2025-07-22 18:09:55',NULL),(6,'Model','','Outdoor','室外',6,0,'2025-07-22 17:52:34','2025-07-22 18:10:28',NULL),(7,'Model','','Natural','自然元素',7,0,'2025-07-22 18:02:32','2025-07-22 18:10:29',NULL),(8,'Model','','Security','安全',8,0,'2025-07-22 18:02:32','2025-07-22 18:57:34',NULL),(9,'Model','','Other','其他',9,0,'2025-07-22 18:02:32','2025-07-22 18:57:34',NULL),(101,'Model','Building','Residence','住宅',1,0,'2025-07-22 18:02:14','2025-07-22 18:52:18',NULL),(102,'Model','Building','Common','公共',2,0,'2025-07-22 18:05:31','2025-07-22 18:52:20',NULL),(103,'Model','Building','Industry','工业',3,0,'2025-07-22 18:09:26','2025-07-22 18:52:25',NULL),(104,'Model','Building','Agriculture','农业',4,0,'2025-07-22 18:11:50','2025-07-22 18:52:28',NULL),(201,'Model','Plant','Arbor','乔木',1,0,'2025-07-22 18:16:30','2025-07-22 18:52:33',NULL),(202,'Model','Plant','Shrub','灌木',2,0,'2025-07-22 18:17:09','2025-07-22 18:52:36',NULL),(203,'Model','Plant','Herb','草本',3,0,'2025-07-22 18:17:57','2025-07-22 18:52:44',NULL),(204,'Model','Herb','Grass','草叶',1,0,'2025-07-22 18:19:41','2025-07-22 18:52:47',NULL),(205,'Model','Herb','Flower','花',2,0,'2025-07-22 18:20:30','2025-07-22 18:52:50',NULL),(301,'Model','Animal','Person','人',1,0,'2025-07-22 18:23:27','2025-07-22 18:52:55',NULL),(302,'Model','Animal','Beast','兽',2,0,'2025-07-22 18:24:20','2025-07-22 18:52:58',NULL),(303,'Model','Animal','Livestock','家畜',3,0,'2025-07-22 18:25:12','2025-07-22 18:53:00',NULL),(304,'Model','Animal','Bird','鸟',4,0,'2025-07-22 18:25:48','2025-07-22 18:53:03',NULL),(305,'Model','Animal','Fish','鱼',5,0,'2025-07-22 18:29:29','2025-07-22 18:53:06',NULL),(401,'Model','Traffic','Land','陆',1,0,'2025-07-22 18:29:53','2025-07-22 18:53:14',NULL),(402,'Model','Traffic','Sky','空',2,0,'2025-07-22 18:29:53','2025-07-22 18:53:16',NULL),(403,'Model','Traffic','Sea','海',3,0,'2025-07-22 18:29:53','2025-07-22 18:53:19',NULL),(404,'Model','Traffic','Facilities','设施',4,0,'2025-07-22 18:34:11','2025-07-22 18:53:28',NULL),(405,'Model','Land','Car','汽车',1,0,'2025-07-22 18:29:53','2025-07-22 18:53:31',NULL),(406,'Model','Land','Train','火车',2,0,'2025-07-22 18:38:16','2025-07-22 18:53:35',NULL),(407,'Model','Land','Non-motor vehicles','非机动车',3,0,'2025-07-22 18:38:16','2025-07-22 18:53:38',NULL),(408,'Model','Land','Tool accessories','工具配件',4,0,'2025-07-22 18:38:16','2025-07-22 18:53:38',NULL),(430,'Model','Sky','Aircraft','飞机',1,0,'2025-07-22 18:38:16','2025-08-01 01:21:55',NULL),(431,'Model','Sky','Drones','无人机',2,0,'2025-07-22 18:41:21','2025-08-01 01:21:58',NULL),(460,'Model','Sea','Ship','船舶',1,0,'2025-07-22 18:41:21','2025-08-01 01:22:01',NULL),(701,'Model','Natural','Terrain','地形',1,0,'2025-07-22 18:02:14','2025-07-22 18:55:03',NULL),(702,'Model','Natural','Rock','岩石',2,0,'2025-07-22 18:02:14','2025-07-22 18:56:25',NULL),(801,'Model','Security','Safety precautions','安防',1,0,'2025-07-22 18:02:14','2025-07-22 18:57:11',NULL),(802,'Model','Security','Fire fight','消防',2,0,'2025-07-22 18:02:14','2025-07-22 18:58:00',NULL),(803,'Model','Safety precautions','Camera','摄像头',1,0,'2025-07-22 18:02:14','2025-07-22 22:35:08',NULL),(10000,'Material',NULL,'Metal','金属',1,0,'2025-07-22 22:37:51','2025-07-22 22:37:55',NULL),(10001,'Material',NULL,'Stone','石材',2,0,'2025-07-22 22:38:46','2025-07-22 22:38:49',NULL),(10002,'Material',NULL,'Wood','木材',3,0,'2025-07-22 22:38:46','2025-07-22 22:39:31',NULL),(10003,'Material',NULL,'Fabric','布料',4,0,'2025-07-22 22:45:33','2025-07-22 22:45:38',NULL),(10004,'Material',NULL,'Glass','玻璃',4,0,'2025-07-22 22:45:33','2025-07-22 22:45:38',NULL),(10005,'Material',NULL,'Ground','地面',5,0,'2025-07-22 22:45:33','2025-07-22 22:45:38',NULL),(10006,'Material',NULL,'Other','其他',6,0,'2025-07-22 22:45:33','2025-07-22 22:48:35',NULL),(20000,'Texture',NULL,'Brick','砖石',1,0,'2025-07-22 22:51:04','2025-07-22 22:51:08',NULL),(30000,'Billboard',NULL,'Weather','天气',1,0,'2025-07-22 22:51:40','2025-07-22 22:51:40',NULL),(30001,'Billboard',NULL,'Animal','动物',2,0,'2025-07-22 22:51:40','2025-08-01 21:12:25',NULL),(30002,'Billboard',NULL,'Plant','植物',3,0,'2025-07-22 22:51:40','2025-08-01 21:12:55',NULL),(40000,'HDR',NULL,'Skies','天空',1,0,'2025-07-22 22:59:08','2025-07-22 22:59:08',NULL),(40001,'HDR',NULL,'Outdoor','室外',2,0,'2025-07-22 22:59:08','2025-07-22 22:59:08',NULL),(40002,'HDR',NULL,'Indoor','室内',3,0,'2025-07-22 22:59:08','2025-07-22 23:00:02',NULL),(40003,'HDR',NULL,'Night','夜晚',4,0,'2025-07-22 22:59:08','2025-07-22 23:00:25',NULL);
-- astral_3d_assets_info DML
INSERT INTO `astral_3d_assets_info` (`id`,`name`,`type`,`category`,`thumbnail`,`size`,`tags`,`file`,`delTag`,`createTime`,`updateTime`,`delTime`) VALUES (69,'美国-军事运输机','Model','Aircraft','upload/assets/model/thumbnail/2a2dea5f-f271-4174-8a34-16cd3cb640b6.png','1725120','','upload/assets/model/美国-军事运输机.glb',0,'2025-07-25 15:58:03','2025-08-01 20:39:25',NULL),(70,'1975 保时捷 911 （930） Turbo','Model','Car','upload/assets/model/thumbnail/0724efff-05dd-47a1-8d2e-34e0bc72ae62.png','74151648','','upload/assets/model/1975 保时捷 911 （930） Turbo.glb',0,'2025-07-25 17:03:00','2025-08-01 20:39:18',NULL),(71,'白色皮卡车','Model','Car','upload/assets/model/thumbnail/4992fb7c-65a8-40bf-b2ce-ecf1ad13c715.png','16087476','','upload/assets/model/白色皮卡车.glb',0,'2025-07-25 17:04:34','2025-08-01 20:39:13',NULL),(72,'报废蓝色轿车','Model','Car','upload/assets/model/thumbnail/63cb6721-8fc4-4513-8457-7f0c671f1ce3.png','105257796','','upload/assets/model/报废小车.glb',0,'2025-07-26 01:19:14','2025-07-26 01:44:44',NULL),(73,'蓝色电动车','Model','Non-motor vehicles','upload/assets/model/thumbnail/7147a0f0-c654-4791-ba51-fa62675ee6bf.png','13211600','','upload/assets/model/电动车.glb',0,'2025-07-26 01:25:18','2025-07-26 01:59:08',NULL),(74,'黄色摩托','Model','Non-motor vehicles','upload/assets/model/thumbnail/f3fcb67c-fe2d-4384-a84c-88893333efda.png','19824140','','upload/assets/model/黄色摩托.glb',0,'2025-07-26 01:28:36','2025-07-26 01:28:36',NULL),(75,'基础乘用车集合','Model','Car','upload/assets/model/thumbnail/d1833ba3-6fdf-4781-b918-e2454901724a.png','26701140','合集','upload/assets/model/基础乘用车集合.glb',0,'2025-07-26 01:33:26','2025-07-26 01:33:26',NULL),(77,'警车','Model','Car','upload/assets/model/thumbnail/e0c5cc25-70dd-4999-963e-088a7a8c6e68.png','536092','','upload/assets/model/警车01.glb',0,'2025-07-26 01:46:53','2025-07-26 01:46:53',NULL),(78,'兰博基尼超跑','Model','Car','upload/assets/model/thumbnail/a02819e4-916c-49dc-8e8a-0880c2b46d68.png','8988996','','upload/assets/model/兰博基尼超跑.glb',0,'2025-07-26 01:49:33','2025-07-26 01:49:33',NULL),(79,'生锈旧车','Model','Car','upload/assets/model/thumbnail/f016b8ab-22af-408c-be14-7fc9d72c53f9.png','11735716','','upload/assets/model/生锈旧车.glb',0,'2025-07-26 01:56:15','2025-07-26 01:56:15',NULL),(81,'车辆三角架','Model','Tool accessories','upload/assets/model/thumbnail/1753984412097车辆三角架_thumbnail.png','234116','','upload/assets/model/车辆三角架.glb',0,'2025-08-01 01:37:54','2025-08-01 01:53:35',NULL),(82,'damaged_plaster_1k','Material','Stone','upload/assets/material/thumbnail/1754050471632damaged_plaster_1k_thumbnail.png','3745775','','upload/assets/material/damaged_plaster_1k.zip',0,'2025-08-01 20:14:36','2025-08-01 20:14:36',NULL),(83,'laminate_floor_2k','Material','Wood','upload/assets/material/thumbnail/1754051273902laminate_floor_2k_thumbnail.png','10490629','','upload/assets/material/laminate_floor_2k.zip',0,'2025-08-01 20:27:57','2025-08-01 20:27:57',NULL),(84,'河床岩_2k','Texture','Brick','upload/assets/texture/thumbnail/dry_riverbed_rock_diff_2k.jpg','4334972','','upload/assets/texture/dry_riverbed_rock_diff_2k.jpg',0,'2025-08-01 20:38:23','2025-08-01 20:38:40',NULL),(85,'圣诞树','Billboard','Plant','upload/assets/billboard/thumbnail/1754056407585圣诞树_thumbnail.png','1215','','upload/assets/billboard/圣诞树.svg',0,'2025-08-01 21:53:31','2025-08-01 21:53:31',NULL),(86,'小红花','Billboard','Plant','upload/assets/billboard/thumbnail/1754060832542小红花_thumbnail.png','1610','','upload/assets/billboard/小红花.svg',0,'2025-08-01 22:36:16','2025-08-01 23:07:15',NULL),(87,'sunny_country_road_2k','HDR','Outdoor','upload/assets/hdr/thumbnail/1754069116547sunny_country_road_2k_thumbnail.png','6456061','2K','upload/assets/hdr/sunny_country_road_2k.hdr',0,'2025-08-02 01:25:25','2025-08-12 01:23:25',NULL),(89,'森林-Test1','Tiles','Terrain','upload/assets/tiles/thumbnail/comic_18_1758985698670.jpg','107556',NULL,'upload/assets/tiles/森林-Test1-1758985698719',0,'2025-09-27 23:08:20','2025-09-27 23:08:20',NULL);
SET FOREIGN_KEY_CHECKS = 1;
