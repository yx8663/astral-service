SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for lb_3d_editor_bim_to_gltf
-- ----------------------------
DROP TABLE IF EXISTS `lb_3d_editor_bim_to_gltf`;
CREATE TABLE `lb_3d_editor_bim_to_gltf`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件名',
  `thumbnail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '缩略图',
  `bim_file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'bim源文件路径',
  `bim_file_size` double NULL DEFAULT NULL COMMENT 'bim源文件大小',
  `gltf_file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '转换后的gltf文件路径',
  `gltf_file_size` double NULL DEFAULT NULL COMMENT '转换后的gltf文件大小',
  `conversion_status` int(0) NOT NULL COMMENT '0 转换中 1 转换完成 2 转换失败',
  `conversion_duration` double NULL DEFAULT NULL COMMENT '转换时长（s）',
  `options` json NULL COMMENT '转换配置',
  `delTag` tinyint(0) NOT NULL DEFAULT 0 COMMENT '删除标记，0 未删除 1 已删除',
  `createTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updateTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `delTime` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 137 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'BIM模型轻量化' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of lb_3d_editor_bim_to_gltf
-- ----------------------------

-- ----------------------------
-- Table structure for lb_3d_editor_cad
-- ----------------------------
DROP TABLE IF EXISTS `lb_3d_editor_cad`;
CREATE TABLE `lb_3d_editor_cad`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '文件名',
  `thumbnail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '缩略图',
  `file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '源文件路径',
  `converter_file_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '转换后的文件路径',
  `conversion_status` int(0) NOT NULL COMMENT '0 转换中 1 转换完成 2 转换失败',
  `delTag` tinyint(0) NOT NULL DEFAULT 0 COMMENT '删除标记，0 未删除 1 已删除',
  `createTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updateTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `delTime` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 147 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of lb_3d_editor_cad
-- ----------------------------

