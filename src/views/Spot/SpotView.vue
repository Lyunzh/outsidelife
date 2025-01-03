<template>
  <div class="spot-container">
    <!-- 修改返回按钮 -->
    <div class="back-button" @click="goBack">
      <el-button type="primary">
        <i class="el-icon-arrow-left"></i>
        <span>返回景点列表</span>
      </el-button>
    </div>

    <!-- 景点图片展示区 -->
    <div class="spot-image-section">
      <el-carousel height="400px">
        <img :src="spot.imageUrl" class="spot-image" />
      </el-carousel>
    </div>

    <!-- 景点基本信息区 -->
    <div class="spot-info-section">
      <h1 class="spot-title">{{ spot.spotName }}</h1>
      <div class="spot-location">
        <i class="el-icon-location"></i>
        <span>{{ spot.location }}</span>
      </div>
    </div>

    <!-- 景点详细介绍区 -->
    <el-card class="spot-description-section">
      <template #header>
        <div class="card-header">
          <span>景点介绍</span>
        </div>
      </template>
      <p>景点位置：{{ spot.location }}</p>
      <p>景点介绍：{{ spot.description }}</p>
    </el-card>

    <!-- 推荐路线区 -->
    <el-card class="spot-routes-section">
      <template #header>
        <div class="card-header">
          <span>推荐路线</span>
        </div>
      </template>
      <el-collapse>
        <el-collapse-item 
          v-for="route in routes" 
          :key="route.id" 
          :title="route.name"
          :name="route.id">
          <template #title>
            <div class="route-preview">
              <span class="route-name">{{ route.name }}</span>
              <span class="route-brief">
                <i class="el-icon-time"></i> 预计时间：{{ route.time }} |
                <i class="el-icon-location"></i> {{ route.description.substring(0, 30) }}...
              </span>
            </div>
          </template>
          <div @click="getRouteDetail(route.routeId)" class="route-info">
            <div class="route-basic-info">
              <span class="route-duration">
                <i class="el-icon-time"></i>
                预计时长：{{ route.time }}
              </span>
              <span class="route-distance">
                <i class="el-icon-map-location"></i>
                <img :src="route.imageUrl" class="route-map" />
              </span>
              <span class="route-difficulty">
                <i class="el-icon-warning"></i>
                难度：{{ route.difficulty }}
              </span>
            </div>
            <div class="route-description">
              {{ route.description }}
            </div>
          </div>
        </el-collapse-item>
      </el-collapse>
    </el-card>

    <!-- 添加 AI 聊天组件 -->
    <AIChat />
  </div>
</template>

<script>
import { ref } from 'vue';
import { getSpotDetails, getSpotRoutes } from '@/apis/spot';
import { basePicturesPath } from '@/utils/alldata';
import { getUserIdentity } from '@/apis/identity';
import AIChat from '@/components/AIChat.vue';

export default {
  name: 'SpotView',
  components: {
    AIChat
  },
  data() {
    return {
      spotId: null,
      spot: {
        imageUrl: '',
        spotName: '',
        location: '',
        description: ''
      },
      routes: ref([]),
      routeId: null,
      userIdentity: null,
      // 景点数据池
      spotPool: [
        {
          spotId: 1,
          imageUrl: '/images/spots/guyiyuan.jpg',
          spotName: '古猗园',
          location: "121.212401, 31.282552",
          description: '古猗园是上海市著名的古典园林，位于松江区，建于明代。园内亭台楼阁错落有致，花木扶疏，是一处极具江南特色的园林。',
          category: '徒步',
          routes: [1, 2, 3]  // 关联的路线ID
        },
        {
          spotId: 2,
          imageUrl: '/images/spots/sheshan.jpg',
          spotName: '佘山',
          location: "121.218022, 31.280645",
          description: '佘山是上海市最高峰，海拔100米，是著名的登山胜地。山上有著名的佘山天主教堂，是上海市重要的宗教文化景观。',
          category: '徒步',
          routes: [1, 4, 2]
        },
        {
          spotId: 3,
          imageUrl: '/images/spots/binjiang.jpg',
          spotName: '滨江森林公园',
          location: "121.21748, 31.285429",
          description: '滨江森林公园是上海市最大的生态型森林公园，占地面积1000公顷，是市民休闲娱乐的好去处。',
          category: '骑行',
          routes: [2, 3, 1]
        }
      ],
      // 路线数据池
      routePool: {
        1: [1, 2],    // 古猗园 -> 佘山
        2: [2, 3],    // 佘山 -> 滨江
        3: [3, 1],    // 滨江 -> 古猗园
        4: [2]        // 佘山环线
      },
      mockUserIdentity: {
        role: 'user',
        userId: 1,
        username: 'test_user'
      }
    }
  },
  methods: {
    async fetchSpotData(spotId) {
      try {
        const response = await getSpotDetails(spotId);
        this.spot = response.data.data;
        this.spot.imageUrl = basePicturesPath + response.data.data.imageUrl;
      } catch(error) {
        this.$message.error('获取景点数据失败');
        // 使用模拟数据
        const foundSpot = this.spotPool.find(spot => spot.spotId === Number(spotId));
        if (foundSpot) {
          this.spot = foundSpot;
        } else {
          this.$message.error('未找到该景点');
          this.$router.push('/');
        }
      }
    },

    async fetchSpotRoutes(spotId) {
      try {
        const response = await getSpotRoutes(spotId);
        this.routes = response.data.data;
      } catch(error) {
        this.$message.error('获取路线数据失败');
        // 使用模拟数据
        const foundSpot = this.spotPool.find(spot => spot.spotId === Number(spotId));
        if (foundSpot) {
          this.routes = foundSpot.routes.map(routeId => {
            const spots = this.routePool[routeId];
            if (!spots) return null;

            const startSpot = this.spotPool.find(s => s.spotId === spots[0]);
            const endSpot = this.spotPool.find(s => s.spotId === spots[spots.length - 1]);

            return {
              id: routeId,
              name: spots.length === 1
                ? `${startSpot.spotName}环线`
                : `${startSpot.spotName}-${endSpot.spotName}线`,
              time: spots.length === 1 ? "1.5小时" : "2小时",
              description: `从${startSpot.spotName}到${endSpot.spotName}的精品路线`,
              imageUrl: "/images/routes/default.jpg",
              difficulty: spots.length === 1 ? "简单" : "中等",
              spots: spots
            };
          }).filter(route => route);
        } else {
          this.routes = [];
        }
      }
    },

    getRouteDetail(routeId) {
      this.$router.push(`/route/${routeId}`);
    },

    async fetchUserIdentity() {
      try {
        const response = await getUserIdentity();
        this.userIdentity = response.data.data;
      } catch(error) {
        this.$message.error('获取用户身份失败');
        // 使用模拟数据
        this.userIdentity = this.mockUserIdentity;
      }
    },

    goBack() {
      this.$router.go(-1);
    }
  },

  created() {
    const spotId = this.$route.params.id;
    this.spotId = spotId;

    if (!this.spotId) {
      this.$message.error('无效的ID');
      this.$router.push('/');
      return;
    }
  },

  async mounted() {
    if (this.spotId) {
      await this.fetchSpotData(this.spotId);
      await this.fetchSpotRoutes(this.spotId);
      await this.fetchUserIdentity();
    }
  }
}
</script>

<style scoped>
.spot-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.spot-image-section {
  margin-bottom: 30px;
  width: 100%;
  height: 0;
  padding-bottom: 56.25%;  /* 16:9 的比例 */
  position: relative;
  overflow: hidden;
  border-radius: 8px;
}

.spot-image {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  height: 100%;
  object-fit: cover;  /* 保持比例裁剪 */
  object-position: center;  /* 居中裁剪 */
}

/* 如果使用 el-carousel，需要修改其样式 */
:deep(.el-carousel) {
  position: absolute;
  width: 100%;
  height: 100%;
}

:deep(.el-carousel__container) {
  height: 100% !important;
}

:deep(.el-carousel__item) {
  height: 100%;
}

.spot-info-section {
  margin-bottom: 30px;
  text-align: center;
}

.spot-title {
  font-size: 2em;
  margin-bottom: 10px;
}

.spot-location {
  color: #666;
  font-size: 1.1em;
}

.spot-description-section,
.spot-routes-section {
  margin-bottom: 30px;
}

.card-header {
  font-size: 1.2em;
  font-weight: bold;
}

.route-info {
  padding: 10px;
}

.route-basic-info {
  display: flex;
  gap: 20px;
  margin-bottom: 15px;
  color: #666;
}

.route-description {
  margin-bottom: 15px;
  line-height: 1.6;
}

.route-steps ol {
  padding-left: 20px;
  line-height: 1.8;
}

.back-button {
  position: fixed;
  top: 20px;
  left: 20px;
  z-index: 100;
}

.back-button .el-button {
  padding: 12px 20px;
  font-size: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
}

.back-button .el-button i {
  margin: 0;
}

.back-button .el-button span {
  display: inline-block;
  text-align: center;
}

.back-button .el-button:hover {
  transform: translateX(-5px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
}

.route-preview {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 5px 0;
}

.route-name {
  font-weight: bold;
  color: #303133;
  min-width: 120px;
}

.route-brief {
  color: #909399;
  font-size: 0.9em;
  display: flex;
  align-items: center;
  gap: 8px;
  white-space: nowrap;
}

.route-brief i {
  margin-right: 4px;
  color: #409EFF;
}

/* 修改折叠面板的样式 */
:deep(.el-collapse-item__header) {
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 4px;
  transition: all 0.3s ease;
}

:deep(.el-collapse-item__header:hover) {
  background-color: #f0f2f5;
}

:deep(.el-collapse-item__content) {
  padding: 20px;
}

/* 添加鼠标悬停效果 */
.route-info {
  cursor: pointer;
  transition: all 0.3s ease;
}

.route-info:hover {
  background-color: #f8f9fa;
  border-radius: 4px;
}
</style>