-- ----------------------------
-- Table structure for lb_3d_editor_cesium
-- ----------------------------
DROP TABLE IF EXISTS `lb_3d_editor_cesium`;
CREATE TABLE `lb_3d_editor_cesium`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `scene_id` varchar(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '场景ID',
  `cesium_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT 'Cesium token',
  `base_map` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'Amap' COMMENT '底图。Amap：高德  Tianditu：天地图',
  `base_map_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'satellite' COMMENT '底图类型，默认影像图。\r\nsatellite：影像图，vector：矢量图',
  `base_map_option` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '默认底图的json 字符串配置项',
  `need_mark_map` int(0) NOT NULL DEFAULT 0 COMMENT '是否叠加标记图（一般只有影像图需要），默认0。 \r\n 0:不叠加 1:叠加',
  `tianditu_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `coordinate_system` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'GCJ-02' COMMENT '地理坐标系统，默认GCJ-02。 \r\nWGS-84：GPS地理坐标系  \r\nGCJ-02：火星坐标系（高德和Google中国）\r\nBD-09：百度坐标系',
  `minLong` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '最小经度，用于定位threejs位于cesium场景哪个位置',
  `minLat` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '最小纬度，用于定位threejs位于cesium场景哪个位置',
  `maxLong` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '最大经度，用于定位threejs位于cesium场景哪个位置',
  `maxLat` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '最大纬度，用于定位threejs位于cesium场景哪个位置',
  `delTag` tinyint(0) NOT NULL DEFAULT 0 COMMENT '删除标记，0 未删除 1 已删除',
  `createTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updateTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `delTime` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of lb_3d_editor_cesium
-- ----------------------------

-- ----------------------------
-- Table structure for lb_3d_editor_scenes
-- ----------------------------
DROP TABLE IF EXISTS `lb_3d_editor_scenes`;
CREATE TABLE `lb_3d_editor_scenes`  (
  `id` varchar(36) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '主键ID,UUID',
  `sceneType` varchar(24) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL COMMENT '场景类型',
  `sceneName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '场景名称',
  `sceneVersion` int(0) NULL DEFAULT 1 COMMENT '场景版本',
  `sceneIntroduction` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '场景描述',
  `coverPicture` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '保存场景时自动生成的封面图url',
  `hasDrawing` int(0) NOT NULL DEFAULT 0 COMMENT '场景是否包含图纸 0:false  1:true',
  `zip` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '场景zip包',
  `zipSize` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '0KB' COMMENT '场景zip包大小',
  `exampleSceneId` varchar(36) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL COMMENT '创建项目时来源于哪一个示例模板项目，null代表从空项目创建。（fk）',
  `projectType` int(0) NOT NULL DEFAULT 0 COMMENT '项目类型。0：Web3D-THREE  1：WebGIS-Cesium',
  `cesiumConfig` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL COMMENT 'WebGIS-Cesium 类型项目的基础Cesium配置',
  `delTag` tinyint(0) NOT NULL DEFAULT 0 COMMENT '删除标记，0 未删除 1 已删除',
  `createTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updateTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `delTime` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci COMMENT = '场景zip压缩包信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of lb_3d_editor_scenes
-- ----------------------------

-- ----------------------------
-- Table structure for lb_3d_editor_scenes_example
-- ----------------------------
DROP TABLE IF EXISTS `lb_3d_editor_scenes_example`;
CREATE TABLE `lb_3d_editor_scenes_example`  (
  `id` varchar(36) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '主键ID,UUID',
  `sceneType` varchar(24) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL COMMENT '场景类型',
  `sceneName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '场景名称',
  `sceneVersion` int(0) NULL DEFAULT 1 COMMENT '场景版本',
  `sceneIntroduction` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '场景描述',
  `coverPicture` varchar(4000) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '保存场景时自动生成的封面图url',
  `hasDrawing` int(0) NOT NULL DEFAULT 0 COMMENT '场景是否包含图纸 0:false  1:true',
  `zip` varchar(128) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL COMMENT '场景zip包',
  `zipSize` varchar(32) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NOT NULL DEFAULT '0KB' COMMENT '场景zip包大小',
  `projectType` int(0) NOT NULL DEFAULT 0 COMMENT '示例项目类型。0：Web3D-THREE  1：WebGIS-Cesium',
  `cesiumConfig` varchar(1000) CHARACTER SET utf8mb3 COLLATE utf8mb3_unicode_ci NULL DEFAULT NULL COMMENT 'WebGIS-Cesium 类型项目的基础Cesium配置',
  `delTag` tinyint(0) NOT NULL DEFAULT 0 COMMENT '删除标记，0 未删除 1 已删除',
  `createTime` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updateTime` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `delTime` datetime(0) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb3 COLLATE = utf8mb3_unicode_ci COMMENT = '新建场景时的示例表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of lb_3d_editor_scenes_example
-- ----------------------------
INSERT INTO `lb_3d_editor_scenes_example` VALUES ('10430f58-640f-46b6-b575-6ed68c3e25f6', '其他', '风格化场景', 1, '', 'example/scenes/screenshot/风格化场景-1747464759493.png', 0, 'example/scenes/packages/风格化场景/风格化场景.zip', '6.97 MB', 0, NULL, 0, '2024-05-20 01:51:09', '2025-05-17 14:53:17', NULL);
INSERT INTO `lb_3d_editor_scenes_example` VALUES ('81d8cf5d-9412-4abe-ad5e-9cadadbdb4fb', '园区', '城市', 1, '园区级场景示例', 'example/scenes/screenshot/城市-1747462954059.png', 0, 'example/scenes/packages/城市/城市.zip', '111.35 MB', 0, NULL, 0, '2024-08-13 01:10:54', '2025-05-17 14:36:24', NULL);
INSERT INTO `lb_3d_editor_scenes_example` VALUES ('b8459a27-64f0-4a2c-8153-ae4be6f27cd2', '其他', 'Wolf animations', 1, '动画场景（含脚本）', 'example/scenes/screenshot/Wolf animations-1747464964778.png', 0, 'example/scenes/packages/Wolf animations/Wolf animations.zip', '6.59 MB', 0, NULL, 0, '2024-04-24 16:52:16', '2025-05-17 15:05:13', NULL);
INSERT INTO `lb_3d_editor_scenes_example` VALUES ('d84f6e9a-7afa-4846-b9cb-a63b63a6dc68', '其他', '特效材质贴图', 1, '', 'example/scenes/screenshot/特效材质贴图-1747465066588.png', 0, 'example/scenes/packages/特效材质贴图/特效材质贴图.zip', '4.43 MB', 0, NULL, 0, '2024-05-20 01:12:51', '2025-05-17 15:05:05', NULL);

SET FOREIGN_KEY_CHECKS = 1;
